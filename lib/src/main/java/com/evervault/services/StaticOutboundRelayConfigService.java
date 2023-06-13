package com.evervault.services;

import com.evervault.contracts.IProvideDecryptionAndIgnoreDomains;
import com.evervault.utils.DomainRegexHandler;

import java.util.Arrays;
import java.util.regex.Pattern;

public class StaticOutboundRelayConfigService implements IProvideDecryptionAndIgnoreDomains {
    private String[] alwaysIgnoreDomains;
    private Pattern[] alwaysIgnoreDomainRegexes;
    private String[] decryptionDomains;
    private Pattern[] decryptionDomainRegexes;

    public StaticOutboundRelayConfigService(String[] alwaysIgnoreDomains, String[] decryptionDomains) {
        this.alwaysIgnoreDomains = alwaysIgnoreDomains;
        this.alwaysIgnoreDomainRegexes = DomainRegexHandler
            .buildDomainRegexesFromPatterns(alwaysIgnoreDomains);
        this.decryptionDomains = decryptionDomains;
        this.decryptionDomainRegexes = DomainRegexHandler
            .buildDomainRegexesFromPatterns(decryptionDomains);
    }

    public String[] getAlwaysIgnoreDomains() {
        return alwaysIgnoreDomains;
    }

    public Pattern[] getAlwaysIgnoreDomainRegexes() {
        return alwaysIgnoreDomainRegexes;
    }

    public String[] getDecryptionDomains() {
        return decryptionDomains;
    }

    public Pattern[] getDecryptionDomainRegexes() {
        return decryptionDomainRegexes;
    }
}
