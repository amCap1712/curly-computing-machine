package dev.lucifer.benchmarks;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.random.RandomGenerator;

@Fork(jvmArgsPrepend = {"--add-modules", "jdk.incubator.vector"})
@State(Scope.Thread)
public class WithLaneBenchmark {

    int[] array;
    int x0, x1, x2, x3, x4, x5, x6, x7;

    static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_256;

    @Setup
    public void setup() {
        RandomGenerator generator = RandomGenerator.getDefault();
        array = generator.ints(8).toArray();
        x0 = generator.nextInt();
        x1 = generator.nextInt();
        x2 = generator.nextInt();
        x3 = generator.nextInt();
        x4 = generator.nextInt();
        x5 = generator.nextInt();
        x6 = generator.nextInt();
        x7 = generator.nextInt();
    }

    @Benchmark
    public IntVector loadFromArray() {
        return IntVector.fromArray(SPECIES, array, 0);
    }

    @Benchmark
    public IntVector loadFromInts() {
        return IntVector.zero(SPECIES)
                .withLane(0, x0)
                .withLane(1, x1)
                .withLane(2, x2)
                .withLane(3, x3)
                .withLane(4, x4)
                .withLane(5, x5)
                .withLane(6, x6)
                .withLane(7, x7);
    }

}
