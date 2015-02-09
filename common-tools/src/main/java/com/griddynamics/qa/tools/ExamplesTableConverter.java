/*
 * Copyright 2015, Grid Dynamics International and/or its affiliates. All rights reserved.
 * Grid Dynamics International PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.griddynamics.qa.tools;

import org.jbehave.core.model.ExamplesTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class containing method for convertation from JBehave Examples table format
 * to the Map object
 *
 * @author ybaturina
 * @author mlykosova
 */
public class ExamplesTableConverter {
    /**
     * @param table - examples table passes from Story file consists of 2 columns [name and value]
     * @return map of parameters where keys is taken from first column, value - from second column
     */
    public static Map<String, String> getParametersFromTable(ExamplesTable table) {
        Map<String, String> parameters = new HashMap<String, String>();
        List<Map<String, String>> rows = table.getRows();

        for (Map<String, String> row : rows) {
            parameters.put(row.get("name"), row.get("value"));
        }

        return parameters;
    }
}
