package com.guojm.json.token;

/**
 *
 */
public interface Token {

    /**
     * @return The line number of the start position of this token
     */
    int lineNumber();

    /**
     * @return The col index of the start position
     */
    int colIndex();

    /**
     * @return original string value of this token
     */
    String originalValue();

    TokenKind kind();

}
