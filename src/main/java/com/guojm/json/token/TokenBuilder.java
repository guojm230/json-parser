package com.guojm.json.token;

public class TokenBuilder {
    private int lineNumber;
    private int colIndex;

    private String originalValue;
    private int position;

    private TokenKind kind;

    public Token build(){
        return new TokenImpl(
                lineNumber,
                colIndex,
                originalValue,
                position,
                kind
        );
    }

    public int lineNumber() {
        return lineNumber;
    }

    public TokenBuilder setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
        return this;
    }

    public int lineIndex() {
        return colIndex;
    }

    public TokenBuilder setColIndex(int colIndex) {
        this.colIndex = colIndex;
        return this;
    }

    public TokenBuilder setPosition(Position position){
        this.lineNumber = position.lineNumber();
        this.colIndex = position.colIndex();
        this.position = position.position();
        return this;
    }

    public String originalValue() {
        return originalValue;
    }

    public TokenBuilder setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
        return this;
    }

    public int position() {
        return position;
    }

    public TokenBuilder setPosition(int position) {
        this.position = position;
        return this;
    }

    public TokenKind kind() {
        return kind;
    }

    public TokenBuilder setKind(TokenKind kind) {
        this.kind = kind;
        return this;
    }

    static class TokenImpl implements Token{
        private final int lineNumber;
        private final int lineIndex;
        private final String originalValue;
        private final TokenKind kind;

        public TokenImpl(int lineNumber, int lineIndex, String originalValue, int position, TokenKind kind) {
            this.lineNumber = lineNumber;
            this.lineIndex = lineIndex;
            this.originalValue = originalValue;
            this.kind = kind;
        }

        @Override
        public int lineNumber() {
            return lineNumber;
        }

        @Override
        public int colIndex() {
            return lineIndex;
        }

        @Override
        public String originalValue() {
            return originalValue;
        }

        @Override
        public TokenKind kind() {
            return kind;
        }
    }
}
