import java.util.ArrayList;

public class TaskList {
    private ArrayList<Task> items;
    protected enum markedState {
        MARKED,
        UNMARKED
    }

    public TaskList() {
        this.items = new ArrayList<>();
    }

    public void list() {
        System.out.println("You have (" + size() + ") tasks:");
        int i = 1;
        for (Task task : items) {
            System.out.println((i++) + ". " + task);
        }
    }

    public void add(Task task) {
        items.add(task);
        System.out.println("Added:\n" + task.toString());
        printSize();
    }

    public void delete(int arrayIndex) throws DyuqueException {
        System.out.println("Removed:\n" + get(arrayIndex));
        items.remove(get(arrayIndex));
        printSize();
    }

    protected void setMarkedState(markedState state, int arrayIndex) throws DyuqueException {
        // arrayIndex is 0-based

        Task task = get(arrayIndex);

        switch (state) {
            case MARKED:
                task.markDone();
                System.out.println("Nice! I've marked this task as done:");
                break;
            case UNMARKED:
                task.markUndone();
                System.out.println("OK, I've marked this task as not done yet:");
                break;
        }
        System.out.println(task);
    }

    public int size() {
        return items.size();
    }

    private Task get(int arrayIndex) throws DyuqueException {
        // arrayIndex is 0-based

        if (arrayIndex < 0 || (arrayIndex + 1) > size()) {
            throw new DyuqueException("Task " + (arrayIndex + 1) + " does not exist.\nThere are only " + size() + " tasks");
        }
        return items.get(arrayIndex);
    }

    private void printSize() {
        System.out.println("You now have (" + size() + ") tasks.");
    }
}
