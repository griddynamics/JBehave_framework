package com.griddynamics.qa.stubs.soapcommon.service.implementation.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ybaturina
 * Date: 10/17/13
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RequestTypeData {

    public final static String PAYMENT_SERVICE_REQUEST_TYPE = "loyaltyCouponInquireInput";
    public final static String PAYMENT_SERVICE_LOG_MESSAGE = "Request to Payment Service endpoint: ";
    public final static String CUSTOMER_SERVICE_REQUEST_TYPE = "validateCoupon";
    public final static String CUSTOMER_SERVICE_LOG_MESSAGE = "Request to Customer Service endpoint: ";

    public final static Map<String, String> REQ_TYPES_MAP = new HashMap<String, String>(){{
        put(PAYMENT_SERVICE_REQUEST_TYPE, PAYMENT_SERVICE_LOG_MESSAGE);
        put(CUSTOMER_SERVICE_REQUEST_TYPE, CUSTOMER_SERVICE_LOG_MESSAGE);
    }};
}
