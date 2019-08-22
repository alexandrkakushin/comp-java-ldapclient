package ru.ak.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;


/**
 * @author a.kakushin
 */
@XmlRootElement
public class LdapAttribute {

    @XmlAttribute
    private String name;

    @XmlElement
    private List<Object> value = new ArrayList<>();

    public LdapAttribute() {}

    public LdapAttribute(String name, Object value) {
        this();
        this.name = name;
        if (value != null) {
            if (value instanceof List<?>) {
                this.value.addAll((List) value);
            } else {
                this.value.add(value);
            }
        }
    }

    public String getName() {
        return name;
    }

    public List<Object> getValue() {
        return value;
    }
}
