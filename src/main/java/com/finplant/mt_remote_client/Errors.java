package com.finplant.mt_remote_client;

public class Errors {

    public static class MtmError extends RuntimeException {
        public MtmError(String message) {
            super(message);
        }
    }

    public static class AlreadyConnectedError extends RuntimeException {
        public AlreadyConnectedError() {
            super("Already connected");
        }
    }

    public static class ConnectionError extends RuntimeException {
        public ConnectionError(Throwable cause) {
            super("Connection error", cause);
        }
    }

    public static class ConnectionLostError extends RuntimeException {
        public ConnectionLostError(int statusCode, String reason) {
            super(String.format("Connection lost. code: %d, reason: %s", statusCode, reason));
        }
    }
}
