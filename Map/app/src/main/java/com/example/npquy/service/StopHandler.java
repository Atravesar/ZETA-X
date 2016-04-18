package com.example.npquy.service;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * 
 * @author npquy
 *
 */
public enum StopHandler implements Serializable {

	IS_RUNNING(0);
	private volatile int version;

	private StopHandler(int version) {
		this.version=version;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	private static class LazyInit {
		private static StopHandler SINGLETON = StopHandler.IS_RUNNING;

	}

	private Object writeReplace() throws ObjectStreamException {
		return createInstance();
	}

	public static StopHandler createInstance() {
		return LazyInit.SINGLETON;
	}
}
