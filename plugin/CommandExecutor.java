package com.feihong.executor;

import com.feihong.bean.CommandExecutionResult;

import java.io.IOException;

public interface CommandExecutor {
    public abstract String getName();
    public abstract CommandExecutionResult exec(String command);
}
