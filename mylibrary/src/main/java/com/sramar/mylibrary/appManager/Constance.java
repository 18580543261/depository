package com.sramar.mylibrary.appManager;


import com.sramar.mylibrary.exceptions.SingleInstantionException;

public class Constance {
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

    private final static String serverHttpAddress = "http://www.smaram.com";
    private final static String serverWsAddress = "http://www.smaram.com";

    private String recorderOutputDir;
    private long screenRecords;

    public static String getServerHttpAddress() {
        return serverHttpAddress;
    }

    public static String getServerWsAddress() {
        return serverWsAddress;
    }

    public String getRecorderOutputDir() {
        return recorderOutputDir;
    }

    public long getScreenRecords() {
        return screenRecords;
    }
}
