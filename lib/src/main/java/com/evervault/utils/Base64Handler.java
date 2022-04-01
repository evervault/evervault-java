package com.evervault.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public abstract class Base64Handler {
    private static final Base64.Decoder decoder = Base64.getDecoder();
    private static final Base64.Encoder encoder = Base64.getEncoder();

//    pythons definition seems to deal the string as if it was ASCII
//    decoded_team_cage_key = base64.b64decode(resp["ecdhKey"])
//
//    ---------------------------------------------------------------------
//
//    def b64decode(s, altchars=None, validate=False):
//            """Decode the Base64 encoded bytes-like object or ASCII string s.
//
//    Optional altchars must be a bytes-like object or ASCII string of length 2
//    which specifies the alternative alphabet used instead of the '+' and '/'
//    characters.
    public static byte[] decodeBase64String(String contentToDecode) {
        return decoder.decode(contentToDecode.getBytes(StandardCharsets.UTF_8));
    }

    public static String encodeBase64(byte[] byteArray) {
        return encoder.encodeToString(byteArray);
    }

    /// Should this be here? Python's SDK has it names as __base_64_remove_padding
    public static String removePadding(String content) {
        int i = content.length() - 1;

        for (; i > 0; i--) {
            if (content.charAt(i) != '=') {
                break;
            }
        }

        return content.substring(0, ++i);
    }
}
