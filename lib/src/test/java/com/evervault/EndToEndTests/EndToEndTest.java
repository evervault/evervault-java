package com.evervault.EndToEndTests;

import com.evervault.Evervault;
import com.evervault.exceptions.EvervaultException;

import org.junit.jupiter.api.BeforeAll;

public class EndToEndTest {
    protected static Evervault evervault;

    @BeforeAll
    static void buildClient() throws EvervaultException {
        if (evervault != null) {
            return;
        }

        evervault = new Evervault(System.getenv("TEST_EV_APP_ID"), System.getenv("TEST_EV_API_KEY"));
    }
}
