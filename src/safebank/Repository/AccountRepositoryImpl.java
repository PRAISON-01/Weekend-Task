package safebank.Repository;

import safebank.Account;
import safebank.AccountNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountRepositoryImpl implements AccountRepository {
    private static final List<Account> accounts = new ArrayList<>();

    @Override
    public int count() {
        return accounts.size();
    }

    @Override
    public Account save(Account account) {
        accounts.add(account);
        return account;
    }

    @Override
    public void deleteAll() {
        accounts.clear();
    }

    @Override
    public Account findByAccountNumber(AccountNumber accountNumber) {
        if (accountNumber == null) {
            return null;
        }

        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }
}
