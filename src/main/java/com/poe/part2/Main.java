package com.poe.part2;

import java.util.Scanner;

// No GUI is used; everything runs in the console.
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("PART 2: Registration, Login & QuickChat\n");

        // Registration
        System.out.println("Register a new account");
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter username (must contain _ and be no more than 5 characters): ");
        String username = scanner.nextLine();

        System.out.print("Enter password (min 8 chars, 1 capital, 1 number, 1 special): ");
        String password = scanner.nextLine();

        System.out.print("Enter South African cell number (e.g. +27838968976): ");
        String cell = scanner.nextLine();

        Login login = new Login(firstName, lastName, username, password, cell);

        System.out.println("\nRegistration Result");
        System.out.println(login.registerUser());

        // Login
        System.out.println("\nLogin");
        System.out.print("Enter username: ");
        String loginUsername = scanner.nextLine();

        System.out.print("Enter password: ");
        String loginPassword = scanner.nextLine();

        System.out.println("\nLogin Status");
        System.out.println(login.returnLoginStatus(loginUsername, loginPassword));

        // I ensured that only logged in Users may send the messages
        if (login.loginUser(loginUsername, loginPassword)) {
            runQuickChat(scanner);
        }

        scanner.close();
    }

    private static void runQuickChat(Scanner scanner) {
        System.out.println("\nWelcome to QuickChat.");

        int limit = readInt(scanner, "How many messages do you wish to enter? ", 1);
        int entered = 0;
        boolean running = true;

        while (running) {
            System.out.println("\nMenu");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            int choice = readInt(scanner, "Choose an option: ", 1);

            switch (choice) {
                case 1:
                    entered = sendMessages(scanner, limit, entered);
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    running = false;
                    break;
                default:
                    System.out.println("Please choose 1, 2 or 3.");
            }
        }

        System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
        System.out.println("Goodbye.");
    }

    private static int sendMessages(Scanner scanner, int limit, int entered) {
        if (entered >= limit) {
            System.out.println("You have already entered all " + limit + " messages.");
            return entered;
        }

        while (entered < limit) {
            System.out.println("\nMessage " + (entered + 1) + " of " + limit);

            // Recipient
            String recipient;
            while (true) {
                System.out.print("Enter recipient cell number (with international code, e.g. +27718693002): ");
                recipient = scanner.nextLine().trim();
                String result = new Message(entered, recipient, "").checkRecipientCell();
                System.out.println(result);
                if (result.equals(Message.CELL_OK)) {
                    break;
                }
            }

            // Message text
            Message msg;
            while (true) {
                System.out.print("Enter your message (max 250 characters): ");
                String text = scanner.nextLine();
                msg = new Message(entered, recipient, text);
                String result = msg.checkMessageLength();
                if (result.equals(Message.MESSAGE_READY)) {
                    System.out.println(result);
                    break;
                }
                System.out.println(result);
            }

            System.out.println(msg.messageIDStatus());

            // Send / disregard / store
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            int option = readInt(scanner, "Choose an option: ", 1);
            while (option > 3) {
                option = readInt(scanner, "Please choose 1, 2 or 3: ", 1);
            }

            System.out.println(msg.sentMessage(option));

            if (option == 1) {
                System.out.println("\n" + msg.details());
            } else if (option == 2) {
                String input;
                do {
                    System.out.print("> ");
                    input = scanner.nextLine().trim();
                } while (!input.equals("0"));
                System.out.println("Message deleted.");
            }

            entered++;
        }

        System.out.println("\nAll messages sent while running:\n");
        System.out.println(Message.printMessages());
        System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
        return entered;
    }

    private static int readInt(Scanner scanner, String prompt, int min) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // fall through and ask again
            }
            System.out.println("Please enter a whole number of " + min + " or more.");
        }
    }
}
