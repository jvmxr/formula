package com.systam.utils.formula.parser;

/**
 * Interfaz Visitor para el patr&oacute;n Visitor aplicado al AST.
 *
 * <p>Permite definir operaciones sobre los nodos del AST sin modificar
 * las clases de los nodos.
 *
 * <p>Implementaciones t&iacute;picas:
 * <ul>
 *   <li>{@link Evaluator} - Eval&uacute;a expresiones</li>
 *   <li>Pretty printer - Imprime &aacute;rboles</li>
 *   <li>Optimizador - Optimiza expresiones</li>
 * </ul>
 *
 * @param <T> el tipo de retorno de las visitas
 * @see ExpressionNode
 * @see Evaluator
 */
public interface ExpressionVisitor<T> {
    /**
     * Visita un nodo num&eacute;rico.
     *
     * @param node el nodo a visitar
     * @return resultado de la operaci&oacute;n
     */
    T visitNumberNode(NumberNode node);

    /**
     * Visita un nodo variable.
     *
     * @param node el nodo a visitar
     * @return resultado de la operaci&oacute;n
     */
    T visitVariableNode(VariableNode node);

    /**
     * Visita un nodo de operaci&oacute;n binaria.
     *
     * @param node el nodo a visitar
     * @return resultado de la operaci&oacute;n
     */
    T visitBinaryNode(BinaryNode node);

    /**
     * Visita un nodo de operaci&oacute;n unaria.
     *
     * @param node el nodo a visitar
     * @return resultado de la operaci&oacute;n
     */
    T visitUnaryNode(UnaryNode node);
}
