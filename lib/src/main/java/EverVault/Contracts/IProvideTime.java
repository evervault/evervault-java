package EverVault.Contracts;

import java.time.Instant;

public interface IProvideTime {
    Instant GetNow();
}
