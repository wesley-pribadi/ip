public class List {
    private final int MAX_SIZE = 100;

    private String[] elements;
    private int size;

    public List() {
        this.elements = new String[MAX_SIZE];
        this.size = 0;
    }

    public boolean add(String element) {
        if (size < elements.length) {
            elements[size] = element;
            size++;
            return true;
        }
        return false; // Array full
    }

    public String get(int index) {
        if (index >= 0 && index < size) {
            return elements[index];
        }
        throw new IndexOutOfBoundsException("Index " + index + " not in list of size " + size);
    }

    public void read() {
        for (int i = 0; i < size; i++) {
            System.out.println(elements[i]);
        }
    }

    public int size() {
        return size;
    }
}
