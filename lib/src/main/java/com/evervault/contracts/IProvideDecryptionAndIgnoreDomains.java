package com.evervault.contracts;

public interface IProvideDecryptionAndIgnoreDomains {
    public String[] getAlwaysIgnoreDomains();
    public String[] getDecryptionDomains();
}
