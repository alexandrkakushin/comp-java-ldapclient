package ru.ak.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.Set;

/**
 * @author a.kakushin
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseAvailableAttributes {

    @XmlElementWrapper(name = "attributes")
    @XmlElement(name = "attribute")
    private Set<String> attributes;

    public ResponseAvailableAttributes(Set<String> attributes) {
        this.attributes = attributes;
    }

    public Set<String> getAttributes() {
        return attributes;
    }
}
