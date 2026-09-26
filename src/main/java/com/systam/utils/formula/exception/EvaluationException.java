package com.systam.utils.formula.exception;

/**
 * Excepci&oacute;n lanzada cuando hay un error durante la evaluaci&oacute;n de una expresi&oacute;n.
 *
 * <p>Esta excepci&oacute;n es抛出 cuando:
 * <ul>
 *   <li>Se intenta usar una variable no definida</li>
 *   <li>Hay divisi&oacute;n por cero</li>
 *   <li>Hay un operador desconocido</li>
 *   <li>Cualquier otro error durante la evaluaci&oacute;n</li>
 * </ul>
 *
 * @see RuntimeException
 */
public class EvaluationException extends RuntimeException {
    /**
     * Crea una excepci&oacute;n de evaluaci&oacute;n.
     *
     * @param message mensaje descriptivo del error
     */
    public EvaluationException(String message) {
        super(message);
    }

    /**
     * Crea una excepci&oacute;n de evaluaci&oacute;n con una causa.
     *
     * @param message mensaje descriptivo del error
     * @param cause   excepci&oacute;n que caus&oacute; este error
     */
    public EvaluationException(String message, Throwable cause) {
        super(message, cause);
    }
}
