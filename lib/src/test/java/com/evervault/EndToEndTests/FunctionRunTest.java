package com.evervault.EndToEndTests;

import com.evervault.exceptions.EvervaultException;
import com.evervault.exceptions.FunctionRunException;
import com.evervault.models.FunctionRun;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class FunctionRunTest extends EndToEndTest {

    private final static String TEST_FUNCTION_NAME = "node-function-synthetic";
    private final static String TEST_FUNCTION_NAME_WITH_INITIATLISATION_ERROR = "node-function-init-error-synthetic";

    @Test
    void itRunsAFunction() throws EvervaultException {
        FunctionRequestPayload requestPayload =
                new FunctionRequestPayload("apple", 12345, 123.45, true, false);

        FunctionResponsePayload res =
                evervault.run(TEST_FUNCTION_NAME, requestPayload, FunctionResponsePayload.class);
        assertEquals(res.string, "string");
        assertEquals(res.integerNumber, "number");
        assertEquals(res.doubleNumber, "number");
        assertEquals(res.falseBoolean, "boolean");
        assertEquals(res.trueBoolean, "boolean");
    }

    @Test
    void itThrowsAnExceptionIfAnErrorOccurs() throws EvervaultException {
        FunctionRequestPayload requestPayload =
                new FunctionRequestPayload("apple", 12345, 123.45, true, false);

        try {
            FunctionResponsePayload res =
                    evervault.run(TEST_FUNCTION_NAME_WITH_INITIATLISATION_ERROR, requestPayload, FunctionResponsePayload.class);
            fail();
        } catch (EvervaultException e) {
            assert(e.getMessage().startsWith("The function failed to initialize"));
        }
    }

    @Test
    void itRunsAFunctionAsynchronously() throws EvervaultException {
        FunctionRequestPayload requestPayload =
                new FunctionRequestPayload("apple", 12345, 123.45, true, false);

        String runId = evervault.runAsynchronously(TEST_FUNCTION_NAME, requestPayload);
        assert(runId.startsWith("func_run_"));
    }

    public class FunctionRequestPayload {
        public Object string;
        public Object integerNumber;
        public Object doubleNumber;
        public Object trueBoolean;
        public Object falseBoolean;

        public FunctionRequestPayload(String string, int integerNumber, double doubleNumber, boolean trueBoolean, boolean falseBoolean) throws EvervaultException {
            this.string = evervault.encrypt(string);
            this.integerNumber = evervault.encrypt(integerNumber);
            this.doubleNumber = evervault.encrypt(doubleNumber);
            this.trueBoolean = evervault.encrypt(trueBoolean);
            this.falseBoolean = evervault.encrypt(falseBoolean);
        }
    }

    public class FunctionResponsePayload {
        public String string;
        public String integerNumber;
        public String doubleNumber;
        public String trueBoolean;
        public String falseBoolean;
    }
}
