package com.example.flutter.bridge;

public final class BridgeException extends RuntimeException {
    public final String code;

    public BridgeException(String code, String message) {
        super(message);
        this.code = code;
    }

    static BridgeException from(Throwable error) {
        while (error instanceof java.util.concurrent.CompletionException && error.getCause() != null) {
            error = error.getCause();
        }
        if (error instanceof BridgeException) return (BridgeException) error;
        return new BridgeException("handler_error", "The bridge handler failed");
    }
}
