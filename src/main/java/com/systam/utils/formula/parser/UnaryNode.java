package com.systam.utils.formula.parser;

import com.systam.utils.formula.lexer.TokenType;

/**
 * Nodo del AST que representa una operaci&oacute;n unaria.
 *
 * <p>Una operaci&oacute;n unaria tiene la forma: {@code operador operando}.
 * Ejemplos: {@code -x}, {@code +5}.
 *
 * @see ExpressionNode
 * @see TokenType
 */
public class UnaryNode implements ExpressionNode {
    private final TokenType operator;
    private final ExpressionNode operand;

    /**
     * Crea un nodo de operaci&oacute;n unaria.
     *
     * @param operator operador unario (+ o -)
     * @param operand   expresi&oacute;n sobre la que opera
     */
    public UnaryNode(TokenType operator, ExpressionNode operand) {
        this.operator = operator;
        this.operand = operand;
    }

    /**
     * @return el operador unario
     */
    public TokenType getOperator() {
        return operator;
    }

    /**
     * @return el operando de la operaci&oacute;n
     */
    public ExpressionNode getOperand() {
        return operand;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitUnaryNode(this);
    }
}
