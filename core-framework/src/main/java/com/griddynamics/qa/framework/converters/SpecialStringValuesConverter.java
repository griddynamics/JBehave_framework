package com.griddynamics.qa.framework.converters;

import org.jbehave.core.steps.ParameterConverters.ParameterConverter;

import java.lang.reflect.Type;

/**
 * Special parameter converter for customized String values in JBehave steps
 *
 * @author ybaturina
 */
public class SpecialStringValuesConverter implements ParameterConverter {

    private enum StringValueType {EMPTY, EMPTY_VALUE, NULL}

    public boolean accept(Type type) {
        return (type instanceof Class<?>) ? String.class.isAssignableFrom((Class<?>) type) : false;
    }

    public Object convertValue(String value, Type type) {
        if (value.toUpperCase().equals(StringValueType.EMPTY.name())
                || value.toUpperCase().equals(StringValueType.EMPTY_VALUE.name().replace("_", " "))) {
            return "";
        } else if (value.toUpperCase().equals(StringValueType.NULL.name())) {
            return null;
        }
        return value;
    }

}
