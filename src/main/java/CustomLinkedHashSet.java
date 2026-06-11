import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;


public class CustomLinkedHashSet<E> implements Set<E>, Cloneable {

    private final transient LinkedHashMap<E, Object> set;
    private static final Object PRESENT = new Object();

    public CustomLinkedHashSet() {
        this.set = new LinkedHashMap<>();
    }

    public CustomLinkedHashSet(final Collection<E> c) {
        Objects.requireNonNull(c);
        set = new LinkedHashMap<>(Math.max((int) (c.size() / .75f) + 1, 16));
        addAll(c);
    }

    public CustomLinkedHashSet(final int initialCapacity) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        set = new LinkedHashMap<>(initialCapacity);
    }

    public CustomLinkedHashSet(final int initialCapacity, final float loadFactor) {
        if (initialCapacity < 0 || loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException();
        set = new LinkedHashMap<>(initialCapacity, loadFactor);
    }

    public boolean add(final E e) {
        return set.put(e, PRESENT) == null;
    }

    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c) {
            if(add(e)) {
                modified = true;
            }
        }
        return modified;
    }

    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return set.keySet().retainAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return set.keySet().removeAll(c);
    }

    public boolean contains(Object o) {
        return set.containsKey(o);
    }

    public boolean containsAll(Collection<?> c) {
        Objects.requireNonNull(c);
        for(Object o : c)
            if (!contains(o))
                return false;
        return true;
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public int size() {
        return set.size();
    }

    public Iterator<E> iterator() {
        return set.keySet().iterator();
    }

    public boolean remove(Object o) {
        return set.remove(o) != null;
    }


    public void clear() {
        set.clear();
    }

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
        } catch (ClassCastException | NullPointerException unused) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (E e : set.keySet()) {
            if (e != null) {
                h += e.hashCode();
            }
        }
        return h;
    }

    @Override
    public String toString() {
        if(isEmpty())
            return "[]";
        StringBuilder stringBuilder = new StringBuilder("[");
        Iterator<E> iterator = iterator();
        while (iterator.hasNext()) {
            E e = iterator.next();
            stringBuilder.append(e);
            if (iterator.hasNext()) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder + "]";
    }

    public Spliterator<E> spliterator() {
        return set.keySet().spliterator();
    }

    public Object[] toArray() {
        return set.keySet().toArray();
    }

    public <T> T[] toArray(T[] a) {
        return set.keySet().toArray(a);
    }

    @Override
    public CustomLinkedHashSet<E> clone() {
        CustomLinkedHashSet<E> clonedSet = new CustomLinkedHashSet<>();
        clonedSet.set.putAll(this.set);
        return clonedSet;
    }
}
