package com.inbank.credit.engine.error;

public class DeclineException extends RuntimeException{
    public DeclineException(String s) {
        super(s);
    }
}
