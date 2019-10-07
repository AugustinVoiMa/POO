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
		return new InitialState();
	}
	
	public class InitialState extends State<Double>{

		public InitialState() {
			super(0, "EDI_initial");
		}

		@Override
		public void lambda() {
			super.key=nomsortie;
			super.value=offset;
		}

		@Override
		public State<Double> timeout() {
			return new StateIntED(Double.POSITIVE_INFINITY, offset, dQ);
		}

		@Override
		public State<Double> input(HashMap<String, Double> X) {			
			if(!X.containsKey(nomentrée))
				return null;

			//else						
			State<Double> s = new StateIntED(Double.POSITIVE_INFINITY, offset, dQ);
			s.input(X);
			return s;
		}

		@Override
		public String getTimeoutMessage() {
			return nomsortie;
		}
		
	}
	
	public class StateIntED extends State<Double>{

		private double y;
		private double dQ;
		
		public StateIntED(double tstep, double y, double dQ) {
			super(tstep, "état événement discret");
			this.y = y + dQ;
			this.dQ = dQ;
		}

		@Override
		public void lambda() {
			super.key = nomsortie;
			super.value = this.y;
		}

		@Override
		public State<Double> timeout() {
			return new StateIntED(ta, y, dQ);
		}

		@Override
		public State<Double> input(HashMap<String, Double> X) {
			if (X.containsKey(nomentrée) && X.get(nomentrée) != null) {				
				
				double x = X.get(nomentrée);
				
				// if x and dQ not the same sign, change dQ to opposite				
				if (x * this.dQ < 0) 
					this.dQ *= -1;
				
				super.ta = dQ / x;			
				if (super.ta == Double.NEGATIVE_INFINITY)
					ta *= -1;
				if (super.ta < 0)
					System.err.println("ta < 0: "+ta+" with x="+x);
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
