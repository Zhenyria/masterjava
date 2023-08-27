package ru.javaops.masterjava.service.mail;

import ru.javaops.web.WebStateException;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.MTOM;
import java.util.Set;

@MTOM
@WebService(targetNamespace = "http://mail.javaops.ru/")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT,
             use = SOAPBinding.Use.LITERAL,
             parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public interface MailService {

    @WebMethod
    String sendToGroup(
            @WebParam(name = "to") Set<Addressee> to,
            @WebParam(name = "cc") Set<Addressee> cc,
            @WebParam(name = "subject") String subject,
            @WebParam(name = "body") String body,
            @WebParam(name = "attachment") @XmlMimeType("application/octet-stream") DataHandler attachment
    ) throws WebStateException;

    @WebMethod
    GroupResult sendBulk(
            @WebParam(name = "to") Set<Addressee> to,
            @WebParam(name = "subject") String subject,
            @WebParam(name = "body") String body,
            @WebParam(name = "attachment") @XmlMimeType("application/octet-stream") DataHandler attachment
    ) throws WebStateException;

}