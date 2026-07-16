package com.evervault.exceptions;

public class InvalidCipherException extends Exception {
    /**
     * @deprecated Leaks BouncyCastle into the SDK's public API. Use
     *             {@link #InvalidCipherException(Throwable)} instead. Retained
     *             for binary compatibility; scheduled for removal in v5.
     */
    @Deprecated
    public InvalidCipherException(org.bouncycastle.crypto.InvalidCipherTextException originalException) {
        super(originalException.getMessage());
    }

    public InvalidCipherException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
