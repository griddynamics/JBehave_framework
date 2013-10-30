package com.griddynamics.qa.stubs.tools.logic;

import com.griddynamics.qa.stubs.tools.data.StubServiceData;

/**
 * Created with IntelliJ IDEA.
 * User: ybaturina
 * Date: 10/11/13
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class SoapStub extends SoapStubCommonLogic{

    public SoapStub(){
        this.stubDataPath = COMMON_DATA_PATH;
        this.stubLoadDataUrl = StubServiceData.BASE_URL + StubServiceData.LOAD_DATA_URL;
        this.stubResetUrl = StubServiceData.BASE_URL + StubServiceData.STUB_RESET_URL;
        this.stubName = StubServiceData.STUB_NAME;
    }
}
