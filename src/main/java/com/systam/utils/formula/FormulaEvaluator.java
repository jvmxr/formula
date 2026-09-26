package com.systam.utils.formula;

import com.systam.utils.formula.exception.EvaluationException;
import com.systam.utils.formula.exception.ParseException;
import com.systam.utils.formula.parser.ExpressionNode;
import com.systam.utils.formula.parser.Parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Evaluador de expresiones matem&aacute;ticas en un lenguaje estilo C.
 *
 * <p>Esta clase proporciona una API fluida para evaluar expresiones matem&aacute;ticas
 * con soporte para variables, operaciones aritm&eacute;ticas b&aacute;sicas y precedencia de operadores.
 *
 * <p>Ejemplo de uso:
 * <pre>{@code
 * double result = new FormulaEvaluator()
 *     .withVariableNumber("x", 10.0)
 *     .withVariableString("y", "5.0")
 *     .evaluate("x + y");
 * }</pre>
 *
 * @see Evaluator
 * @see Parser
 */
public class FormulaEvaluator {
    private Map<String, Double> variables;

    /**
     * Crea un nuevo FormulaEvaluator sin variables inicializadas.
     */
    public FormulaEvaluator() {
        this.variables = new HashMap<>();
    }

    /**
     * Eval&uacute;a una expresi&oacute;n matem&aacute;tica usando las variables configuradas.
     *
     * <p>Este m&eacute;todo usa las variables establecidas mediante {@link #withVariableNumber(String, Double)}
     * y {@link #withVariableString(String, String)}.
     *
     * @param expression la expresi&oacute;n a evaluar (no puede ser null o vac&iacute;a)
     * @return el resultado de la evaluaci&oacute;n
     * @throws ParseException si la expresi&oacute;n no puede ser parseada
     * @throws EvaluationException si la evaluaci&oacute;n falla (e.g., variable indefinida)
     */
    public double evaluate(String expression) {
        if (this.variables == null) {
            this.variables = new HashMap<>();
        }
        return evaluate(expression, this.variables);
    }

    /**
     * Eval&uacute;a una expresi&oacute;n con variables adicionales.
     *
     * <p>Las variables establecidas mediante {@code withVariable*} se fusionan con
     * {@code additionalVariables}. En caso de conflicto, {@code additionalVariables} tiene prioridad.
     *
     * @param expression la expresi&oacute;n a evaluar (no puede ser null o vac&iacute;a)
     * @param additionalVariables variables adicionales para la evaluaci&oacute;n (puede ser null)
     * @return el resultado de la evaluaci&oacute;n
     * @throws ParseException si la expresi&oacute;n no puede ser parseada
     * @throws EvaluationException si la evaluaci&oacute;n falla
     */
    public double evaluate(String expression, Map<String, Double> additionalVariables) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new ParseException("Expression cannot be null or empty", 0);
        }
        ExpressionNode ast = Parser.parse(expression);
        Map<String, Double> allVariables = new HashMap<>(variables);
        if (additionalVariables != null) {
            allVariables.putAll(additionalVariables);
        }
        return Evaluator.evaluate(ast, allVariables);
    }

    /**
     * Agrega una variable con valor num&eacute;rico.
     *
     * <p>M&eacute;todo flu&iacute;do que permite encadenar llamadas:
     * <pre>{@code
     * evaluator.withVariableNumber("x", 10.0).withVariableNumber("y", 5.0);
     * }</pre>
     *
     * @param name nombre de la variable
     * @param value valor num&eacute;rico (puede ser null)
     * @return esta instancia para encadenamiento
     */
    public FormulaEvaluator withVariableNumber(String name, Double value) {
        variables.put(name, value);
        return this;
    }

    /**
     * Agrega una variable con valor en formato string.
     *
     * <p>Si el valor es {@code null} o la cadena "null" (case-insensitive),
     * la variable se almacenar&aacute; como {@code null} y se eval&uacute;a como {@code 0}.
     *
     * <p>Un valor no num&eacute;rico se reporta como {@link EvaluationException} para mantener
     * el contrato de excepciones de la API, en lugar de propagar
     * {@link NumberFormatException}.
     *
     * <p>M&eacute;todo flu&oacute;do que permite encadenar llamadas:
     * <pre>{@code
     * evaluator.withVariableString("x", "10.0").withVariableString("y", "null");
     * }</pre>
     *
     * @param name nombre de la variable
     * @param value valor como string (puede ser null o "null")
     * @return esta instancia para encadenamiento
     * @throws EvaluationException si el valor no es un n&uacute;mero v&aacute;lido
     */
    public FormulaEvaluator withVariableString(String name, String value) {
        if (value == null || value.equalsIgnoreCase("null")) {
            variables.put(name, null);
        } else {
            try {
                variables.put(name, Double.parseDouble(value));
            } catch (NumberFormatException e) {
                throw new EvaluationException(
                    String.format("Variable '%s' has a non-numeric value: '%s'", name, value), e);
            }
        }
        return this;
    }

    /**
     * Obtiene el valor de una variable.
     *
     * @param name nombre de la variable
     * @return el valor de la variable
     * @throws EvaluationException si la variable no existe o es null
     */
    public double getVariable(String name) {
        Double value = variables.get(name);
        if (value == null) {
            throw new EvaluationException(String.format("Undefined variable: %s", name));
        }
        return value;
    }

    /**
     * Obtiene una copia del mapa de variables.
     *
     * @return una copia mutable del mapa de variables
     */
    public Map<String, Double> getVariablesCopy() {
        return new HashMap<>(variables);
    }
}
