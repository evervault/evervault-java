package com.evervault;

import com.evervault.contracts.DataHeader;
import com.evervault.services.R1StdEncryptionOutputFormat;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Stream;

public class WhenUsingR1EncryptionOutputFormatTests {
    private final static R1StdEncryptionOutputFormat std = new R1StdEncryptionOutputFormat();
    private final static String everVaultVersionToUse = new String(Base64.getEncoder().encode("NOC".getBytes(StandardCharsets.UTF_8)), StandardCharsets.US_ASCII);

    @ParameterizedTest
    @MethodSource("formattingParameters")
    void formattingEncryptedDataMustReturnDataInCorrectFormat(String expectedResult, DataHeader header, String iv, String publicKey, String payLoad) {
        assert std.format(header, iv, publicKey, payLoad).equals(expectedResult);
    }

    static Stream<Arguments> formattingParameters() {
        return Stream.of(
                Arguments.of(String.format("ev:%s:IV:PK:PL:$", everVaultVersionToUse), DataHeader.String, "IV", "PK", "PL"),
                Arguments.of(String.format("ev:%s:boolean:IV:PK:PL:$", everVaultVersionToUse), DataHeader.Boolean, "IV", "PK", "PL"),
                Arguments.of(String.format("ev:%s:number:IV:PK:PL:$", everVaultVersionToUse), DataHeader.Number, "IV", "PK", "PL"),
                Arguments.of(String.format("ev:%s:IV:PK:PL:$", everVaultVersionToUse), DataHeader.String, "IV====", "PK==", "PL===="),
                Arguments.of(String.format("ev:%s:boolean:IV:PK:PL:$", everVaultVersionToUse), DataHeader.Boolean, "IV==", "PK=", "PL"),
                Arguments.of(String.format("ev:%s:number:IV:PK:PL:$", everVaultVersionToUse), DataHeader.Number, "IV==", "PK=", "PL=========="));
    }
}
