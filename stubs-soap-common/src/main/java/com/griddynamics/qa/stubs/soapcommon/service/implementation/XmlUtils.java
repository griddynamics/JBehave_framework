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

import static org.apache.commons.lang3.StringEscapeUtils.unescapeXml;

/**
 * Class with utils for working with XML documents
 *
 * @author ybaturina
 */
public class XmlUtils {

    public final static String TEXT_NODE = "#text";

    static Logger logger = Logger.getLogger(XmlUtils.class.getName());

    /**
     * Get root element of the XML document
     * @param input - InputStream or String object containing XML doc
     * @return  root element
     */
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

    /**
     * Method checks whether XML Node has other child nodes besides text nodes
     * @param node
     * @return
     */
    public static boolean hasNotTextChildren(Node node){
        NodeList nodeList = node.getChildNodes();
        for (int i=0; i<nodeList.getLength(); i++){
            if (!nodeList.item(i).getNodeName().equals(TEXT_NODE)){
                return true;
            }
        }
        return false;
    }

    /**
     * Find content of the child node by it's tag in the XML element
     *
     * @param rootElement - root XML Element
     * @param tagName     - tag of the node
     * @return node content as String
     */
    static String getTextContentFromNode(Element rootElement, String tagName) {
        NodeList requests = rootElement.getElementsByTagName(tagName);
        if (requests.getLength() > 1) {
            StringBuilder errorMessage = new StringBuilder("[ERROR] There is more than one " + tagName + " in the entry: ")
                    .append(rootElement.getTextContent());
            logger.error(errorMessage.toString());
            throw new RuntimeException(errorMessage.toString());
        } else {
            return unescapeXml(requests.item(0).getTextContent());
        }
    }
}
