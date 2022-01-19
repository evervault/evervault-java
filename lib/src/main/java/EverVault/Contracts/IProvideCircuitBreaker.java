package EverVault.Contracts;

import java.lang.reflect.InvocationTargetException;

public interface IProvideCircuitBreaker {
    <TReturn> TReturn execute(IProvideCagePublicKeyFromHttpApi executable) throws InvocationTargetException, IllegalAccessException;
}
