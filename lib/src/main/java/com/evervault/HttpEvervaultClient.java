package com.evervault;

import java.util.List;
import java.util.Map;

public class HttpEvervaultClient implements EvervaultClient {

    private EvervaultCredentials credentials;

    public HttpEvervaultClient(EvervaultCredentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public String encrypt(boolean bool) {
        return null;
    }

    @Override
    public String encrypt(int integer) {
        return null;
    }

    @Override
    public String encrypt(double decimal) {
        return null;
    }

    @Override
    public String encrypt(String string) {
        return null;
    }

    @Override
    public boolean decrypt(boolean bool) {
        return false;
    }

    @Override
    public int decrypt(int decimal) {
        return 0;
    }

    @Override
    public double decrypt(double decimal) {
        return 0;
    }

    @Override
    public String decrypt(String string) {
        return null;
    }

    @Override
    public List<Object> decrypt(List<String> list) {
        return null;
    }

    @Override
    public Map<String, Object> decrypt(Map<String, String> map) {
        return null;
    }

    @Override
    public <T> T run(String functionName, Object payload, Class<T> resultType) {
        return null;
    }

    @Override
    public void runAsync(String functionName, Object payload) {

    }

    @Override
    public String createClientSideToken(ClientSideTokenAction action, Object payload) {

        return null;
    }

    @Override
    public String createClientSideToken(ClientSideTokenAction action, Object payload, RunClientSideTokenOptions options) {
        return null;
    }

    @Override
    public String createClientSideToken(ClientSideTokenAction action) {
        return null;
    }

    @Override
    public String createClientSideToken(ClientSideTokenAction action, RunClientSideTokenOptions options) {
        return null;
    }
}
