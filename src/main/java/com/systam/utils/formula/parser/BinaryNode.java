package com.systam.utils.formula.parser;

import com.systam.utils.formula.lexer.TokenType;

/**
 * Nodo del AST que representa una operaci&oacute;n binaria.
 *
 * <p>Una operaci&oacute;n binaria tiene la forma: {@code operando1 operador operando2}.
 * Ejemplos: {@code a + b}, {@code x * y}, {@code 5 / 2}.
 *
 * @see ExpressionNode
 * @see TokenType
 */
public class BinaryNode implements ExpressionNode {
    private final ExpressionNode left;
    private final TokenType operator;
    private final ExpressionNode right;

    /**
     * Crea un nodo de operaci&oacute;n binaria.
     *
     * @param left     expresi&oacute;n izquierda
     * @param operator operador binario
     * @param right    expresi&oacute;n derecha
     */
    public BinaryNode(ExpressionNode left, TokenType operator, ExpressionNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    /**
     * @return la expresi&oacute;n izquierda
     */
    public ExpressionNode getLeft() {
        return left;
    }

    /**
     * @return el operador de la operaci&oacute;n
     */
    public TokenType getOperator() {
        return operator;
    }

    /**
     * @return la expresi&oacute;n derecha
     */
    public ExpressionNode getRight() {
        return right;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitBinaryNode(this);
    }
}
