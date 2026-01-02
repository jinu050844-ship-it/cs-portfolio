import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class W04_MyArrayList<E> implements Iterable<E> {

    //내부 상태
    private Object[] elementData;
    private int size;
    private int modCount;

    private static final int DEFAULT_CAPACITY = 10;

    //생성자
    public W04_MyArrayList() {
        this.elementData = new Object[DEFAULT_CAPACITY];
        this.size = 0;
        this.modCount = 0;
    }

    public W04_MyArrayList(int initialCapacity) {
        if (initialCapacity < 0) throw new IllegalArgumentException("capacity " + initialCapacity);
        this.elementData = new Object[Math.max(initialCapacity, DEFAULT_CAPACITY)];
        this.size = 0;
        this.modCount = 0;
    }

    //용량 관리
    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= elementData.length) return;
        int oldCap = elementData.length;
        int grown = oldCap + (oldCap >> 1);
        int newCap = Math.max(grown, minCapacity);
        elementData = Arrays.copyOf(elementData, newCap);
    }

    //기본 질의 연산
    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size ==0;
    }
    
    //접근/갱신
    @SuppressWarnings("unchecked")
    public E get(int index) {
        rangeCheck(index);
        return (E) elementData[index];
    }    

    @SuppressWarnings("unchecked")
    public E set(int index, E e) {
        rangeCheck(index);
        E old = (E) elementData[index];
        elementData[index] = e;
        return old;
    }

    //삽입
    public void add(E e) {
        ensureCapacity(size + 1);
        elementData[size++] = e;
        modCount++;
    }

    public void add(int index, E e) {
        rangeCheckForAdd(index);
        ensureCapacity(size + 1);
        System.arraycopy(elementData, index, elementData, index + 1, size - index);
        elementData[index] = e;
        size++;
        modCount++;
    }

    //삭제
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        rangeCheck(index);
        E old = (E) elementData[index];
        System.arraycopy(elementData, index + 1, elementData, index, size - index - 1);
        elementData[--size] = null;
        modCount++;
        return old;
    }

    @SuppressWarnings("unchecked")
    public E removeLast() {
        if (size == 0) throw new NoSuchElementException("empty");
        E old = (E) elementData[--size];
        elementData[size] = null;
        modCount++;
        return old;
    }

    public void clear() {
        Arrays.fill(elementData, 0, size, null);
        size = 0;
        modCount++;
    }

    //이터레이터(fail-fast)
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            int cursor = 0;
            int expected = modCount;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }
        
        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            if (expected != modCount) throw new ConcurrentModificationException();
            if (cursor >= size) throw new NoSuchElementException();
                return (E) elementData[cursor++];
        }

        @Override
        public void remove() {
                throw new UnsupportedOperationException();      
        }
    };
}

//유효성 검사/에러 메시지

    private void rangeCheck(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException(oob(index));
    }

    private void rangeCheckForAdd(int index) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException(oob(index));
    }

    private String oob(int index) {
        return "Index: " + index + ", Size: " + size;
    }

//(선택) 디버깅 도움용

@Override
public String toString() {
    Object[] visible = Arrays.copyOf(elementData, size);
    return Arrays.toString(visible);
}
}



