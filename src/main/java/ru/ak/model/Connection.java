package ru.ak.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author a.kakushin
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Connection {

    private String host;
    private int port;
    private String login;
    private String password;
    private String domain;

    public Connection() {}

    public Connection(String host, int port, String login, String password, String domain) {
        this.host = host;
        this.port = port;
        this.login = login;
        this.password = password;
        this.domain = domain;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getDomain() {
        return domain;
    }
}
