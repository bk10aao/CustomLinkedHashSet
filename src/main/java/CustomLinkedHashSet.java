import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;

/**
 * A specialized hash table and doubly-linked list-based implementation of the {@link Set} interface.
 * <p>This implementation delegates all structural storage and indexing behavior to an internal
 * {@link LinkedHashMap} instance. By leveraging the backing map's key-set mechanics, it inherits
 * predictable iteration order matching the insertion history of the elements.
 * <p>This class permits the insertion of all elements supported by the underlying map layout,
 * including {@code null}. Basic single-element operations ({@code add}, {@code remove}, and
 * {@code contains}) provide constant-time performance on average, assuming the hash function
 * disperses elements uniformly across the backing buckets.
 * <p>This class implements optional {@link Set} capabilities, including object cloning via
 * a shallow copy pattern, and exposes fail-fast iteration structures.
 * <p><b>Note:</b> This implementation is not synchronized.
 *
 * @param <E> the type of elements maintained by this set
 * @author Benjamin Kane
 * @see <a href="https://www.linkedin.com/in/benjamin-kane-81149482/">LinkedIn</a>
 * @see <a href="https://github.com/bk10aao">GitHub account bk10aao</a>
 * @see <a href="https://github.com/bk10aao/CustomLinkedHashSet">Repository</a>
 */
public class CustomLinkedHashSet<E> implements Set<E>, Cloneable {

    private volatile LinkedHashMap<E, Object> set;
    private static final Object PRESENT = new Object();

    /**
     * Constructs a new, empty custom linked hash set with the default initial
     * capacity (16) and load factor (0.75).
     */
    public CustomLinkedHashSet() {
        this.set = new LinkedHashMap<>();
    }

    /**
     * Constructs a new custom linked hash set containing the elements of the
     * specified collection. The underlying storage is initialized with a capacity
     * mathematically configured to accommodate the elements inside the incoming
     * collection without triggering immediate rehashing under the default
     * load factor (0.75).
     *
     * @param c - the collection whose elements are to be placed into
     * this custom set
     *
     * @throws NullPointerException if the specified collection is null
     */
    public CustomLinkedHashSet(final Collection<E> c) {
        Objects.requireNonNull(c);
        set = new LinkedHashMap<>(Math.max((int) (c.size() / .75f) + 1, 16));
        for(E e: c)
            set.put(e, PRESENT);
    }

    /**
     * Constructs a new, empty linked hash set with the specified initial
     * capacity and the default load factor (0.75).
     *
     * @apiNote
     * To create a {@code CustomLinkedHashSet} with an initial capacity that accommodates
     * an expected number of elements.
     *
     * @param initialCapacity - the initial capacity of the custom linked hash set
     *
     * @throws IllegalArgumentException if the initial capacity is less than zero
     */
    public CustomLinkedHashSet(final int initialCapacity) {
        if(initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        set = new LinkedHashMap<>(initialCapacity);
    }

    /**
     * Constructs a new, empty linked hash set with the specified initial
     * capacity and load factor.
     *
     * @apiNote
     * To create a {@code CustomLinkedHashSet} with a default load factor that accommodates
     * an expected number of elements, use {@link #CustomLinkedHashSet(int)}.
     *
     * @param initialCapacity - the initial capacity of the linked hash set
     * @param loadFactor - the load factor of the linked hash set
     *
     * @throws IllegalArgumentException  if the initial capacity is less
     * than zero, or if the load factor is non-positive
     */
    public CustomLinkedHashSet(final int initialCapacity, final float loadFactor) {
        if(initialCapacity < 0 || loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException();
        set = new LinkedHashMap<>(initialCapacity, loadFactor);
    }

    /**
     * Adds the specified element to this set if it is not already present.
     * More formally, adds the specified element {@code e} to this set if the set
     * does not already contain an element {@code existingElement} such that
     * {@code Objects.equals(e, existingElement)}.
     * If this set already contains the element, the call leaves the set unchanged
     * and returns {@code false}.
     *
     * @param e - element to be added to this set
     *
     * @return {@code true} if this set did not already contain the specified element
     */
    public boolean add(final E e) {
        return set.put(e, PRESENT) == null;
    }

    /**
     * Adds all the elements in the specified collection to this set if they
     * are not already present.
     * <p>The iteration order of the specified collection is followed sequentially.
     * If the specified collection contains duplicate elements, only the first
     * unique occurrence is preserved, while subsequent identical elements are
     * short-circuited by the underlying insertion logic.
     *
     * @param c - collection containing elements to be added to this set
     *
     * @return {@code true} if this set changed as a result of the call
     *
     * @throws NullPointerException if the specified collection is null
     */
    public boolean addAll(final Collection<? extends E> c) {
        if(c.isEmpty())
            return false;
        if(this.isEmpty())
            this.set = new LinkedHashMap<>(Math.max((int) (c.size() / .75f) + 1, 16));
        boolean modified = false;
        for(E e : c)
            if(add(e))
                modified = true;
        return modified;
    }

    /**
     * Removes all the elements from this set. The set will be empty after
     * this call returns.
     * <p>This method delegates directly to the underlying {@code LinkedHashMap},
     * which clears all internal bucket arrays and untethers all references within
     * the sequential doubly-linked chain. This releases references to stored
     * elements, allowing them to be eligible for garbage collection.
     *
     * @see Collection#clear()
     */
    public void clear() {
        set.clear();
    }

    /**
     * Returns a shallow copy of this {@code CustomLinkedHashSet} instance.
     * <p>The elements themselves are not cloned; only the internal reference structures
     * are duplicated. This method instantiates a completely separate, empty
     * {@code CustomLinkedHashSet} shell and populates its internal {@code LinkedHashMap}
     * tracking configuration using a batch insertion copy. This structural duplication
     * ensures that mutations to the composition of the cloned set will not alter the original,
     * while maintaining identical relative insertion iteration ordering.
     *
     * @return a shallow copy of this set instance
     *
     * @see Cloneable
     * @see Object#clone()
     */
    @Override
    public CustomLinkedHashSet<E> clone() {
        try {
            CustomLinkedHashSet<E> clonedSet = (CustomLinkedHashSet<E>) super.clone();
            clonedSet.set = new LinkedHashMap<>(this.set);
            return clonedSet;
        } catch(CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    /**
     * Returns {@code true} if this set contains the specified element.
     * <p>This method leverages the constant-time lookup performance of the backing
     * {@code LinkedHashMap}, verifying membership directly via key-set containment
     * checking without needing to traverse the sequential doubly-linked chain.
     *
     * @param o - element whose presence in this set is to be tested
     *
     * @return {@code true} if this set contains the specified element
     */
    public boolean contains(Object o) {
        return set.containsKey(o);
    }

    /**
     * Returns {@code true} if this set contains all the elements in the
     * specified collection.
     * <p>This method explicitly validates the argument and sequentially checks each
     * element in the incoming collection against this set. It provides a
     * short-circuiting exit mechanism; the traversal terminates immediately and
     * returns {@code false} upon encountering the first element that is missing
     * from this set.
     *
     * @param c - collection containing elements to be checked for containment in this set
     *
     * @return {@code true} if this set contains all the elements in the specified collection
     *
     * @throws NullPointerException if the specified collection is null
     *
     * @see #contains(Object)
     */
    public boolean containsAll(Collection<?> c) {
        Objects.requireNonNull(c);
        for(Object o : c)
            if(!contains(o))
                return false;
        return true;
    }

    /**
     * Compares the specified object with this set for equality. Returns
     * {@code true} if the given object is also a set, the two sets have the
     * same size, and every member of the given set is contained in this set.
     * <p>This implementation checks structural identities sequentially:
     * <ol>
     * <li>Verifies if the target object is the identical memory reference (reflexivity short-circuit).</li>
     * <li>Validates type conformance using an {@code instanceof} check against the {@link Set} interface.</li>
     * <li>Compares the sizing constraints to short-circuit completely if element totals mismatch.</li>
     * <li>Performs a deep logical membership scan via {@link #containsAll(Collection)}.</li>
     * </ol>
     * <p>The membership evaluation step is wrapped within a defensive {@code try-catch} block
     * to handle situations where the incoming set contains incompatible types or un-hashable
     * references, safely falling back to returning {@code false} instead of crashing at runtime.
     *
     * @param o - object to be compared for equality with this set
     *
     * @return {@code true} if the specified object is equal to this set
     *
     * @see Set#equals(Object)
     * @see #containsAll(Collection)
     */
    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;
        if(!(o instanceof Set<?> other))
            return false;
        if(other.size() != size())
            return false;
        try {
            return containsAll(other);
        } catch(ClassCastException | NullPointerException unused) {
            return false;
        }
    }

    /**
     * Returns the hash code value for this set. The hash code of a set is defined
     * to be the sum of the hash codes of the elements in the set, where the hash
     * code of a {@code null} element is defined to be zero.
     * <p>This implementation iterates through the keys of the backing {@code LinkedHashMap}
     * and sequentially accumulates their individual hash values. It includes an explicit
     * {@code null} safety check to prevent a {@link NullPointerException} if the set contains
     * a {@code null} element, skipping its contribution to the sum (effectively treating it
     * as zero).
     * <p>This calculation ensures compliance with the general contract for the
     * {@link Object#hashCode()} method, guaranteeing that if {@code set1.equals(set2)}
     * evaluates to {@code true}, then {@code set1.hashCode() == set2.hashCode()} will
     * also hold true, regardless of the order in which elements were inserted.
     *
     * @return the hash code value for this set
     *
     * @see Object#equals(Object)
     * @see #equals(Object)
     * @see Set#hashCode()
     */
    @Override
    public int hashCode() {
        int h = 0;
        for(E e : set.keySet())
            if(e != null)
                h += e.hashCode();
        return h;
    }

    /**
     * Returns {@code true} if this set contains no elements.
     * <p>This method delegates directly to the underlying {@code LinkedHashMap},
     * evaluating whether the internal table structure is entirely unpopulated.
     * Because it performs a structural boundary check without iterating through
     * individual buckets or nodes, this operation runs in constant time $O(1)$.
     *
     * @return {@code true} if this set contains no elements
     * @see Collection#isEmpty()
     */
    public boolean isEmpty() {
        return set.isEmpty();
    }

    /**
     * Returns an iterator over the elements in this set.
     * <p>The elements are returned in the exact order in which they were inserted
     * into the set (insertion-order). This method delegates directly to the key-set
     * iterator of the backing {@code LinkedHashMap}.
     * <p>The returned iterator is <i>fail-fast</i>: if the backing map is structurally
     * modified at any time after the iterator is created, in any way except through
     * the iterators own {@code remove} method, the iterator will throw a
     * {@link java.util.ConcurrentModificationException}. Thus, in the face of concurrent
     * modification, the iterator fails quickly and cleanly, rather than risking
     * arbitrary, non-deterministic behavior at an undetermined time in the future.
     *
     * @return an {@code Iterator} over the elements in this set in insertion-order
     *
     * @see ConcurrentModificationException
     * @see Iterator
     */
    public Iterator<E> iterator() {
        return set.keySet().iterator();
    }

    /**
     * Removes the specified element from this set if it is present.
     * More formally, removes an element {@code e} such that {@code Objects.equals(o, e)},
     * if this set contains such an element. Returns {@code true} if this set
     * contained the element (or equivalently, if this set changed as a result
     * of the call). This set will not contain the element once the call returns.
     *
     * <p>This operation delegates directly to the underlying {@code LinkedHashMap}'s
     * removal logic. When a match is found, the mapping key is purged from the hash
     * table array and unlinked from the internal doubly-linked chain, adjusting
     * neighboring traversal pointers smoothly in $O(1)$ constant-time on average.
     *
     * @param o - object to be removed from this set, if present
     *
     * @return {@code true} if the set contained the specified element
     */
    public boolean remove(Object o) {
        return set.remove(o) != null;
    }

    /**
     * Removes from this set all of its elements that are contained in the
     * specified collection.
     * <p>This method explicitly validates the argument and then delegates mutation
     * directly to the backing {@code LinkedHashMap}'s key set. All matching elements
     * are unlinked from the internal hash table buckets and their corresponding
     * references are dropped from the doubly-linked list, shifting trailing element
     * iteration pointers accordingly.
     *
     * @param c - collection containing elements to be removed from this set
     *
     * @return {@code true} if this set changed as a result of the call
     *
     * @throws NullPointerException if the specified collection is null
     *
     * @see Collection#removeAll(Collection)
     */
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return set.keySet().removeAll(c);
    }

    /**
     * Retains only the elements in this set that are contained in the specified
     * collection. In other words, removes from this set all of its elements that
     * are not contained in the specified collection.
     * <p>This method leverages the backing {@code LinkedHashMap}'s key-set mutation,
     * which ensures that elements not found in the incoming collection are unlinked
     * from both the hash table buckets and the internal doubly-linked list,
     * correctly preserving the original insertion order of the remaining items.
     *
     * @param c - collection containing elements to be retained in this set
     *
     * @return {@code true} if this set changed as a result of the call
     *
     * @throws NullPointerException if the specified collection is null
     *
     * @see Collection#retainAll(Collection)
     */
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return set.keySet().retainAll(c);
    }

    /**
     * Returns the number of elements in this set (its cardinality).
     * <p>This method delegates directly to the underlying {@code LinkedHashMap},
     * retrieving the recorded count of keys currently mapped within the collection.
     * Because the internal map explicitly tracks its size as elements are added
     * and removed, this operation runs in constant time $O(1)$ without needing
     * to traverse or count individual items.
     *
     * @return the number of elements in this set (its cardinality)
     *
     * @see Collection#size()
     */
    public int size() {
        return set.size();
    }

    /**
     * Returns a string representation of this set. The string representation
     * consists of a list of the set's elements in the order they are returned
     * by its iterator, enclosed in square brackets ({@code "[]"}). Adjacent elements
     * are separated by the characters {@code ", "} (a comma and a space).
     * <p>This implementation features a short-circuit boundary check that instantly
     * returns {@code "[]"} if the set is empty. Otherwise, it utilizes a
     * {@link StringBuilder} to sequentially append each element's string equivalent
     * (via {@link String#valueOf(Object)}).
     * <p>Because this method relies directly on the set's insertion-ordered iterator,
     * the resulting string accurately mirrors the structural historical sequence of
     * the elements.
     *
     * @return a string representation of this set
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        if(isEmpty())
            return "[]";
        StringBuilder stringBuilder = new StringBuilder("[");
        Iterator<E> iterator = iterator();
        while(iterator.hasNext()) {
            stringBuilder.append(iterator.next());
            if(iterator.hasNext())
                stringBuilder.append(", ");
        }
        return stringBuilder + "]";
    }

    /**
     * Creates a {@link Spliterator} over the elements in this set.
     * <p>The returned spliterator inherits its characteristics directly from the
     * backing {@code LinkedHashMap}'s key set. It is late-binding, fail-fast,
     * and reports the following characteristics:
     * <ul>
     * <li>{@link Spliterator#DISTINCT}: Indicates that all elements encountered are unique.</li>
     * <li>{@link Spliterator#ORDERED}: Indicates that elements are encountered in a
     * well-defined sequence (matching the insertion order of the set).</li>
     * </ul>
     *
     * @return a {@code Spliterator} over the elements in this set
     *
     * @see Spliterator
     *
     * @since 1.8
     */
    public Spliterator<E> spliterator() {
        return set.keySet().spliterator();
    }

    /**
     * Returns an array containing all the elements in this set.
     * <p>The elements in the returned array are arranged in the exact order in
     * which they are returned by this set's iterator (insertion-order).
     * <p>The returned array is "safe" in that no references to it are maintained
     * by this set. (In other words, this method allocates a completely new array).
     * The caller is thus free to modify the returned array without affecting the
     * internal composition of this set.
     * <p>This method delegates directly to the key-set conversion logic of the
     * backing {@code LinkedHashMap}.
     *
     * @return an array containing all the elements in this set
     *
     * @see Collection#toArray()
     */
    public Object[] toArray() {
        return set.keySet().toArray();
    }

    /**
     * Returns an array containing all the elements in this set; the runtime
     * type of the returned array is that of the specified array. If the set fits
     * in the specified array, it is returned therein. Otherwise, a new array is
     * allocated with the runtime type of the specified array and the size of this set.
     *
     * <p>If this set fits in the specified array with room to spare (i.e., the array
     * has more elements than this set), the element in the array immediately following
     * the end of the set is set to {@code null}. (This is useful in determining the
     * length of this set <i>only</i> if the caller knows that this set does not contain
     * any null elements.)
     * * <p>Like the {@link #toArray()} method, this method preserves insertion-order
     * traversal and guarantees the safety of the returned array.
     *
     * @param <T> the component type of the array to contain the collection
     * @param a the array into which the elements of this set are to be stored, if it is
     * big enough; otherwise, a new array of the same runtime type is allocated
     * for this purpose.
     *
     * @return an array containing all the elements in this set
     *
     * @throws ArrayStoreException if the runtime type of the specified array is not a
     * supertype of the runtime type of every element in this set
     * @throws NullPointerException if the specified array is null
     *
     * @see Collection#toArray(Object[])
     */
    public <T> T[] toArray(T[] a) {
        return set.keySet().toArray(a);
    }
}
