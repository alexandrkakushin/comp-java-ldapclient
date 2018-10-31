package ru.ak.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;


/**
 * @author a.kakushin
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class LdapAttribute {

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String value;

    public LdapAttribute(String name, String value) {
        this.name = name;
        if (value != null) {
            this.value = value.trim();
        }
    }
}
