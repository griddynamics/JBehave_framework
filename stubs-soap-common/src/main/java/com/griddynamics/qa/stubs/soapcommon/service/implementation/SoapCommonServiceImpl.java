package com.griddynamics.qa.stubs.soapcommon.service.implementation;

import com.griddynamics.qa.stubs.soapcommon.service.SoapCommonService;
import com.griddynamics.qa.stubs.soapcommon.service.implementation.data.HomePageData;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.naming.ServiceUnavailableException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements all methods from {@link com.griddynamics.qa.stubs.soapcommon.service.SoapCommonService}.
 *
 * @author ybaturina
 * @author lzakharova
 */
@Component
public class SoapCommonServiceImpl implements SoapCommonService, HomePageData {
    public static Logger logger = Logger.getLogger(
            SoapCommonServiceImpl.class.getName());
    public final static String REQUEST_TAG = "request";
    public final static String RESPONSE_TAG = "response";
    public final static String ENTRY_TAG = "entry";

    private Map<String, Map<RequestData, ResponseData>> responses = new HashMap<String, Map<RequestData, ResponseData>>();

    private List history = new ArrayList();
    /*Map containing possible stub request types and messages, which real services take such types */
    private static Map<String, String> REQ_TYPES_MAP = new HashMap<String, String>();

    private boolean available = true;
    private int responseTime = 0;


    @Override
    public String getHomePage() {
        return HOME_PAGE_CONTENT;
    }

    @Override
    public String getCommonResponse(String request) throws ServiceUnavailableException {
        String response = getResponse(request);
        addHistoryItem(request, response.replaceAll("\t", ""));

        return response;
    }

    @Override
    public void fillFromFile(InputStream inputStream) {
        Map<String, String> pairsMap = getRequestResponsePairs(inputStream);
        for (Map.Entry<String, String> pair : pairsMap.entrySet()) {
            addPair(pair.getKey(), pair.getValue());
        }
    }

    @Override
    public void fillRequestDataFromFile(InputStream inputStream) {
        REQ_TYPES_MAP.putAll(getRequestResponsePairs(inputStream));
    }

    /**
     * Get request-response pairs from the input stream
     *
     * The XML with request-response pairs should have the following format:
     * <CODE>
     * <?xml version="1.0" encoding="UTF-8" standalone="no"?>
     * <stubbingData>
     * <entry>
     * <request><![CDATA[...]]></request>
     * <response><![CDATA[...]]></response>
     * </entry>
     * ...
     * </stubbingData>
     * </CODE>
     * <p/>
     *
     * Content of request node is XML, content of response node
     * is SOAP message.
     *
     * @param inputStream
     * @return
     */
    private Map<String, String> getRequestResponsePairs(InputStream inputStream) {
        Map<String, String> pairsMap = new HashMap<String, String>();
        Element docElement = XmlUtils.getDocumentElement(inputStream);
        NodeList entries = docElement.getElementsByTagName(ENTRY_TAG);
        for (int i = 0; i < entries.getLength(); i++) {
            Element entryElement = (Element) entries.item(i);
            String requestText = XmlUtils.getTextContentFromNode(entryElement, REQUEST_TAG);
            String responseText = XmlUtils.getTextContentFromNode(entryElement, RESPONSE_TAG);
            pairsMap.put(requestText, responseText);
        }
        return pairsMap;
    }

    @Override
    public void addPair(String request, String response) {
        RequestData requestData = new RequestData(request);
        ResponseData responseData = new ResponseData(response);
        if (!responses.containsKey(requestData.getRequestType())) {
            responses.put(requestData.getRequestType(), new HashMap<RequestData, ResponseData>());
        }
        responses.get(requestData.getRequestType()).put(requestData, responseData);
    }

    @Override
    public void clearHistory() {
        history.clear();
    }

    @Override
    public void clearData() {
        responses.clear();
    }

    @Override
    public void clear() {
        responses.clear();
        history.clear();
    }

    @Override
    public List history() {
        return history;
    }

    @Override
    public void setAvailability(boolean available) {
        this.available = available;
    }

    @Override
    public void setTimeout(int delta) {
        this.responseTime = delta;
    }

    /**
     * Method processes request sent to the stub and returns the response
     * in case the request matched with loaded to the stub data
     *
     * @param request
     * @return response content as String
     * @throws ServiceUnavailableException
     */
    private String getResponse(String request) throws ServiceUnavailableException {
        IncomingRequest incRequest = new IncomingRequest(request);

        logRequestType(incRequest.getRequestType(), request);

        /*Delay sending the response in case if responseTime is positive*/
        if (responseTime != 0) {
            try {
                Thread.sleep(responseTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Stub sleep process was interrupted");
            }
        }

        /*Do not return response in case if the stub is unavailable*/
        if (!available) {
            addHistoryItem(request, "Service unavailable");
            throw new ServiceUnavailableException();
        }

        /*Exception invocation in case when requestType is not matched with data loaded into stub*/
        if (!responses.containsKey(incRequest.getRequestType())) {
            addHistoryItem(request, "There is no request-response pairs for this xml type: " +
                    incRequest.getRequestType());
            throw new ServiceUnavailableException("There is no request-response pairs for this request type: " +
                    incRequest.getRequestType());
        }

        String response = "";

        /*Find the response for the matched request*/
        for (Map.Entry<RequestData, ResponseData> entry : responses.get(incRequest.getRequestType()).entrySet()) {
            if (entry.getKey().match(incRequest.getRequestBody())) {
                response = entry.getValue().getResponseData();
                break;
            }
        }

        if (response.isEmpty()) {
            response = "Response data for the incoming request was not loaded to the stub.\n";
        }

        return response;
    }

    /**
     * Method adds request-response pairs to the stub history
     * @param request
     * @param response
     */
    private void addHistoryItem(String request, String response) {
        logger.info("Response: " + response);

        Map<String, String> historyEntity = new HashMap<String, String>();
        historyEntity.put("requestPart", request);
        historyEntity.put("responsePart", response);
        history.add(historyEntity);
    }

    /**
     * Method is used to fill the log with messages which
     * real service takes requests of the specified type
     * @param type - type of the request sent to stub
     * @param request - request content
     */
    private void logRequestType(String type, String request) {
        String logMessage = REQ_TYPES_MAP.get(type);
        if (logMessage != null) {
            logger.info(logMessage + request);
        }
    }
}
