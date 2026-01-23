public class Dyuque {
    public static void main(String[] args) {
        /*
        String logo = " ____        _        \n"
                + "|  _ \\ _   _| | _____ \n"
                + "| | | | | | | |/ / _ \\\n"
                + "| |_| | |_| |   <  __/\n"
                + "|____/ \\__,_|_|\\_\\___|\n";
        System.out.println("Hello from\n" + logo);
         */
        printLine();
        printGreet();
        printExit();
    }

    public static void printLine() {
        System.out.println("____________________________________________________________");
    }

    public static void printGreet() {
        System.out.println("Hello! I'm Dyuque");
        System.out.println("What can I do for you?");
        printLine();
    }

    public static void printExit() {
        System.out.println("Bye. Hope to see you again soon!");
        printLine();
    }
}
