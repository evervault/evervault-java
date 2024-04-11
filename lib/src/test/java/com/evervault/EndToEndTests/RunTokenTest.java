package com.evervault.EndToEndTests;

import com.evervault.exceptions.EvervaultException;
import com.evervault.models.RunTokenResult;
import org.junit.jupiter.api.Test;

public class RunTokenTest extends EndToEndTest {

    private static final String FUNCTION_NAME = "node-function-synthetic";

    @Test
    void itCreatesARunTokenWithoutPayload() throws EvervaultException {
        RunTokenResult runToken = evervault.createRunToken(FUNCTION_NAME);
        assert(runToken.token.startsWith("ey"));
    }

    @Test
    void itCreatesARunTokenWithPayload() throws EvervaultException {
        Payload payload = new Payload("hello world");
        RunTokenResult runToken = evervault.createRunToken(FUNCTION_NAME, payload);
        assert(runToken.token.startsWith("ey"));
    }

    class Payload {
        private String message;

        public Payload(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
