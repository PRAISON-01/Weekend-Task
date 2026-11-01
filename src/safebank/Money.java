package safebank;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import safebank.exceptions.InvalidMoneyException;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode
public class Money {
    private BigDecimal value;

    public Money(BigDecimal value) {
        setValue(value);
    }

    public void setValue(BigDecimal value){
        if(value == null) throw new InvalidMoneyException("Invalid Monetary Value");
        if( value.compareTo(BigDecimal.ZERO) < 0) throw new InvalidMoneyException("Money cannot be Negative");
        this.value = value;
    }


    public Money add(Money anotherMoney) {
        BigDecimal sum = this.value.add(anotherMoney.getValue());
        return new Money(sum);
    }

    public Money subtract(Money anotherMoney) {
        BigDecimal difference = this.value.subtract(anotherMoney.getValue());
        return new Money(difference);
    }


}

