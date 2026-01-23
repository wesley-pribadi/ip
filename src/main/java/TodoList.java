public class TodoList {
    private String[] elements;
    private int size;

    public TodoList() {
        int LIST_SIZE = 100;
        this.elements = new String[LIST_SIZE];
        this.size = 0;
    }

    public boolean add(String element) {
        if (size < elements.length) {
            elements[size] = element;
            size++;
            System.out.println("added: " + element);
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
            System.out.print((i+1) + ". ");
            System.out.println(elements[i]);
        }
    }

    public int size() {
        return size;
    }
}
