package com.systam.utils.formula.parser;

/**
 * Nodo del AST que representa una variable.
 *
 * <p>Almacena el nombre de una variable que ser&aacute; resuelta
 * en tiempo de evaluaci&oacute;n.
 *
 * @see ExpressionNode
 */
public class VariableNode implements ExpressionNode {
    private final String name;

    /**
     * Crea un nodo variable con el nombre especificado.
     *
     * @param name el nombre de la variable
     */
    public VariableNode(String name) {
        this.name = name;
    }

    /**
     * @return el nombre de la variable
     */
    public String getName() {
        return name;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitVariableNode(this);
    }
}
