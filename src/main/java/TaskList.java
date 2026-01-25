public class TaskList {
    private final Task[] items;
    private final static int LIST_SIZE = 100;
    private int numOfTasks;
    protected enum markedState {
        MARKED,
        UNMARKED
    }

    public TaskList() {
        this.items = new Task[LIST_SIZE];
        this.numOfTasks = 0;
    }

    public void add(Task task) {
        if (numOfTasks < items.length) {
            items[numOfTasks] = task;
            numOfTasks++;

            System.out.println("Added:\n" + task.toString());
            System.out.println("You now have (" + numOfTasks + ") tasks.");
        } else {
            throw new IllegalStateException("Array is full");
        }
    }

    protected void setMarkedState(markedState state, int index) throws DyuqueException {
        index--;

        if (index < 0 || index > items.length) {
            throw new IndexOutOfBoundsException("Index " + index + " not in max tasklist size of " + LIST_SIZE);
        } else if (index > numOfTasks) {
            throw new DyuqueException("Task " + index + " does not exist. There are only " + numOfTasks + " tasks");
        } else {
            switch (state) {
                case MARKED:
                    items[index].markDone();
                    System.out.println("Nice! I've marked this task as done:");
                    break;
                case UNMARKED:
                    items[index].markUndone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    break;
            }
            System.out.println(items[index].toString());
        }
    }

    public void list() {
        System.out.println("You have (" + numOfTasks + ") tasks:");
        for (int i = 0; i < numOfTasks; i++) {
            System.out.println((i + 1) + ". " + items[i].toString());
        }
    }

    public int size() {
        return numOfTasks;
    }
}
