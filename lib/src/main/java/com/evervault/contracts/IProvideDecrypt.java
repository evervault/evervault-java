package com.evervault.contracts;

import com.evervault.exceptions.HttpFailureException;

import java.io.IOException;

public interface IProvideDecrypt {
    <T> T decrypt(String url, Object data, Class<T> valueType) throws HttpFailureException, IOException, InterruptedException;
}
