package me.stevenkin.http.mineserver.core.exception;

/**
 * Created by wjg on 16-4-26.
 */
public class InnerErrorException extends RuntimeException {
    public InnerErrorException() {
        super("500 INNER ERROR");
    }
}
