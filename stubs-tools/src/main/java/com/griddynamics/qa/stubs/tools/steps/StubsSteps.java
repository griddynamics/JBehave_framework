package com.griddynamics.qa.stubs.tools.steps;


import com.griddynamics.qa.stubs.tools.logic.SoapStub;
import org.jbehave.core.annotations.Given;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Class with JBehave steps for working with common SOAP stub.
 *
 * @author ybaturina
 */

@Component
@Scope("thread")
public class StubsSteps {

    private static SoapStub stub = new SoapStub();

    @Given("stub is cleared")
    public static void clearStub(){
        stub.invokeCleanStubMethod();
    }

    @Given("test data $files is loaded to the stub")
    public static void invokeLoadDataToStubMethod(String files) {
        stub.invokeLoadDataToStubMethod(files);
    }
}


