import java.util.NoSuchElementException;

public class W04_MyQueue<E> {
    private final W04_MyArrayList<E> a
        = new W04_MyArrayList<>();

    public void enqueue(E e) {
        if (e == null)
            throw new NullPointerException("null not allowed");
            a.add(e);
    }

    public E dequeue() {
        if (a.isEmpty())
            throw new NoSuchElementException("queue empty");
        return a.remove(0);
    }

    public E front() {
        if (a.isEmpty())
            throw new NoSuchElementException("queue empty");
            return a.get(0);
    }

    public int size() { return a.size(); }
    public boolean isEmpty() { return a.isEmpty(); }

    //데모 실행용 main
    public static void main(String[] args) {
        W04_MyQueue<Integer> q = new W04_MyQueue<>();
        for (int i = 1; i <= 10; i++) q.enqueue(i);
        while (!q.isEmpty()) 
            System.out.print(q.dequeue() + " ");
        System.out.println();    
    }
}

