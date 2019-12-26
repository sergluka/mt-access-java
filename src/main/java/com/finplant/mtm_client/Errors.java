package com.finplant.mtm_client;

public class Errors {

    public static class MtmError extends Exception {
        public MtmError(String message) {
            super(message);
        }
    }

    public static class ConnectionUnexpectedCloseError extends Exception {
        public ConnectionUnexpectedCloseError(int statusCode) {
            super(String.format("Connection closed with a status %d", statusCode));
        }
    }

    public static class ConnectionError extends Exception {
        public ConnectionError(Throwable cause) {
            super("Connection error", cause);
        }
    }
}
