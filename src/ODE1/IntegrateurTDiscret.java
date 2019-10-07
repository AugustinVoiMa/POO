package ODE1;

import java.util.HashMap;

import api.AtomicComponent;
import api.State;

public class IntegrateurTDiscret extends AtomicComponent<Double> {

	private double hstep;

	public IntegrateurTDiscret(double hstep) {
		super("Intégrateur");
		this.hstep = hstep;
	}

	@Override
	protected State<Double> generateInitialState() {
		return  new IntState(hstep);
	}
	
	public class IntState extends State<Double>{
		
		private final double y;
		private double d;
		
		public IntState(double hstep) {
			super(hstep, "intégrateur");
			y=0;
			d = 0;			
		}
		
		public IntState(double hstep, double y, double d) {
			super(hstep, "intégrateur");
			this.y=y+ (d*hstep);
			this.d = d;
		}

		@Override
		public void lambda() {
			super.key = "/x dt";
			super.value = y;
		}

		@Override
		public State<Double> timeout() {						
			return new IntState(this.ta, y, d);
		}

		@Override
		public State<Double> input(HashMap<String, Double> X) {
			if (X.containsKey("x") && X.get("x") != null) {
				double d2 = X.get("x");
				X.clear();
				this.d = d2;
				System.out.println("new d="+d2+"; timeout in "+getTr());
			}
			return null;
		}

		@Override
		public String getTimeoutMessage() {
			return "/x dt";
		}		
	}
}
