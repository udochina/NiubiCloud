package com.niubicloud.service.exception;

public class ExceptionUtil {
    public static boolean shouldRethrow(Exception exception) {
        return exception instanceof FinishRequest || exception instanceof UnpredictedException || exception instanceof ProtocolException;
    }
}
