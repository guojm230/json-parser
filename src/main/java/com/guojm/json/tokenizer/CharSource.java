package com.guojm.json.tokenizer;

import com.guojm.json.token.Position;

public interface CharSource {

    char peek();

    char peek(int len);

    int length();

    char next();

    void consume(int len);

    /**
     * @param startPosition start position(included)
     * @param endPosition end position(excluded)
     * @return str
     */
    String slice(int startPosition,int endPosition);

    boolean hasNext();

    int lineNumber();

    int colIndex();

    int position();

    Position positionData();
}
