package com.evervault.services;

import com.evervault.exceptions.Asn1EncodingException;
import com.evervault.models.Secp256r1Constants;
import com.evervault.utils.HexHandler;

public class DEREncoder {
    protected final Secp256r1Constants curveValues;

    public DEREncoder(Secp256r1Constants constants) {
        this.curveValues = constants;
    }

    public byte[] publicKeyToDer(byte[] decompressedPublicKey) throws Asn1EncodingException {
        ASN1Encoder ASN1 = new ASN1Encoder(this.curveValues);
        String encoded = ASN1.encode(
            "30",
            ASN1.encode(
                "30",
                // 1.2.840.10045.2.1 ecPublicKey
                // (ANSI X9.62 public key type)
                ASN1.encode("06", "2A 86 48 CE 3D 02 01"),
                ASN1.encode(
                    "30",
                    // ECParameters Version
                    ASN1.UINT("01"),
                    ASN1.encode(
                        "30",
                        // X9.62 Prime Field
                        ASN1.encode("06", "2A 86 48 CE 3D 01 01"),
                        ASN1.UINT(this.curveValues.p)
                    ),
                    ASN1.encode(
                        "30",
                        ASN1.encode("04", this.curveValues.a),
                        ASN1.encode("04", this.curveValues.b),
                        ASN1.BITSTR(this.curveValues.seed)
                    ),
                    // curve generate point in decompressed form
                    ASN1.encode("04", this.curveValues.generator),
                    ASN1.UINT(this.curveValues.n),
                    ASN1.UINT(this.curveValues.h)
                )
            ),
            ASN1.BITSTR(HexHandler.encode(decompressedPublicKey))
        );
        return HexHandler.decode(encoded);
    }
}
