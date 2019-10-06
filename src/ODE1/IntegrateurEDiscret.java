package ODE1;

import java.util.HashMap;

import api.AtomicComponent;
import api.State;

public class IntegrateurEDiscret extends AtomicComponent<Double> {

	private double dQ;
	private final String nomsortie;
	private String nomentrée;
	private double offset;

	public IntegrateurEDiscret(double dQ) {
		super("intégrateur événement discret");
		this.dQ = dQ;
		this.nomsortie = "/x dt";
		this.nomentrée = "x";
		this.offset = 0;
	}
	
	public IntegrateurEDiscret(double dQ, double offset, String nomentrée, String nomsortie) {
		super("intégrateur événement discret");
		this.dQ = dQ;
		this.offset = offset;
		this.nomsortie = nomsortie;
		this.nomentrée = nomentrée;
	}

	@Override
	protected State<Double> generateInitialState() {
		return new StateIntED(Double.POSITIVE_INFINITY, offset);
	}
	
	public class StateIntED extends State<Double>{

		private double y;
		
		public StateIntED(double tstep, double y) {
			super(tstep, "état événement discret");
			this.y = y;
		}

		@Override
		public void lambda() {
			super.key = nomsortie;
			super.value = this.y;
		}

		@Override
		public State<Double> timeout() {
			System.out.println("IED timeout y="+(y+dQ));
			return new StateIntED(ta, y+dQ);
		}

		@Override
		public State<Double> input(HashMap<String, Double> X) {
			if (X.containsKey(nomentrée) && X.get(nomentrée) != null) {				
				
				double x = X.get(nomentrée);
				
				super.ta = dQ / x;
				assert(super.ta > 0);
				System.out.println("IED << "+x+"; ta="+ta);
				X.clear();
			}
			return null;
		}

		@Override
		public String getTimeoutMessage() {
			return nomsortie;
		}		
	}

}
