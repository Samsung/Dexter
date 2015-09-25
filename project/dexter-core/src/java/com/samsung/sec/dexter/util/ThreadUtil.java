package com.samsung.sec.dexter.util;

public class ThreadUtil {
	public static void sleepOneSecond() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// Do nothing
		}
	};
}
