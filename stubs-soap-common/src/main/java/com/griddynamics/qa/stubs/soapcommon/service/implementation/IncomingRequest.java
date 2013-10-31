package com.griddynamics.qa.stubs.soapcommon.service.implementation;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class contains methods for processing requests sent to the stub
 *
 * @author ybaturina
 * @author lzakharova
 */

public class IncomingRequest {

    static Logger logger = Logger.getLogger(
            IncomingRequest.class.getName());
    private Element requestBody;
    private String requestType;

    public IncomingRequest(String request) {
        setRequestBodyAndType(request);
    }

    public String getRequestType() {
        return requestType;
    }

    public Element getRequestBody() {
        return requestBody;
    }

    /**
     * Method determines request type and body according to it's String SOAP content
     * @param request - request as String
     */
    private void setRequestBodyAndType(String request) {
        InputStream is = new ByteArrayInputStream(request.getBytes());
        try {
            SOAPMessage sm = MessageFactory.newInstance(SOAPConstants.DEFAULT_SOAP_PROTOCOL).createMessage(new MimeHeaders(), is);
            SOAPBody body = sm.getSOAPBody();
            NodeList nodes = body.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (!nodes.item(i).getNodeName().equals(XmlUtils.TEXT_NODE)) {
                    requestType = node.getLocalName();
                    requestBody = (Element) node;
                    return;
                }
            }
        } catch (IOException e) {
            logger.fatal("Error during input request reading: " + e.getMessage());
        } catch (SOAPException e) {
            logger.fatal("Error during input request parsing: " + e.getMessage());
        }
    }
}
