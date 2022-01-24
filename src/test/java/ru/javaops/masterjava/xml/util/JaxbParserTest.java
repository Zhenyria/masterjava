package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import org.junit.Test;
import ru.javaops.masterjava.xml.schema.CityType;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.PayloadType;

public class JaxbParserTest {
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();
    private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);

    static {
        JAXB_PARSER.setSchema(Schemas.ofClasspath("payload.xsd"));
    }

    @Test
    public void testPayload() throws Exception {
//        JaxbParserTest.class.getResourceAsStream("/city.xml")
        PayloadType payload = JAXB_PARSER.unmarshal(Resources.getResource("payload.xml").openStream());
        String strPayload = JAXB_PARSER.marshal(OBJECT_FACTORY.createPayload(payload));
        JAXB_PARSER.validate(strPayload);
        System.out.println(strPayload);
    }

    @Test
    public void testCity() throws Exception {
        CityType cityElement = JAXB_PARSER.unmarshal(Resources.getResource("city.xml").openStream());
        String strCity = JAXB_PARSER.marshal(OBJECT_FACTORY.createCity(cityElement));
        JAXB_PARSER.validate(strCity);
        System.out.println(strCity);
    }
}