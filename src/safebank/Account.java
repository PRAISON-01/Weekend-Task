package safebank;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import safebank.exceptions.InvalidAmountException;
import safebank.exceptions.InvalidPinException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode
public class Account {
    private String firstName;
    private String secondName;
    private AccountNumber  accountNumber;
    private String pin;
    private Money balance = new Money(BigDecimal.ZERO);
    private boolean LoggedIn;
    @Getter
    private final List<Transaction> transactions = new ArrayList<>();
    private int transactionCount = transactions.size();




    public void credit(Money money) {

        if(money.getValue().compareTo(BigDecimal.ZERO) == 0) throw new InvalidAmountException("Cannot Deposit Zero Amount");
        this.balance = this.balance.add(money);
        this.transactions.add(new Transaction(String.format("%d", transactionCount), TransactionType.CREDIT, money));
    }


    public void debit(Money money, String pin) {
        if(!validate(pin)) throw new InvalidPinException("Incorrect Pin");
        if(money.getValue().compareTo(BigDecimal.ZERO) == 0) throw new InvalidAmountException("Cannot Withdraw Zero Amount");

        this.balance = this.balance.subtract(money);
        this.transactions.add(new Transaction(String.format("%d", transactionCount), TransactionType.DEBIT, money));
    }

    private boolean validate(String pin) {
       return this.pin.equals(pin);
    }

}
