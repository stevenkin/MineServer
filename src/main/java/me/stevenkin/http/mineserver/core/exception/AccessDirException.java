package me.stevenkin.http.mineserver.core.exception;

/**
 * Created by wjg on 2018/1/20.
 */
public class AccessDirException extends RuntimeException {
    public AccessDirException(){
        super("400 access a dir,but do not show dir");
    }
}
