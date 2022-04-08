package com.evervault.services;

import com.evervault.exceptions.Asn1EncodingException;
import com.evervault.models.Secp256r1Constants;

import java.math.BigInteger;
import java.util.Arrays;
import java.lang.Math;

public class ASN1Encoder {
    protected final Secp256r1Constants curveConstants;

    public ASN1Encoder(Secp256r1Constants curveConstants) {
        this.curveConstants = curveConstants;
    };

    public String encode(String... arguments) throws Asn1EncodingException {
        String type = arguments[0];

        String[] hexStrings = Arrays.copyOfRange(arguments, 1, arguments.length);
        String combinedHexStrings = String.join("", hexStrings);

        String str = combinedHexStrings.replaceAll("\\s+", "").toLowerCase();

        Integer len = str.length() / 2;
        Integer lenlen = 0;
        String hex = type;

        // We can't have an odd number of hex chars
        if (len != Math.floor(len)) {
            throw new Asn1EncodingException();
        }

        // The first bye of any ASN.1 sequence is the type (Sequence, Integer, etc)
        // The second byte is either the size of the value, or the size of its size
        // 1. If the second byte is < 0x80 (128) it is considered the size
        // 2. If it is  > 0x80 then it describes the number of bytes of the size
        //    eg: 0x82 means the next to bytes describe the size of the value
        // 3. The special case of exactly 0x80 is "indefinite" length (to end-of-file)
        if (len > 127) {
            lenlen += 1;
            while (len > 255) {
                lenlen += 1;
                len = len >> 8;
            }
        }

        if (lenlen > 0) {
            hex += this.numToHex(0x80 + lenlen);
        }

        return hex + this.numToHex(str.length() / 2) + str; 
    };

    // The Integer type has some special rules
    public String UINT(String... arguments) throws Asn1EncodingException {
        String str = String.join("", arguments);
        BigInteger first = new BigInteger(str.substring(0, 2), 16);

        if (new BigInteger("80", 16).and(first).compareTo(BigInteger.ZERO) > 0) {
            str = "00" + str;
        };

        return this.encode("02", str);
    };

    // Bit String type also has a special rule
    public String BITSTR(String... arguments) throws Asn1EncodingException{
        String str = String.join("", arguments);
        // '00' is a mask of how many bits of the next byte to ignore
        return this.encode("03", "00" + str);
    };

    public String numToHex(int d) {
        String hexString = Integer.toHexString(d);
        if (hexString.length() % 2 != 0) {
            return "0" + hexString;
        }
        return hexString;
    }
}
