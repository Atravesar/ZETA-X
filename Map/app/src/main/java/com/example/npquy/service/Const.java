package com.example.npquy.service;

public abstract class Const {
	public static final String URL = "http://104.42.107.187:82/api/";
	
	// connection timeout, in milliseconds (waiting to connect)
    public static final int CONN_TIMEOUT = 6000;
    
 // socket timeout, in milliseconds (waiting for data)
    public static final int SOCKET_TIMEOUT = 10000;
    
    /** The number of available processors in system. */
	public static int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();

	/** Time alive of a thread. */
	public static long KeepAliveTime = 60;

	public static double longitude = -0.1275920;

	public static double latitude = 51.5034070;
}
