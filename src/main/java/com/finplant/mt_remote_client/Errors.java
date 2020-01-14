package com.finplant.mt_remote_client;

public class Errors {

    public static class MtmError extends RuntimeException {
        public MtmError(String message) {
            super(message);
        }
    }

    public static class ConnectionError extends RuntimeException {
        public ConnectionError(Throwable cause) {
            super("Connection error", cause);
        }
    }
}
