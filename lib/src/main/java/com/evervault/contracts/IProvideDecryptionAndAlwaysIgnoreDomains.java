package com.evervault.contracts;

public interface IProvideDecryptionAndAlwaysIgnoreDomains {
    public String[] getAlwaysIgnoreDomains();
    public String[] getDecryptionDomains();
}
