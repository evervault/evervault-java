package evervault.services;

import evervault.contracts.IProvideTime;

import java.time.Instant;

public class TimeService implements IProvideTime {
    @Override
    public Instant GetNow() {
        return Instant.now();
    }
}
