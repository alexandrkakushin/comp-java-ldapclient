package ru.ak.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * @author a.kakushin
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class LdapEntry {

    private String dn;

    @XmlElementWrapper(name = "attributes")
    @XmlElement(name = "attribute")
    private List<LdapAttribute> attributes;

    public LdapEntry(String dn, List<LdapAttribute> attributes) {
        this.dn = dn;
        this.attributes = attributes;
    }
}
