package com.systam.utils.formula.exception;

/**
 * Excepci&oacute;n lanzada cuando hay un error durante el parseo de una expresi&oacute;n.
 *
 * <p>Esta excepci&oacute;n es抛出 cuando:
 * <ul>
 *   <li>La expresi&oacute;n es null o vac&iacute;a</li>
 *   <li>Hay un token inesperado</li>
 *   <li>Falta un par&eacute;ntesis de cierre</li>
 *   <li>El formato del n&uacute;mero es inv&aacute;lido</li>
 * </ul>
 *
 * @see RuntimeException
 */
public class ParseException extends RuntimeException {
    private final int position;

    /**
     * Crea una excepci&oacute;n de parseo.
     *
     * @param message  mensaje descriptivo del error
     * @param position posici&oacute;n en la expresi&oacute;n donde occurredi&oacute; el error
     */
    public ParseException(String message, int position) {
        super(message);
        this.position = position;
    }

    /**
     * @return la posici&oacute;n donde occurredi&oacute; el error
     */
    public int getPosition() {
        return position;
    }

    @Override
    public String getMessage() {
        return String.format("Parse error at position %d: %s", position, super.getMessage());
    }
}
