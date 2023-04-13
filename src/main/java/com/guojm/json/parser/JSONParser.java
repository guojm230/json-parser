package com.guojm.json.parser;

import com.guojm.json.JSONArray;
import com.guojm.json.JSONObject;
import com.guojm.json.exception.JSONException;
import com.guojm.json.token.Token;
import com.guojm.json.token.TokenKind;

import java.util.List;

public class JSONParser {

    private List<Token> tokenStream;
    private int position;

    public JSONParser(List<Token> tokenStream){
        this.tokenStream = tokenStream;
    }

    /**
     * parse json from token stream
     * @return JSONValue json value
     */
    public Object parse(){
        if (tokenStream == null || tokenStream.isEmpty()){
            throw new JSONException("parse error,empty token list");
        }
        return parseValue();
    }

    Object parseValue(){
        var token = tokenStream.get(position);
        return switch (token.kind()){
            case OBJECT_BEGIN -> parseObject();
            case ARRAY_BEGIN -> parseArray();
            case Null -> parseNull();
            case IntNumber -> parseNumber();
            case FloatNumber -> parseFloatNumber();
            case String -> parseString();
            case Bool -> parseBool();
            default -> throw new JSONException("syntax error");
        };
    }

    JSONObject parseObject(){
        var jsonObject = new JSONObject();
        checkTokenKind(tokenStream.get(position++),TokenKind.OBJECT_BEGIN);
        while (position < tokenStream.size() &&
                (tokenStream.get(position)).kind() != TokenKind.OBJECT_END){
            if (!jsonObject.isEmpty()){
                parseValueSeparator();
            }
            var name = parseString();
            parseNameSeparator();
            jsonObject.put(name,parseValue());
        }
        checkTokenKind(tokenStream.get(position++),TokenKind.OBJECT_END);
        return jsonObject;
    }

    JSONArray parseArray(){
        var jsonArray = new JSONArray();
        checkTokenKind(tokenStream.get(position++),TokenKind.ARRAY_BEGIN);
        while (position < tokenStream.size() &&
                (tokenStream.get(position)).kind() != TokenKind.ARRAY_END){
            if (!jsonArray.isEmpty()){
                parseValueSeparator();
            }
            jsonArray.add(parseValue());
        }
        checkTokenKind(tokenStream.get(position++),TokenKind.ARRAY_END);
        return jsonArray;
    }

    Object parseNull(){
        checkTokenKind(tokenStream.get(position++),TokenKind.Null);
        return null;
    }

    String parseString(){
        var token = tokenStream.get(position++);
        checkTokenKind(token,TokenKind.String);
        return token.originalValue();
    }

    void parseNameSeparator(){
        var token = tokenStream.get(position++);
        checkTokenKind(token,TokenKind.NameSeparator);
    }

    void parseValueSeparator(){
        var token = tokenStream.get(position++);
        checkTokenKind(token,TokenKind.ValueSeparator);
    }

    Number parseFloatNumber(){
        var token = tokenStream.get(position++);
        checkTokenKind(token,TokenKind.FloatNumber);
        try {
            return Double.valueOf(token.originalValue());
        } catch (NumberFormatException e){
            throw new JSONException("parse number error:"+token.originalValue());
        }
    }

    Number parseNumber(){
        var token = tokenStream.get(position++);
        checkTokenKind(token,TokenKind.IntNumber);
        try {
            return Long.parseLong(token.originalValue());
        } catch (NumberFormatException e){
            throw new JSONException("parse number error:"+token.originalValue());
        }
    }

    boolean parseBool(){
        var token = tokenStream.get(position++);
        checkTokenKind(token,TokenKind.Bool);
        return "true".equals(token.originalValue());
    }

    private void checkTokenKind(Token token,TokenKind expectKind){
        if (token.kind() != expectKind){
            throw new JSONException("error json syntax,expect '"+expectKind+"'"+",but actual is "+token.originalValue()+"' at line:"+token.lineNumber()+",col:"+token.colIndex());
        }
    }

    public List<Token> tokenStream() {
        return tokenStream;
    }

    public JSONParser setTokenStream(List<Token> tokenStream) {
        this.tokenStream = tokenStream;
        return this;
    }
}
