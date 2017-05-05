package com.kco.proto.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by Administrator on 2017/5/5.
 */
public class HexOutputStream extends OutputStream{
    private PrintStream wrap = System.out;

    @Override
    public void flush() throws IOException {
        wrap.flush();
    }

    @Override
    public void close() throws IOException {
        wrap.close();
    }

    @Override
    public void write(int b) throws IOException {
        int a = b & 0xff;
        String s = Integer.toHexString(a);
        if (s.length() % 2 == 1){
            s = "0" + s;
        }
        wrap.print(s + " ");
    }
}
