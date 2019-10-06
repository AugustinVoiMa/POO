package ODE1;

import java.util.HashMap;
import java.util.Map.Entry;

import api.AtomicComponent;
import api.State;

public class Adder extends AtomicComponent<Double> {

	double somme;
	
	public Adder() {
		super("Adder");
		this.somme = 0;
	}

	private void output() {
		Y.put("somme", somme);
	}
	@Override
	protected State<Double> generateInitialState() {
		return new AdderState_a();
	}

	public class AdderState_a extends State<Double> {

		public AdderState_a() {
			super(Double.POSITIVE_INFINITY, "s");
		}

		@Override
		public void lambda() {
			super.key="somme";
			super.value=somme;
		}

		@Override
		public State<Double> timeout() {
			return null;
		}

		@Override
		public State<Double> input(HashMap<String, Double> X) {
			for (Entry<String, Double> e: X.entrySet()) {
				somme += e.getValue();				
			}
			
			X.clear();
			output();
			return null;
		}

		@Override
		public String getTimeoutMessage() {
			return null;
		}
		
	}
}
