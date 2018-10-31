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
public class LdapSearch {

    @XmlElementWrapper(name = "entries")
    @XmlElement(name = "entry")
    private List<LdapEntry> entries;

    public LdapSearch(List<LdapEntry> entries) {
        this.entries = entries;
    }
}
