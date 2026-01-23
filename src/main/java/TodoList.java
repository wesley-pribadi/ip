public class TodoList {
    private TodoListItem[] items;
    private int size;

    public TodoList() {
        int LIST_SIZE = 100;
        this.items = new TodoListItem[LIST_SIZE];
        this.size = 0;
    }

    public void add(String description) {
        if (size < items.length) {
            items[size] = new TodoListItem(description);
            size++;
            System.out.println("added: " + description);
        } else {
            throw new IllegalStateException("Array is full");
        }
    }

    public void mark(int index) {
        if (index >= 0 && index < size) {
            items[index].markDone();
            System.out.println("Nice! I've marked this task as done:");
            System.out.println(items[index].toString());
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " not in list of size " + size);
        }
    }

    public void unmark(int index) {
        if (index >= 0 && index < size) {
            items[index].markUndone();
            System.out.println("OK, I've marked this task as not done yet:");
            System.out.println(items[index].toString());
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " not in list of size " + size);
        }
    }

    public void read() {
        for (int i = 0; i < size; i++) {
            System.out.print((i+1) + ". ");
            System.out.println(items[i].toString());
        }
    }

    public int size() {
        return size;
    }
}
