package com.evervault.contracts;

import com.evervault.exceptions.HttpFailureException;

import java.io.IOException;

public interface IProvideRunToken {
    String createRunToken(String url, String cageName, Object data) throws HttpFailureException, IOException, InterruptedException;
}
