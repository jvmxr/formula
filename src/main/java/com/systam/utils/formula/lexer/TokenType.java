package com.systam.utils.formula.lexer;

/**
 * Enum que define los tipos de tokens reconocidos por el Lexer.
 *
 * <p>Tokens disponibles:
 * <ul>
 *   <li>{@code NUMBER} - Literal num&eacute;rico</li>
 *   <li>{@code IDENTIFIER} - Identificador (nombre de variable)</li>
 *   <li>{@code PLUS} - Operador suma (+)</li>
 *   <li>{@code MINUS} - Operador resta (-)</li>
 *   <li>{@code MULTIPLY} - Operador multiplicaci&oacute;n (*)</li>
 *   <li>{@code DIVIDE} - Operador divisi&oacute;n (/)</li>
 *   <li>{@code MODULO} - Operador m&oacute;dulo (%)</li>
 *   <li>{@code POWER} - Operador potencia (**)</li>
 *   <li>{@code LPAREN} - Par&eacute;ntesis izquierdo (()</li>
 *   <li>{@code RPAREN} - Par&eacute;ntesis derecho ())</li>
 *   <li>{@code EOF} - Fin de archivo</li>
 * </ul>
 *
 * @see Token
 * @see Lexer
 */
public enum TokenType {
    NUMBER,
    IDENTIFIER,
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    MODULO,
    POWER,
    LPAREN,
    RPAREN,
    EOF
}
