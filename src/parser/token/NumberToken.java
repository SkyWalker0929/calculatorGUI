package parser.token;

import java.math.BigDecimal;

public record NumberToken(BigDecimal value) implements Token {
    @Override
    public TokenType type() {
        return TokenType.NUMBER;
    }
}
