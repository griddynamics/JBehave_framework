package com.griddynamics.qa.stubs.soapcommon.service.implementation;

import com.griddynamics.qa.stubs.soapcommon.service.SoapCommonService;
import com.griddynamics.qa.stubs.soapcommon.service.implementation.data.HomePageData;
import com.griddynamics.qa.stubs.soapcommon.service.implementation.data.RequestTypeData;
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

import static org.apache.commons.lang3.StringEscapeUtils.unescapeXml;

/**
 * This class implements all methods from {@link com.griddynamics.qa.stubs.soapcommon.service.SoapCommonService}.
 *
 * @author ybaturina
 */
@Component
public class SoapCommonServiceImpl implements SoapCommonService, HomePageData, RequestTypeData {
    static Logger logger = Logger.getLogger(
            SoapCommonServiceImpl.class.getName());
    public final static String REQUEST_TAG = "request";
    public final static String RESPONSE_TAG = "response";
    public final static String ENTRY_TAG = "entry";

    private Map<String, Map<RequestData, ResponseData>> responses = new HashMap<String, Map<RequestData, ResponseData>>();

    private List history = new ArrayList();

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
        Element docElement = XmlUtils.getDocumentElement(inputStream);
        NodeList entries = docElement.getElementsByTagName(ENTRY_TAG);
        for (int i = 0; i < entries.getLength(); i++) {
            Element entryElement = (Element) entries.item(i);
            String requestText = getTextContentFromNode(entryElement, REQUEST_TAG);
            String responseText = getTextContentFromNode(entryElement, RESPONSE_TAG);
            addPair(requestText, responseText);
        }
    }

    private String getTextContentFromNode(Element rootElement, String tagName){
        NodeList requests = rootElement.getElementsByTagName(tagName);
        if (requests.getLength() > 1) {
            StringBuilder errorMessage = new StringBuilder("[ERROR] There is more than one "+tagName+" in the entry: ")
                    .append(rootElement.getTextContent());
            logger.error(errorMessage.toString());
            throw new RuntimeException(errorMessage.toString());
        } else {
            return unescapeXml(requests.item(0).getTextContent());
        }
    }

    @Override
    public void addPair(String request, String response) {
        RequestData requestData = new RequestData(request);
        ResponseData responseData = new ResponseData(response);
        if(!responses.containsKey(requestData.getRequestType())){
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

    private String getResponse(String request) throws ServiceUnavailableException {
        IncomingRequest incRequest = new IncomingRequest(request);

        logRequestType(incRequest.getRequestType(), request);

        if (responseTime != 0) {
            try {
                Thread.sleep(responseTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Stub sleep process was interrupted");
            }
        }

        if (!available) {
            addHistoryItem(request, "Service unavailable");
            throw new ServiceUnavailableException();
        }

        if (!responses.containsKey(incRequest.getRequestType())) {
            addHistoryItem(request, "There is no request-response pairs for this xml type: " +
                    incRequest.getRequestType());
            throw new ServiceUnavailableException("There is no request-response pairs for this request type: " +
                    incRequest.getRequestType());
        }

        String response = "";

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

    private void addHistoryItem(String request, String response) {
        logger.info("Response: " + response);

        Map<String, String> historyEntity = new HashMap<String, String>();
        historyEntity.put("requestPart", request);
        historyEntity.put("responsePart", response);
        history.add(historyEntity);
    }

    private void logRequestType(String type, String request) {
        String logMessage = REQ_TYPES_MAP.get(type);
        if (logMessage != null) {
            logger.info(logMessage + request);
        }
    }
}
