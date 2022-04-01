package com.evervault.services;

import com.evervault.contracts.IProvideTime;

import java.time.Instant;

public class TimeService implements IProvideTime {
    @Override
    public Instant GetNow() {
        return Instant.now();
    }
}
