import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Spliterator;

public class LinkedHashSetPerformanceTest {
    public static void main(String[] args) {
        // Logarithmic scale sizes to comprehensively test performance boundaries
        int[] sizes = {1, 10, 50, 100, 250, 500, 750, 1000, 2500, 5000, 7500, 10000,
                25000, 50000, 100000};

        ArrayList<long[]> results = new ArrayList<>();
        Random random = new Random();

        // JVM Warm-up phase to trigger Just-In-Time (JIT) compilation optimizations
        System.out.println("Warming up JVM...");
        for (int i = 0; i < 10000; i++) {
            LinkedHashSet<Integer> warmUpSet = new LinkedHashSet<>();
            warmUpSet.add(random.nextInt(1000));
            warmUpSet.contains(random.nextInt(1000));
            int h = warmUpSet.hashCode();
        }
        System.out.println("Warm-up complete. Starting benchmarks.");

        for (int size : sizes) {
            System.out.println("Processing collection size: " + size);

            List<Integer> collection = generateCollection(size, random);
            LinkedHashSet<Integer> set = new LinkedHashSet<>();
            set.addAll(collection);

            // 1. Single-Element Writing & Bulk Collection Operations
            long addTime = benchmarkAdd(new LinkedHashSet<>(), size, random);
            long addAllTime = benchmarkAddAll(new LinkedHashSet<>(), collection);
            long clearTime = benchmarkClear(new LinkedHashSet<>(collection));

            // 2. Lookup & Inspection Operations
            long containsTime = benchmarkContains(set, collection, random);
            long containsAllTime = benchmarkContainsAll(set, collection);
            long isEmptyTime = benchmarkIsEmpty(set);
            long sizeTime = benchmarkSize(set);

            // 3. Destructive Deletions & Retentions
            long removeTime = benchmarkRemove(set, collection, random);

            LinkedHashSet<Integer> removeAllSet = new LinkedHashSet<>(collection);
            long removeAllTime = benchmarkRemoveAll(removeAllSet, collection);

            LinkedHashSet<Integer> retainAllSet = new LinkedHashSet<>(collection);
            long retainAllTime = benchmarkRetainAll(retainAllSet, collection);

            // 4. Utility Serialization & Structural Data Transformations
            long toArrayTime = benchmarkToArray(set);
            long toArrayTypedTime = benchmarkToArrayTyped(set, size);
            long toStringTime = benchmarkToString(set);

            // 5. Comparison, Clones & Stream-Foundation Lifecycles (Previously Missing)
            long hashCodeTime = benchmarkHashCode(set);
            long cloneTime = benchmarkClone(set);
            long iteratorConsumeTime = benchmarkIteratorConsumption(set);
            long spliteratorConsumeTime = benchmarkSpliteratorConsumption(set);

            LinkedHashSet<Integer> equalMatchSet = new LinkedHashSet<>(collection);
            long equalsTime = benchmarkEquals(set, equalMatchSet);

            // Store aligned row vectors
            results.add(new long[]{
                    size, addTime, addAllTime, clearTime, containsTime, containsAllTime,
                    isEmptyTime, removeTime, removeAllTime, retainAllTime, sizeTime,
                    toArrayTime, toArrayTypedTime, toStringTime, hashCodeTime, cloneTime,
                    iteratorConsumeTime, spliteratorConsumeTime, equalsTime
            });
        }

        // Write output to comma-separated file utilizing explicit formal API parameter naming conventions
        String csvFileName = "LinkedHashSet_performance_data.csv";
        try (FileWriter writer = new FileWriter(csvFileName)) {
            writer.write("Size," +
                    "add(E)," +
                    "addAll(Collection<? extends E>)," +
                    "clear()," +
                    "contains(Object)," +
                    "containsAll(Collection<?>)," +
                    "isEmpty()," +
                    "remove(Object)," +
                    "removeAll(Collection<?>)," +
                    "retainAll(Collection<?>)," +
                    "size()," +
                    "toArray()," +
                    "toArray(T[])," +
                    "toString()," +
                    "hashCode()," +
                    "clone()," +
                    "iterator()," +
                    "spliterator()," +
                    "equals(Object)\n");

            for (long[] result : results) {
                StringBuilder row = new StringBuilder();
                for (int i = 0; i < result.length; i++) {
                    row.append(result[i]);
                    if (i < result.length - 1) {
                        row.append(",");
                    }
                }
                writer.write(row.toString() + "\n");
            }
            System.out.println("Benchmark output saved successfully to: " + csvFileName);
        } catch (IOException e) {
            System.err.println("Fatal Error executing file serialization layout: " + e.getMessage());
        }
    }

    // --- Benchmark Subroutines ---

    private static long benchmarkAdd(LinkedHashSet<Integer> set, int size, Random random) {
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            set.add(random.nextInt());
        }
        return System.nanoTime() - start;
    }

    private static long benchmarkAddAll(LinkedHashSet<Integer> set, Collection<Integer> collection) {
        long start = System.nanoTime();
        set.addAll(collection);
        return System.nanoTime() - start;
    }

    private static long benchmarkClear(LinkedHashSet<Integer> set) {
        long start = System.nanoTime();
        set.clear();
        return System.nanoTime() - start;
    }

    private static long benchmarkContains(LinkedHashSet<Integer> set, List<Integer> collection, Random random) {
        if (collection.isEmpty()) return 0;
        int item = collection.get(random.nextInt(collection.size()));
        long start = System.nanoTime();
        set.contains(item);
        return System.nanoTime() - start;
    }

    private static long benchmarkContainsAll(LinkedHashSet<Integer> set, Collection<Integer> collection) {
        long start = System.nanoTime();
        set.containsAll(collection);
        return System.nanoTime() - start;
    }

    private static long benchmarkIsEmpty(LinkedHashSet<Integer> set) {
        long start = System.nanoTime();
        set.isEmpty();
        return System.nanoTime() - start;
    }

    private static long benchmarkRemove(LinkedHashSet<Integer> set, List<Integer> collection, Random random) {
        if (collection.isEmpty()) return 0;
        int item = collection.get(random.nextInt(collection.size()));
        long start = System.nanoTime();
        set.remove(item);
        return System.nanoTime() - start;
    }

    private static long benchmarkRemoveAll(LinkedHashSet<Integer> set, Collection<Integer> collection) {
        long start = System.nanoTime();
        set.removeAll(collection);
        return System.nanoTime() - start;
    }

    private static long benchmarkRetainAll(LinkedHashSet<Integer> set, Collection<Integer> collection) {
        long start = System.nanoTime();
        set.retainAll(collection);
        return System.nanoTime() - start;
    }

    private static long benchmarkSize(LinkedHashSet<Integer> set) {
        long start = System.nanoTime();
        set.size();
        return System.nanoTime() - start;
    }

    private static long benchmarkToArray(LinkedHashSet<Integer> set) {
        long start = System.nanoTime();
        set.toArray();
        return System.nanoTime() - start;
    }

    private static long benchmarkToArrayTyped(LinkedHashSet<Integer> set, int size) {
        Integer[] template = new Integer[size];
        long start = System.nanoTime();
        set.toArray(template);
        return System.nanoTime() - start;
    }

    private static long benchmarkToString(LinkedHashSet<Integer> set) {
        long start = System.nanoTime();
        set.toString();
        return System.nanoTime() - start;
    }

    private static long benchmarkHashCode(LinkedHashSet<Integer> set) {
        long start = System.nanoTime();
        int h = set.hashCode();
        return System.nanoTime() - start;
    }

    private static long benchmarkClone(LinkedHashSet<Integer> set) {
        long start = System.nanoTime();
        LinkedHashSet<Integer> cloned = (LinkedHashSet<Integer>) set.clone();
        return System.nanoTime() - start;
    }

    private static long benchmarkEquals(LinkedHashSet<Integer> set, Object other) {
        long start = System.nanoTime();
        boolean eq = set.equals(other);
        return System.nanoTime() - start;
    }

    private static long benchmarkIteratorConsumption(LinkedHashSet<Integer> set) {
        long start = System.nanoTime();
        Iterator<Integer> it = set.iterator();
        while (it.hasNext()) {
            Integer next = it.next();
        }
        return System.nanoTime() - start;
    }

    private static long benchmarkSpliteratorConsumption(LinkedHashSet<Integer> set) {
        long start = System.nanoTime();
        Spliterator<Integer> spliterator = set.spliterator();
        spliterator.forEachRemaining(element -> {});
        return System.nanoTime() - start;
    }

    // --- Auxiliary Utilities ---

    private static List<Integer> generateCollection(int size, Random random) {
        List<Integer> collection = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            collection.add(random.nextInt());
        }
        return collection;
    }
}