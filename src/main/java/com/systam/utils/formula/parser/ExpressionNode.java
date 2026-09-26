package com.systam.utils.formula.parser;

/**
 * Interfaz base para todos los nodos del &Aacute;rbol de Sintaxis Abstracta (AST).
 *
 * <p>Representa una expresi&oacute;n en el AST. Implementa el patr&oacute;n Visitor
 * mediante el m&eacute;todo {@code accept()}.
 *
 * @see ExpressionVisitor
 * @see NumberNode
 * @see VariableNode
 * @see BinaryNode
 * @see UnaryNode
 */
public interface ExpressionNode {
    /**
     * Acepta un visitor para ejecutar una operaci&oacute;n sobre este nodo.
     *
     * @param visitor el visitor a aplicar
     * @param <T> el tipo de retorno del visitor
     * @return el resultado de la operaci&oacute;n del visitor
     */
    <T> T accept(ExpressionVisitor<T> visitor);
}
