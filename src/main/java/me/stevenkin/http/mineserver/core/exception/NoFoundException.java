package me.stevenkin.http.mineserver.core.exception;

/**
 * Created by wjg on 16-4-26.
 */
public class NoFoundException extends RuntimeException {
    public NoFoundException() {
        super("404 NO FOUND!");
    }
}
