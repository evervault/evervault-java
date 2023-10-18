package com.evervault;

public class EvervaultClientTest {

    public void test() {
        EvervaultCredentials credentials = new EvervaultCredentials("app_123456789098", "ev:key:...");
        EvervaultClient client = new HttpEvervaultClient(credentials);
        String str = "this is a test string!";
        String encrypted = client.encrypt(str);
        String decrypted = client.decrypt(str);

        TestFunctionPayload payload = new TestFunctionPayload(encrypted);
        TestFunctionResult result = client.run("functionName", payload, TestFunctionResult.class);
        client.runAsync("functionName", payload);

        String decryptToken = client.createClientSideToken(, decrypted)
    }

    public class TestFunctionPayload {
        private String name;

        public TestFunctionPayload(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class TestFunctionResult {
        private String message;

        public TestFunctionResult(String message) {
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
