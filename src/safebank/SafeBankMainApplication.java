package safebank;

import safebank.Repository.AccountRepository;
import safebank.Repository.AccountRepositoryImpl;

import java.math.BigDecimal;
import java.util.Scanner;

public class SafeBankMainApplication {

    private static AccountNumber activeAccountNumber;
    private static AccountRepository accountRepository = new AccountRepositoryImpl();


    public static void main(String[] args) {
        goToMainMenu();
    }

    private static void goToMainMenu() {
        String menu = """
                \n=========================
                WELCOME TO SAFE BANK
                1. REGISTER ACCOUNT
                2. LOGIN
                3. EXIT
                ============================
                """;

        print(menu);
        try {
            int choice = inputInt("Enter an option >> ");
            scanner.nextLine();

            if (choice == 3) {
                print("Thank you for banking with SafeBank. Goodbye!");
                System.exit(0);
            }

            switch (choice) {
                case 1 -> registerAccount();
                case 2 -> login();
                default -> {
                    print("Enter a valid option");
                    goToMainMenu();
                }
            }
        } catch (Exception e) {
            print("Error: " + e.getMessage());
            if (scanner.hasNextLine()) scanner.nextLine(); 
            goToMainMenu();
        }
    }

    private static void registerAccount() {
        try {
            String firstName = input("Enter first name >> ");
            String secondName = input("Enter second name >> ");
            String pin = input("Create 4-digit PIN >> ");

            Account account = new Account();
            account.setFirstName(firstName);
            account.setSecondName(secondName);
            account.setPin(pin);

            Account registered = SafeBank.registerAccount(account);

            print("\nAccount created successfully!!!!");
            print("Welcome, " + registered.getFirstName() + " " + registered.getSecondName());
            print("YOUR NUBAN ACCOUNT NUMBER IS >> " + registered.getAccountNumber().getAccountNumber());
        } catch (Exception e) {
            print("Error: " + e.getMessage());
        }
        goToMainMenu();
    }

    private static void login() {
        try {
            String AccountNumberInput = input("Enter your 10-digit Account Number >> ");
            String pin = input("Enter your 4-digit PIN >> ");

            if (AccountNumberInput == null || AccountNumberInput.length() != 10) {
                print("Error: Account number must be exactly 10 digits");
                goToMainMenu();
                return;
            }

            String serial = AccountNumberInput.substring(0, 9);
            AccountNumber accountLookupKey = AccountNumber.generateAccountNumber("044", serial);

            Account loggedInUser = SafeBank.login(pin, accountLookupKey);
            activeAccountNumber = loggedInUser.getAccountNumber();

            print("\nLogin Successful!!! Welcome back.");
            goToBankingMenu();
        } catch (Exception e) {
            print("Error: " + e.getMessage());
            goToMainMenu();
        }
    }

    private static void goToBankingMenu() {
        String menu = """
                \n=========================
                SAFEBANK TRANSACTION MENU
                1. CHECK BALANCE
                2. DEPOSIT (CREDIT)
                3. WITHDRAW (DEBIT)
                4. TRANSFER
                5. VIEW TRANSACTION STATEMENT
                6. LOGOUT
                =========================
                """;
        print(menu);
        try {
            int choice = inputInt("Enter an option >> ");
            scanner.nextLine();

            switch (choice) {
                case 1 -> checkBalance();
                case 2 -> deposit();
                case 3 -> withdraw();
                case 4 -> transfer();
                case 5 -> viewStatement();
                case 6 -> logout();
                default -> {
                    print("Enter a valid option");
                    goToBankingMenu();
                }
            }
        } catch (Exception e) {
            print("Error: " + e.getMessage());
            if (scanner.hasNextLine()) scanner.nextLine();
            goToBankingMenu();
        }
    }

    private static void checkBalance() {
        try {
            Account account = accountRepository.findByAccountNumber(activeAccountNumber);
            print("Your current balance is >> #" + account.getBalance().getValue());
        } catch (Exception e) {
            print("Error: " + e.getMessage());
        }
        goToBankingMenu();
    }

    private static void deposit() {
        try {
            double rawAmount = inputDouble("Enter amount to deposit >> ");
            scanner.nextLine();

            Money money = new Money(BigDecimal.valueOf(rawAmount));
            SafeBank.deposit(activeAccountNumber, money);

            print("Deposit of #" + rawAmount + " completed successfully!!!");
        } catch (Exception e) {
            print("Error: " + e.getMessage());
        }
        goToBankingMenu();
    }

    private static void withdraw() {
        try {
            double rawAmount = inputDouble("Enter amount to withdraw >> ");
            scanner.nextLine();
            String pin = input("Enter PIN to verify transaction >> ");

            Money money = new Money(BigDecimal.valueOf(rawAmount));
            SafeBank.withdraw(activeAccountNumber, money, pin);

            print("Withdrawal of #" + rawAmount + " completed successfully!!!");
        } catch (Exception e) {
            print("Error: " + e.getMessage());
        }
        goToBankingMenu();
    }

    private static void transfer() {
        try {
            String destinationRaw = input("Enter receiver's 10-digit Account Number >> ");
            double rawAmount = inputDouble("Enter amount to transfer >> ");
            scanner.nextLine();
            String pin = input("Enter your PIN to verify transaction >> ");

            if (destinationRaw == null || destinationRaw.length() != 10) {
                throw new IllegalArgumentException("Invalid receiver account number length");
            }

            String targetSerial = destinationRaw.substring(0, 9);
            AccountNumber receiverKey = AccountNumber.generateAccountNumber("044", targetSerial);
            Money amount = new Money(BigDecimal.valueOf(rawAmount));

            SafeBank.transfer(activeAccountNumber, receiverKey, amount, pin);
            print("Successfully transferred #" + rawAmount + " to account " + destinationRaw);
        } catch (Exception e) {
            print("Error: " + e.getMessage());
        }
        goToBankingMenu();
    }

    private static void viewStatement() {
        try {
            Account account = accountRepository.findByAccountNumber(activeAccountNumber);

            print("\n--- TRANSACTION HISTORY ---");
            if (account.getTransactions().isEmpty()) {
                print("No histoy");
            } else {
                for (Transaction transaction : account.getTransactions()) {
                    print(String.format("ID: %s | Type: %s | Amount: #%s",
                            transaction.getId(), transaction.getType(), transaction.getAmount().getValue()));
                }
            }
            print("--------------------------------");
        } catch (Exception e) {
            print("Error: " + e.getMessage());
        }
        goToBankingMenu();
    }

    private static void logout() {
        try {
            if (activeAccountNumber != null) {
                Account account = accountRepository.findByAccountNumber(activeAccountNumber);
                if (account != null) {
                    account.setLoggedIn(false);
                }
            }
            activeAccountNumber = null;
            print("Logged out successfully. Thank you!");
        } catch (Exception e) {
            print("Error during logout closing steps: " + e.getMessage());
        }
        goToMainMenu();
    }

    private static final Scanner scanner = new Scanner(System.in);

    private static String input(String message) {
        print(message);
        return scanner.nextLine();
    }

    private static int inputInt(String message) {
        print(message);
        return scanner.nextInt();
    }

    private static double inputDouble(String message) {
        print(message);
        return scanner.nextDouble();
    }

    private static void print(String message) {
        System.out.println(message);
    }
}
