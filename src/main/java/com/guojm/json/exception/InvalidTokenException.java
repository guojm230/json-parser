package com.guojm.json.exception;

import com.guojm.json.token.Position;

public class InvalidTokenException extends JSONException {

    public InvalidTokenException(int lineNumber,int lineIndex,String message) {
        super("invalid token at line "+lineNumber+":"+lineIndex+","+message);
    }

    public InvalidTokenException(Position position,String message){
        this(position.lineNumber(), position.colIndex(), message);
    }
}
