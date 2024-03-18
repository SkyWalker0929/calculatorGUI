package parser;

import parser.token.BinaryOperationToken;
import parser.token.NumberToken;
import parser.token.Token;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class StackMachine {
    public BigDecimal evaluate(List<Token> postfixExpression) {
        Deque<BigDecimal> valueStack = new LinkedList<>();
        for (Token token : postfixExpression) {
            if (token instanceof NumberToken number) {
                valueStack.push(number.value());
            } else if (token instanceof BinaryOperationToken operation) {
                BigDecimal right = valueStack.pop();
                BigDecimal left = valueStack.pop();
                BigDecimal result = switch (operation.operationType()) {
                    case PLUS -> left.add(right);
                    case MINUS -> left.subtract(right);
                    case MULTIPLY -> left.multiply(right);
                    case DIVIDE -> {
                        if (right.equals(new BigDecimal(0))) {
                            throw new RuntimeException("Divide by zero!");
                        }
                        yield left.divide(right, 15, RoundingMode.HALF_UP);
                    }
                    case POW -> BigDecimal.valueOf(Math.pow(left.doubleValue(), right.doubleValue()));
                };
                valueStack.push(result);
            }
        }
        return valueStack.pop();
    }

}
