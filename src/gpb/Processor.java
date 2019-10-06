package gpb;

import java.util.HashMap;

import api.AtomicComponent;
import api.State;

public class Processor extends AtomicComponent<Boolean> {

	public Processor() {
		super("Processeur");		
	}
	
	@Override
	protected State<Boolean> generateInitialState() {
		return new State_P_free();
	}
	
	public class State_P_free extends State<Boolean>{

		public State_P_free() {
			super(Double.POSITIVE_INFINITY, "Proc_free");
		}

		@Override
		public void lambda() {}

		@Override
		public State<Boolean> timeout() {
			System.err.println("Timeout should not be called here (State_P_free)");
			return null;
		}

		@Override
		public State<Boolean> input(HashMap<String, Boolean> X) {
			if (X.containsKey("req") && X.get("req")) {				
				X.remove("req");
				return new State_P_busy();
			}			
			return null;
		}

		@Override
		public String getTimeoutMessage() {
			return null;
		}
		
	}
	
	public class State_P_busy extends State<Boolean>{

		public State_P_busy() {
			super(3, "Proc_busy");
		}

		@Override
		public void lambda() {
			super.key="done";
			super.value=true;
		}

		@Override
		public State<Boolean> timeout() {			
			return new State_P_free();
		}

		@Override
		public State<Boolean> input(HashMap<String, Boolean> X) {
			return null;
		}

		@Override
		public String getTimeoutMessage() {
			return "done";
		}
		
	}

}
