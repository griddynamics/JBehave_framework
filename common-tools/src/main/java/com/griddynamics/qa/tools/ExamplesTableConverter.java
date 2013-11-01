package com.griddynamics.qa.tools;

import org.jbehave.core.model.ExamplesTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
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

        int i = 0;
        for (Map<String, String> row : rows) {

            String key = row.get("name");
            String value = row.get("value");

            parameters.put(key, value);
            i++;
        }

        return parameters;
    }
}
