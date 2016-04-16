package me.stevenkin.http.mineserver.core;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wjg on 16-4-14.
 */
public class ReadWriteBuffer {
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;

    public ReadWriteBuffer() {
        this.readBuffer = ByteBuffer.allocate(2048);
    }

    public ByteBuffer getReadBuffer() {
        return readBuffer;
    }

    public ByteBuffer getWriteBuffer() {
        return writeBuffer;
    }

    public void setWriteBuffer(ByteBuffer writeBuffer) {
        this.writeBuffer = writeBuffer;
    }
}
