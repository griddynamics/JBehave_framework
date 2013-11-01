package com.griddynamics.qa.stubs.soapcommon.service.implementation;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The handler providing data set request.
 *
 * @author ybaturina
 * @author lzakharova
 */
public class RequestData {

    public static final String COUNT_ATTRIBUTE = "count";

    private String type;
    private NodeList requestData;

    /**
     * Creates instance of {@link RequestData}.
     *
     * @param requestXml The XML contained as String.
     */
    public RequestData(String requestXml) {
        Element root = XmlUtils.getDocumentElement(requestXml);

        type = root.getTagName();

        requestData = root.getChildNodes();
    }

    /**
     * Returns true if element contains required node value from the request.
     *
     * @param element - root element of incoming Request XML
     */
    public boolean match(Element element) {
        return matchXml(requestData, element);
    }

    /**
     * Method compares attributes, text content and child nodes of expected and actual DOM structure
     * @param expectedNodes
     * @param actualElement
     * @return flag whether the nodes were matched
     */
    private boolean matchXml(NodeList expectedNodes, Element actualElement) {
        boolean matched;

        //cycle over expected nodes
        for (int i = 0; i < expectedNodes.getLength(); i++) {
            matched = true;
            Node expectedNode = expectedNodes.item(i);
            //we skip text nodes
            if (!expectedNode.getNodeName().equals(XmlUtils.TEXT_NODE)) {
                //find all nodes in actual DOM structure with the same name as in expected XML
                NodeList actualNodes = actualElement.getElementsByTagName(expectedNode.getNodeName());

                //compare number of found nodes with expected ones
                if (!matchCount(expectedNode, actualNodes)) {
                    return false;
                }

                //cycle over nodes from actual DOM structure with equal names
                for (int j = 0; j < actualNodes.getLength(); j++) {
                    Node actualNode = actualNodes.item(j);

                    //compare attributes of actual and expected node
                    if (!matchAttribute(expectedNode, actualNode)) {
                        //if attributes didn't match, then it means that we should stop processing
                        //current actual node and go to the next one
                        matched = false;
                        continue;
                    } else {
                        matched = true;
                        //check if the actual node has no complicated children and contains the text only
                        if (!XmlUtils.hasNotTextChildren(expectedNode)) {
                            //compare the text content of actual and expected node
                            if (!expectedNode.getTextContent().equals(actualNode.getTextContent())) {
                                //if the text content didn't match, then it means that we should stop processing
                                //current actual node and go to the next one
                                matched = false;
                                continue;
                            }
                        } else {
                            //recursive invokation of matchXml method for every child node of the expected one
                            matched = matchXml(expectedNode.getChildNodes(), ((Element) actualNode));
                            if (!matched) {
                                //if the children didn't match, we should continue cycling over actual nodes to find the
                                //actual node containing the same children as the expected one
                                continue;
                            } else {
                                return matched;
                            }
                        }
                    }
                }
            }
            //in case if all actual nodes were processed, but no matched actual node was found, we should return "false"
            if (!matched) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method verifies if an actual node has attributes as an expected node
     * @param expectedNode
     * @param actualNode
     * @return
     */
    private boolean matchAttribute(Node expectedNode, Node actualNode) {
        boolean isAttributeFound = expectedNode.getAttributes().getLength() > 0 ? false : true;
        //cycle over the attributes of the expected node
        for (int k = 0; k < expectedNode.getAttributes().getLength(); k++) {
            Node expectedAttribute = expectedNode.getAttributes().item(k);
            String attrName = expectedAttribute.getNodeName();
            String attrVal = expectedAttribute.getNodeValue();
            //we skip "count" attribute as it's purpose is to store the number of nodes with equal tag names
            //which should be present in the actual document
            if (!attrName.equals(COUNT_ATTRIBUTE)) {
                if (((Element) actualNode).hasAttribute(attrName) && ((Element) actualNode).getAttribute(attrName).equals(attrVal)) {
                    isAttributeFound = true;
                } else {
                    isAttributeFound = false;
                    break;
                }
            } else {
                isAttributeFound = true;
            }
        }
        return isAttributeFound;
    }

    /**
     * Method compares count of nodes with equal names in expected and actual DOM structure
     * Expected Request data in stub has specific {@link #COUNT_ATTRIBUTE} node attribute, which indicates
     * the number of the nodes with the same tag which should present in actual request XML.
     * @param expectedNode
     * @param actualNodes
     * @return
     */
    private boolean matchCount(Node expectedNode, NodeList actualNodes) {
        int expCount, actCount;
        //"count" attribute stores the number of nodes with the same name which should present in actual DOM structure
        if (((Element) expectedNode).hasAttribute(COUNT_ATTRIBUTE)) {
            expCount = Integer.valueOf(((Element) expectedNode).getAttribute(COUNT_ATTRIBUTE));
        } else {
            expCount = 1;
        }
        actCount = actualNodes.getLength();
        if (actCount != expCount) {
            return false;
        }
        return true;
    }

    public String getRequestType() {
        return type;
    }

}
