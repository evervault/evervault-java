package com.evervault;

import java.net.Proxy;
import java.util.List;
import java.util.Map;

public interface EvervaultClient {
    String encrypt(boolean bool);
    String encrypt(int integer);
    String encrypt(double decimal);
    String encrypt(String str);
    boolean decryptBoolean(String encrypted);
    int decryptInt(String encrypted);
    double decryptDouble(String encrypted);
    String decryptString(String encrypted);
    List<Object> decryptList(List<String> encrypted);
    Map<String, Object> decryptMap(Map<String, String> encrypted);
    <T> T run(String functionName, Object payload, Class<T> resultType);
    String createClientSideToken(DecryptClientSideTokenOptions options);
    String createClientSideToken(RunClientSideTokenOptions options);
    Proxy getRelayProxy();
}
