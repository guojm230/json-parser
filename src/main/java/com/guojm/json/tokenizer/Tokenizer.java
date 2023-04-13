package com.guojm.json.tokenizer;

import com.guojm.json.exception.InvalidTokenException;
import com.guojm.json.token.Position;
import com.guojm.json.token.Token;
import com.guojm.json.token.TokenBuilder;
import com.guojm.json.token.TokenKind;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    private final CharSource source;

    public Tokenizer(CharSource source) {
        this.source = source;
    }

    public List<Token> scan(){
        List<Token> result = new ArrayList<>();
        while (source.hasNext()){
            Token token = null;
            Position startPosition = source.positionData();
            char c = source.peek();
            switch (c) {
                case '{' -> {
                    source.next();
                    token = buildToken(TokenKind.OBJECT_BEGIN, startPosition);
                }
                case '}' -> {
                    source.next();
                    token = buildToken(TokenKind.OBJECT_END, startPosition);
                }
                case '[' -> {
                    source.next();
                    token = buildToken(TokenKind.ARRAY_BEGIN, startPosition);
                }
                case ']' -> {
                    source.next();
                    token = buildToken(TokenKind.ARRAY_END, startPosition);
                }
                case ':' -> {
                    source.next();
                    token = buildToken(TokenKind.NameSeparator, startPosition);
                }
                case ',' -> {
                    source.next();
                    token = buildToken(TokenKind.ValueSeparator, startPosition);
                }
                case '-','0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> result.add(readNumber());
                case '"' -> token = readStringLiteral();
                case ' ', '\n', '\r', '\f' -> source.next();
                default -> {
                    if (c == 'n' && (token = readNullLiteral()) != null) {
                        break;
                    } else if (c == 't' && (token = readBooleanLiteral()) != null) {
                        break;
                    } else if (c == 'f' && (token = readBooleanLiteral()) != null) {
                        break;
                    }
                    throw new InvalidTokenException(source.lineNumber(), source.colIndex(), "invalid token:" + c);
                }
            }

            if (token != null){
                result.add(token);
            }

        }
        return result;
    }

    Token readNumber(){
        Position startPosition = source.positionData();
        boolean hasExponent = false;
        boolean hasDot = false;
        char firstNumber = source.next();
        if (firstNumber == '-'){
            firstNumber = source.next();
        }
        while (source.hasNext()){
            char next = source.peek();
            if (isDigit(next)){
                if (next != '0' && !hasDot && firstNumber == '0'){
                    throw new InvalidTokenException(startPosition,"invalid number,don't permit lead-zero");
                }
                source.next();
            } else if (!hasDot && next == '.' && source.position() + 1 < source.length() &&
                    isDigit(source.peek(1))){
                hasDot = true;
                source.next();
                source.next();
            } else if (!hasExponent && (next == 'e' || next == 'E')) {
                hasExponent = true;
                if (source.position() + 1 < source.length() && isDigit(source.peek(1))){
                    source.next();
                    source.next();
                } else if (source.position() + 2 < source.length() && (source.peek(1) == '+'
                        || source.peek(1) == '-') && isDigit(source.peek(2))){
                    source.next();
                    source.next();
                    source.next();
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        if (hasDot || hasExponent){
            return buildToken(TokenKind.FloatNumber, startPosition);
        }

        return buildToken(TokenKind.IntNumber,startPosition);
    }


    Token readStringLiteral(){
        StringBuilder stringBuilder = new StringBuilder();
        var startPosition = source.positionData();
        source.next();
        while (source.hasNext()){
            char c = source.next();
            if (c == '\n' || c == '\r'){
                //error
                throw new InvalidTokenException(source.lineNumber(), source.colIndex(), "invalid single line string");
            }
            //resolve escape
            if (c == '\\'){
                char escape = source.next();
                switch (escape) {
                    case 'b' -> stringBuilder.append('\b');
                    case 'f' -> stringBuilder.append('\f');
                    case 'n' -> stringBuilder.append('\n');
                    case 'r' -> stringBuilder.append('\r');
                    case 't' -> stringBuilder.append('\t');
                    case '/' -> stringBuilder.append('/');
                    case '\\' ->stringBuilder.append('\\');
                    case '"' -> stringBuilder.append('"');
                    case 'u' -> {
                        //resolve unicode code point
                        var codePointPosition = source.positionData();
                        if (source.position() + 4 >= source.length()){
                            throw new InvalidTokenException(codePointPosition,
                                    "invalid character code point");
                        }
                        for (int i = 0; i < 4; i++) {
                            if (!isHexDigit(source.next())){
                                throw new InvalidTokenException(codePointPosition, "invalid character code point");
                            }
                        }
                        var codePoint = Integer.parseInt(source.slice(source.position()-4, source.position()),16);
                        stringBuilder.appendCodePoint(codePoint);
                    }
                    default -> throw new InvalidTokenException(source.lineNumber(),
                            source.colIndex(),
                            "invalid escape character \\" + escape);
                }
            } else if (c == '"'){
                return new TokenBuilder()
                        .setKind(TokenKind.String)
                        .setOriginalValue(stringBuilder.toString())
                        .setLineNumber(source.lineNumber())
                        .setColIndex(source.colIndex())
                        .setPosition(source.position())
                        .build();
            } else {
                stringBuilder.append(c);
            }
        }
        throw new InvalidTokenException(startPosition, "invalid string,expect string delimiter \"");
    }

    Token readBooleanLiteral(){
        Position startPosition = source.positionData();
        if (source.peek() == 't' && source.position() + 3 < source.length()){
            String value = source.slice(source.position(), source.position()+4);
            if (value.equals("true")){
                source.consume(4);
                return buildToken(TokenKind.Bool,startPosition);
            }
        } else if (source.peek() == 'f' && source.position() + 4 < source.length()){
            String value = source.slice(source.position(), source.position()+5);
            if (value.equals("false")){
                source.consume(5);
                return buildToken(TokenKind.Bool,startPosition);
            }
        }
        return null;
    }

    Token readNullLiteral(){
        Position startPosition = source.positionData();
        if (source.position() + 3 < source.length()){
            if (source.slice(source.position(), source.position()+4).equals("null")){
                source.consume(4);
                return buildToken(TokenKind.Null,startPosition);
            }
        }
        return null;
    }

    private Token buildToken(TokenKind kind, Position startPosition, int endPosition){
        return new TokenBuilder()
                .setKind(kind)
                .setOriginalValue(source.slice(startPosition.position(),endPosition))
                .setLineNumber(startPosition.lineNumber())
                .setColIndex(startPosition.colIndex())
                .setPosition(source.position())
                .build();
    }

    private Token buildToken(TokenKind kind, Position startPosition){
        return buildToken(kind, startPosition, source.position());
    }

    private boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }

    private boolean isHexDigit(char c){
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')
                || (c >= 'A' && c <= 'F');
    }

}
