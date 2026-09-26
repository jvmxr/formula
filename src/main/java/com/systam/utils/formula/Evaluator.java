package com.systam.utils.formula;

import com.systam.utils.formula.exception.EvaluationException;
import com.systam.utils.formula.lexer.TokenType;
import com.systam.utils.formula.parser.BinaryNode;
import com.systam.utils.formula.parser.ExpressionNode;
import com.systam.utils.formula.parser.ExpressionVisitor;
import com.systam.utils.formula.parser.NumberNode;
import com.systam.utils.formula.parser.UnaryNode;
import com.systam.utils.formula.parser.VariableNode;
import java.util.Map;

/**
 * Evaluador de nodos AST (Abstract Syntax Tree) que implementa el patr&oacute;n Visitor.
 *
 * <p>Esta clase recorre el AST generado por el {@link Parser} y calcula el resultado
 * de la expresi&oacute;n matem&aacute;tica.
 *
 * <p>Operaciones soportadas:
 * <ul>
 *   <li>Suma (+)</li>
 *   <li>Resta (-)</li>
 *   <li>Multiplicaci&oacute;n (*)</li>
 *   <li>Divisi&oacute;n (/)</li>
 *   <li>M&oacute;dulo (%)</li>
 *   <li>Potencia (**)</li>
 * </ul>
 *
 * @see ExpressionVisitor
 * @see Parser
 */
public class Evaluator implements ExpressionVisitor<Double> {
    private final Map<String, Double> variables;

    /**
     * Crea un Evaluator con el mapa de variables especificado.
     *
     * @param variables mapa de nombres de variables a sus valores
     */
    public Evaluator(Map<String, Double> variables) {
        this.variables = variables;
    }

    /**
     * Eval&uacute;a un nodo de expresi&oacute;n y devuelve el resultado.
     *
     * @param node el nodo ra&iacute;z del AST
     * @param variables mapa de variables para la evaluaci&oacute;n
     * @return el resultado num&eacute;rico de la evaluaci&oacute;n
     */
    public static double evaluate(ExpressionNode node, Map<String, Double> variables) {
        Evaluator evaluator = new Evaluator(variables);
        return node.accept(evaluator);
    }

    /**
     * Eval&uacute;a un nodo num&eacute;rico.
     *
     * @param node el nodo a evaluar
     * @return el valor del nodo
     */
    @Override
    public Double visitNumberNode(NumberNode node) {
        return node.getValue();
    }

    /**
     * Eval&uacute;a un nodo variable.
     *
     * <p>Si la variable no existe, lanza {@link EvaluationException}.
     * Si el valor es {@code null}, retorna {@code 0.0}.
     *
     * @param node el nodo a evaluar
     * @return el valor de la variable, o 0.0 si es null
     * @throws EvaluationException si la variable no est&aacute; definida
     */
    @Override
    public Double visitVariableNode(VariableNode node) {
        String name = node.getName();
        if (!variables.containsKey(name)) {
            throw new EvaluationException(String.format("Undefined variable: %s", name));
        }
        Double value = variables.get(name);
        return value != null ? value : 0.0;
    }

    /**
     * Eval&uacute;a un nodo de operaci&oacute;n binaria.
     *
     * @param node el nodo a evaluar
     * @return el resultado de la operaci&oacute;n binaria
     * @throws EvaluationException si hay divisi&oacute;n por cero o operador desconocido
     */
    @Override
    public Double visitBinaryNode(BinaryNode node) {
        double left = node.getLeft().accept(this);
        double right = node.getRight().accept(this);
        TokenType operator = node.getOperator();

        switch (operator) {
            case PLUS:
                return left + right;
            case MINUS:
                return left - right;
            case MULTIPLY:
                return left * right;
            case DIVIDE:
                if (right == 0.0) {
                    throw new EvaluationException("Division by zero");
                }
                return left / right;
            case MODULO:
                if (right == 0.0) {
                    throw new EvaluationException("Modulo by zero");
                }
                return left % right;
            case POWER:
                return Math.pow(left, right);
            default:
                throw new EvaluationException(
                    String.format("Unknown operator: %s", operator));
        }
    }

    /**
     * Eval&uacute;a un nodo de operaci&oacute;n unaria.
     *
     * @param node el nodo a evaluar
     * @return el resultado de la operaci&oacute;n unaria
     * @throws EvaluationException si el operador es desconocido
     */
    @Override
    public Double visitUnaryNode(UnaryNode node) {
        double operand = node.getOperand().accept(this);
        switch (node.getOperator()) {
            case MINUS:
                return -operand;
            case PLUS:
                return operand;
            default:
                throw new EvaluationException(
                    String.format("Unknown unary operator: %s", node.getOperator()));
        }
    }
}
