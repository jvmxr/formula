package com.systam.utils.formula;

import com.systam.utils.formula.exception.ParseException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NashornComparator {
    private final ScriptEngine engine;
    private final boolean available;
    private static final Pattern POWER_PATTERN = Pattern.compile("(\\d+\\.?\\d*|\\w+)\\s*\\*\\*\\s*(\\d+\\.?\\d*|\\w+)");

    public NashornComparator() {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("JavaScript");
        this.available = (engine != null);
    }

    public boolean isAvailable() {
        return available;
    }

    private String convertPowerOperator(String expression) {
        Matcher matcher = POWER_PATTERN.matcher(expression);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String left = matcher.group(1);
            String right = matcher.group(2);
            matcher.appendReplacement(result, "Math.pow(" + left + "," + right + ")");
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public double evaluateWithJS(String expression, Map<String, Double> variables)
            throws ScriptException {
        if (engine == null) {
            throw new ScriptException("JavaScript engine not available");
        }
        StringBuilder jsCode = new StringBuilder();
        for (Map.Entry<String, Double> entry : variables.entrySet()) {
            jsCode.append("var ").append(entry.getKey()).append(" = ")
                   .append(entry.getValue()).append("; ");
        }
        jsCode.append(convertPowerOperator(expression));
        Object result = engine.eval(jsCode.toString());
        if (result == null) {
            throw new ScriptException("Expression returned null");
        }
        return ((Number) result).doubleValue();
    }

    /**
     * Compara el resultado de Java con el de JavaScript.
     *
     * @throws IllegalStateException si Nashorn no esta disponible, o si el motor de JS
     *         no puede evaluar la expresion; en ambos casos el test falla en lugar de
     *         reportar una coincidencia que nunca se verifico
     */
    public boolean resultsMatch(String expression, Map<String, Double> variables, 
                               double javaResult, double delta) {
        if (!available) {
            throw new IllegalStateException(
                "JavaScript engine not available: call isAvailable() and skip the test");
        }
        try {
            double jsResult = evaluateWithJS(expression, variables);
            return Math.abs(jsResult - javaResult) < delta;
        } catch (ScriptException e) {
            throw new IllegalStateException(
                "JavaScript engine could not evaluate: " + expression, e);
        }
    }
}
