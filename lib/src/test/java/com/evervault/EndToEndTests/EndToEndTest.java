package com.evervault.EndToEndTests;

import com.evervault.Evervault;
import com.evervault.exceptions.EvervaultException;

public class EndToEndTest {
    protected static Evervault evervault;

    static {
        try {
            evervault = new Evervault(System.getenv("TEST_EV_APP_ID"), System.getenv("TEST_EV_API_KEY"));
        } catch (EvervaultException e) {
            throw new RuntimeException(e);
        }
    }

}
