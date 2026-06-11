import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("AssertBetweenInconvertibleTypes")
class CustomLinkedHashSetTest {

    @Test
    public void onCreatingSetWitNegativeSize_and_loadFactorOf_50_throws_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new CustomLinkedHashSet<>(-10, 0.5f));
    }

    @Test
    public void onCreatingSetWithSize_10_and_loadFactorOf_zero_throws_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new CustomLinkedHashSet<>(10, 0));
    }

    @Test
    public void onCreatingSetWithSize_10_and_loadFactorOf_zero_point_five_constructsCorrectly() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>(10, 0.5f);
        assertTrue(customSet.isEmpty());
    }

    @Test
    public void onCreatingSetWithNegativeSize_and_negativeLoadFactor_throws_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new CustomLinkedHashSet<>(-10, -10));
    }

    @Test
    public void onCreatingSetWithSizeOf_50_and_negativeLoadFactor_throws_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new CustomLinkedHashSet<>(50, -10));
    }

    @Test
    public void onConstructingSet_withSizeLessThan_0_throws_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new CustomLinkedHashSet<>(-1));
    }

    @Test
    public void onConstructingSet_withCollectionOfNull_throws_NullPointerException() {
        assertThrows(NullPointerException.class, () -> new CustomLinkedHashSet<>(null));
    }

    @Test
    public void onConstructingSet_with_10_constructsCorrectly() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>(10);
        assertTrue(customSet.isEmpty());
    }

    @Test
    public void onConstructingSet_returnsEmptySet() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>();
        assertTrue(customSet.isEmpty());
    }

    @Test
    public void onConstructingSet_withInitialCapacityOf_10_andLoadFactor_NaN_throws_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new CustomLinkedHashSet<Integer>(10, Float.NaN));
    }

    @Test
    public void onConstructingSetWithOneItem_on_isEmpty_returnsFalse() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>();
        assertTrue(customSet.add(10));
        assertTrue(customSet.contains(10));
        assertFalse(customSet.isEmpty());
        assertEquals(1, customSet.size());
    }

    @Test
    public void onConstructingSet_withCollectionOfFiveItems_sizeOf_5_inTrueOrder() {
        Collection<Integer> collection = IntStream.iterate(10, i -> i <= 50, i -> i + 10).boxed().collect(Collectors.toList());
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>(collection);
        assertEquals(5, customSet.size());
        assertTrue(customSet.contains(10));
        assertTrue(customSet.contains(20));
        assertTrue(customSet.contains(30));
        assertTrue(customSet.contains(40));
        assertTrue(customSet.contains(50));
        assertFalse(customSet.contains(100));
        checkCorrectOrder(customSet, 50);
    }

    @Test
    public void givenEmptySet_onAdd_null_returnsTrue_withSizeOf_1() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>();
        assertTrue(customSet.add(null));
        assertEquals(1, customSet.size());
    }

    @Test
    public void givenEmptySet_onAdd_null_and_onContains_null_returns_true() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>();
        assertTrue(customSet.add(null));
        assertTrue(customSet.contains(null));
        assertEquals(1, customSet.size());
    }

    @Test
    public void givenSetWithValueOf_null_onRemove_returns_true() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>();
        assertTrue(customSet.add(null));
        assertTrue(customSet.remove(null));
        assertFalse(customSet.contains(null));
        assertEquals(0, customSet.size());
    }

    @Test
    public void onAddingToSet_10_returns_true_and_sizeOf_1() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>();
        assertTrue(customSet.add(10));
        assertEquals(1, customSet.size());
    }

    @Test
    public void onAddingToSet_10_20_returns_true_and_sizeOf_2_inCorrectOrder() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>();
        customSet.add(10);
        customSet.add(20);
        assertEquals(2, customSet.size());
        checkCorrectOrder(customSet, 20);
    }

    @Test
    public void onAddingToSet_10_items_returns_true_andSizeOf_10_andInCorrectOrder() {
        CustomLinkedHashSet<Integer> customSet = createDynamicSet(10);
        assertEquals(10, customSet.size());
        checkCorrectOrder(customSet, 100);
    }

    @Test
    public void onAddingToSet_twoIdenticalNumbersToSet_onlyAddsOne() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>();
        assertTrue(customSet.add(10));
        assertEquals(1, customSet.size());
        assertFalse(customSet.add(10));
        assertEquals(1, customSet.size());
        assertEquals(10, customSet.iterator().next());
    }

    @Test
    public void onAddingToSet_50_items_returns_true_andSizeOf_50_andInCorrectOrder() {
        CustomLinkedHashSet<Integer> customSet = createDynamicSet(50);
        assertEquals(50, customSet.size());
        checkCorrectOrder(customSet, 500);
    }

    @Test
    public void onAddingToSet_twoSameValues_returns_sizeOf_1() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>();
        assertTrue(customSet.add(1));
        assertFalse(customSet.add(1));
        assertEquals(1, customSet.size());
    }

    @Test
    public void onAddingToSet_twoSameValues_andOneUnique_returns_sizeOf_2() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>(List.of(1, 1, 2));
        assertTrue(customSet.contains(1));
        assertTrue(customSet.contains(2));
        assertEquals(2, customSet.size());
    }

    @Test
    public void onAddingToSet_twoValuesOf_10_20_onRemove_30_returns_false() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>(List.of(10, 20));
        assertEquals(2, customSet.size());
        assertFalse(customSet.remove(30));
    }

    @Test
    public void onAddingToSet_twoValuesOf_10_20_onRemove_10_returns_true() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>(List.of(10, 20));
        assertEquals(2, customSet.size());
        assertTrue(customSet.remove(10));
        assertEquals(1, customSet.size());
        assertEquals(20, customSet.iterator().next());
    }

    @Test
    public void givenNonEmptySet_onRetainAllWithEmptyCollection_clearsSet() {
        CustomLinkedHashSet<Integer> customSet = createDynamicSet(5);
        Collection<Integer> empty = new ArrayList<>();
        assertTrue(customSet.retainAll(empty));
        assertTrue(customSet.isEmpty());
    }

    @Test
    public void givenSetOfValue_10_20_30_40_50_onRetainAllForCollection_20_30_returnsSetOf_20_30() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>(List.of(10, 20, 30, 40, 50));
        Collection<Integer> c = new ArrayList<>();
        c.add(20);
        c.add(30);
        assertTrue(customSet.retainAll(c));
        assertFalse(customSet.contains(10));
        assertFalse(customSet.contains(40));
        assertFalse(customSet.contains(50));
        assertTrue(customSet.contains(20));
        assertTrue(customSet.contains(30));
        assertEquals(2, customSet.size());
    }

    @Test
    public void givenSetOfValue_10_20_30_40_50_onRetailAllForCollectionContainingValueThatDoesNotExist_returnsFalse() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>(List.of(10, 20, 30, 40, 50));
        Collection<Integer> c = new ArrayList<>();
        c.add(60);
        assertTrue(customSet.retainAll(c));
        assertEquals(0, customSet.size());
    }

    @Test
    public void givenSetOfValue_10_20_30_40_50_onRetailAllForCollection_20_30_60_returnsSetOf_20_30() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>(List.of(10, 20, 30, 40, 50));
        Collection<Integer> c = new ArrayList<>();
        c.add(20);
        c.add(30);
        c.add(60);
        assertTrue(customSet.retainAll(c));
        assertFalse(customSet.contains(10));
        assertFalse(customSet.contains(40));
        assertFalse(customSet.contains(50));
        assertTrue(customSet.contains(20));
        assertTrue(customSet.contains(30));
        assertEquals(2, customSet.size());
    }

    @Test
    public void givenSetOfValue_10_20_30_onContainsAllForCollection_20_30_40_returns_false() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>(List.of(10, 20, 30));
        Collection<Integer> c = new ArrayList<>();
        c.add(20);
        c.add(30);
        c.add(40);
        assertFalse(customSet.containsAll(c));
    }

    @Test
    public void givenSetOfValue_10_20_30_40_50_onRemoveAllForCollection_20_30_returnsCollectionOf_10_40_50() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>(List.of(10, 20, 30, 40, 50));
        Collection<Integer> c = new ArrayList<>();
        c.add(20);
        c.add(30);
        assertTrue(customSet.removeAll(c));
        assertTrue(customSet.contains(10));
        assertFalse(customSet.contains(20));
        assertFalse(customSet.contains(30));
        assertTrue(customSet.contains(40));
        assertTrue(customSet.contains(50));
    }

    @Test
    public void givenSetOfValue_10_20_30_onContainsAllForCollection_20_30_returns_true() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>(List.of(10, 20, 30));
        Collection<Integer> c = new ArrayList<>();
        c.add(20);
        c.add(30);
        assertTrue(customSet.containsAll(c));
    }

    @Test
    public void givenSetOfValue_10_20_30_onAddAllForCollectionOf_10_20_returns_false() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>(List.of(10, 20, 30));
        Collection<Integer> c = new ArrayList<>();
        c.add(10);
        c.add(20);
        assertFalse(customSet.addAll(c));
    }

    @Test
    public void givenSetOfValue_10_20_30_onAddAllForCollectionOf_40_50_returns_true() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>(List.of(10, 20, 30));
        Collection<Integer> c = new ArrayList<>();
        c.add(40);
        c.add(50);
        assertTrue(customSet.addAll(c));
    }

    @Test
    public void onAddingToSet_50_items_andClearingSet_returns_newSet() {
        CustomLinkedHashSet<Integer> customSet = createDynamicSet(50);
        assertEquals(50, customSet.size());
        customSet.clear();
        assertTrue(customSet.isEmpty());
    }

    @Test
    public void onConstructingEmptySet_returnsEmptyCurlyBracket_on_toString() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>();
        assertEquals("[]", customSet.toString());
    }

    @Test
    public void onConstructingSet_withCollectionOfFiveItems_returnsCorrect_toString_inOrderOfEntry() {
        Collection<Integer> collection = IntStream.iterate(0, i -> i <= 50, i -> i + 10).boxed().collect(Collectors.toList());
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>(collection);
        String setAsString = customSet.toString();
        assertEquals("[0, 10, 20, 30, 40, 50]", setAsString);
    }

    @Test
    public void givenEmptySet_onToArray_returns_emptyArray() {
        CustomLinkedHashSet<String> customSet = new CustomLinkedHashSet<>();
        Object[] values = customSet.toArray();
        Object[] expected = new Object[] { };
        assertEquals(0, values.length);
        assertArrayEquals(expected, values);
    }

    @Test
    public void onConstructingSet_withCollectionOfThreeItems_returnsCorrectArray_andInOrderOfInsertion() {
        CustomLinkedHashSet<String> customSet = new CustomLinkedHashSet<>(List.of("10", "20", "30"));
        Object[] values = customSet.toArray();
        Object[] expected = new Object[] { "10", "20", "30" };
        assertEquals(3, values.length);
        assertArrayEquals(expected, values);
    }

    @Test
    public void onAdding_1_000_000_values_causesNoIndexOverwritingIssues_andAdds_1_000_000_values() {
        CustomLinkedHashSet<Long> customSet = new CustomLinkedHashSet<>();
        LongStream.range(0, 1_000_000).forEach(customSet::add);
        assertEquals(1_000_000, customSet.size());
    }

    @Test
    public void onAdding_2_000_000_values_causesNoIndexOverwritingIssues_andAdds_2_000_000_values() {
        CustomLinkedHashSet<Long> customSet = new CustomLinkedHashSet<>();
        LongStream.range(0, 2_000_000).forEach(customSet::add);
        assertEquals(2_000_000, customSet.size());
    }

    @Test
    public void onAdding_3_000_000_values_causesNoIndexOverwritingIssues_andAdds_3_000_000_values() {
        CustomLinkedHashSet<Long> customSet = new CustomLinkedHashSet<>();
        LongStream.range(0, 3_000_000).forEach(customSet::add);
        assertEquals(3_000_000, customSet.size());
    }

    @Test
    public void givenSetOfThreeItems_iterator_iteratesOverAllElements_andReturnsSizeOf_3() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>();
        customSet.add(1);
        customSet.add(2);
        customSet.add(3);

        int count = 0;
        Iterator<Integer> iterator = customSet.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }

        assertEquals(3, count);
    }

    @Test
    public void givenSetOfThreeItems_iterator_remove_removesCurrentElement() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>();
        customSet.add(1);
        customSet.add(2);
        customSet.add(3);

        Iterator<Integer> iterator = customSet.iterator();
        while (iterator.hasNext()) {
            Integer value = iterator.next();
            if (value.equals(2)) {
                iterator.remove();
            }
        }

        assertFalse(customSet.contains(2));
        iterator = customSet.iterator();
        assertEquals(1, iterator.next());
        assertEquals(3, iterator.next());
        assertEquals(2, customSet.size());
    }

    @Test
    public void givenSetOf_100_items_spliterator_estimateSize_matchesSetSize() {
        CustomLinkedHashSet<Integer> customSet = createDynamicSet(100);
        assertEquals(100, customSet.spliterator().estimateSize());
    }

    @Test
    public void givenSameSet_onEquals_returns_true() {
        CustomLinkedHashSet<Integer> a = new CustomLinkedHashSet<>(List.of(1, 2));
        assertEquals(a, a);
    }

    @Test
    public void givenSetsOfDifferentSizes_onEquals_returns_false() {
        CustomLinkedHashSet<Integer> a = new CustomLinkedHashSet<>(List.of(1, 2));
        CustomLinkedHashSet<Integer> b = new CustomLinkedHashSet<>(List.of(1, 2, 3));
        assertNotEquals(a, b);
    }

    @Test
    public void givenTwoIdenticalSet_onEquals_returnsTrue() {
        CustomLinkedHashSet<Integer> a = new CustomLinkedHashSet<>();
        a.add(1);
        a.add(2);
        CustomLinkedHashSet<Integer> b = new CustomLinkedHashSet<>();
        b.add(1);
        b.add(2);
        assertEquals(a, b);
    }

    @Test
    public void givenTwoDifferentSets_onEquals_returnsFalse() {
        CustomLinkedHashSet<Integer> a = new CustomLinkedHashSet<>();
        a.add(1);
        a.add(2);
        CustomLinkedHashSet<Integer> b = new CustomLinkedHashSet<>();
        b.add(1);
        b.add(3);
        assertNotEquals(a, b);
    }

    @Test
    public void givenCustomSetOf_1_2_3_onEqualsNonMatchingObject_returns_false() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>(List.of(1, 2, 3));
        assertNotEquals(customSet, new ArrayList<>());
    }

    @Test
    public void givenTwoIdenticalSets_hashCode_IsTheSame() {
        CustomLinkedHashSet<Integer> a = new CustomLinkedHashSet<>();
        a.add(1);
        a.add(2);
        CustomLinkedHashSet<Integer> b = new CustomLinkedHashSet<>();
        b.add(1);
        b.add(2);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void givenTwoDifferentSets_hashCode_IsDifferent() {
        CustomLinkedHashSet<Integer> a = new CustomLinkedHashSet<>();
        a.add(1);
        a.add(2);
        CustomLinkedHashSet<Integer> b = new CustomLinkedHashSet<>();
        b.add(1);
        b.add(3);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void givenSetOfType_Integer_withValues_1_2_3_onToArray_withIntArrayParameter_returnsCorrectArray() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>();
        customSet.add(1);
        customSet.add(2);
        customSet.add(3);
        assertArrayEquals(new Integer[] {1, 2, 3}, customSet.toArray(new Integer[0]));
    }

    @Test
    public void givenSetOfType_Integer_withValues_1_2_3_onToArray_withIntArrayParameterP_withSize_2_returnsCorrectArray() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>();
        customSet.add(1);
        customSet.add(2);
        customSet.add(3);
        assertArrayEquals(new Integer[] {1, 2, 3}, customSet.toArray(new Integer[2]));
    }

    @Test
    public void givenSetOfType_Integer_withValues_1_2_3_onToArray_withIntArrayParameterP_withSize_5_returnsCorrectArray() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>();
        customSet.add(1);
        customSet.add(2);
        customSet.add(3);
        assertArrayEquals(new Integer[] {1, 2, 3, null, null}, customSet.toArray(new Integer[5]));
    }

    @Test
    public void givenSetOfType_Integer_withValues_1_2_3_onClone_returnsEqualSets() {
        CustomLinkedHashSet<Integer> customSet = new CustomLinkedHashSet<>();
        customSet.add(1);
        customSet.add(2);
        customSet.add(3);
        CustomLinkedHashSet<Integer> clone = customSet.clone();
        assertEquals(clone, customSet);
    }

    private void checkCorrectOrder(CustomLinkedHashSet<Integer> customSet, int maxNumber) {
        Iterator<Integer> iterator = customSet.iterator();
        for(int i = 10; i <= maxNumber; i+= 10)
            assertEquals(i, iterator.next());
        assertFalse(iterator.hasNext());
    }

    private CustomLinkedHashSet<Integer> createDynamicSet(int entries) {
        CustomLinkedHashSet<Integer> customLinkedHashSet = new CustomLinkedHashSet<>();
        IntStream.rangeClosed(1, entries).mapToObj(i -> i * 10).forEach(customLinkedHashSet::add);
        return customLinkedHashSet;
    }
}