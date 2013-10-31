package com.griddynamics.qa.stubs.soapcommon.service.implementation.data;

import java.util.HashMap;
import java.util.Map;

/**
 * This interface should contain map with predefined request types and messages indicating
 * which real service takes requests of such type
 *
 * @author ybaturina
 */
public interface RequestTypeData {

    public final static Map<String, String> REQ_TYPES_MAP = new HashMap<String, String>();
}
