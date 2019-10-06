package ODE1;

import java.util.HashMap;

import api.AtomicComponent;
import api.State;

public class Constante extends AtomicComponent<Double> {

	private final Double const_value;
	private final String label;

	public Constante(Double value, String label) {
		super("constante");
		this.const_value = value;
		this.label = label;
	}

	@Override
	protected State<Double> generateInitialState() {
		return new ConstInit();
	}
	
	public class ConstInit extends State<Double>{

		public ConstInit() {
			super(0, "init");
		}

		@Override
		public void lambda() {
			super.key = label;
			super.value = const_value;
		}

		@Override
		public State<Double> timeout() {			
			return new ConstFinal();
		}

		@Override
		public State<Double> input(HashMap<String, Double> X) {
			return null;
		}

		@Override
		public String getTimeoutMessage() {
			return label;
		}
		
	}
	
	public class ConstFinal extends State<Double>{

		public ConstFinal() {
			super(Double.POSITIVE_INFINITY, "final");
		}

		@Override
		public void lambda() {}

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
