package com.finplant.mt_remote;

import com.fasterxml.jackson.databind.JsonNode;

public class Errors {

    public static class MtRemoteError extends RuntimeException {
        public MtRemoteError(String message) {
            super(message);
        }
    }

    public static class MtRemoteConnectionError extends RuntimeException {
        private final int statusCode;

        public MtRemoteConnectionError(int statusCode, String reason) {
            super(String.format("Connection error. code: %d, reason: %s", statusCode, reason));
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }

    public static class MessageParsingError extends RuntimeException {
        public MessageParsingError(String message) {
            super(message);
        }
    }

    public static class UnexpectedResponseError extends RuntimeException {
        public UnexpectedResponseError(JsonNode json) {
            super(String.format("Unexpected response: %s", json));
        }
    }
}
