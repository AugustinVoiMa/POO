package gpb;

import java.util.HashMap;

import api.AtomicComponent;
import api.State;

public class Buffer extends AtomicComponent<Boolean>{
	
	private String job_vs_done;

	public int q;
	public Buffer(String job_vs_done) {
		super("Cache");
		this.q = 0;		
		this.job_vs_done = job_vs_done;
	}

	@Override
	protected State<Boolean> generateInitialState() {
		return new State_B_a();
	}
		
	public class State_B_a extends State<Boolean>{

		public State_B_a() {
			super(Double.POSITIVE_INFINITY, "Buf_a");
		}

		@Override
		public void lambda() {}

		@Override
		public State<Boolean> timeout() {
			System.err.println("Timeout should not be called here (State_B_a)");
			return null;
		}

		@Override
		public State<Boolean> input(HashMap<String, Boolean> X) {
			if (X.containsKey("job") && X.get("job")) {
				q ++;
				X.clear();
				return new State_B_b();
			}
			return null;
		}

		@Override
		public String getTimeoutMessage() {
			/*
			 * don't send anything
			 */
			return null;
		}
		
	}
	
	public class State_B_b extends State<Boolean>{

		public State_B_b() {
			super(0, "Buf_b");
		}

		@Override
		public void lambda() {
			super.key = "req";
			super.value = true;
		}

		@Override
		public State<Boolean> timeout() {
			q --;
			return new State_B_c();
		}

		@Override
		public State<Boolean> input(HashMap<String, Boolean> X) {
			if (X.containsKey("job") && X.get("job")) {
				q += 1;
				X.clear();
				return new State_B_b();
			}
			return null;
		}

		@Override
		public String getTimeoutMessage() {
			/*
			 * on timeout send a req message
			 */
			return "req";
		}
		
	}
	
	public class State_B_c extends State<Boolean>{


		public State_B_c() {
			super(Double.POSITIVE_INFINITY, "Buf_c");
		}

		@Override
		public void lambda() {}

		@Override
		public State<Boolean> timeout() {
			System.err.println("Timeout should not be called here (State_B_c)");
			return null;
		}

		@Override
		public State<Boolean> input(HashMap<String, Boolean> X) {
			if(X.containsKey("done") && X.get("done") && 
					(job_vs_done.equals("done") || ! X.containsKey("job"))) {
				X.clear();
				if(q > 0) {
					return new State_B_b();
				}else {
					return new State_B_a();
				}
			}
			if (X.containsKey("job") && X.get("job")) {
				q += 1;
				X.clear();
				return new State_B_c();
			}
			return null;
		}

		@Override
		public String getTimeoutMessage() {
			return null;
		}
		
	}
}
