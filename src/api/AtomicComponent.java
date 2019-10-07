package api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class AtomicComponent<T>{
	public final String name;
	public boolean ins;
	
	private State<T>  s;
	private HashMap<String, T> X;
	protected HashMap<String, T> Y;
	private Set<Port<T>> ports;
	
	
	
	
	/*
	 * e	Time since last state change
	 * tl	Last state change
	 * tn 	Next state change
	 * tr 	Time to next state change
	 * 
	 */
	private double tl, e, tr, tn;
	
	public AtomicComponent(String name) {		
		this.X = new HashMap<String, T>();
		this.Y = new HashMap<String, T>();
		this.name = name;
		this.ports = new HashSet<Port<T>>();
		this.ins = false;
	}
	
	public double getTr() {
		return tr;
	}
	
	public Set<Port<T>> getPorts() {
		return ports;
	}

	public Port<T> getPort(String portname){
		return this.ports.stream().filter(p -> p.name == portname).findFirst().get();
	}
	
	public void addPort(Port<T> p) {
		this.ports.add(p);
	}
	
	public void init(double t0) {
		this.s = this.generateInitialState();
		
		this.tl = t0;
		this.tn = t0 + s.ta; // stay ta time in this state before change		
		this.update_time(t0);
		this.ins=false;
	}

	public void update_time(double t) {
		/*
		 * tl, tn fixed on state change
		 */
		this.tn = this.tl + s.ta;
		this.tr = this.tn - t;		
		this.e =  t - this.tl;
		
		assert(this.tr >= 0);
		assert(this.e >= 0);
	}
	
	public void lambda() throws Exception {
		this.s.lambda();
		if (this.Y.containsKey(this.s.key) && this.Y.get(this.s.key) != null)
			throw new Exception(this.name+" : Y already contains key "+this.s.key);
		else
			this.Y.put(this.s.key, this.s.value);
	}
	
	public void external(double t) {
		State<T> nstate = this.s.input(this.X);
		if (nstate != null) {
			this.s = nstate;
			this.tl = t;
			update_time(t);
		}
	}
	
	public void internal(double t) throws Exception {
		if (t>= this.tn) {
			State<T> nstate = this.s.timeout();
			if (nstate != null) {
				this.s = nstate;
				this.tl = t;
				update_time(t);
			} else {
				throw new Exception("Null state returned on timeout");
			}
		}
	}
	
	public T popY(String key) {
		if(this.Y.containsKey(key)) {
			T msg = this.Y.get(key);
			this.Y.remove(key);
			return msg;
		}
		else
			return null;
	}
	
	public T getY(String key) {
		if(this.Y.containsKey(key)) {
			T msg = this.Y.get(key);			
			return msg;
		}
		else
			return null;
	}
	
	public void input(String key, T value) throws Exception {
		if (this.X.containsKey(key) || this.X.get(key) != null)
			throw new Exception("Trying to overwrite an input value "+this.name+"("+this.s.name+"):"+key);
			
		this.X.put(key, value);
	}	
	
	public String getNextTimeoutMessage() {
		return this.s.getTimeoutMessage();
	}
	
	protected abstract State<T> generateInitialState();

	public String getStateName() {
		return this.s.name;
	}
 }
