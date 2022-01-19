package EverVault.Services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.http.HttpTimeoutException;
import java.util.HashMap;

public class CircuitBreaker {
    private final HashMap<String, ResourceControl> control;

    public CircuitBreaker() {
        control = new HashMap<>();
    }

    public <TReturn> TReturn execute(Method method, Object[] parameters) throws InvocationTargetException, IllegalAccessException {
//        try {
//            method.invoke(parameters);
//        } catch (HttpTimeoutException _) {
//            if (!control.containsKey(method.getName())) {
//                control.put(method.getName(), new ResourceControl());
//            }
//
//            var resourceControl = control.get(method.getName());
//
//            resourceControl.timeOutOccurred();
//
//            if (!resourceControl.timeOutOccurred()) {
//
//            }
//        }
        return null;
    }
}
