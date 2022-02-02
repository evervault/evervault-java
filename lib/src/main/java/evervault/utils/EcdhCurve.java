package evervault.utils;

import java.util.HashMap;
import java.util.Map;

public enum EcdhCurve {
    SECP256K1("secp256k1"),
    SECP256R1("secp256r1");

    private final String curveId;

    EcdhCurve(String curveId) {
        this.curveId = curveId;
    }

    public String getEcdhCurve() {
        return this.curveId;
    }

    static final Map<String, EcdhCurve> curveMap = new HashMap<String, EcdhCurve>();

    static {
        for (EcdhCurve curve: EcdhCurve.values()) {
            curveMap.put(curve.toString(), curve);
        }
    }
}