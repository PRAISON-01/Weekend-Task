package safebank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccountNumberTest {
    @Test
    public void testThat_generateAccountNumber_returnsAccountNumber() {
        String bankCode = "044";
        String serialNumber = "000001457";


        String generatedAccount = AccountNumber.generateAccountNumber(bankCode, serialNumber);
        assertEquals("0000014579", generatedAccount);
    }


    @Test
    public void testThat_ValidateCorrectAccountNumber_returnTrue() {
        String bankCode = "011";
        String validAccountNumber = "0000014579";

        boolean isValid = AccountNumber.isValidAccountNumber(bankCode, validAccountNumber);

        assertTrue(isValid);
    }

    @Test
    public void testThat_wrongAccountNumber_returnFalse() {
        String bankCode = "011";
        String invalidAccountNumber = "0000014574"; // Ended with 4 instead of 9

        boolean isValid = AccountNumber.isValidAccountNumber(bankCode, invalidAccountNumber);

        assertFalse(isValid);
    }
}
