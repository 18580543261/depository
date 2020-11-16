package com.sramar.mylibrary.exceptions;

public class ContextNullException extends Exception {
    public ContextNullException(){
        this("The context is null, check it");
    }


    public ContextNullException(String msg) {
        super(msg);
//        StackTraceElement[] es = Thread.currentThread().getStackTrace();
//
//        for (StackTraceElement e : es) {
//            System.out.println(e.getClassName());
//        }
    }
}
