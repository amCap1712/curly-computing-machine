package dev.lucifer.benchmarks;

import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.openjdk.jmh.annotations.Benchmark;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ArrayMismatchBenchmark {

    int scalarMismatch(byte[] data1, byte[] data2, int startIndex) {
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
    public int mismatchIntrinsic(BytePrefixData data) {
        return Arrays.mismatch(data.data1, data.data2);
    }

    static final VectorSpecies<Byte> SPECIES_PREFERRED = ByteVector.SPECIES_PREFERRED;

    @Benchmark
    public int mismatchVector(BytePrefixData data) {
        byte[] data1 = data.data1;
        byte[] data2 = data.data2;
        int length = Math.min(data1.length, data2.length);
        int index = 0;
        for (; index <= length - SPECIES_PREFERRED.length(); index += SPECIES_PREFERRED.length()) {
            ByteVector vector1 = ByteVector.fromArray( SPECIES_PREFERRED, data1, index );
            ByteVector vector2 = ByteVector.fromArray( SPECIES_PREFERRED, data2, index );
            VectorMask<Byte> mask = vector1.compare( VectorOperators.NE, vector2 );
            if ( mask.anyTrue() )
                return index + mask.firstTrue();
        }
        return scalarMismatch( data1, data2, index );
    }

}
