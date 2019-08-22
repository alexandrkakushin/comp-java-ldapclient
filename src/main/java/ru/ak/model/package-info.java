//@javax.xml.bind.annotation.XmlSchema(namespace = "http://ldap.ak.ru/",
// elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)
//
//package ru.ak.model;

@XmlSchema(attributeFormDefault = XmlNsForm.QUALIFIED,
        elementFormDefault = XmlNsForm.QUALIFIED,
        namespace = "http://ldap.ak.ru/",
        xmlns = {
                @XmlNs(namespaceURI = "http://ldap.ak.ru/", prefix = ""),
                @XmlNs(namespaceURI = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
                @XmlNs(namespaceURI = "http://www.w3.org/2001/XMLSchema", prefix = "xs")})
package ru.ak.model;

        import javax.xml.bind.annotation.XmlNs;
        import javax.xml.bind.annotation.XmlNsForm;
        import javax.xml.bind.annotation.XmlSchema;