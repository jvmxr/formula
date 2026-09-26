package com.systam.utils.formula.parser;

import com.systam.utils.formula.exception.ParseException;
import com.systam.utils.formula.lexer.Lexer;
import com.systam.utils.formula.lexer.Token;
import com.systam.utils.formula.lexer.TokenType;
import java.util.List;

/**
 * Parser para expresiones matem&aacute;ticas que implementa un parser recursivo descendente.
 *
 * <p>Convierte una cadena de tokens (generada por el {@link com.systam.utils.formula.lexer.Lexer})
 * en un &Aacute;rbol de Sintaxis Abstracta (AST).
 *
 * <p>Precedencia de operadores (de menor a mayor):
 * <ol>
 *   <li>Suma/Resta (+, -)</li>
 *   <li>Multiplicaci&oacute;n/Divisi&oacute;n/M&oacute;dulo (*, /, %)</li>
 *   <li>Potencia (**)</li>
 *   <li>Operadores unarios (+, -)</li>
 * </ol>
 *
 * @see Lexer
 * @see ExpressionNode
 */
public class Parser {
    /**
     * M&aacute;xima profundidad de anidamiento recursivo aceptada.
     *
     * <p>El contador solo se incrementa en los puntos donde la recursi&oacute;n refleja
     * anidamiento escrito por el usuario, no en el descenso fijo de la gramática:
     * un nivel por par&eacute;ntesis ({@code parseNested}), uno por operando de
     * {@code **} encadenado ({@code parsePower}) y uno por operador unario
     * ({@code parseUnary}).
     *
     * <p>Superarla lanza {@link ParseException} en lugar de propagar un
     * {@link StackOverflowError}, de modo que una expresi&oacute;n patol&oacute;gica produzca
     * un error de parseo con posici&oacute;n.
     */
    public static final int MAX_DEPTH = 64;

    private final List<Token> tokens;
    private int current;
    private int depth;

    /**
     * Crea un Parser con la lista de tokens a parsear.
     *
     * @param tokens lista de tokens generada por el Lexer
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.current = 0;
    }

    /**
     * Parsea una expresi&oacute;n matem&aacute;tica y devuelve el nodo ra&iacute;z del AST.
     *
     * <p>M&eacute;todo de entrada que crea un Lexer, tokeniza la expresi&oacute;n
     * y luego la parsea.
     *
     * @param expression la expresi&oacute;n a parsear
     * @return el nodo ra&iacute;z del AST
     * @throws ParseException si hay errores de sintaxis
     */
    public static ExpressionNode parse(String expression) {
        Lexer lexer = new Lexer(expression);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);
        return parser.parseExpression();
    }

    /**
     * Inicia el parseo de una expresi&oacute;n y exige que se consuman todos los tokens.
     *
     * <p>Tras la expresi&oacute;n solo puede quedar el token {@link TokenType#EOF}. Cualquier
     * token sobrante indica una expresi&oacute;n mal formada y lanza {@link ParseException};
     * sin esta comprobaci&oacute;n el parser aceptar&iacute;a en silencio entradas como
     * {@code "1 5"} o {@code "(10 + 5))"} y devolver&iacute;a un resultado incorrecto.
     *
     * @return el nodo de expresi&oacute;n parseado
     * @throws ParseException si hay errores de sintaxis o tokens sin consumir
     */
    public ExpressionNode parseExpression() {
        ExpressionNode expression = parseAddition();
        if (!isAtEnd()) {
            Token token = peek();
            throw new ParseException(
                String.format("Unexpected token '%s'", token.getLexeme()),
                token.getPosition());
        }
        return expression;
    }

    private ExpressionNode parseAddition() {
        ExpressionNode left = parseMultiplication();
        while (check(TokenType.PLUS) || check(TokenType.MINUS)) {
            TokenType operator = advance().getType();
            ExpressionNode right = parseMultiplication();
            left = new BinaryNode(left, operator, right);
        }
        return left;
    }

    /**
     * Parsea una expresi&oacute;n sin exigir EOF.
     *
     * <p>Es la variante usada dentro de par&eacute;ntesis, donde el token siguiente es
     * {@code ')'} y no {@link TokenType#EOF}. La comprobaci&oacute;n de tokens sobrantes
     * corresponde &uacute;nicamente a la entrada p&uacute;blica {@link #parseExpression()}.
     *
     * @return el nodo de expresi&oacute;n parseado
     */
    private ExpressionNode parseNested() {
        enterRecursion();
        try {
            return parseAddition();
        } finally {
            depth--;
        }
    }

    private ExpressionNode parseMultiplication() {
        ExpressionNode left = parsePower();
        while (check(TokenType.MULTIPLY) || check(TokenType.DIVIDE) || check(TokenType.MODULO)) {
            TokenType operator = advance().getType();
            ExpressionNode right = parsePower();
            left = new BinaryNode(left, operator, right);
        }
        return left;
    }

    private ExpressionNode parsePower() {
        ExpressionNode left = parseUnary();
        if (check(TokenType.POWER)) {
            Token operator = advance();
            enterRecursion();
            try {
                ExpressionNode right = parsePower();
                return new BinaryNode(left, operator.getType(), right);
            } finally {
                depth--;
            }
        }
        return left;
    }

    private ExpressionNode parseUnary() {
        if (check(TokenType.MINUS) || check(TokenType.PLUS)) {
            Token operator = advance();
            enterRecursion();
            try {
                return operator.getType() == TokenType.MINUS
                        ? new UnaryNode(TokenType.MINUS, parseUnary())
                        : parseUnary();
            } finally {
                depth--;
            }
        }
        return parsePrimary();
    }

    private void enterRecursion() {
        if (++depth > MAX_DEPTH) {
            throw new ParseException(
                String.format("Expression nesting exceeds the maximum depth of %d", MAX_DEPTH),
                peek().getPosition());
        }
    }

    private ExpressionNode parsePrimary() {
        if (check(TokenType.NUMBER)) {
            double value = advance().getNumberValue();
            return new NumberNode(value);
        }
        if (check(TokenType.IDENTIFIER)) {
            String name = advance().getLexeme();
            return new VariableNode(name);
        }
        if (check(TokenType.LPAREN)) {
            advance();
            ExpressionNode expr = parseNested();
            if (!check(TokenType.RPAREN)) {
                throw new ParseException("Expected ')' after expression", current);
            }
            advance();
            return expr;
        }
        Token token = peek();
        throw new ParseException(
            String.format("Unexpected token '%s'", token.getLexeme()),
            token.getPosition());
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }
        return peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }
}
