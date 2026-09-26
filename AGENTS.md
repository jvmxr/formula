

## Project Overview

This is a **Java Gradle project** (Gradle 8.0) for evaluating mathematical formulas in a C-like language.
The goal is to replace JavaScript/Nashorn evaluation with a custom formula evaluator.

- **Language**: Java 8 (source and target compatibility 1.8)
- **Build System**: Gradle 8.0 (via Gradle Wrapper), pinned to a **Java 8 toolchain**
- **Package Base**: `com.systam.utils.formula`
- **Testing Framework**: JUnit 5 (Jupiter)
- **Source Structure**: `src/main/java/`, `src/test/java/`

### JDK 8 is a hard requirement

Nashorn was removed in JDK 15. `NashornComparator` uses it to verify that every
formula produces the same result as JavaScript, so `build.gradle` declares a Java 8
toolchain for both compilation and tests. Without it the build still compiles, but
the 33 JavaScript-parity tests are silently skipped rather than verified.

Do not reintroduce a hardcoded JDK path (for example
`test { executable = 'C:/Program Files/Java/...' }`). Use the toolchain so Gradle can
locate the installed JDK.

### Project language

`AGENTS.md` is written in English because it is agent-facing instruction.
Everything a human reads is in **Spanish**: `README.md`, all Javadoc, all
`@DisplayName` strings, and all test method names. Keep new prose consistent with
the file it lands in.

---

## Build Command

> On Windows use `.\gradlew.bat`; `./gradlew` is the POSIX form.


### Full Build
```bash
./gradlew build        # Compile, test, and create build artifacts
./gradlew clean build  # Clean and rebuild from scratch
```

### Testing
```bash
./gradlew test              # Run all tests
./gradlew test --info       # Run tests with verbose output
```

### Single Test Execution
```bash
# Run a specific test class
./gradlew test --tests "com.systam.utils.formula.ClassNameTest"

# Run a specific test method
./gradlew test --tests "com.systam.utils.formula.ClassNameTest.testMethodName"

# Run tests matching a pattern
./gradlew test --tests "com.systam.utils.formula.*"  # All tests in formula package
./gradlew test --tests "*Test"                  # All classes ending in Test
```

### Other Useful Commands
```bash
./gradlew compileJava     # Compile main source only
./gradlew compileTestJava # Compile test source only
./gradlew dependencies    # Show project dependencies
./gradlew --version       # Show Gradle version
```

---

## Code Style Guidelines

### General Formatting
- **Indentation**: 4 spaces (no tabs)
- **Line Length**: Aim for ~100 characters max; wrap when necessary
- **Braces**: K&R style - opening brace on same line
  ```java
  if (condition) {
      doSomething();
  } else {
      doSomethingElse();
  }
  ```
- **Line Wrapping**: Indent 8 spaces for wrapped lines
- **Blank Lines**: Single blank line between methods and between sections within a class

### Naming Conventions
| Element            | Convention        | Example                          |
|--------------------|-------------------|----------------------------------|
| Package names      | lowercase, dots   | `com.systam.utils.formula`       |
| Class names        | PascalCase        | `FormulaEvaluator`, `ParserTest` |
| Interface names    | PascalCase        | `ExpressionNode`, `Token`        |
| Method names       | camelCase         | `evaluate()`, `parseExpression()`|
| Variable names     | camelCase         | `inputString`, `resultValue`     |
| Constant names     | SCREAMING_SNAKE   | `MAX_PRECEDENCE`, `EOF_TOKEN`    |
| Enum values        | SCREAMING_SNAKE   | `PLUS`, `MINUS`, `MULTIPLY`      |

### File Organization
- One public class per file
- File name must match the class name exactly
- Order within file:
  1. Package declaration
  2. Import statements (grouped and sorted)
  3. Class declaration
  4. Static fields
  5. Instance fields
  6. Constructors
  7. Public methods
  8. Package-private methods
  9. Private methods

### Import Organization
Organize imports in the following order with blank lines between groups:
```java
package com.systam.formula;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.systam.formula.lexer.Token;
import com.systam.formula.parser.ExpressionNode;
```

---

## Documentation Conventions

### Javadoc Requirements
- **Required**: Javadoc for all public classes and methods
- **Required**: `@param`, `@return`, `@throws` tags where applicable
- **Optional**: Javadoc for private methods when logic is non-obvious

### Javadoc Style
```java
/**
 * Evaluates a mathematical expression and returns the result.
 *
 * <p>Supports basic arithmetic operations (+, -, *, /), parentheses,
 * and variable substitution.
 *
 * @param expression the expression string to evaluate
 * @param variables  map of variable names to their values
 * @return the computed result
 * @throws ParseException if the expression cannot be parsed
 * @throws EvaluationException if evaluation fails (e.g., division by zero)
 */
public double evaluate(String expression, Map<String, Double> variables)
    throws ParseException, EvaluationException {
}
```

### Inline Comments
- Use sparingly; code should be self-documenting
- Explain **why**, not **what**
- Avoid obvious comments like `// increment i`
- Use TODO/FIXME markers: `// TODO: optimize for large expressions`

---

## Error Handling

### Exception Guidelines
1. **Use specific exception types** - never catch generic `Exception` or `Throwable`
2. **Include meaningful messages** - explain what went wrong and why
3. **Include context** - add relevant values to exception messages
   ```java
   // Bad
   throw new RuntimeException("Error");
   
   // Good
   throw new ParseException(
       String.format("Unexpected token '%s' at position %d", token, position)
   );
   ```

### Exception Types
- **This project has two custom exceptions, both unchecked** (they extend `RuntimeException`):
  - `ParseException` — carries the `position` in the expression where parsing failed
  - `EvaluationException` — variable undefined, division/modulo by zero, unknown operator
- Because they are unchecked, `@throws` documents them for readers; it does not force
  callers to handle them. Do not add `throws` clauses to method signatures for them.
- **Never leak a JDK exception type through the public API.** Wrap it in the matching
  custom exception and pass the original as the cause:
  ```java
  // Bad - callers must now catch NumberFormatException
  variables.put(name, Double.parseDouble(value));

  // Good
  try {
      variables.put(name, Double.parseDouble(value));
  } catch (NumberFormatException e) {
      throw new EvaluationException(
          String.format("Variable '%s' has a non-numeric value: '%s'", name, value), e);
  }
  ```

### Resource Management
- Always use try-with-resources for `AutoCloseable` resources
  ```java
  try (FileReader reader = new FileReader(path)) {
      return reader.read();
  }
  ```
- Java 8: `var` is **not** available. Always declare explicit types.

### Silent Failures
- **Never silently catch exceptions**
- **Never swallow exceptions** with empty catch blocks
- **Log exceptions** before rethrowing or converting

---

## Testing Conventions

### Test Structure (AAA Pattern)
```java
@Test
@DisplayName("Division should return correct quotient")
void division_shouldReturnQuotient_whenDividingPositiveNumbers() {
    // Arrange
    FormulaEvaluator evaluator = new FormulaEvaluator();
    String expression = "10 / 2";

    // Act
    double result = evaluator.evaluate(expression);

    // Assert
    assertEquals(5.0, result, 0.0001);
}
```

### Test Naming
- Format: `methodName_shouldExpectedBehavior_whenCondition()`
- Use `@DisplayName` for human-readable test names, written in **Spanish**
- One assertion concept per test method

### Assertions
- Use specific assertion methods (`assertEquals`, `assertTrue`, `assertThrows`)
- Always include a delta for floating-point comparisons: `assertEquals(expected, actual, 0.0001)`
- Use `assertThrows` for exception testing

### Test Organization
- Place tests in `src/test/java/` matching the source package structure
- Test class naming: `ClassNameTest` or `ClassNameIT` (integration)
- Group related tests with `@Nested` classes

---

## Project Structure

```
src/
├── main/
│   └── java/com/systam/utils/formula/
│       ├── FormulaEvaluator.java    # Main API (root)
│       ├── Evaluator.java          # AST evaluator (root)
│       ├── exception/
│       │   ├── ParseException.java
│       │   └── EvaluationException.java
│       ├── lexer/
│       │   ├── Token.java
│       │   ├── TokenType.java
│       │   └── Lexer.java
│       └── parser/
│           ├── ExpressionNode.java
│           ├── ExpressionVisitor.java
│           ├── NumberNode.java
│           ├── VariableNode.java
│           ├── BinaryNode.java
│           ├── UnaryNode.java
│           └── Parser.java
└── test/
    └── java/com/systam/utils/formula/
        ├── FormulaEvaluatorTest.java # 53 tests (33 JS-parity + 20 native)
        └── NashornComparator.java    # JS comparison utility
```

---

## Development Workflow

1. **Create feature branch** from main
2. **Write tests first** (TDD approach recommended)
3. **Implement functionality**
4. **Run tests**: `./gradlew test`
5. **Verify build**: `./gradlew build`
6. **Review code** before committing

---

## Notes for AI Agents

- This is a formula evaluator project - focus on parsing, tokenization, and mathematical operations
- The evaluator should produce results equivalent to JavaScript evaluation
- Maintain clean separation between lexer, parser, and evaluator components
- Add comprehensive tests for edge cases (operator precedence, parentheses, variable substitution)
