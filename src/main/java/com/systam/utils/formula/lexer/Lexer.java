package com.systam.utils.formula.lexer;

import com.systam.utils.formula.exception.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lexer (tokenizador) para expresiones matem&aacute;ticas.
 *
 * <p>Convierte una cadena de entrada en una lista de Tokens para ser parseados
 * por el {@link com.systam.utils.formula.parser.Parser}.
 *
 * <p>Tokens reconocidos:
 * <ul>
 *   <li>N&uacute;meros (enteros y decimales)</li>
 *   <li>Identificadores (nombres de variables)</li>
 *   <li>Operadores: +, -, *, /, %, **</li>
 *   <li>Par&eacute;ntesis: (, )</li>
 * </ul>
 *
 * @see Token
 * @see TokenType
 * @see com.systam.utils.formula.parser.Parser
 */
public class Lexer {
    private final String input;
    private int current;
    private final List<Token> tokens;

    /**
     * Crea un Lexer con la expresi&oacute;n de entrada.
     *
     * @param input la expresi&oacute;n a tokenizar
     */
    public Lexer(String input) {
        this.input = input;
        this.current = 0;
        this.tokens = new ArrayList<>();
    }

    /**
     * Tokeniza la expresi&oacute;n de entrada.
     *
     * @return lista de tokens
     * @throws ParseException si hay un car&aacute;cter inv&aacute;lido
     */
    public List<Token> tokenize() {
        while (!isAtEnd()) {
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", current));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '+':
                addToken(TokenType.PLUS);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '*':
                if (match('*')) {
                    addToken(TokenType.POWER);
                } else {
                    addToken(TokenType.MULTIPLY);
                }
                break;
            case '/':
                addToken(TokenType.DIVIDE);
                break;
            case '%':
                addToken(TokenType.MODULO);
                break;
            case '(':
                addToken(TokenType.LPAREN);
                break;
            case ')':
                addToken(TokenType.RPAREN);
                break;
            case ' ':
            case '\t':
            case '\r':
            case '\n':
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    throw new ParseException(
                        String.format("Unexpected character '%s'", c), current - 1);
                }
        }
    }

    private void number() {
        int start = current - 1;
        while (isDigit(peek())) {
            advance();
        }
        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) {
                advance();
            }
        }
        String numberStr = input.substring(start, current);
        try {
            double value = Double.parseDouble(numberStr);
            tokens.add(new Token(TokenType.NUMBER, value, numberStr, start));
        } catch (NumberFormatException e) {
            throw new ParseException(
                String.format("Invalid number format: %s", numberStr), start);
        }
    }

    private void identifier() {
        int start = current - 1;
        while (isAlphaNumeric(peek())) {
            advance();
        }
        String text = input.substring(start, current);
        tokens.add(new Token(TokenType.IDENTIFIER, text, start));
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return input.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= input.length()) {
            return '\0';
        }
        return input.charAt(current + 1);
    }

    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (input.charAt(current) != expected) {
            return false;
        }
        current++;
        return true;
    }

    private char advance() {
        return input.charAt(current++);
    }

    private boolean isAtEnd() {
        return current >= input.length();
    }

    private void addToken(TokenType type) {
        tokens.add(new Token(type, input.substring(current - 1, current), current - 1));
    }
}
