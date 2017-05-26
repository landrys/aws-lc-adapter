package com.amazonaws.lambda.lcadapter.lcclient;

public class LcUtils {

    static final public String  PRODACCOUNT = "30427"; // These are getting passed in by constructor/Spring/server.prperties

    public static String URL = "https://api.merchantos.com/API/Account";
    public static int READ_TIMEOUT = 180000; //  The timeout for the api call ( 3 minutes )

}
