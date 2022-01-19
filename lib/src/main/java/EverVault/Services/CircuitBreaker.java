package EverVault.Services;

import EverVault.Contracts.IProvideCagePublicKeyFromHttpApi;
import EverVault.Contracts.IProvideCircuitBreaker;
import EverVault.Contracts.IProvideECPublicKey;

import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.http.HttpTimeoutException;
import java.util.HashMap;

public class CircuitBreaker implements IProvideCircuitBreaker {
    private final HashMap<String, ResourceControl> control;

    public CircuitBreaker() {
        control = new HashMap<>();
    }

    public <TReturn> TReturn execute(IProvideCagePublicKeyFromHttpApi cagePublicKeyFromHttpProvider) throws InvocationTargetException, IllegalAccessException {
//        try {
//            method.invoke(parameters);
//        } catch (HttpTimeoutException _) {
//            if (!control.containsKey(method.getName())) {
//                control.put(method.getName(), new ResourceControl());
//            }
//
//            var resourceControl = control.get(method.getName());
//            resourceControl.timeOutOccurred();
//
//        }

        return null;
    }
}
