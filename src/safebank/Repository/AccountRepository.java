package safebank.Repository;

import safebank.Account;
import safebank.AccountNumber;

public interface AccountRepository {
    int count();
    Account save(Account account);
    void deleteAll();
    Account findByAccountNumber(AccountNumber accountNumber);
}
