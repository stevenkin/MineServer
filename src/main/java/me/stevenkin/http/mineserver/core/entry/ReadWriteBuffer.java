package me.stevenkin.http.mineserver.core.entry;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wjg on 16-4-14.
 */
public class ReadWriteBuffer {
    private byte[] requestHeaderBytes;
    private byte[] requestBodyBytes;
    private byte[] responseBytes;

    public ReadWriteBuffer() {
        this.responseBytes = new byte[0];
        this.requestHeaderBytes = new byte[0];
        this.requestBodyBytes = new byte[0];
    }

    public byte[] getRequestHeaderBytes() {
        return requestHeaderBytes;
    }

    public void setRequestHeaderBytes(byte[] requestHeaderBytes) {
        this.requestHeaderBytes = requestHeaderBytes;
    }

    public byte[] getRequestBodyBytes() {
        return requestBodyBytes;
    }

    public void setRequestBodyBytes(byte[] requestBodyBytes) {
        this.requestBodyBytes = requestBodyBytes;
    }

    public byte[] getResponseBytes() {
        return responseBytes;
    }

    public void setResponseBytes(byte[] responseBytes) {
        this.responseBytes = responseBytes;
    }
}
