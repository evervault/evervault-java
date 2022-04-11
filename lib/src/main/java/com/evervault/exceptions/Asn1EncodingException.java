package com.evervault.exceptions;

public class Asn1EncodingException extends Exception{
    private static final String ERROR_MESSAGE = "ASN1 encoding error";

    public Asn1EncodingException() {
        super(ERROR_MESSAGE);
    }
}
