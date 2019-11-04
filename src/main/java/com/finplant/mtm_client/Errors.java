package com.finplant.mtm_client;

import io.crossbar.autobahn.wamp.exceptions.ApplicationError;

public class Errors {

    public static MtmError from(ApplicationError error) {
        switch ((String) error.kwargs.get("uri")) {
            case "unknown.error":
                return new UnknownError((String) error.kwargs.get("message"));
            default:
                throw new IllegalStateException("Unexpected value: " + (String) error.kwargs.get("uri"));
        }
    }

    public static class MtmError extends RuntimeException {
        public MtmError(String message) {
            super(message);
        }
    }

    public static class UnknownError extends MtmError {
        public UnknownError(String message) {
            super(message);
        }
    }
}
