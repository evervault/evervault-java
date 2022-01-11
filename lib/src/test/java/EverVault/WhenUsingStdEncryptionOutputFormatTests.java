package EverVault;

import EverVault.Contracts.DataHeader;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class WhenUsingStdEncryptionOutputFormatTests {
    private final StdEncryptionOutputFormat std;

    public WhenUsingStdEncryptionOutputFormatTests() {
        std = new StdEncryptionOutputFormat();
    }

    @ParameterizedTest
    @MethodSource("formattingParameters")
    void formattingEncryptedDataMustReturnDataInCorrectFormat(String expectedResult, DataHeader header, String iv, String publicKey, String payLoad) {
        assert std.format("DUB", header, iv, publicKey, payLoad).equals(expectedResult);
    }

    static Stream<Arguments> formattingParameters() {
        return Stream.of(
                Arguments.of("ev:DUB:IV:PK:PL:$", DataHeader.String, "IV", "PK", "PL"),
                Arguments.of("ev:DUB:boolean:IV:PK:PL:$", DataHeader.Boolean, "IV", "PK", "PL"),
                Arguments.of("ev:DUB:number:IV:PK:PL:$", DataHeader.Number, "IV", "PK", "PL"),
                Arguments.of("ev:DUB:IV:PK:PL:$", DataHeader.String, "IV====", "PK==", "PL===="),
                Arguments.of("ev:DUB:boolean:IV:PK:PL:$", DataHeader.Boolean, "IV==", "PK=", "PL"),
                Arguments.of("ev:DUB:number:IV:PK:PL:$", DataHeader.Number, "IV==", "PK=", "PL=========="));
    }
}
