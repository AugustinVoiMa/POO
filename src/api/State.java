package api;

import java.util.HashMap;

public abstract class State<T> {
	public final double ta;
	
	public final String name;
	
	public String key;
	public T value;
	
	public State(double ta, String name) {
		this.ta = ta;
		this.name = name;
	}
	
	/**
	 * only update key and value, 
	 * does not return anything
	 */
	public abstract void lambda(); 
	
	/**
	 * 
	 * @return next state (should not be null)
	 */
	public abstract State<T> timeout();
	
	/**
	 * @X HashMap Containing messages (keys and associated values)
	 * @return next state or null
	 */
	public abstract State<T> input(HashMap<String, T> X);
	
	
	/** 
	 * @return Message key that is to be sent on timeout
	 */
	public abstract String getTimeoutMessage();
	
}

