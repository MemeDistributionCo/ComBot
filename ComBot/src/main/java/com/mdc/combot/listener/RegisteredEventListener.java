package com.mdc.combot.listener;

import java.lang.reflect.Method;

public class RegisteredEventListener {

	private final Method method;
	private final Listener listenerInstance;
	
	public RegisteredEventListener(Method m, Listener listenerInstance) {
		this.method = m;
		this.listenerInstance = listenerInstance;
	}
	
	public Method getMethod() {
		return this.method;
	}
	
	public Listener getListenerInstance() {
		return this.listenerInstance;
	}
	
}
