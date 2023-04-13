package com.guojm.json.tokenizer;

import com.guojm.json.token.Position;

public class StringSource implements CharSource{
    private int position = 0;
    private int lineNumber = 0;
    private int colIndex = 0;
    private final String value;

    public StringSource(String value){
        this.value = value;
    }

    @Override
    public char peek() {
        return value.charAt(position);
    }

    @Override
    public char peek(int len) {
        return value.charAt(position+len);
    }

    @Override
    public int length() {
        return value.length();
    }

    @Override
    public char next() {
        char c = value.charAt(position++);
        if (c == '\n'){
            lineNumber++;
            colIndex = 0;
        } else if (c == '\r' && (!hasNext() || value.charAt(position+1) != '\n')){
            lineNumber++;
            colIndex = 0;
        } else {
            colIndex++;
        }
        return c;
    }

    @Override
    public void consume(int len) {
        for (int i = 0; i < len; i++) {
            next();
        }
    }

    @Override
    public String slice(int startPosition, int endPosition) {
        return value.substring(startPosition,endPosition);
    }

    @Override
    public boolean hasNext() {
        return position < value.length();
    }

    @Override
    public int lineNumber() {
        return lineNumber;
    }

    @Override
    public int colIndex() {
        return colIndex;
    }

    @Override
    public int position() {
        return position;
    }

    @Override
    public Position positionData() {
        return new Position(
                lineNumber,
                colIndex,
                position
        );
    }

    public String value() {
        return value;
    }
}
