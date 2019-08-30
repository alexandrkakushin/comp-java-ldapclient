package ru.ak.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * @author a.kakushin
 */
public class LdapEntry {

    private String dn;

    private List<LdapAttribute> attributes;

    public LdapEntry() {}

    public LdapEntry(String dn, List<LdapAttribute> attributes) {
        this();
        this.dn = dn;
        this.attributes = attributes;
    }

    @XmlElement
    public String getDn() {
        return dn;
    }

    @XmlElementWrapper(name = "attributes")
    @XmlElement(name = "attribute")
    public List<LdapAttribute> getAttributes() {
        return attributes;
    }
}
