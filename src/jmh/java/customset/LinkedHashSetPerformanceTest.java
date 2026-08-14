package customset;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Spliterator;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class LinkedHashSetPerformanceTest {

    @Param({"10000", "20000", "30000", "40000", "50000", "60000", "70000", "80000", "90000", "100000"})
    public int size;

    private List<Integer> collection;
    private LinkedHashSet<Integer> set;
    private LinkedHashSet<Integer> equalMatchSet;
    private Random random;

    @Setup(Level.Trial)
    public void setupTrial() {
        random = new Random(42);
        collection = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            collection.add(random.nextInt());
        }
        equalMatchSet = new LinkedHashSet<>(collection);
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        set = new LinkedHashSet<>();
        set.addAll(collection);
    }

    // --- Constructor Benchmarks ---

    @Benchmark
    public LinkedHashSet<Integer> benchmarkDefaultConstructor() {
        return new LinkedHashSet<>();
    }

    @Benchmark
    public LinkedHashSet<Integer> benchmarkCapacityConstructor() {
        return new LinkedHashSet<>(size);
    }

    @Benchmark
    public LinkedHashSet<Integer> benchmarkCapacityAndLoadFactorConstructor() {
        return new LinkedHashSet<>(Math.max(size, 16), 0.75f);
    }

    @Benchmark
    public LinkedHashSet<Integer> benchmarkCollectionConstructor() {
        return new LinkedHashSet<>(collection);
    }

    // --- Method Benchmarks ---

    @Benchmark
    public LinkedHashSet<Integer> benchmarkAdd() {
        LinkedHashSet<Integer> target = new LinkedHashSet<>();
        for (int i = 0; i < size; i++) {
            target.add(random.nextInt());
        }
        return target;
    }

    @Benchmark
    public LinkedHashSet<Integer> benchmarkAddAll() {
        LinkedHashSet<Integer> target = new LinkedHashSet<>();
        target.addAll(collection);
        return target;
    }

    @Benchmark
    public void benchmarkClear() {
        set.clear();
    }

    @Benchmark
    public Object benchmarkClone() {
        return set.clone();
    }

    @Benchmark
    public boolean benchmarkContains() {
        if (collection.isEmpty()) return false;
        int item = collection.get(random.nextInt(collection.size()));
        return set.contains(item);
    }

    @Benchmark
    public boolean benchmarkContainsAll() {
        return set.containsAll(collection);
    }

    @Benchmark
    public boolean benchmarkEquals() {
        return set.equals(equalMatchSet);
    }

    @Benchmark
    public int benchmarkHashCode() {
        return set.hashCode();
    }

    @Benchmark
    public boolean benchmarkIsEmpty() {
        return set.isEmpty();
    }

    @Benchmark
    public int benchmarkIteratorConsumption() {
        int count = 0;
        Iterator<Integer> it = set.iterator();
        while (it.hasNext()) {
            count += it.next();
        }
        return count;
    }

    @Benchmark
    public boolean benchmarkRemove() {
        if (collection.isEmpty()) return false;
        int item = collection.get(random.nextInt(collection.size()));
        return set.remove(item);
    }

    @Benchmark
    public boolean benchmarkRemoveAll() {
        return set.removeAll(collection);
    }

    @Benchmark
    public boolean benchmarkRetainAll() {
        return set.retainAll(collection);
    }

    @Benchmark
    public int benchmarkSize() {
        return set.size();
    }

    @Benchmark
    public int benchmarkSpliteratorConsumption() {
        final int[] count = {0};
        Spliterator<Integer> spliterator = set.spliterator();
        spliterator.forEachRemaining(element -> count[0] += element);
        return count[0];
    }

    @Benchmark
    public Object[] benchmarkToArray() {
        return set.toArray();
    }

    @Benchmark
    public Integer[] benchmarkToArrayTyped() {
        Integer[] template = new Integer[size];
        return set.toArray(template);
    }

    @Benchmark
    public String benchmarkToString() {
        return set.toString();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(LinkedHashSetPerformanceTest.class.getSimpleName())
                .forks(1)
                .result("LinkedHashSet_performance_results.csv")
                .resultFormat(ResultFormatType.CSV)
                .build();

        Collection<RunResult> results = new Runner(opt).run();
        writeCustomCsv(results);
    }

    private static void writeCustomCsv(Collection<RunResult> results) {
        try (FileWriter writer = new FileWriter("LinkedHashSet_jmh_performance.csv")) {
            writer.write("Benchmark;Size;Score (ns/op)\n");
            for (RunResult result : results) {
                String benchmarkName = result.getParams().getBenchmark();
                String shortName = benchmarkName.substring(benchmarkName.lastIndexOf('.') + 1);

                double score = result.getPrimaryResult().getScore();
                String sizeVal = result.getParams().getParam("size");

                writer.write("\"" + shortName + "\";" + (sizeVal != null ? sizeVal : "N/A") + ";" + score + "\n");
            }
            System.out.println("JMH Performance report saved: LinkedHashSet_jmh_performance.csv");
        } catch (IOException e) {
            System.err.println("Failed to write CSV: " + e.getMessage());
        }
    }
}