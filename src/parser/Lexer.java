package parser;

import parser.token.*;

import java.math.BigDecimal;
import java.util.*;

public class Lexer {
    private static final String DELIMITERS = " +-*/^%()";

    public List<Token> getTokens(String source) {
        StringTokenizer tokenizer = new StringTokenizer(source, DELIMITERS, true);
        List<Token> tokens = new ArrayList<>();

        boolean isNextNegativeNumber = false;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.isBlank()) {
                continue;
            } else if (isNumber(token)) {
                if(isNextNegativeNumber) {
                    tokens.add(new NumberToken(new BigDecimal(token).multiply(new BigDecimal(-1))));
                    isNextNegativeNumber = false;
                    continue;
                }

                tokens.add(new NumberToken(new BigDecimal(token)));
                continue;
            }

            if(!tokens.isEmpty()) {
                Token previousToken = tokens.getLast();
                if (previousToken.type() != TokenType.NUMBER && token.equals("-"))
                    isNextNegativeNumber = true;
            } else if(token.equals("-"))
                isNextNegativeNumber = true;

            if(isNextNegativeNumber)
                continue;

            tokens.add(
                    switch (token) {
                        case "+" -> new BinaryOperationToken(OperationType.PLUS);
                        case "-" -> new BinaryOperationToken(OperationType.MINUS);
                        case "*" -> new BinaryOperationToken(OperationType.MULTIPLY);
                        case "/" -> new BinaryOperationToken(OperationType.DIVIDE);
                        case "^" -> new BinaryOperationToken(OperationType.POW);
                        case "%" -> {
                            Token lastToken = tokens.getLast();
                            if(lastToken.type() != TokenType.NUMBER)
                                throw new RuntimeException("Unexpected token: " + lastToken + "%");
                            NumberToken percent = (NumberToken) lastToken;
                            tokens.removeLast();
                            yield  new NumberToken(percent.value().divide(new BigDecimal(100)));
                        }
                        case "(" -> new OtherToken(TokenType.OPEN_BRACKET);
                        case ")" -> new OtherToken(TokenType.CLOSE_BRACKET);
                        default -> throw new RuntimeException("Unexpected token: " + token);
                    }
            );
        }
        return tokens;
    }

    public List<Token> convertToPostfix(List<Token> source) {
        List<Token> postfixExpression = new ArrayList<>();
        Deque<Token> operationStack = new LinkedList<>();

        for (Token token : source) {
            switch (token.type()) {
                case NUMBER -> postfixExpression.add(token);
                case OPEN_BRACKET -> operationStack.push(token);
                case CLOSE_BRACKET -> {
                    while (!operationStack.isEmpty() && operationStack.peek().type() != TokenType.OPEN_BRACKET) {
                        postfixExpression.add(operationStack.pop());
                    }
                    operationStack.pop(); // открывающая скобка
                }
                case BINARY_OPERATION -> {
                    while (!operationStack.isEmpty() && getPriority(operationStack.peek()) >= getPriority(token)) {
                        postfixExpression.add(operationStack.pop());
                    }
                    operationStack.push(token);
                }
            }
        }

        while (!operationStack.isEmpty()) {
            postfixExpression.add(operationStack.pop());
        }
        return postfixExpression;
    }

    private int getPriority(Token token) {
        if (token instanceof BinaryOperationToken operation) {
            return switch (operation.operationType()) {
                case PLUS, MINUS -> 1;
                case MULTIPLY, DIVIDE -> 2;
                case POW -> 3;
            };
        }
        return 0; // для открывающей скобки
    }


    private boolean isNumber(String token) {
        boolean hasDot = false;
        for (int i = 0; i < token.length(); i++) {
            if (!Character.isDigit(token.charAt(i))) {
                if(token.charAt(i) != '.' && !hasDot)
                    return false;
                hasDot = true;
            }
        }
        return true;
    }
}
