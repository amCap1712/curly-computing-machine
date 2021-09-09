package dev.lucifer.benchmarks;

import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ArrayMismatchBenchmark {

    static final VectorSpecies<Byte> SPECIES_PREFERRED = ByteVector.SPECIES_PREFERRED;

    int mismatchScalar(byte[] data1, byte[] data2, int startIndex) {
        int length = Math.min(data1.length, data2.length);
        int mismatch = -1;
        for (int i = startIndex; i < length; ++i) {
            if (data1[i] != data2[i]) {
                mismatch = i;
                break;
            }
        }
        return mismatch;
    }

    @Benchmark
    public int mismatchScalar(BytePrefixData data) {
        return mismatchScalar(data.data1, data.data2, 0);
    }

    @Benchmark
    public int mismatchVector(BytePrefixData data) {
        byte[] data1 = data.data1;
        byte[] data2 = data.data2;
        int length = Math.min(data1.length, data2.length);
        int index = 0;
        for (; index <= length - SPECIES_PREFERRED.length(); index += SPECIES_PREFERRED.length()) {
            ByteVector vector1 = ByteVector.fromArray(SPECIES_PREFERRED, data1, index);
            ByteVector vector2 = ByteVector.fromArray(SPECIES_PREFERRED, data2, index);
            VectorMask<Byte> mask = vector1.compare(VectorOperators.NE, vector2);
            if (mask.anyTrue()) {
                return index + mask.firstTrue();
            }
        }
        return mismatchScalar(data1, data2, index);
    }

    @Benchmark
    public int mismatchVectorTail(BytePrefixData data) {
        byte[] data1 = data.data1;
        byte[] data2 = data.data2;
        int length = Math.min(data1.length, data2.length);
        int UPPER_BOUND = SPECIES_PREFERRED.loopBound(length);
        int index = 0;
        ByteVector vector1;
        ByteVector vector2;
        VectorMask<Byte> mask;
        for (; index < UPPER_BOUND; index += SPECIES_PREFERRED.length()) {
            vector1 = ByteVector.fromArray(SPECIES_PREFERRED, data1, index);
            vector2 = ByteVector.fromArray(SPECIES_PREFERRED, data2, index);
            mask = vector1.compare(VectorOperators.NE, vector2);
            if (mask.anyTrue()) {
                return index + mask.firstTrue();
            }
        }
        VectorMask<Byte> mask1 = SPECIES_PREFERRED.indexInRange(index, length);
        VectorMask<Byte> mask2 = SPECIES_PREFERRED.indexInRange(index, length);
        vector1 = ByteVector.fromArray(SPECIES_PREFERRED, data1, index, mask1);
        vector2 = ByteVector.fromArray(SPECIES_PREFERRED, data2, index, mask2);
        mask = vector1.compare(VectorOperators.NE, vector2);;
        if (mask.anyTrue()) {
            return index + mask.firstTrue();
        }
        return -1;
    }

    @Benchmark
    public int mismatchVectorMask(BytePrefixData data) {
        byte[] data1 = data.data1;
        byte[] data2 = data.data2;
        int length = Math.min(data1.length, data2.length);
        int UPPER_BOUND = SPECIES_PREFERRED.loopBound(length);
        for (int index = 0; index < UPPER_BOUND; index += SPECIES_PREFERRED.length()) {
            VectorMask<Byte> mask1 = SPECIES_PREFERRED.indexInRange(index, length);
            VectorMask<Byte> mask2 = SPECIES_PREFERRED.indexInRange(index, length);
            ByteVector vector1 = ByteVector.fromArray(SPECIES_PREFERRED, data1, index, mask1);
            ByteVector vector2 = ByteVector.fromArray(SPECIES_PREFERRED, data2, index, mask2);
            VectorMask<Byte> mask = vector1.compare(VectorOperators.NE, vector2);;
            if (mask.anyTrue()) {
                return index + mask.firstTrue();
            }
        }
        return -1;
    }

    @Benchmark
    public int mismatchIntrinsic(BytePrefixData data) {
        return Arrays.mismatch(data.data1, data.data2);
    }

}
