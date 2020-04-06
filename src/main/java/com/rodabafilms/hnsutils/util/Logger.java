package com.rodabafilms.hnsutils.util;

import java.util.logging.LogManager;

public class Logger {
	private static org.apache.logging.log4j.Logger Logger;
	
	public static void SetLogger(org.apache.logging.log4j.Logger log) {
		Logger = log;
	}
	
	public static void info(String message) {
		Logger.info(message);
	}
}
