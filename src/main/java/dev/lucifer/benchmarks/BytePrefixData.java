package dev.lucifer.benchmarks;

import java.util.Random;

import org.openjdk.jmh.annotations.*;

@State(Scope.Thread)
public class BytePrefixData {

    @Param({"0.25", "0.50", "0.75", "1.00"})
    double prefix;

    @Param({"1000", "10000", "100000"})
    int size;

    public byte[] data1;
    public byte[] data2;


    public static byte[] createByteArray(int size) {
        Random rand = new Random();
        byte[] array = new byte[size];
        rand.nextBytes(array);
        return array;
    }

    @Setup(Level.Trial)
    public void init() {
        int prefixLength = (int)(prefix * size);
        byte[] commonPrefix = createByteArray(prefixLength);
        data1 = createByteArray(size);
        data2 = createByteArray(size);
        System.arraycopy(commonPrefix, 0, data1, 0, prefixLength);
        System.arraycopy(commonPrefix, 0, data2, 0, prefixLength);
    }
}
