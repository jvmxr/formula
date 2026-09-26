package com.systam.utils.formula.parser;

/**
 * Nodo del AST que representa un n&uacute;mero literal.
 *
 * <p>Almacena un valor num&eacute;rico (double) que puede ser entero o decimal.
 *
 * @see ExpressionNode
 */
public class NumberNode implements ExpressionNode {
    private final double value;

    /**
     * Crea un nodo num&eacute;rico con el valor especificado.
     *
     * @param value el valor num&eacute;rico
     */
    public NumberNode(double value) {
        this.value = value;
    }

    /**
     * @return el valor num&eacute;rico
     */
    public double getValue() {
        return value;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitNumberNode(this);
    }
}
