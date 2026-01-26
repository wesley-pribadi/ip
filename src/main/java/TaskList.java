import java.util.ArrayList;

public class TaskList {
    //private final Task[] items;
    private final static int LIST_SIZE = 100;
    private ArrayList<Task> items;
    //private int numOfTasks;
    protected enum markedState {
        MARKED,
        UNMARKED
    }

    public TaskList() {
        this.items = new ArrayList<>();
        //this.numOfTasks = 0;
    }

    public void add(Task task) {
        items.add(task);
        System.out.println("Added:\n" + task.toString());
        System.out.println("You now have (" + size() + ") tasks.");
    }

    protected void setMarkedState(markedState state, int arrayIndex) throws DyuqueException {
        // arrayIndex is 0-based

        if (arrayIndex < 0 || (arrayIndex + 1) > size()) {
            throw new DyuqueException("Task " + (arrayIndex + 1) + " does not exist.\nThere are only " + size() + " tasks");
        } else {
            switch (state) {
                case MARKED:
                    get(arrayIndex).markDone();
                    System.out.println("Nice! I've marked this task as done:");
                    break;
                case UNMARKED:
                    get(arrayIndex).markUndone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    break;
            }
            System.out.println(get(arrayIndex));
        }
    }

    public void list() {
        System.out.println("You have (" + size() + ") tasks:");
        for (int i = 0; i < size(); i++) {
            System.out.println((i + 1) + ". " + get(i));
        }
    }

    public int size() {
        return items.size();
    }

    private Task get(int index) {
        return items.get(index);
    }
}
