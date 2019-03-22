package ru.ak.ldap;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.*;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import com.unboundid.util.LDAPTestUtils;
import ru.ak.model.*;
import ru.ak.model.ResponseAvailableAttributes;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author a.kakushin
 */
@WebService(name = "LdapService", serviceName = "LdapService", portName = "LdapServicePort")
public class LdapService {

    private Map<UUID, LDAPConnection> connections = new HashMap<>();

    /**
     * Получение LDAP-соединения по уникальному идентификатору
     * @param uuid идентификатор соединения
     * @return LDAPConnection
     */
    private LDAPConnection getConnection(UUID uuid) {
        return connections.get(uuid);
    }

    /**
     * Добавление LDAP-соединения в список подключений
     * @param connection LDAPConnection
     * @return UUID
     */
    private UUID addConnection(LDAPConnection connection) {
        UUID uuid = UUID.randomUUID();
        connections.put(uuid, connection);
        return uuid;
    }

    /**
     * Подключения
    */

    private LDAPConnection getLdapConnection(UUID uuid) throws Exception {

        LDAPConnection ldapConnection = getConnection(uuid);

        if (ldapConnection == null) {
            throw new Exception("Connection not found");
        } else {
            if (!ldapConnection.isConnected()) {
                throw new Exception("Connection was closed");
            }
        }

        return ldapConnection;
    }

    @WebMethod(operationName = "disconnect")
    public void disconnect(@WebParam(name = "uuid") String uuid) {
        LDAPConnection ldapConnection = getConnection(UUID.fromString(uuid));
        if (ldapConnection != null) {
            if (ldapConnection.isConnected()) {
                ldapConnection.close();
            }
            connections.remove(UUID.fromString(uuid));
        }
    }

    @WebMethod(operationName = "connect")
    public String connect(
            @WebParam(name = "connection") Connection connection,
            @WebParam(name = "viewLdap") ViewLdap viewLdap) throws Exception {

        LDAPConnection ldapConnection = null;

        try {
            // Windows Active Directory
            if (viewLdap == ViewLdap.AD) {
                ldapConnection = auth_simple(connection);

                // Astra Linux Directory
            } else if (viewLdap == ViewLdap.ALD) {
                ldapConnection = auth_GSSAPI(connection);

            } else {
                throw new Exception("No support this view LDAP");
            }
        } catch (LDAPException ex) {
            throw new Exception(ex.getResultCode().toString());
        }

        return addConnection(ldapConnection).toString();
    }

    private LDAPConnection auth_simple(Connection connection) throws LDAPException {
        LDAPConnection ldapConnection = new LDAPConnection(
                connection.getHost(), connection.getPort());

        ldapConnection.bind(connection.getLogin(), connection.getPassword());

        return ldapConnection;
    }

    private LDAPConnection auth_GSSAPI(Connection connection) throws LDAPException {

        GSSAPIBindRequestProperties gssapiProperties =
                new GSSAPIBindRequestProperties(connection.getLogin(), connection.getPassword());
        gssapiProperties.setKDCAddress(connection.getHost());
        gssapiProperties.setRealm(connection.getDomain());

        gssapiProperties.setEnableGSSAPIDebugging(true);

        GSSAPIBindRequest bindRequest = new GSSAPIBindRequest(gssapiProperties);

        LDAPConnection ldapConnection = new LDAPConnection(
                connection.getHost(), connection.getPort());

        ldapConnection.bind(bindRequest);

        return ldapConnection;
    }

    @WebMethod(operationName = "addAttribute")
    public boolean addAttribute(
            @WebParam(name = "uuid") String uuid,
            @WebParam(name = "dn") String dn,
            @WebParam(name = "name") String name,
            @WebParam(name = "value") String value) throws Exception {

        LDAPConnection ldapConnection = getConnection(UUID.fromString(uuid));
        if (ldapConnection != null) {
            if (ldapConnection.isConnected()) {
                Modification mod = new Modification(ModificationType.ADD, name, value);
                ModifyRequest modifyRequest = new ModifyRequest(dn, mod);
                ldapConnection.modify(modifyRequest);
            } else {
                throw new Exception("Connection was closed");
            }
        } else {
            throw new Exception("Connection not found");
        }
        return true;
    }

    /**
     * Поиск
    */

    @SuppressWarnings("ValidExternallyBoundObject")
    @WebMethod(operationName = "searchByFilter")
    public LdapSearch searchByFilter(
            @WebParam(name = "parameters") SearchParameters parameters,
            @WebParam(name = "attributes") String attributes) throws Exception {

        return search(parameters,
                attributes.isEmpty() ? null : attributes.replaceAll("\\s+", "").split(Pattern.quote(",")));
    }

    @SuppressWarnings("ValidExternallyBoundObject")
    @WebMethod(operationName = "availableAttributes")
    public ResponseAvailableAttributes availableAttributes(
            @WebParam(name = "parameters") SearchParameters parameters) throws Exception {

        ResponseAvailableAttributes result = new ResponseAvailableAttributes(new HashSet<>());

        LDAPConnection ldapConnection = getLdapConnection(UUID.fromString(parameters.getUuid()));
        SearchRequest searchRequest = new SearchRequest(
                parameters.getBaseDn(), SearchScope.SUB, parameters.getFilter());

        ASN1OctetString resumeCookie = null;
        while (true) {
            searchRequest.setControls(new SimplePagedResultsControl(100, resumeCookie));
            SearchResult searchResult = ldapConnection.search(searchRequest);

            searchResult.getSearchEntries()
                .forEach(
                    searchResultEntry -> searchResultEntry.getAttributes()
                        .forEach(
                            attribute -> {
                                result.getAttributes().add(attribute.getName());
                            })
                );

            LDAPTestUtils.assertHasControl(searchResult, SimplePagedResultsControl.PAGED_RESULTS_OID);
            SimplePagedResultsControl responseControl = SimplePagedResultsControl.get(searchResult);
            if (responseControl.moreResultsToReturn()) {
                resumeCookie = responseControl.getCookie();
            } else {
                break;
            }
        }
        return result;
    }

    private LdapSearch search(SearchParameters parameters, String[] attributes) throws Exception {

        List<LdapEntry> entries = new ArrayList<>();

        LDAPConnection ldapConnection = getLdapConnection(UUID.fromString(parameters.getUuid()));

        SearchRequest searchRequest = new SearchRequest(
                parameters.getBaseDn(), SearchScope.SUB, parameters.getFilter(), attributes);

        ASN1OctetString resumeCookie = null;
        while (true) {
            searchRequest.setControls(new SimplePagedResultsControl(100, resumeCookie));
            SearchResult searchResult = ldapConnection.search(searchRequest);

            entries.addAll(
                searchResult.getSearchEntries().stream()
                    .map(searchEntry -> new LdapEntry(
                        searchEntry.getDN(),
                        searchEntry.getAttributes().stream()
                            .map(attr -> new LdapAttribute(attr.getName(), AttributeReader.read(attr)))
                            .collect(Collectors.toList())))
                    .collect(Collectors.toList()));

            LDAPTestUtils.assertHasControl(searchResult, SimplePagedResultsControl.PAGED_RESULTS_OID);
            SimplePagedResultsControl responseControl = SimplePagedResultsControl.get(searchResult);
            if (responseControl.moreResultsToReturn()) {
                resumeCookie = responseControl.getCookie();
            } else {
                break;
            }
        }
        return new LdapSearch(entries);
    }
}