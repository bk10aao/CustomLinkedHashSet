# Custom Linked Set

Implementation of a LinkedHashSet backed by a LinkedHashMap.

All methods implemented are identical to those found in the Java Set interface.

# Build and Test

To build and test the project run command `./gradlew clean build`

# Time Complexity

| Method                                |     Custom      |        JDK        | Winner  |
|:--------------------------------------|:---------------:|:-----------------:|:-------:|
| **`add(E)`**                          |     $O(1)$      |      $O(1)$       | **Tie** |
| **`addAll(Collection<? extends E>)`** |     $O(M)$      |      $O(M)$       | **Tie** |
| **`clear()`**                         |     $O(N)$      |      $O(N)$       | **Tie** |
| **`clone()`**                         |     $O(N)$      |      $O(N)$       | **Tie** |
| **`contains(Object)`**                |     $O(1)$      |      $O(1)$       | **Tie** |
| **`containsAll(Collection<?>)`**      |     $O(M)$      |      $O(M)$       | **Tie** |
| **`equals(Object)`**                  |     $O(N)$      |      $O(N)$       | **Tie** |
| **`hashCode()`**                      |     $O(N)$      |      $O(N)$       | **Tie** |
| **`isEmpty()`**                       |     $O(1)$      |      $O(1)$       | **Tie** |
| **`iterator()`**                      |     $O(1)$      |      $O(1)$       | **Tie** |
| **`remove(Object)`**                  |     $O(1)$      |      $O(1)$       | **Tie** |
| **`removeAll(Collection<?>)`**        | $O(N \times M)$ |  $O(N \times M)$  | **Tie** |
| **`retainAll(Collection<?>)`**        | $O(N \times M)$ |  $O(N \times M)$  | **Tie** |
| **`size()`**                          |     $O(1)$      |      $O(1)$       | **Tie** |
| **`spliterator()`**                   |     $O(1)$      |      $O(1)$       | **Tie** |
| **`toArray()`**                       |     $O(N)$      |      $O(N)$       | **Tie** |
| **`toArray(T[])`**                    |     $O(N)$      |      $O(N)$       | **Tie** |
| **`toString()`**                      |     $O(N)$      |      $O(N)$       | **Tie** |

# Space Complexity

| Method                                | Custom |  JDK   |  Winner  |
|:--------------------------------------|:------:|:------:|:--------:|
| **`add(E)`**                          | $O(1)$ | $O(1)$ | **Tie**  |
| **`addAll(Collection<? extends E>)`** | $O(1)$ | $O(1)$ | **Tie**  |
| **`clear()`**                         | $O(1)$ | $O(1)$ | **Tie**  |
| **`clone()`**                         | $O(N)$ | $O(N)$ | **Tie**  |
| **`contains(Object)`**                | $O(1)$ | $O(1)$ | **Tie**  |
| **`containsAll(Collection<?>)`**      | $O(1)$ | $O(1)$ | **Tie**  |
| **`equals(Object)`**                  | $O(1)$ | $O(1)$ | **Tie**  |
| **`hashCode()`**                      | $O(1)$ | $O(1)$ | **Tie**  |
| **`isEmpty()`**                       | $O(1)$ | $O(1)$ | **Tie**  |
| **`iterator()`**                      | $O(1)$ | $O(1)$ | **Tie**  |
| **`remove(Object)`**                  | $O(1)$ | $O(1)$ | **Tie**  |
| **`removeAll(Collection<?>)`**        | $O(1)$ | $O(1)$ | **Tie**  |
| **`retainAll(Collection<?>)`**        | $O(1)$ | $O(1)$ | **Tie**  |
| **`size()`**                          | $O(1)$ | $O(1)$ | **Tie**  |
| **`spliterator()`**                   | $O(1)$ | $O(1)$ | **Tie**  |
| **`toArray()`**                       | $O(N)$ | $O(N)$ | **Tie**  |
| **`toArray(T[])`**                    | $O(N)$ | $O(N)$ | **Tie**  |
| **`toString()`**                      | $O(N)$ | $O(N)$ | **Tie**  |

**Notes**:
- n: Total number of elements currently stored within the set.
- m: Number of elements in the incoming collection argument.

# Performance Comparison

### V2 vs JDK

| Method                    | Custom Avg (ns) | JDK Avg (ns)  |            Winner            | Margin |
|:--------------------------|:----------------|:--------------|:----------------------------:|:------:|
| `Constructor()`           | 60              | 85            |          **Custom**          | 1.43x  |
| `Constructor(Collection)` | 889,714         | 1,332,410     |          **Custom**          | 1.50x  |
| `Constructor(int)`        | 90              | 205           |          **Custom**          | 2.27x  |
| `Constructor(int, float)` | 70              | 109           |          **Custom**          | 1.55x  |
| `add(E)`                  | 2,096,563       | 2,120,029     | **Statistically Equivalent** | 1.01x  |
| `addAll(Collection`       | 825,988         | 857,148       | **Statistically Equivalent** | 1.04x  |
| `clear()`                 | 27,707          | 44,376        |          **Custom**          | 1.60x  |
| `clone()`                 | 820,906         | 1,287,559     |          **Custom**          | 1.57x  |
| `contains(Object)`        | 115             | 227           |          **Custom**          | 1.98x  |
| `containsAll(Collection)` | 501,230         | 850,807       |          **Custom**          | 1.70x  |
| `equals(Object)`          | 559,604         | 996,686       |          **Custom**          | 1.78x  |
| `hashCode()`              | 131,695         | 234,715       |          **Custom**          | 1.78x  |
| `isEmpty()`               | 42              | 67            |          **Custom**          | 1.61x  |
| `iterator()`              | 114,376         | 238,337       |          **Custom**          | 2.08x  |
| `remove(Object)`          | 168             | 679           |          **Custom**          | 4.04x  |
| `removeAll(Collection)`   | 1,043,358,311   | 1,819,780,036 |          **Custom**          | 1.74x  |
| `retainAll(Collection)`   | 1,042,457,650   | 1,825,860,116 |          **Custom**          | 1.75x  |
| `size()`                  | 37              | 52            |          **Custom**          | 1.40x  |
| `spliterator()`           | 131,350         | 219,133       |          **Custom**          | 1.67x  |
| `toArray()`               | 78,326          | 133,753       |          **Custom**          | 1.71x  |
| `toArray(T[])`            | 93,016          | 161,971       |          **Custom**          | 1.74x  |
| `toString()`              | 1,817,993       | 2,612,949     |          **Custom**          | 1.44x  |

### V1 vs V2

| Method                    | V1 Avg (ns) | V2 Avg (ns)   |            Winner            | Margin  |
|:--------------------------|:------------|:--------------|:----------------------------:|:-------:|
| `Constructor()`           | 82          | 60            |            **V2**            |  1.37x  |
| `Constructor(Collection)` | 584,748     | 887,114       |            **V1**            |  1.52x  |
| `Constructor(int)`        | 3,957       | 90            |            **V2**            | 43.97x  |
| `Constructor(int, float)` | 3,677       | 70            |            **V2**            | 52.53x  |
| `add(E)`                  | 1,168,549   | 2,096,563     |            **V1**            |  1.79x  |
| `addAll(Collection)`      | 544,731     | 825,988       |            **V1**            |  1.52x  |
| `clear()`                 | 117,109     | 27,707        |            **V2**            |  4.23x  |
| `clone()`                 | 231,725     | 820,906       |            **V1**            |  3.54x  |
| `contains(Object)`        | 193         | 115           |            **V2**            |  1.68x  |
| `containsAll(Collection)` | 359,168     | 501,230       |            **V1**            |  1.40x  |
| `equals(Object)`          | 429,149     | 559,604       |            **V1**            |  1.30x  |
| `hashCode()`              | 111,547     | 131,695       |            **V1**            |  1.18x  |
| `isEmpty()`               | 44          | 42            | **Statistically Equivalent** |  1.05x  |
| `iterator()`              | 132,805     | 114,376       |            **V2**            |  1.16x  |
| `remove(Object)`          | 197         | 168           |            **V2**            |  1.17x  |
| `removeAll(Collection)`   | 1,986,255   | 1,043,358,311 |            **V1**            | 525.29x |
| `retainAll(Collection)`   | 1,281,655   | 1,042,457,650 |            **V1**            | 813.37x |
| `size()`                  | 36          | 37            | **Statistically Equivalent** |  1.03x  |
| `spliterator()`           | 124,418     | 131,350       | **Statistically Equivalent** |  1.06x  |
| `toArray()`               | 78,527      | 78,326        | **Statistically Equivalent** |  1.00x  |
| `toArray(T[])`            | 94,648      | 93,016        | **Statistically Equivalent** |  1.02x  |
| `toString()`              | 2,177,275   | 1,817,993     |            **V2**            |  1.20x  |

# Performance Charts

#### Note: The following performance charts are designed to be viewed in dark mode.

### Heat Maps
![heatmap.png](PerformanceCharts/V2_JDK/heatmap.png)
![heatmap.png](PerformanceCharts/V1_V2/heatmap.png)
![heatmap.png](PerformanceCharts/V1_JDK/heatmap.png)

### Geometric

![geometric.png](PerformanceCharts/V2_JDK/geometric.png)
![geometric.png](PerformanceCharts/V1_V2/geometric.png)
![geometric.png](PerformanceCharts/V1_JDK/geometric.png)

### V1 vs V2 vs JDK
![constructor.png](PerformanceCharts/V1_V2_JDK/constructor.png)
![constructor_collection.png](PerformanceCharts/V1_V2_JDK/constructor_collection.png)
![constructor_int.png](PerformanceCharts/V1_V2_JDK/constructor_int.png)
![constructor_int_float.png](PerformanceCharts/V1_V2_JDK/constructor_int_float.png)
![add.png](PerformanceCharts/V1_V2_JDK/add.png)
![addAll.png](PerformanceCharts/V1_V2_JDK/addAll.png)
![clear.png](PerformanceCharts/V1_V2_JDK/clear.png)
![clone.png](PerformanceCharts/V1_V2_JDK/clone.png)
![contains.png](PerformanceCharts/V1_V2_JDK/contains.png)
![containsAll.png](PerformanceCharts/V1_V2_JDK/containsAll.png)
![equals.png](PerformanceCharts/V1_V2_JDK/equals.png)
![hashCode.png](PerformanceCharts/V1_V2_JDK/hashCode.png)
![isEmpty.png](PerformanceCharts/V1_V2_JDK/isEmpty.png)
![iterator.png](PerformanceCharts/V1_V2_JDK/iterator.png)
![remove.png](PerformanceCharts/V1_V2_JDK/remove.png)
![removeAll.png](PerformanceCharts/V1_V2_JDK/removeAll.png)
![retainAll.png](PerformanceCharts/V1_V2_JDK/retainAll.png)
![size.png](PerformanceCharts/V1_V2_JDK/size.png)
![spliterator.png](PerformanceCharts/V1_V2_JDK/spliterator.png)
![toArray.png](PerformanceCharts/V1_V2_JDK/toArray.png)
![toArray_T.png](PerformanceCharts/V1_V2_JDK/toArray_T.png)
![toString.png](PerformanceCharts/V1_V2_JDK/toString.png)

### V2 vs JDK
![constructor.png](PerformanceCharts/V2_JDK/constructor.png)
![constructor_collection.png](PerformanceCharts/V2_JDK/constructor_collection.png)
![constructor_int.png](PerformanceCharts/V2_JDK/constructor_int.png)
![constructor_int_float.png](PerformanceCharts/V2_JDK/constructor_int_float.png)
![add.png](PerformanceCharts/V2_JDK/add.png)
![addAll.png](PerformanceCharts/V2_JDK/addAll.png)
![clear.png](PerformanceCharts/V2_JDK/clear.png)
![clone.png](PerformanceCharts/V2_JDK/clone.png)
![contains.png](PerformanceCharts/V1_V2_JDK/contains.png)
![containsAll.png](PerformanceCharts/V2_JDK/containsAll.png)
![equals.png](PerformanceCharts/V2_JDK/equals.png)
![hashCode.png](PerformanceCharts/V2_JDK/hashCode.png)
![isEmpty.png](PerformanceCharts/V2_JDK/isEmpty.png)
![iterator.png](PerformanceCharts/V2_JDK/iterator.png)
![remove.png](PerformanceCharts/V2_JDK/remove.png)
![removeAll.png](PerformanceCharts/V2_JDK/removeAll.png)
![retainAll.png](PerformanceCharts/V2_JDK/retainAll.png)
![size.png](PerformanceCharts/V2_JDK/size.png)
![spliterator.png](PerformanceCharts/V2_JDK/spliterator.png)
![toArray.png](PerformanceCharts/V2_JDK/toArray.png)
![toArray_T.png](PerformanceCharts/V2_JDK/toArray_T.png)
![toString.png](PerformanceCharts/V2_JDK/toString.png)

### V1 vs V2

![constructor.png](PerformanceCharts/V1_V2/constructor.png)
![constructor_collection.png](PerformanceCharts/V1_V2/constructor_collection.png)
![constructor_int.png](PerformanceCharts/V1_V2/constructor_int.png)
![constructor_int_float.png](PerformanceCharts/V1_V2/constructor_int_float.png)
![add.png](PerformanceCharts/V1_V2/add.png)
![addAll.png](PerformanceCharts/V1_V2/addAll.png)
![clear.png](PerformanceCharts/V1_V2/clear.png)
![clone.png](PerformanceCharts/V1_V2/clone.png)
![contains.png](PerformanceCharts/V1_V2/contains.png)
![containsAll.png](PerformanceCharts/V1_V2/containsAll.png)
![equals.png](PerformanceCharts/V1_V2/equals.png)
![hashCode.png](PerformanceCharts/V1_V2/hashCode.png)
![isEmpty.png](PerformanceCharts/V1_V2/isEmpty.png)
![iterator.png](PerformanceCharts/V1_V2/iterator.png)
![remove.png](PerformanceCharts/V1_V2/remove.png)
![removeAll.png](PerformanceCharts/V1_V2/removeAll.png)
![retainAll.png](PerformanceCharts/V1_V2/retainAll.png)
![size.png](PerformanceCharts/V1_V2/size.png)
![spliterator.png](PerformanceCharts/V1_V2/spliterator.png)
![toArray.png](PerformanceCharts/V1_V2/toArray.png)
![toArray_T.png](PerformanceCharts/V1_V2/toArray_T.png)
![toString.png](PerformanceCharts/V1_V2/toString.png)

### V1 vs JDK

![constructor.png](PerformanceCharts/V1_JDK/constructor.png)
![constructor_collection.png](PerformanceCharts/V1_JDK/constructor_collection.png)
![constructor_int.png](PerformanceCharts/V1_JDK/constructor_int.png)
![constructor_int_float.png](PerformanceCharts/V1_JDK/constructor_int_float.png)
![add.png](PerformanceCharts/V1_JDK/add.png)
![addAll.png](PerformanceCharts/V1_JDK/addAll.png)
![clear.png](PerformanceCharts/V1_JDK/clear.png)
![clone.png](PerformanceCharts/V1_JDK/clone.png)
![contains.png](PerformanceCharts/V1_JDK/contains.png)
![containsAll.png](PerformanceCharts/V1_JDK/containsAll.png)
![equals.png](PerformanceCharts/V1_JDK/equals.png)
![hashCode.png](PerformanceCharts/V1_JDK/hashCode.png)
![isEmpty.png](PerformanceCharts/V1_JDK/isEmpty.png)
![iterator.png](PerformanceCharts/V1_JDK/iterator.png)
![remove.png](PerformanceCharts/V1_JDK/remove.png)
![removeAll.png](PerformanceCharts/V1_JDK/removeAll.png)
![retainAll.png](PerformanceCharts/V1_JDK/retainAll.png)
![size.png](PerformanceCharts/V1_JDK/size.png)
![spliterator.png](PerformanceCharts/V1_JDK/spliterator.png)
![toArray.png](PerformanceCharts/V1_JDK/toArray.png)
![toArray_T.png](PerformanceCharts/V1_JDK/toArray_T.png)
![toString.png](PerformanceCharts/V1_JDK/toString.png)