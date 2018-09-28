package ru.ak.ldap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author a.kakushin
 */
@WebService(name = "LdapService", serviceName = "LdapService", portName = "LdapServicePort")
public class LdapService {

    @WebMethod
    public List<String> supportedSASLMechanisms (
            @WebParam(name = "host") String host,
            @WebParam(name = "port") String port) {

        List<String> result = new ArrayList<>();

        try {
            DirContext ctx = new InitialDirContext();
            Attributes attrs = ctx.getAttributes(
                    String.format("ldap://%s:%s", host, port), new String[]{"supportedSASLMechanisms"});

            if (attrs != null) {
                BasicAttribute sasl = (BasicAttribute) attrs.get("supportedSASLMechanisms");
                for (int i = 0; i < sasl.size(); i++) {
                    result.add(sasl.get(i).toString());
                }
            }

        } catch (NamingException e) {
            e.printStackTrace();
        }

        return result;
    }
}
