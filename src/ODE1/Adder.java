package ODE1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import api.AtomicComponent;
import api.State;

public class Adder extends AtomicComponent<Double> {

	double somme;
	private ArrayList<Double> Xvals;
	
	public Adder(int nbx) {
		super("Adder");
		this.somme = 0;
		this.Xvals = new ArrayList<Double>();
		for (int  i= 0; i < nbx; i++)
			this.Xvals.add(0.d);
	}

	private void output() {
		Y.put("x", somme);
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
		public void lambda() {}

		@Override
		public State<Double> timeout() {
			return null;
		}

		@Override
		public State<Double> input(HashMap<String, Double> X) {
			for (Entry<String, Double> e: X.entrySet()) {
				int nx = Integer.parseInt(e.getKey().substring(1))-1;
				Xvals.set(nx,e.getValue());				
			}
			somme = 0;
			
			Xvals.forEach(v->somme += v);
			
			X.clear();
			output();
			return null;
		}

		@Override
		public String getTimeoutMessage() {
			return "x";
		}
		
	}
}
