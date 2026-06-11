# Custom Linked Hashset

Implementation of a LinkedHashSet.

All methods implemented are identical to those found in the Java Set interface.

# Build and Test

To build and test the project run command ./gradlew clean build

# Time Complexity

| Method                                | CustomLinkedHashSet  | LinkedHashSet (JDK)  |       Winner       |
|:--------------------------------------|:--------------------:|:--------------------:|:------------------:|
| **`add(E)`**                          |        $O(1)$        |        $O(1)$        |      **Tie**       |
| **`addAll(Collection<? extends E>)`** |        $O(M)$        |        $O(M)$        |      **Tie**       |
| **`clear()`**                         |        $O(N)$        |        $O(N)$        |      **Tie**       |
| **`clone()`**                         |        $O(N)$        |        $O(N)$        |      **Tie**       |
| **`contains(Object)`**                |        $O(1)$        |        $O(1)$        |      **Tie**       |
| **`containsAll(Collection<?>)`**      |        $O(M)$        |        $O(M)$        |      **Tie**       |
| **`equals(Object)`**                  |        $O(N)$        |        $O(N)$        |      **Tie**       |
| **`hashCode()`**                      |        $O(N)$        |        $O(N)$        |      **Tie**       |
| **`isEmpty()`**                       |        $O(1)$        |        $O(1)$        |      **Tie**       |
| **`iterator()`**                      |        $O(1)$        |        $O(1)$        |      **Tie**       |
| **`remove(Object)`**                  |        $O(1)$        |        $O(1)$        |      **Tie**       |
| **`removeAll(Collection<?>)`**        |   $O(N \times M)$    |      $O(N + M)$      | **LinkedHashSet**  |
| **`retainAll(Collection<?>)`**        |   $O(N \times M)$    |   $O(N \times M)$    |      **Tie**       |
| **`size()`**                          |        $O(1)$        |        $O(1)$        |      **Tie**       |
| **`spliterator()`**                   |        $O(1)$        |        $O(1)$        |      **Tie**       |
| **`toArray()`**                       |        $O(N)$        |        $O(N)$        |      **Tie**       |
| **`toArray(T[])`**                    |        $O(N)$        |        $O(N)$        |      **Tie**       |
| **`toString()`**                      |        $O(N)$        |        $O(N)$        |      **Tie**       |

# Space Complexity

| Method                                | CustomLinkedHashSet  | LinkedHashSet (JDK)  |  Winner  |
|:--------------------------------------|:--------------------:|:--------------------:|:--------:|
| **`add(E)`**                          |        $O(1)$        |        $O(1)$        | **Tie**  |
| **`addAll(Collection<? extends E>)`** |        $O(1)$        |        $O(1)$        | **Tie**  |
| **`clear()`**                         |        $O(1)$        |        $O(1)$        | **Tie**  |
| **`clone()`**                         |        $O(N)$        |        $O(N)$        | **Tie**  |
| **`contains(Object)`**                |        $O(1)$        |        $O(1)$        | **Tie**  |
| **`containsAll(Collection<?>)`**      |        $O(1)$        |        $O(1)$        | **Tie**  |
| **`equals(Object)`**                  |        $O(1)$        |        $O(1)$        | **Tie**  |
| **`hashCode()`**                      |        $O(1)$        |        $O(1)$        | **Tie**  |
| **`isEmpty()`**                       |        $O(1)$        |        $O(1)$        | **Tie**  |
| **`iterator()`**                      |        $O(1)$        |        $O(1)$        | **Tie**  |
| **`remove(Object)`**                  |        $O(1)$        |        $O(1)$        | **Tie**  |
| **`removeAll(Collection<?>)`**        |        $O(1)$        |        $O(1)$        | **Tie**  |
| **`retainAll(Collection<?>)`**        |        $O(1)$        |        $O(1)$        | **Tie**  |
| **`size()`**                          |        $O(1)$        |        $O(1)$        | **Tie**  |
| **`spliterator()`**                   |        $O(1)$        |        $O(1)$        | **Tie**  |
| **`toArray()`**                       |        $O(N)$        |        $O(N)$        | **Tie**  |
| **`toArray(T[])`**                    |        $O(N)$        |        $O(N)$        | **Tie**  |
| **`toString()`**                      |        $O(N)$        |        $O(N)$        | **Tie**  |

**Notes**:
- n: Total number of elements currently stored within the set.
- m: Number of elements in the incoming collection argument.

# Performance Charts

![add(E).png](PerformanceCharts/plot_add_E_.png)
![addAll(Collection).png](PerformanceCharts/plot_addAll_CollectionanyextendsE_.png)
![clear().png](PerformanceCharts/plot_clear__.png)
![clone().png](PerformanceCharts/plot_clone__.png)
![contains(Object).png](PerformanceCharts/plot_contains_Object_.png)
![containsAll(Collection](PerformanceCharts/plot_containsAll_Collection_.png)
![equals(Object).png](PerformanceCharts/plot_equals_Object_.png)
![hashCode().png](PerformanceCharts/plot_hashCode__.png)
![isEmpty().png](PerformanceCharts/plot_isEmpty__.png)
![iterator().png](PerformanceCharts/plot_iterator__.png)
![remove(Object).png](PerformanceCharts/plot_remove_Object_.png)
![removeAll(Collection).png](PerformanceCharts/plot_removeAll_Collection_.png)
![retainAll(Collection).png](PerformanceCharts/plot_retainAll_Collection_.png)
![size(Collection).png](PerformanceCharts/plot_size__.png)
![spliterator(Collection).png](PerformanceCharts/plot_spliterator__.png)
![toArray(Collection).png](PerformanceCharts/plot_toArray__.png)
![toArray(T[])(Collection).png](PerformanceCharts/plot_toArray_T[]_.png)
![toString(Collection).png](PerformanceCharts/plot_toString__.png)