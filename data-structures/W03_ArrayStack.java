import java.util.NoSuchElementException;

public class W03_ArrayStack implements W03_Stack {

        private final char[] a;
        private int n;

        public W03_ArrayStack() {
            this(100);
        }

        public W03_ArrayStack(int capacity) {
            if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
            a = new char[capacity];
            n = 0;
        }

        public int size() {
            return n;
        }

        public boolean isEmpty(){
            return n == 0;
        }

        public char top() {
            if (n == 0) throw new NoSuchElementException("Stack is empty");
            return a[n - 1];
        }

        public char push(char o) {
            if (n == a.length) throw new IllegalStateException("Stack overflow");
            a[n++] = o;
            return o;
        }
        
        public char pop() {
            if (n == 0) throw new NoSuchElementException("Stack is Empty");
            return a[--n];
        }
}


