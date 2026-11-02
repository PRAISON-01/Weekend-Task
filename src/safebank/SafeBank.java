package safebank;

import safebank.Repository.AccountRepository;
import safebank.Repository.AccountRepositoryImpl;
import safebank.exceptions.AccountAuthenticatedException;
import safebank.exceptions.AccountNumberNotFound;
import safebank.exceptions.InvalidPinException;
import safebank.exceptions.TransactionException;

import java.math.BigDecimal;

public class SafeBank {
    private static final AccountRepository accountRepository = new AccountRepositoryImpl();

    private static final String BANK_CODE = "044";
    private static final Money SINGLE_WITHDRAW_LIMIT = new Money(BigDecimal.valueOf(100000));

    public static Account registerAccount(Account account) {
        validateRegistrationDetails(account);

        int count = accountRepository.count() + 1;
        String serialNumber = String.format("%09d", count);

        AccountNumber generatedNumber = AccountNumber.generateAccountNumber(BANK_CODE, serialNumber);
        account.setAccountNumber(generatedNumber);

        account.setBalance(new Money(BigDecimal.ZERO));
        account.setLoggedIn(false);

        return accountRepository.save(account);
    }

    public static Account login(String pin, AccountNumber accountNumber) {
        validateLoginInputs(pin, accountNumber);

        Account account = accountRepository.findByAccountNumber(accountNumber);
        validateRegistration(account);
        validateAccountPinMatch(account, pin);

        account.setLoggedIn(true);
        return account;
    }

    public static void deposit(AccountNumber accountNumber, Money money) {
        validateTransactionInputs(accountNumber, money, "Deposit");

        Account account = accountRepository.findByAccountNumber(accountNumber);
        validateRegistration(account);
        validateAccountLoginState(account, "make a deposit");

        account.credit(money);
    }

    public static void withdraw(AccountNumber accountNumber, Money money, String pin) {
        validateTransactionInputs(accountNumber, money, "Withdrawal");
        validatePinInput(pin);

        Account account = accountRepository.findByAccountNumber(accountNumber);
        validateRegistration(account);
        validateAccountLoginState(account, "make a withdrawal");
        validateAccountPinMatch(account, pin);
        validateSufficientFunds(account, money);
        validateWithdrawalLimit(money);

        account.debit(money, pin);
    }

    public static void transfer(AccountNumber senderAccountNumber, AccountNumber recieverAccountNumber, Money amount, String pin) {
        validateTransactionInputs(senderAccountNumber, amount, "Transfer");
        if (recieverAccountNumber == null) {
            throw new AccountNumberNotFound("Receiver account number cannot be null");
        }
        if (senderAccountNumber.equals(recieverAccountNumber)) {
            throw new IllegalArgumentException("Cannot execute a transfer to the exact same account profile");
        }
        validatePinInput(pin);

        Account sender = accountRepository.findByAccountNumber(senderAccountNumber);
        Account receiver = accountRepository.findByAccountNumber(recieverAccountNumber);

        if (sender == null || receiver == null) {
            throw new AccountAuthenticatedException("One or both accounts are not registered in our system");
        }

        validateAccountLoginState(sender, "execute a transfer");
        validateAccountPinMatch(sender, pin);
        validateSufficientFunds(sender, amount);

        sender.debit(amount, pin);
        receiver.credit(amount);
    }


    private static void validateRegistrationDetails(Account account) {
        if (account == null) {
            throw new AccountAuthenticatedException("Account details cannot be null");
        }
        if (account.getFirstName() == null || account.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required for registration");
        }
        if (account.getSecondName() == null || account.getSecondName().trim().isEmpty()) {
            throw new IllegalArgumentException("Second name is required for registration");
        }
        if (account.getPin() == null || account.getPin().length() != 4) {
            throw new InvalidPinException("A valid 4-digit PIN is required");
        }
    }

    private static void validateLoginInputs(String pin, AccountNumber accountNumber) {
        if (accountNumber == null) {
            throw new AccountNumberNotFound("Account number not found");
        }
        if (pin == null || pin.trim().isEmpty()) {
            throw new InvalidPinException("Account number and PIN must be provided");
        }
    }

    private static void validateTransactionInputs(AccountNumber accountNumber, Money money, String transactionType) {
        if (accountNumber == null) {
            throw new AccountNumberNotFound("Account number cannot be null");
        }
        if (money == null) {
            throw new TransactionException(transactionType + " amount cannot be null");
        }
    }

    private static void validatePinInput(String pin) {
        if (pin == null || pin.trim().isEmpty()) {
            throw new InvalidPinException("PIN must be provided");
        }
    }

    private static void validateRegistration(Account account) {
        if (account == null) {
            throw new AccountAuthenticatedException("Account not registered");
        }
    }

    private static void validateAccountLoginState(Account account, String actionDescription) {
        if (!account.isLoggedIn()) {
            throw new IllegalStateException("Authentication Required: You must be logged in to " + actionDescription);
        }
    }

    private static void validateAccountPinMatch(Account account, String pin) {
        if (!account.getPin().equals(pin)) {
            throw new IllegalArgumentException("Invalid account number or PIN");
        }
    }

    private static void validateSufficientFunds(Account account, Money amount) {
        if (account.getBalance().getValue().compareTo(amount.getValue()) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
    }

    private static void validateWithdrawalLimit(Money money) {
        if (money.getValue().compareTo(SINGLE_WITHDRAW_LIMIT.getValue()) > 0) {
            throw new TransactionException("Maximum withdraw amount is ₦100,000");
        }
    }
}
