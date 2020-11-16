package com.sramar.mylibrary.appManager;


import com.sramar.mylibrary.exceptions.SingleInstantionException;

public class Constance {
    static class Cons{
         final static String serverHttpAddress = "http://www.smaram.com";
         final static String serverWsAddress = "http://www.smaram.com";

         static String recorderOutputDir;
         static long screenRecords;
    }

    private static Constance instance;
    private Constance(){
        if (instance != null){
            try {
                throw new SingleInstantionException();
            } catch (SingleInstantionException e) {
                e.printStackTrace();
            }
        }
    }
    protected synchronized static Constance getInstance(){
        if (instance == null){
            synchronized (AppManager.class){
                if (instance == null){
                    instance = new Constance();
                }
            }
        }
        return instance;
    }

    public static String getServerHttpAddress() {
        return Cons.serverHttpAddress;
    }

    public static String getServerWsAddress() {
        return Cons.serverWsAddress;
    }

    public String getRecorderOutputDir() {
        return Cons.recorderOutputDir;
    }

    public long getScreenRecords() {
        return Cons.screenRecords;
    }
}
