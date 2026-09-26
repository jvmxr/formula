# Formulas - Evaluador de Expresiones Matemáticas

Evaluador de fórmulas matemáticas estilo JavaScript para Java.

## Objetivo

Reemplazar la evaluación de fórmulas mediante JavaScript (Nashorn) con un evaluador nativo en Java.
El evaluador debe producir resultados idénticos a JavaScript para expresiones aritméticas,
soportando sustitución de variables y precedencia de operadores estándar.

## Características

- **Operadores soportados**: `+`, `-`, `*`, `/`, `%`, `**` (potencia)
- **Precedencia de operadores**: igual que JavaScript
- **Paréntesis**: soportados y con precedencia correcta
- **Variables**: sustitución de variables en expresiones
- **API fluida**: métodos encadenables con `withVariable*`
- **Comparación con Nashorn**: tests que verifican coincidencia con JavaScript

## Uso

### API Básica

```java
import com.systam.utils.formula.FormulaEvaluator;

public class Main {
    public static void main(String[] args) {
        FormulaEvaluator evaluator = new FormulaEvaluator();
        
        // Ejemplo simple
        double result = evaluator.evaluate("10 + 5 * 2");
        System.out.println("10 + 5 * 2 = " + result); // 20
        
        // Con variables adicionales
        Map<String, Double> variables = new HashMap<>();
        variables.put("x", 10.0);
        variables.put("y", 5.0);
        
        result = evaluator.evaluate("x + y * 2", variables);
        System.out.println("x + y * 2 = " + result); // 20
    }
}
```

### API Fluida (recomendada)

```java
import com.systam.utils.formula.FormulaEvaluator;

// Con valores Double
double result = new FormulaEvaluator()
    .withVariableNumber("x", 10.0)
    .withVariableNumber("y", 5.0)
    .evaluate("x + y");  // 15.0

// Con valores String
double result = new FormulaEvaluator()
    .withVariableString("x", "10.0")
    .withVariableString("y", "5.0")
    .evaluate("x + y");  // 15.0

// Valores null (tratados como 0)
double result = new FormulaEvaluator()
    .withVariableNumber("a", null)
    .withVariableString("b", "null")
    .evaluate("a + b + 10");  // 10.0

// Encadenamiento completo
double result = new FormulaEvaluator()
    .withVariableNumber("x", 10.0)
    .withVariableNumber("y", 5.0)
    .withVariableString("z", "2.0")
    .evaluate("(x + y) * z");  // 30.0
```

## Expresiones Válidas

| Expresión | Resultado | Descripción |
|-----------|-----------|-------------|
| `10 + 5` | 15 | Suma |
| `10 - 5` | 5 | Resta |
| `10 * 5` | 50 | Multiplicación |
| `10 / 5` | 2 | División |
| `17 % 5` | 2 | Módulo |
| `2 ** 3` | 8 | Potencia |
| `(10 + 5) * 2` | 30 | Paréntesis |
| `x + y * 2` | Varía | Con variables |
| `-x + 5` | Varía | Operador unario |
| `+10` | 10 | Operador unario positivo |

## Precedencia de Operadores

De mayor a menor:
1. `**` (potencia)
2. `*`, `/`, `%` (multiplicativos)
3. `+`, `-` (aditivos)
4. operadores unarios `+`, `-`

Los paréntesis siempre tienen la mayor precedencia.

## Divergencias respecto a JavaScript

El objetivo es reproducir JavaScript, y en casi todo caso lo consigue. Hay dos
comportamientos deliberados en los que **no** coincide, y conviene conocerlos:

### 1. Potencia con base negativa

| Expresión | Este evaluador | JavaScript |
|-----------|----------------|------------|
| `-2 ** 2` | `4.0` | `-4.0` |
| `(-2) ** 2` | `4.0` | `4.0` |

El menos unario liga con menor precedencia que `**`, igual que en Python, y **no** como
en JavaScript. Quien necesite el comportamiento de JavaScript debe escribir el paréntesis:
`(-2) ** 2`.

### 2. `getVariable()` con valor `null`

Durante la **evaluación** una variable `null` se trata como `0` (para coincidir con
JavaScript, donde `null * 5` es `0`). En cambio `getVariable(name)` lanza
`EvaluationException` si la variable no existe o tiene valor `null`: leer una variable
ausente es un error de programación, no un cero silencioso.

## Límites y parseo estricto

- Se rechaza cualquier token sin consumir: `"1 5"` y `"(10 + 5))"` lanzan `ParseException`
  en lugar de devolver un resultado parcial.
- El anidamiento se limita a `Parser.MAX_DEPTH` (64). Superarlo lanza `ParseException`
  con la posición, en vez de propagar un `StackOverflowError`.
- `withVariableString("x", "abc")` lanza `EvaluationException`; nunca se filtra un
  `NumberFormatException` de la API pública.

## Diagrama del pipeline

`docs/diagrams/pipeline-evaluacion.html` documenta el recorrido completo
(`FormulaEvaluator` → `Lexer` → `Parser`/AST → `Evaluator`) y los errores que puede
emitir cada etapa. Ábrelo en el navegador; es autónomo y no necesita servidor.

## Requisitos

- **JDK 8 (obligatorio para compilar y ejecutar los tests)**
- Gradle 8.0 (incluido en el *Gradle Wrapper*)

### Por qué JDK 8 es obligatorio

`NashornComparator` usa Nashorn para contrastar cada fórmula contra JavaScript, y Nashorn
se eliminó en JDK 15. `build.gradle` declara un *toolchain* de Java 8 para la compilación
y para los tests, de modo que Gradle localiza el JDK instalado en lugar de depender de una
ruta absoluta.

Si se compila con un JDK posterior, el proyecto sigue compilando, pero los **33 tests de
paridad con JavaScript se omiten silenciosamente** en vez de verificarse. Un build verde
con todos los tests omitidos no es un build verificado: comprueba que la ejecución reporta
0 tests omitidos.

Si Gradle no encuentra el JDK 8, indica su ubicación en `gradle.properties`:

```properties
org.gradle.java.installations.paths=C:/Program Files/Java/jdk1.8.0_261
```

## Construcción

```bash
# Compilar y ejecutar tests
./gradlew build

# Solo tests
./gradlew test

# Un test específico
./gradlew test --tests "com.systam.utils.formula.FormulaEvaluatorTest"

# Compilar
./gradlew compileJava
```

## Estructura del Proyecto

```
src/
├── main/java/com/systam/utils/formula/
│   ├── FormulaEvaluator.java          # API principal
│   ├── Evaluator.java                # Evaluación del AST
│   ├── lexer/                        # Tokenización
│   │   ├── Lexer.java
│   │   ├── Token.java
│   │   └── TokenType.java
│   ├── parser/                       # Construcción de AST
│   │   ├── Parser.java
│   │   ├── ExpressionNode.java
│   │   ├── ExpressionVisitor.java
│   │   ├── NumberNode.java
│   │   ├── VariableNode.java
│   │   ├── BinaryNode.java
│   │   └── UnaryNode.java
│   └── exception/                    # Excepciones personalizadas
│       ├── ParseException.java
│       └── EvaluationException.java
└── test/java/com/systam/utils/formula/
    ├── FormulaEvaluatorTest.java     # 53 tests (33 de paridad con JS + 20 nativos)
    └── NashornComparator.java        # Utilidad de comparación con JavaScript

docs/diagrams/
├── pipeline-evaluacion.workflow.json # Fuente del diagrama (Archify v2)
└── pipeline-evaluacion.html          # Diagrama interactivo autónomo
```

## Tests

Los tests comparan automáticamente los resultados del evaluador Java con Nashorn
(JavaScript de Mozilla) para verificar que producen resultados idénticos.

Son **53 tests**: 33 de paridad con JavaScript y 20 que verifican parseo estricto,
errores y límites. Todos se ejecutan sobre JDK 8.

```bash
# Ver todos los tests
./gradlew test --info

# Tests que comparan con JS
./gradlew test --tests "com.systam.utils.formula.FormulaEvaluatorTest"
```

## API Reference

### FormulaEvaluator

| Método | Descripción |
|--------|-------------|
| `evaluate(String expression)` | Evalúa una expresión |
| `evaluate(String expression, Map<String, Double> variables)` | Evalúa con variables adicionales |
| `withVariableNumber(String name, Double value)` | Agrega variable Double (fluido) |
| `withVariableString(String name, String value)` | Agrega variable String (fluido) |
| `getVariable(String name)` | Obtiene valor de variable |
| `getVariablesCopy()` | Obtiene copia del mapa de variables |

### Valores Null

Las variables con valor `null` son tratadas como `0` durante la evaluación:
- `withVariableNumber("x", null)` → treated as 0
- `withVariableString("x", "null")` → treated as null → 0

`getVariable("x")` sí lanza `EvaluationException` en ese caso (ver
[Divergencias](#divergencias-respecto-a-javascript)).

## Licencia

Uso interno.
