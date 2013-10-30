package com.griddynamics.qa.stubs.soapcommon.service.implementation;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: ybaturina
 * Date: 10/21/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlUtils {

    public final static String TEXT_NODE = "#text";

    static Logger logger = Logger.getLogger(
            XmlUtils.class.getName());

    public static Element getDocumentElement(Object input) {
        Document doc;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            if (input instanceof InputStream) {
                doc = dBuilder.parse((InputStream) input);
            } else if (input instanceof String) {
                doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(((String) input).getBytes("utf-8"))));
            } else {
                throw new RuntimeException("[ERROR] Input should be the object either of InputStream or String classes");
            }
            doc.getDocumentElement().normalize();
            return doc.getDocumentElement();
        } catch (ParserConfigurationException e) {
            StringBuilder errorMessage = new StringBuilder("[ERROR] Parsing exception occurred: ").append(e.getMessage());
            logger.error(e.toString());
            throw new RuntimeException(errorMessage.toString());
        } catch (SAXException e) {
            StringBuilder errorMessage = new StringBuilder("[ERROR] SAX exception occurred: ").append(e.getMessage());
            logger.error(e.toString());
            throw new RuntimeException(errorMessage.toString());
        } catch (IOException e) {
            StringBuilder errorMessage = new StringBuilder("[ERROR] IO Exception occurred: ").append(e.getMessage());
            logger.error(e.toString());
            throw new RuntimeException(errorMessage.toString());
        }
    }

    public static boolean hasNotTextChildren(Node node){
        NodeList nodeList = node.getChildNodes();
        for (int i=0; i<nodeList.getLength(); i++){
            if (!nodeList.item(i).getNodeName().equals(TEXT_NODE)){
                return true;
            }
        }
        return false;
    }

}
