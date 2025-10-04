import java.util.Optional;
import java.util.Scanner;


public class MovieTheaterApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SeatManager manager = new SeatManager(5, 6); 

        int choice;
        do {
            System.out.println("===== Movie Theater Seat Reservation System =====");
            System.out.println("1. Display Seating Chart");
            System.out.println("2. Reserve a Seat");
            System.out.println("3. Cancel a Reservation");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            while (!sc.hasNextInt()) { System.out.print("Enter a number (1-4): "); sc.next(); }
            choice = sc.nextInt();

            switch (choice) {
                case 1 -> System.out.println(manager.render());

                case 2 -> {
                    int row = ask(sc, "Enter row (1-" + manager.getRows() + "): ") - 1;
                    int col = ask(sc, "Enter seat (1-" + manager.getCols() + "): ") - 1;
                    if (manager.reserve(row, col)) {
                        System.out.println("Seat reserved successfully!");
                    } else {
                        System.out.println("That seat is taken or invalid.");
                        Optional<int[]> sug = manager.suggestFirstAvailable();
                        System.out.println(sug.isPresent()
                                ? "Suggested: Row " + (sug.get()[0] + 1) + " Seat " + (sug.get()[1] + 1)
                                : "No seats available!");
                    }
                    System.out.println(manager.render());
                }

                case 3 -> {
                    int row = ask(sc, "Enter row (1-" + manager.getRows() + "): ") - 1;
                    int col = ask(sc, "Enter seat (1-" + manager.getCols() + "): ") - 1;
                    System.out.println(manager.cancel(row, col)
                            ? "Reservation cancelled." : "That seat wasn’t reserved/invalid.");
                    System.out.println(manager.render());
                }

                case 4 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 4);

        sc.close();
    }

    private static int ask(Scanner sc, String prompt) {
        System.out.print(prompt);
        while (!sc.hasNextInt()) { System.out.print(prompt); sc.next(); }
        return sc.nextInt();
    }
}
