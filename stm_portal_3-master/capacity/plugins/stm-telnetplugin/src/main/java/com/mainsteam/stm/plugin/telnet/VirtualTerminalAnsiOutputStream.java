package com.mainsteam.stm.plugin.telnet;

import org.fusesource.jansi.AnsiOutputStream;

import java.io.OutputStream;
import java.util.LinkedList;
import java.util.ListIterator;

public class VirtualTerminalAnsiOutputStream extends AnsiOutputStream {

    protected LinkedList<Byte> buffer;
    protected ListIterator<Byte> bufferIterator;

    public VirtualTerminalAnsiOutputStream() {
        super(null);
        buffer = new LinkedList<>();
        bufferIterator = buffer.listIterator();
        out = new LinkedArrayOutputStream();
    }

    @Override
    protected void processCursorLeft(int count) {
        while (count-- > 0) {
            if (!bufferIterator.hasPrevious())
                break;
            if (bufferIterator.previous() == '\n') {
                bufferIterator.next();
                break;
            }
        }
    }

    public byte[] toByteArray() {
        byte[] result = new byte[buffer.size()];
        ListIterator<Byte> iterator = buffer.listIterator();
        int p = 0;
        while (iterator.hasNext()) {
            result[p++] = iterator.next();
        }
        return result;
    }

    private class LinkedArrayOutputStream extends OutputStream {
        private static final int BS = 8;

        @Override
        public void write(int b) {
            if (b == BS && bufferIterator.hasPrevious()) {
                if (bufferIterator.previous() != '\n') {
                    bufferIterator.remove();
                } else {
                    bufferIterator.next();
                }
            } else {
                if (bufferIterator.hasNext()) {
                    bufferIterator.next();
                    bufferIterator.remove();
                }
                bufferIterator.add((byte) b);
            }
        }
    }
}
