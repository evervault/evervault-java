package com.evervault.services;

import com.evervault.contracts.IProvideDecryptionAndIgnoreDomains;

public class StaticOutboundRelayConfigService implements IProvideDecryptionAndIgnoreDomains {
    private String[] alwaysIgnoreDomains;
    private String[] decryptionDomains;

    public StaticOutboundRelayConfigService(String[] alwaysIgnoreDomains, String[] decryptionDomains) {
        this.alwaysIgnoreDomains = alwaysIgnoreDomains;
        this.decryptionDomains = decryptionDomains;
    }

    public String[] getAlwaysIgnoreDomains() {
        return alwaysIgnoreDomains;
    }

    public String[] getDecryptionDomains() {
        return decryptionDomains;
    }
}
