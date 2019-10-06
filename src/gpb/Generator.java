package gpb;

import java.util.HashMap;

import api.AtomicComponent;
import api.State;

public class Generator extends AtomicComponent<Boolean> {

	public Generator() {
		super("Générateur");		
	}

	@Override
	protected State<Boolean> generateInitialState() {
		return new State_s();
	}
	public class State_s extends State<Boolean>{

		public State_s() {
			super(2, "Gen_s");
		}

		@Override
		public void lambda() {
			super.key = "job";
			super.value = true; 
		}

		@Override
		public State<Boolean> timeout() {
			return new State_s(); // always restart this state
		}

		@Override
		public State<Boolean> input(HashMap<String, Boolean> X) {						
			return null; // No waiting for any input
		}

		@Override
		public String getTimeoutMessage() {
			return "job";
		}
		
	}
}
