package safebank.Repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safebank.Account;
import safebank.AccountNumber;
import safebank.Money;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class AccountRepositoryImplTest {

    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp() {
        accountRepository = new AccountRepositoryImpl();
        accountRepository.deleteAll();
    }

    private Account createAccount(String firstName, String secondName, String serial) {
        Account account = new Account();
        account.setFirstName(firstName);
        account.setSecondName(secondName);
        account.setAccountNumber(AccountNumber.generateAccountNumber("044", serial));
        account.setBalance(new Money(BigDecimal.ZERO));
        account.setPin("1234");
        return account;
    }

    @Test
    public void testThat_saveAccount_returnAccount_repoCountIsOne() {
        Account account = createAccount("Agoremi", "Aken", "000001457");
        Account savedAccount = accountRepository.save(account);
        assertNotNull(savedAccount);
        assertEquals(1, accountRepository.count());
        assertEquals("Agoremi", savedAccount.getFirstName());
    }


    @Test
    public void testThat_findByAccountNumber_returnsCorrectAccount() {
        Account firstAccount = createAccount("Agoremi", "Aken", "000001457");
        Account secondAccount = createAccount("Tunde", "Ola", "000001458");

        accountRepository.save(firstAccount);
        accountRepository.save(secondAccount);

        AccountNumber accountNumber = AccountNumber.generateAccountNumber("044", "000001458");
        Account foundAccount = accountRepository.findByAccountNumber(accountNumber);

        assertNotNull(foundAccount);
        assertEquals("Tunde", foundAccount.getFirstName());
    }

    @Test
    public void testThat_findByAccountNumber_returnsNull_ifAccountDoesNotExist() {
        AccountNumber accountNumber = AccountNumber.generateAccountNumber("044", "999999999");
        Account foundAccount = accountRepository.findByAccountNumber(accountNumber);

        assertNull(foundAccount);
    }

    @Test
    public void testThat_findByAccountNumberWithNull_returnsNullSafely() {
        assertNull(accountRepository.findByAccountNumber(null));
    }

    @Test
    public void testThat_deleteAll_clearsAllRecords() {
        accountRepository.save(createAccount("Emmanuel", "Nneji", "000001451"));
        accountRepository.save(createAccount("Agoremi", "Aken", "000001452"));
        assertEquals(2, accountRepository.count());

        accountRepository.deleteAll();

        assertEquals(0, accountRepository.count());
    }
}

