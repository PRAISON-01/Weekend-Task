package safebank;

public class AccountNumber {

    private static final int[] NUMBERS =  {3, 7, 3, 3, 7, 3,  3, 7,  3, 3,  7, 3};

    public static String generateAccountNumber(String bankCode, String serialNumber) {
        int checkDigit = calculateCheckDigit(bankCode, serialNumber);
        return serialNumber + checkDigit;
    }

    public static boolean isValidAccountNumber(String bankCode, String accountNumber) {

            String serialNumber = accountNumber.substring(0, 9);
            int checkDigit = Character.getNumericValue(accountNumber.charAt(9));

            int calculatedCheckDigit = calculateCheckDigit(bankCode, serialNumber);
            return calculatedCheckDigit == checkDigit;

    }

        private static int calculateCheckDigit(String bankCode, String serialNumber) {
        String bankCodePlusSerialNumber = bankCode + serialNumber;

        int total = 0;

        for (int count= 0; count< bankCodePlusSerialNumber.length(); count++) {
            int digit = Character.getNumericValue(bankCodePlusSerialNumber.charAt(count));
            total += digit * NUMBERS[count];
        }

        int remainder = total % 10;
        int checkDigit = 10 - remainder;

        if( checkDigit == 10) {
            return 0;
        }

        return checkDigit;
    }
}
