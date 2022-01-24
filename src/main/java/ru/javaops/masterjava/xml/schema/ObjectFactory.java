
package ru.javaops.masterjava.xml.schema;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.javaops.masterjava.xml.schema package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Payload_QNAME = new QName("http://javaops.ru", "Payload");
    private final static QName _City_QNAME = new QName("http://javaops.ru", "City");
    private final static QName _Project_QNAME = new QName("http://javaops.ru", "Project");
    private final static QName _Group_QNAME = new QName("http://javaops.ru", "Group");
    private final static QName _User_QNAME = new QName("http://javaops.ru", "User");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.javaops.masterjava.xml.schema
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PayloadType }
     * 
     */
    public PayloadType createPayloadType() {
        return new PayloadType();
    }

    /**
     * Create an instance of {@link ProjectType }
     * 
     */
    public ProjectType createProjectType() {
        return new ProjectType();
    }

    /**
     * Create an instance of {@link GroupType }
     * 
     */
    public GroupType createGroupType() {
        return new GroupType();
    }

    /**
     * Create an instance of {@link UserType }
     * 
     */
    public UserType createUserType() {
        return new UserType();
    }

    /**
     * Create an instance of {@link CityType }
     * 
     */
    public CityType createCityType() {
        return new CityType();
    }

    /**
     * Create an instance of {@link PayloadType.Cities }
     * 
     */
    public PayloadType.Cities createPayloadTypeCities() {
        return new PayloadType.Cities();
    }

    /**
     * Create an instance of {@link PayloadType.Users }
     * 
     */
    public PayloadType.Users createPayloadTypeUsers() {
        return new PayloadType.Users();
    }

    /**
     * Create an instance of {@link PayloadType.Projects }
     * 
     */
    public PayloadType.Projects createPayloadTypeProjects() {
        return new PayloadType.Projects();
    }

    /**
     * Create an instance of {@link ProjectType.Groups }
     * 
     */
    public ProjectType.Groups createProjectTypeGroups() {
        return new ProjectType.Groups();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PayloadType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://javaops.ru", name = "Payload")
    public JAXBElement<PayloadType> createPayload(PayloadType value) {
        return new JAXBElement<PayloadType>(_Payload_QNAME, PayloadType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CityType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://javaops.ru", name = "City")
    public JAXBElement<CityType> createCity(CityType value) {
        return new JAXBElement<CityType>(_City_QNAME, CityType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProjectType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://javaops.ru", name = "Project")
    public JAXBElement<ProjectType> createProject(ProjectType value) {
        return new JAXBElement<ProjectType>(_Project_QNAME, ProjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GroupType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://javaops.ru", name = "Group")
    public JAXBElement<GroupType> createGroup(GroupType value) {
        return new JAXBElement<GroupType>(_Group_QNAME, GroupType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UserType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://javaops.ru", name = "User")
    public JAXBElement<UserType> createUser(UserType value) {
        return new JAXBElement<UserType>(_User_QNAME, UserType.class, null, value);
    }

}
