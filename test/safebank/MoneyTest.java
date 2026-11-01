package safebank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safebank.exceptions.InvalidMoneyException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoneyTest {
    private Money money;

    @BeforeEach
    public void startWithThis() {
         money = new Money(BigDecimal.valueOf(1));
    }
    @Test
    public void TestThat_nullAmount_moneyIsNotValid(){
        InvalidMoneyException exception = assertThrows(
                InvalidMoneyException.class,
                () -> money.setValue(null)
        );

        assertEquals("Invalid Monetary Value", exception.getMessage());


    }

    @Test
    public void TestThat_negativeAmount_moneyIsNotValid(){
        InvalidMoneyException exception = assertThrows(
                InvalidMoneyException.class,
                () -> money.setValue(BigDecimal.valueOf(-1))
        );

        assertEquals("Money cannot be less than or equals Zero", exception.getMessage());


    }
    @Test
    public void TestThat_zeroAmount_moneyIsNotValid(){
        InvalidMoneyException exception = assertThrows(
                InvalidMoneyException.class,
                () -> money.setValue(BigDecimal.valueOf(0))
        );

        assertEquals("Money cannot be less than or equals Zero", exception.getMessage());


    }

    @Test
    public void TestThat_validAmountIsSet_GetMoney_moneyIsReturned(){
        money.setValue(BigDecimal.valueOf(1000));
        BigDecimal actual = money.getValue();

        assertEquals(BigDecimal.valueOf(1000), actual);


    }

    @Test
    public void TestThat_addMoney_sumIsReturned(){
        money.setValue(BigDecimal.valueOf(500));
        Money anotherMoney = new Money(BigDecimal.valueOf(500));

        Money sum = money.add(anotherMoney);

        assertEquals(new Money(BigDecimal.valueOf(1000)), sum);


    }



    @Test
    public void TestThat_subTractSamevalue_throwException(){
        money.setValue(BigDecimal.valueOf(500));
        Money anotherMoney = new Money(BigDecimal.valueOf(500));
       InvalidMoneyException exception = assertThrows(
               InvalidMoneyException.class,
               ()-> money.subtract(anotherMoney)
       );

        assertEquals("Money cannot be less than or equals Zero", exception.getMessage());


    }


    @Test
    public void TestThat_subTractBigMoneyFromSmallMoney_throwException(){
        money.setValue(BigDecimal.valueOf(500));
        Money anotherMoney = new Money(BigDecimal.valueOf(1000));
        InvalidMoneyException exception = assertThrows(
                InvalidMoneyException.class,
                ()-> money.subtract(anotherMoney)
        );

        assertEquals("Money cannot be less than or equals Zero", exception.getMessage());


    }

    @Test
    public void TestThat_subTractCorectly_returnDifference(){
        money.setValue(BigDecimal.valueOf(1000));
        Money anotherMoney = new Money(BigDecimal.valueOf(500));
        Money difference = money.subtract(anotherMoney);

        assertEquals(new Money(BigDecimal.valueOf(500)), difference);


    }






}
