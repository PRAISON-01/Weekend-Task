package safebank;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import safebank.exceptions.AccountAuthenticatedException;
import safebank.exceptions.InvalidAmountException;
import safebank.exceptions.InvalidMoneyException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountTest {

    @Test
    public void testThat_accountNotRegistered_cannotDeposit(){
        Account account  = new Account();
        AccountAuthenticatedException exception = assertThrows(
                AccountAuthenticatedException.class,
                ()-> account.deposit(new Money(BigDecimal.valueOf(500)))
        );

        assertEquals("Account Not Registered", exception.getMessage());
    }

    @Test
    public void testThat_registerAccount_findByAccountNumber() {
        Account account = new Account();
        account.setFirstName("Agoremi");
        account.setSecondName("Aken");
        account.setAccountNumber("1234567890");
    }

    @Test
    public void testThat_deposit_1500_balanceIs_1500() {
        Account  account = new Account();
        account.deposit(new Money(BigDecimal.valueOf(1500)));


        assertEquals(new Money(BigDecimal.valueOf(1500)), account.getBalance());
    }

    @Test
    public void testThat_depositZero_throwException(){
        Account account = new Account();
        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                ()-> account.deposit(new Money(BigDecimal.ZERO))
        );

        assertEquals("Cannot Deposit Zero Amount", exception.getMessage());
    }


    @Test
    public void testThat_deposit_1000_twice_balanceIs_2000() {
        Account  account = new Account();
        account.deposit(new Money(BigDecimal.valueOf(1000)));
        account.deposit(new Money(BigDecimal.valueOf(1000)));


        assertEquals(new Money(BigDecimal.valueOf(2000)), account.getBalance());
    }


    @Test
    public void testThat_withDraw_500_FromZeroBalance_throwException() {
        Account  account = new Account();
        InvalidMoneyException exception = assertThrows(
                InvalidMoneyException.class,
                ()-> account.withdraw(new Money(BigDecimal.valueOf(1500)))
        );
        assertEquals("Money cannot be Negative", exception.getMessage());



    }

    @Test
    public void testThat_withdrawZero_throwException(){
        Account account = new Account();
        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                ()-> account.withdraw(new Money(BigDecimal.ZERO))
        );

        assertEquals("Cannot Withdraw Zero Amount", exception.getMessage());
    }

    @Test
    public void testThat_deposit_2000_withDraw_3000_throwException() {
        Account  account = new Account();

        account.deposit(new Money(BigDecimal.valueOf(2000)));

        InvalidMoneyException exception = assertThrows(
                InvalidMoneyException.class,
                ()-> account.withdraw(new Money(BigDecimal.valueOf(3000)))
        );
        assertEquals("Money cannot be Negative", exception.getMessage());



    }

    @Test
    public void testThat_deposit_2000_withDraw_2000_balanceIsZero() {
        Account  account = new Account();

        account.deposit(new Money(BigDecimal.valueOf(2000)));

        account.withdraw(new Money(BigDecimal.valueOf(2000)));

        assertEquals(new Money(BigDecimal.ZERO), account.getBalance());



    }


    @Test
    public void testThat_deposit_3000_withDraw_2000_balanceIs_1000() {
        Account  account = new Account();

        account.deposit(new Money(BigDecimal.valueOf(3000)));

        account.withdraw(new Money(BigDecimal.valueOf(2000)));

        assertEquals(new Money(BigDecimal.valueOf(1000)), account.getBalance());
    }







}
