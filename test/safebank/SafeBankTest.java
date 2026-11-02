package safebank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safebank.Repository.AccountRepository;
import safebank.Repository.AccountRepositoryImpl;
import safebank.exceptions.AccountAuthenticatedException;
import safebank.exceptions.InvalidPinException;
import safebank.exceptions.TransactionException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class SafeBankTest {

    private final AccountRepository repo = new AccountRepositoryImpl();

    @BeforeEach
    public void resetSystemState() {

        repo.deleteAll();


    }

    private static Account createAccount(String name, String secondName, String pin) {
        Account account = new Account();
        account.setFirstName(name);
        account.setSecondName(secondName);
        account.setPin(pin);
        return account;
    }

    @Test
    public void testThat_SuccessfullyRegisterFirstCustomer_WithValidAccountNUmberAndZeroBalance() {


        Account registered = SafeBank.registerAccount(createAccount("Agoremi", "Aken", "1234"));

        assertNotNull(registered);
        assertNotNull(registered.getAccountNumber());

        assertEquals(10, registered.getAccountNumber().getAccountNumber().length());
        assertEquals(BigDecimal.ZERO, registered.getBalance().getValue());
        assertFalse(registered.isLoggedIn());
        assertEquals(1, repo.count());
    }

    @Test
    public void testThat_AssignSequentialNumbers_ForMultipleRegistrations() {

        Account registeredOne = SafeBank.registerAccount(createAccount("Agoremi", "Aken", "1234"));
        Account registeredTwo = SafeBank.registerAccount(createAccount("Emmanuel", "Nneji", "4321"));

        assertNotEquals(registeredOne.getAccountNumber(), registeredTwo.getAccountNumber());
        assertEquals(2, repo.count());
    }

    @Test
    public void testThat_ThrowIllegalArgumentException_WhenFirstNameIsMissing() {
        Account invalidAccount = new Account();
        invalidAccount.setSecondName("Aken");
        invalidAccount.setPin("1234");

        assertThrows(IllegalArgumentException.class, () -> {
            SafeBank.registerAccount(invalidAccount);
        });
    }

    @Test
    public void testThat_throwException_WhenPinIsInvalidLength() {
        assertThrows(InvalidPinException.class, () -> {
            SafeBank.registerAccount(createAccount("Agoremi", "Aken", "12"));
        });
    }

    @Test
    public void testThat_ThrowIllegalArgumentException_AccountIsNull() {

        assertThrows(AccountAuthenticatedException.class, () -> {
            SafeBank.registerAccount(null);
        });
    }

    @Test
    public void testThat_accountNotRegistered_cannotLogin() {
        Account account = createAccount("Agoremi", "Aken", "1234");
        AccountAuthenticatedException exception =assertThrows(
                AccountAuthenticatedException.class,
                ()-> SafeBank.login("1234", new AccountNumber("000000001"))
        );

        assertEquals("Account not registered", exception.getMessage());
    }

    @Test
    public void testThat_unauthenticatedUser_cannotDeposit() {
        Account registered = SafeBank.registerAccount(createAccount("Agoremi", "Aken", "1234"));
        Money depositAmount = new Money(BigDecimal.valueOf(5000));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            SafeBank.deposit(registered.getAccountNumber(), depositAmount);
        });

        assertEquals("Authentication Required: You must be logged in to make a deposit", exception.getMessage());
        assertEquals(BigDecimal.ZERO, registered.getBalance().getValue());
    }

    @Test
    public void testThat_loggedInUser_canSuccessfullyDeposit() {
        Account registered = SafeBank.registerAccount(createAccount("Agoremi", "Aken", "1234"));
        SafeBank.login("1234", registered.getAccountNumber());

        Money depositAmount = new Money(BigDecimal.valueOf(15000));

        SafeBank.deposit(registered.getAccountNumber(), depositAmount);

        assertEquals(BigDecimal.valueOf(15000), registered.getBalance().getValue());
    }

    @Test
    public void testThat_depositWithNullOrUnregisteredAccountNumber_throwsException() {
        Money depositAmount = new Money(BigDecimal.valueOf(2000));

        assertThrows(RuntimeException.class, () -> {
            SafeBank.deposit(null, depositAmount);
        });

        AccountNumber dummyNumber = AccountNumber.generateAccountNumber("044", "999999999");
        assertThrows(AccountAuthenticatedException.class, () -> {
            SafeBank.deposit(dummyNumber, depositAmount);
        });
    }

    @Test
    public void testThat_unauthenticatedUser_cannotWithdraw() {
        Account registered = SafeBank.registerAccount(createAccount("Agoremi", "Aken", "1234"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            SafeBank.withdraw(registered.getAccountNumber(), new Money(BigDecimal.valueOf(1000)), "1234");
        });

        assertTrue(exception.getMessage().contains("must be logged in"));
    }

    @Test
    public void testThat_loggedInUser_withIncorrectPin_cannotWithdraw() {
        Account registered = SafeBank.registerAccount(createAccount("Agoremi", "Aken", "1234"));
        SafeBank.login("1234", registered.getAccountNumber());

        assertThrows(IllegalArgumentException.class, () -> {
            SafeBank.withdraw(registered.getAccountNumber(), new Money(BigDecimal.valueOf(1000)), "9999");
        });
    }

    @Test
    public void testThat_loggedInUser_withInsufficientFunds_cannotWithdraw() {
        Account registered = SafeBank.registerAccount(createAccount("Agoremi", "Aken", "1234"));
        SafeBank.login("1234", registered.getAccountNumber());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            SafeBank.withdraw(registered.getAccountNumber(), new Money(BigDecimal.valueOf(5000)), "1234");
        });

        assertEquals("Insufficient funds", exception.getMessage());
    }

    @Test
    public void testThat_loggedInUser_withSufficientFunds_canWithdrawSuccessfully() {
        Account registered = SafeBank.registerAccount(createAccount("Agoremi", "Aken", "1234"));
        SafeBank.login("1234", registered.getAccountNumber());

        SafeBank.deposit(registered.getAccountNumber(), new Money(BigDecimal.valueOf(10000)));

        SafeBank.withdraw(registered.getAccountNumber(), new Money(BigDecimal.valueOf(4000)), "1234");

        assertEquals(BigDecimal.valueOf(6000), registered.getBalance().getValue());
    }

    @Test
    public void testThat_loggedInUser_deposit500000_maxWithdrawIs100000() {
        Account registered = SafeBank.registerAccount(createAccount("Agoremi", "Aken", "1234"));
        SafeBank.login("1234", registered.getAccountNumber());
        SafeBank.deposit(registered.getAccountNumber(), new Money(BigDecimal.valueOf(500000)));

        TransactionException exception = assertThrows(TransactionException.class, () -> {
            SafeBank.withdraw(registered.getAccountNumber(), new Money(BigDecimal.valueOf(100000)), "1234");
        });

        assertEquals("Maximum withdraw amount is #100,000", exception.getMessage());
    }




}
