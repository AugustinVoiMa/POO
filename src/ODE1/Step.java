package ODE1;

import java.util.HashMap;

import api.AtomicComponent;
import api.State;

public class Step extends AtomicComponent<Double> {
	public double xi;
	public double xf;
	public double ts;
	private int numStep;
	
	public Step(int numStep, double xi, double xf, double ts) {
		super("Step");
		this.xi = xi;
		this.xf = xf;
		this.ts = ts;
		this.numStep = numStep;
	}

	@Override
	protected State<Double> generateInitialState() {
		return new Step_initial(this.ts);
	}
	
	public class Step_initial extends State<Double>{

		public Step_initial(double ts) {
			super(0, "initial");
		}

		@Override
		public void lambda() {
			super.key="x"+numStep;
			super.value = xi;
		}

		@Override
		public State<Double> timeout() {
			return new Step_transition();
		}

		@Override
		public State<Double> input(HashMap<String,Double> X) {
			return null;
		}

		@Override
		public String getTimeoutMessage() {
			return "x"+numStep;
		}		
	}
	
	public class Step_transition extends State<Double>{

		public Step_transition() {
			super(ts, "transition");
		}

		@Override
		public void lambda() {
			super.key = "x"+numStep;
			super.value = xf;
		}

		@Override
		public State<Double> timeout() {
			return new Step_final();
		}

		@Override
		public State<Double> input(HashMap<String, Double> X) {
			return null;
		}

		@Override
		public String getTimeoutMessage() {
			return "x"+numStep;
		}
		
	}
	
	public class Step_final extends State<Double>{

		public Step_final() {
			super(Double.POSITIVE_INFINITY, "final");
		}

		@Override
		public void lambda() {
		}

		@Override
		public State<Double> timeout() {
			return null;
		}

		@Override
		public State<Double> input(HashMap<String, Double> X) {
			return null;
		}

		@Override
		public String getTimeoutMessage() {
			return null;
		}
		
	}
}
