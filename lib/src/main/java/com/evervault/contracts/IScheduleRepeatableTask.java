package com.evervault.contracts;

public interface IScheduleRepeatableTask {
    void schedule(IExecuteRepeatableTask executable);
}
