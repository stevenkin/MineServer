package me.stevenkin.http.mineserver.core.exception;

/**
 * Created by wjg on 16-4-23.
 */
public class ProtocolSyntaxException extends RuntimeException {
    private String message;

    public ProtocolSyntaxException(String message) {
        super(message);
    }
}
