package evervault.Services;

import evervault.Contracts.IProvideTime;

import java.time.Instant;

public class TimeService implements IProvideTime {
    @Override
    public Instant GetNow() {
        return Instant.now();
    }
}
