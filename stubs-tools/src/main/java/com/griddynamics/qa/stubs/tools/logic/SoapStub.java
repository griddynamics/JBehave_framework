/*
 * Copyright 2015, Grid Dynamics International and/or its affiliates. All rights reserved.
 * Grid Dynamics International PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.griddynamics.qa.stubs.tools.logic;

import com.griddynamics.qa.stubs.tools.data.StubServiceData;

/**
 * Class for stub parameters configuration
 *
 * @author ybaturina
 */
public class SoapStub extends SoapStubCommonLogic{

    public SoapStub(){
        this.stubDataPath = COMMON_DATA_PATH;
        this.stubLoadDataUrl = StubServiceData.BASE_URL + StubServiceData.LOAD_DATA_URL;
        this.stubResetUrl = StubServiceData.BASE_URL + StubServiceData.STUB_RESET_URL;
        this.stubName = StubServiceData.STUB_NAME;
    }
}
