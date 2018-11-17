package ru.ak.ldap;

import com.unboundid.ldap.sdk.*;
import ru.ak.model.Connection;
import ru.ak.model.LdapAttribute;
import ru.ak.model.LdapEntry;
import ru.ak.model.LdapSearch;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author a.kakushin
 */
@WebService(name = "LdapService", serviceName = "LdapService", portName = "LdapServicePort")
public class LdapService {

    Map<UUID, LDAPConnection> connections = new HashMap<>();

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
        BindResult bindResult = ldapConnection.bind(bindRequest);

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
                LDAPResult modifyResult = ldapConnection.modify(modifyRequest);
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
    @WebMethod(operationName = "searchAll")
    public LdapSearch searchAll(
            @WebParam(name = "uuid") String uuid,
            @WebParam(name = "domain") String domain) throws Exception {
        return search(UUID.fromString(uuid), domain, "(objectClass=*)");
    }

    @SuppressWarnings("ValidExternallyBoundObject")
    @WebMethod(operationName = "searchByFilter")
    public LdapSearch searchByFilter(
            @WebParam(name = "uuid") String uuid,
            @WebParam(name = "domain") String domain,
            @WebParam(name = "filter") String filter) throws Exception {
        return search(UUID.fromString(uuid), domain, filter);
    }

    private LdapSearch search(UUID uuid, String domain, String filter) throws Exception {

        List<LdapEntry> entries;

        LDAPConnection ldapConnection = getConnection(uuid);
        if (ldapConnection != null) {
            if (ldapConnection.isConnected()) {
                String[] dc = domain.split(Pattern.quote("."));
                StringBuilder sbDn = new StringBuilder();
                for (int i = 0; i < dc.length; i++) {
                    sbDn.append(String.format("dc=%s%s", dc[i], i < dc.length - 1 ? "," : ""));
                }

                entries = ldapConnection.search(sbDn.toString(), SearchScope.SUB, filter).getSearchEntries().stream()
                    .map(
                        searchEntry -> new LdapEntry(
                            searchEntry.getDN(),
                            searchEntry.getAttributes().stream()
                                .map(attr -> new LdapAttribute(attr.getName(), AttributeReader.read(attr)))
                                .collect(Collectors.toList())))
                    .collect(Collectors.toList());
            } else {
                throw new Exception("Connection was closed");
            }
        } else {
            throw new Exception("Connection not found");
        }
        return new LdapSearch(entries);
    }
}