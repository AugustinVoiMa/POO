package api;

import java.util.HashSet;
import java.util.Set;

public class Port<T> {
	public final String name;
	private Set<AtomicComponent<T>> listeners;
	
	public Port(String name) {
		this.name = name;
		this.listeners = new HashSet<AtomicComponent<T>>(); 
	}
	
	public void addAtomicListener(AtomicComponent<T> listener) {
		this.listeners.add(listener);
	}
	
	public void broadcast(T message) {
		this.listeners.forEach(a -> {
			try {
				a.input(this.name, message);
			} catch (Exception e) {
				e.printStackTrace();
			}
			a.ins=true;
		});
	}
}
