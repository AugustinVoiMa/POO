package ODE1;

import java.util.HashMap;
import java.util.Map.Entry;

import api.AtomicComponent;
import api.Port;
import api.Scheduler;
import chart.Chart;
import chart.ChartFrame;

public class Ode1 extends Scheduler<Double> {
	
	public static void main(String [] args) {
		Ode1 ode = new Ode1();
		ode.run();
	}
	
	
	private Step step1;
	private Step step2;
	private Step step3;
	private Step step4;
	private Adder adder;
	private Port<Double> port1;
	private Port<Double> port2;
	private Port<Double> port3;
	private Port<Double> port4;
	private HashMap<String, Double> nextmessages;
	private Object last_trace;
	private ChartFrame cf;
	private Port<Double> portI;
	private Chart chartsomme;
	private IntegrateurTDiscret integrateurTD;
	private IntegrateurEDiscret integrateurED;
	private Chart chartxdttd;
	private Chart chartxdted;

	protected Ode1() {
		super(2);
		this.step1 = new Step(1, 1, -3, .65);
		this.step2 = new Step(2, 0, 1, .35);
		this.step3 = new Step(3, 0, 1, 1);
		this.step4 = new Step(4, 0, 4, 1.5);
		
		this.adder = new Adder();

		this.integrateurTD = new IntegrateurTDiscret(.1);
		this.integrateurED = new IntegrateurEDiscret(.1, 0, "x", "/x dt");

		super.C.add(step1);
		super.C.add(step2);
		super.C.add(step3);
		super.C.add(step4);
		super.C.add(adder);
		super.C.add(integrateurTD);
		super.C.add(integrateurED);
		
		this.port1 = new Port<Double>("x1");
		port1.addAtomicListener(adder);
		step1.addPort(port1);
		
		
		this.port2 = new Port<Double>("x2");
		port2.addAtomicListener(adder);
		step2.addPort(port2);
		
		this.port3 = new Port<Double>("x3");
		port3.addAtomicListener(adder);
		step3.addPort(port3);
		
		this.port4 = new Port<Double>("x4");
		port4.addAtomicListener(adder);
		step4.addPort(port4);
		
		this.portI = new Port<Double>("x");
		portI.addAtomicListener(integrateurTD);
		portI.addAtomicListener(integrateurED);
		adder.addPort(portI);
		
		this.nextmessages = new HashMap<String, Double>();
		
		
		this.cf = new ChartFrame("Ode1", "Evolution de la somme");
		this.chartsomme = new Chart("somme");
		this.chartxdttd = new Chart("/x dt (temps discret)");
		this.chartxdted = new Chart("/x dt (événement discret)");
		cf.addToLineChartPane(chartsomme);
		cf.addToLineChartPane(chartxdttd);
		cf.addToLineChartPane(chartxdted);
		

	}


	@Override
	protected void trace_begin_loop() {		
		this.nextmessages.clear();
	}
	
	@Override
	protected void trace_transmit_msg(AtomicComponent<Double> c, Port<Double> p, Double msg) {}

	@Override
	protected void trace_pre_updates() {}

	@Override
	protected void trace_update_pre(AtomicComponent<Double> c) {}

	@Override
	protected void trace_update_post(AtomicComponent<Double> c) {
		String nmsg = c.getNextTimeoutMessage();
		if(nmsg != null)
			this.nextmessages.put(nmsg, c.getTr());		
	}

	@Override
	protected void trace_end_loop() {
		
		String _trace = "t="+ (super.t - super.trmin)+"\n"; // t already updated 
		/*
		 * print future messages
		 */
		for (Entry<String, Double> e : this.nextmessages.entrySet())
			_trace += (e.getKey()+"|"+e.getValue())+"\n";
				
		_trace += ("\n");
		if (!_trace.equals(this.last_trace))
			System.out.println(_trace);
		this.last_trace = _trace;
		
		Double y = this.adder.getY("x");
		if(y != null)
			chartsomme.addDataToSeries(super.t-trmin, y);

		Double xdttd = this.integrateurTD.popY("/x dt");
		if(xdttd != null)
			chartxdttd.addDataToSeries(super.t-trmin, xdttd);
			
		Double xdted = this.integrateurED.popY("/x dt");
		if(xdted != null)
			chartxdted.addDataToSeries(super.t-trmin, xdted);

	}

	@Override
	protected void trace_end_run() {
		System.out.println("job terminated on time t="+super.t);
	}
}
