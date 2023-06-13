package com.evervault.contracts;

import java.util.regex.Pattern;

public interface IProvideDecryptionAndIgnoreDomains {
    public String[] getAlwaysIgnoreDomains();
    public Pattern[] getAlwaysIgnoreDomainRegexes();
    public String[] getDecryptionDomains();
    public Pattern[] getDecryptionDomainRegexes();
}
