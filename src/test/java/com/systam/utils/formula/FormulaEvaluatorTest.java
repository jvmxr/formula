package com.systam.utils.formula;

import com.systam.utils.formula.exception.EvaluationException;
import com.systam.utils.formula.exception.ParseException;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("FormulaEvaluator Tests - Comparados con Nashorn")
class FormulaEvaluatorTest {

    private FormulaEvaluator evaluator;
    //private Map<String, Double> variables;
    private NashornComparator nashorn;

    @BeforeEach
    void setUp() {
        evaluator = new FormulaEvaluator()
            .withVariableNumber("x", 10.0)
            .withVariableNumber("y", 5.0)
            .withVariableNumber("a", 3.0)
            .withVariableNumber("b", 2.0)
            .withVariableNumber("d", 2.5)
            .withVariableString("strNum", "4.5")
            .withVariableString("strNull", "null");

        //variables = evaluator.getVariablesCopy();
        
        nashorn = new NashornComparator();

        Assumptions.assumeTrue(nashorn.isAvailable(),
            "Nashorn no disponible: los tests de paridad con JavaScript se omiten");
    }

    private void assertMatchesJS(String expression) {
        double javaResult = evaluator.evaluate(expression);//, variables);
        double jsResult = 0;
        boolean match = false;
        
        System.out.println("\n========================================");
        System.out.println("EXPRESSION: " + expression);
        if (!evaluator.getVariablesCopy().isEmpty()) {
            System.out.print("VARIABLES:  ");
            evaluator.getVariablesCopy().forEach((k, v) -> System.out.print(k + "=" + v + " "));
            System.out.println();
        }
        System.out.println("----------------------------------------");
        
        try {
            jsResult = nashorn.evaluateWithJS(expression, evaluator.getVariablesCopy());
            match = Math.abs(jsResult - javaResult) < 0.0001;
        } catch (Exception e) {
            System.out.println("JS ERROR: " + e.getMessage());
        }
        
        System.out.printf("FormulaEvaluator: %.10f%n", javaResult);
        System.out.printf("ScriptManager:    %.10f%n", jsResult);
        System.out.println("----------------------------------------");
        if (match) {
            System.out.println("MATCH: YES");
        } else {
            System.err.println("MATCH: NO (results differ)");
        }
        System.out.println("========================================");
        
        assertTrue(
            nashorn.resultsMatch(expression, evaluator.getVariablesCopy(), javaResult, 0.0001),
            String.format("JS result does not match Java result for: %s", expression)
        );
    }

    @Nested
    @DisplayName("Operaciones Aritmeticas Basicas")
    class BasicArithmeticTests {

        @Test
        @DisplayName("Suma debe coincidir con JS: 10 + 5")
        void addition_shouldMatchJS() {
            assertMatchesJS("10 + 5");
        }

        @Test
        @DisplayName("Resta debe coincidir con JS: 10 - 5")
        void subtraction_shouldMatchJS() {
            assertMatchesJS("10 - 5");
        }

        @Test
        @DisplayName("Multiplicacion debe coincidir con JS: 10 * 5")
        void multiplication_shouldMatchJS() {
            assertMatchesJS("10 * 5");
        }

        @Test
        @DisplayName("Division debe coincidir con JS: 10 / 5")
        void division_shouldMatchJS() {
            assertMatchesJS("10 / 5");
        }

        @Test
        @DisplayName("Modulo debe coincidir con JS: 17 % 5")
        void modulo_shouldMatchJS() {
            assertMatchesJS("17 % 5");
        }

        @Test
        @DisplayName("Potencia debe coincidir con JS: 2 ** 3")
        void power_shouldMatchJS() {
            assertMatchesJS("2 ** 3");
        }
    }

    @Nested
    @DisplayName("Precedencia de Operadores")
    class OperatorPrecedenceTests {

        @Test
        @DisplayName("Multiplicacion tiene mayor precedencia que suma: 10 + 5 * 2")
        void multiplication_shouldHaveHigherPrecedenceThanAddition() {
            assertMatchesJS("10 + 5 * 2");
        }

        @Test
        @DisplayName("Division tiene mayor precedencia que resta: 10 - 20 / 5")
        void division_shouldHaveHigherPrecedenceThanSubtraction() {
            assertMatchesJS("10 - 20 / 5");
        }

        @Test
        @DisplayName("Potencia tiene mayor precedencia que multiplicacion: 2 * 3 ** 2")
        void power_shouldHaveHigherPrecedenceThanMultiplication() {
            assertMatchesJS("2 * 3 ** 2");
        }

        @Test
        @DisplayName("Expresion compleja con precedencia: 2 + 3 * 4 - 6 / 2")
        void complexPrecedence_shouldMatchJS() {
            assertMatchesJS("2 + 3 * 4 - 6 / 2");
        }
    }

    @Nested
    @DisplayName("Parentesis")
    class ParenthesesTests {

        @Test
        @DisplayName("Parentesis debeOverride precedencia: (10 + 5) * 2")
        void parentheses_shouldOverridePrecedence() {
            assertMatchesJS("(10 + 5) * 2");
        }

        @Test
        @DisplayName("Parentesis anidados: ((2 + 3) * 2) + 2")
        void nestedParentheses_shouldMatchJS() {
            assertMatchesJS("((2 + 3) * 2) + 2");
        }

        @Test
        @DisplayName("Multiples pares de parentesis: (2 + 3) * (4 - 1)")
        void multipleParentheses_shouldMatchJS() {
            assertMatchesJS("(2 + 3) * (4 - 1)");
        }

        @Test
        @DisplayName("Parentesis anidados profundos: (((1 + 2)))")
        void deeplyNestedParentheses_shouldMatchJS() {
            assertMatchesJS("(((1 + 2)))");
        }

        @Test
        @DisplayName("Expresion con parentesis y variables: (x + y) * (a - b)")
        void parenthesesWithVariables_shouldMatchJS() {
            assertMatchesJS("(x + y) * (a - b)");
        }

        @Test
        @DisplayName("Mezcla de parentesis y precedencia: (2 + 3) * 4 + 6 / 2")
        void mixedParenthesesAndPrecedence_shouldMatchJS() {
            assertMatchesJS("(2 + 3) * 4 + 6 / 2");
        }
    }

    @Nested
    @DisplayName("Sustitucion de Variables")
    class VariableSubstitutionTests {

        @Test
        @DisplayName("Variable simple debe coincidir con JS")
        void singleVariable_shouldMatchJS() {
            assertMatchesJS("x");
        }

        @Test
        @DisplayName("Multiples variables deben coincidir con JS: x + y")
        void multipleVariables_shouldMatchJS() {
            assertMatchesJS("x + y");
        }

        @Test
        @DisplayName("Variables con operaciones: x * y + a ** b")
        void variablesWithOperations_shouldMatchJS() {
            assertMatchesJS("x * y + a ** b");
        }

        @Test
        @DisplayName("Variables con parentesis: (x + y) * a")
        void variablesWithParentheses_shouldMatchJS() {
            assertMatchesJS("(x + y) * a");
        }

        @Test
        @DisplayName("Todas las variables combinadas")
        void allVariablesCombined_shouldMatchJS() {
            assertMatchesJS("x + y * a - b ** 2");
        }
    }

    @Nested
    @DisplayName("Operadores Unarios")
    class UnaryOperatorTests {

        @Test
        @DisplayName("Numero negativo debe coincidir con JS")
        void negativeNumber_shouldMatchJS() {
            assertMatchesJS("-10");
        }

        @Test
        @DisplayName("Variable negativa debe coincidir con JS: -x")
        void negativeVariable_shouldMatchJS() {
            assertMatchesJS("-x");
        }

        @Test
        @DisplayName("Numero positivo debe coincidir con JS: +10")
        void positiveNumber_shouldMatchJS() {
            assertMatchesJS("+10");
        }

        @Test
        @DisplayName("Negacion con operacion: -x + y")
        void negationWithOperation_shouldMatchJS() {
            assertMatchesJS("-x + y");
        }
    }

    @Nested
    @DisplayName("Numeros Decimales")
    class DecimalNumberTests {

        @Test
        @DisplayName("Numeros decimales deben coincidir con JS")
        void decimalNumbers_shouldMatchJS() {
            assertMatchesJS("1.5 + 2.5");
        }

        @Test
        @DisplayName("Division decimal debe coincidir con JS")
        void decimalDivision_shouldMatchJS() {
            assertMatchesJS("5 / 2");
        }

        @Test
        @DisplayName("Decimal con variables debe coincidir con JS")
        void decimalWithVariables_shouldMatchJS() {
            evaluator.withVariableNumber("z", 2.5);
            assertMatchesJS("x / z");
        }
    }

    @Nested
    @DisplayName("Expresiones Complejas")
    class ComplexExpressionTests {

        @Test
        @DisplayName("Expresion compleja completa debe coincidir con JS")
        void complexExpression_shouldMatchJS() {
            assertMatchesJS("(x + y) * (a - b) + x ** 2 / a");
        }

        @Test
        @DisplayName("Expresion larga debe coincidir con JS")
        void longExpression_shouldMatchJS() {
            assertMatchesJS("1 + 2 + 3 + 4 + 5");
        }

        @Test
        @DisplayName("Expresion con muchos parentesis debe coincidir con JS")
        void manyParentheses_shouldMatchJS() {
            assertMatchesJS("((1 + 2) * (3 + 4)) + ((5 + 6) * (7 + 8))");
        }
    }

    @Nested
    @DisplayName("Manejo de Errores")
    class ErrorHandlingTests {

        @Test
        void divisionByZero_shouldThrowEvaluationException() {
            evaluator.withVariableNumber("z", 0.0);
            assertThrows(EvaluationException.class, 
                () -> evaluator.evaluate("10 / z"));//, variables));
        }

        @Test
        void undefinedVariable_shouldThrowEvaluationException() {
            assertThrows(EvaluationException.class, 
                () -> evaluator.evaluate("undefined_var + 1"));//, variables));
        }

        @Test
        void emptyExpression_shouldThrowParseException() {
            assertThrows(ParseException.class, 
                () -> evaluator.evaluate(""));//, variables));
        }

        @Test
        void nullExpression_shouldThrowParseException() {
            assertThrows(ParseException.class, 
                () -> evaluator.evaluate(null));//, variables));
        }

        @Test
        void unmatchedParentheses_shouldThrowParseException() {
            assertThrows(ParseException.class, 
                () -> evaluator.evaluate("(10 + 5"));//, variables));
        }
    }

    @Nested
    @DisplayName("Parseo Estricto")
    class StrictParsingTests {

        @Test
        @DisplayName("Tokens sobrantes tras un numero deben lanzar ParseException")
        void trailingTokens_shouldThrowParseException() {
            assertThrows(ParseException.class,
                () -> evaluator.evaluate("1 5"));
        }

        @Test
        @DisplayName("Parentesis de cierre sobrantes deben lanzar ParseException")
        void extraClosingParenthesis_shouldThrowParseException() {
            assertThrows(ParseException.class,
                () -> evaluator.evaluate("(10 + 5))"));
        }

        @Test
        @DisplayName("Expresion incompleta debe lanzar ParseException")
        void incompleteExpression_shouldThrowParseException() {
            assertThrows(ParseException.class,
                () -> evaluator.evaluate("2 +"));
        }

        @Test
        @DisplayName("Dos expressions concatenadas deben lanzar ParseException")
        void twoExpressions_shouldThrowParseException() {
            assertThrows(ParseException.class,
                () -> evaluator.evaluate("1 + 2 3 * 4"));
        }

        @Test
        @DisplayName("Anidamiento por encima del limite debe lanzar ParseException, no StackOverflowError")
        void excessiveNesting_shouldThrowParseException() {
            int depth = 200;
            StringBuilder expression = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                expression.append('(');
            }
            expression.append('1');
            for (int i = 0; i < depth; i++) {
                expression.append(')');
            }

            assertThrows(ParseException.class,
                () -> evaluator.evaluate(expression.toString()));
        }

        @Test
        @DisplayName("Anidamiento dentro del limite debe evaluarse correctamente")
        void nestingWithinLimit_shouldEvaluate() {
            int depth = 32;
            StringBuilder expression = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                expression.append('(');
            }
            expression.append('7');
            for (int i = 0; i < depth; i++) {
                expression.append(')');
            }

            assertEquals(7.0, evaluator.evaluate(expression.toString()), 0.0001);
        }
    }

    @Nested
    @DisplayName("Variables Null")
    class NullVariablesTests {

        @Test
        void nullVariable_shouldBeTreatedAsZero() {
            evaluator.withVariableNumber("nullVar", null);
            assertMatchesJS("nullVar + 5");
        }

        @Test
        void multipleNullVariables_shouldBeTreatedAsZero() {
            evaluator.withVariableNumber("a", null);
            evaluator.withVariableNumber("b", null);
            assertMatchesJS("a + b + 10");
        }

        @Test
        void nullVariableWithOperation_shouldMatchJS() {
            evaluator.withVariableNumber("n", null);
            assertMatchesJS("n * 5 + 3");
        }

        @Test
        void nullVariableNegative_shouldMatchJS() {
            evaluator.withVariableNumber("n", null);
            assertMatchesJS("-n + 10");
        }

        @Test
        void nullVariable_power_shouldMatchJS() {
            evaluator.withVariableNumber("n", null);
            assertMatchesJS("n ** 2 + 5");
        }
    }

    @Nested
    @DisplayName("WithVariable Methods")
    class WithVariableTests {

        @Test
        @DisplayName("withVariable Double debe funcionar")
        void withVariable_double_shouldWork() {
            FormulaEvaluator ev = new FormulaEvaluator()
                .withVariableNumber("a", 10.0)
                .withVariableNumber("b", 5.0);
            
            assertEquals(15.0, ev.evaluate("a + b"), 0.0001);
        }

        @Test
        @DisplayName("withVariable String numerico debe funcionar")
        void withVariable_stringNumber_shouldWork() {
            FormulaEvaluator ev = new FormulaEvaluator()
                .withVariableString("a", "10.5")
                .withVariableString("b", "5.5");
            
            assertEquals(16.0, ev.evaluate("a + b"), 0.0001);
        }

        @Test
        @DisplayName("withVariable String null debe trattarse como cero")
        void withVariable_stringNull_shouldBeTreatedAsZero() {
            FormulaEvaluator ev = new FormulaEvaluator()
                .withVariableString("a", "null")
                .withVariableNumber("b", 10.0);
            
            assertEquals(10.0, ev.evaluate("a + b"), 0.0001);
        }

        @Test
        @DisplayName("withVariable null debe trattarse como cero")
        void withVariable_nullValue_shouldBeTreatedAsZero() {
            FormulaEvaluator ev = new FormulaEvaluator()
                .withVariableNumber("a", (Double) null)
                .withVariableNumber("b", 10.0);
            
            assertEquals(10.0, ev.evaluate("a + b"), 0.0001);
        }

        @Test
        @DisplayName("Chaining withVariable debe funcionar")
        void chaining_withVariable_shouldWork() {
            double result = new FormulaEvaluator()
                .withVariableNumber("x", 1.0)
                .withVariableNumber("y", 2.0)
                .withVariableNumber("z", 3.0)
                .withVariableString("s", "4.0")
                .evaluate("x + y + z + s");
            
            assertEquals(10.0, result, 0.0001);
        }

        @Test
        @DisplayName("withVariable String no numerico debe lanzar EvaluationException, no NumberFormatException")
        void withVariable_nonNumericString_shouldThrowEvaluationException() {
            FormulaEvaluator ev = new FormulaEvaluator();

            EvaluationException exception = assertThrows(EvaluationException.class,
                () -> ev.withVariableString("a", "no-es-un-numero"));

            assertTrue(
                exception.getMessage().contains("a"),
                "El mensaje debe identificar la variable culpable: " + exception.getMessage());
        }
    }
}
