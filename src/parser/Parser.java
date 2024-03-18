package parser;

import parser.token.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;

public class Parser {
    private static Lexer lexer = new Lexer();
    private static StackMachine stackMachine = new StackMachine();

    public static BigDecimal calculate(String expression) {
        List<Token> tokens = lexer.getTokens(expression);
        List<Token> postfixExpression = lexer.convertToPostfix(tokens);
        BigDecimal result = stackMachine.evaluate(postfixExpression);
        System.out.println(result);
        return result;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String input;
        while(!(input = reader.readLine()).equals("exit")) {
            Parser.calculate(input);
        }
    }
}
