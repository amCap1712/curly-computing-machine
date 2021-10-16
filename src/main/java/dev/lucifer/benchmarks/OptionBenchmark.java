package dev.lucifer.benchmarks;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 5)
public class OptionBenchmark {

    private final long MAGIC_NUMBER = 7;

    // Variant 1.
    // Probably the simplest way to sum numbers.
    // No boxing, no objects involved, just primitive long values everywhere.
    // This is probably what a C-programmer converted to Java would write ;)
    private long getNumber(long i) {
        return i & 0xFF;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public long sumSimple() {
        long sum = 0;
        for (long i = 0; i < 1_000_000; ++i) {
            long n = getNumber(i);
            if (n != MAGIC_NUMBER)
                sum += n;
        }
        return sum;
    }

    // Variant 2.
    // Replace MAGIC_NUMBER with a null.
    // To be able to return null, we need to box long into a Long object.
    private Long getNumberOrNull(long i) {
        long n = i & 0xFF;
        return n == MAGIC_NUMBER ? null : n;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public long sumNulls() {
        long sum = 0;
        for (long i = 0; i < 1_000_000; ++i) {
            Long n = getNumberOrNull(i);
            if (n != null) {
                sum += n;
            }
        }
        return sum;
    }


    // Variant 3.
    // Replace MAGIC_NUMBER with Optional.empty().
    // Now we not only need to box the value into a Long, but also create the Optionsl wrapper.
    private OptionalLong getOptionalNumber(long i) {
        long n = i & 0xFF;
        return n == MAGIC_NUMBER ? OptionalLong.empty() : OptionalLong.of(n);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public long sumOptional() {
        long sum = 0;
        for (long i = 0; i < 1_000_000; ++i) {
            OptionalLong n = getOptionalNumber(i);
            if (n.isPresent()) {
                sum += n.getAsLong();
            }
        }
        return sum;
    }
}
