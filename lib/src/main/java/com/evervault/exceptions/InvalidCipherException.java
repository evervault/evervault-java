package com.evervault.exceptions;

public class InvalidCipherException extends Exception {
    public InvalidCipherException(org.bouncycastle.crypto.InvalidCipherTextException originalException) {
        super(originalException.getMessage());
    }
}
