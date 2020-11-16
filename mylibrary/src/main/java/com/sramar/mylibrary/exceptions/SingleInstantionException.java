package com.sramar.mylibrary.exceptions;

public class SingleInstantionException extends Exception {
    public SingleInstantionException() {
        this("singleInstance should not be instantiated outside");
    }

    public SingleInstantionException(String message) {
        super(message);
    }
}
