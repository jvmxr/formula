package com.systam.utils.formula.lexer;

/**
 * Representa un token individual generado por el Lexer.
 *
 * <p>Un token es la unidad m&aacute;s peque&ntilde;a de una expresi&oacute;n que tiene significado.
 *
 * @see TokenType
 * @see Lexer
 */
public class Token {
    private final TokenType type;
    private final String lexeme;
    private final double numberValue;
    private final int position;

    /**
     * Crea un token no num&eacute;rico.
     *
     * @param type     tipo del token
     * @param lexeme   texto original del token
     * @param position posici&oacute;n en la entrada
     */
    public Token(TokenType type, String lexeme, int position) {
        this.type = type;
        this.lexeme = lexeme;
        this.position = position;
        this.numberValue = 0;
    }

    /**
     * Crea un token num&eacute;rico conservando el lexema original de la entrada.
     *
     * @param type     tipo del token (NUMBER)
     * @param value    valor num&eacute;rico
     * @param lexeme   texto original tal como aparece en la entrada
     * @param position posici&oacute;n en la entrada
     */
    public Token(TokenType type, double value, String lexeme, int position) {
        this.type = type;
        this.lexeme = lexeme;
        this.numberValue = value;
        this.position = position;
    }

    /**
     * @return el tipo del token
     */
    public TokenType getType() {
        return type;
    }

    /**
     * @return el texto original del token
     */
    public String getLexeme() {
        return lexeme;
    }

    /**
     * @return el valor num&eacute;rico (v&aacute;lido solo para tokens NUMBER)
     */
    public double getNumberValue() {
        return numberValue;
    }
    /**
     * @return la posici&oacute;n del token en la cadena de entrada
     */
    public int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return String.format("Token(%s, '%s', pos=%d)", type, lexeme, position);
    }
}
