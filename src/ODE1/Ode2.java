package ODE1;

import java.util.HashMap;
import java.util.Map.Entry;

import api.AtomicComponent;
import api.Port;
import api.Scheduler;
import chart.Chart;
import chart.ChartFrame;

public class Ode2 extends Scheduler<Double> {

	public static void main(String[] args) {
		new Ode2(5).run();
	}


	private Constante g;
	private IntegrateurEDiscret v;
	private IntegrateurEDiscret h;
	private Chart chart_h;
	private HashMap<String, Double> nextmessages;
	private String last_trace;

	
	protected Ode2(double tend) {
		super(tend);
		
		this.g = new Constante(-9.81, "g");
		this.v = new IntegrateurEDiscret(-0.1, 0, "g", "v");
		this.h = new IntegrateurEDiscret(-.01, 10, "v", "h");
		
		Port<Double> pg = new Port<Double>("g");
		pg.addAtomicListener(v);
		g.addPort(pg);

		Port<Double> pv = new Port<Double>("v");
		pv.addAtomicListener(h);
		v.addPort(pv);

		super.C.add(g);
		super.C.add(v);
		super.C.add(h);

		ChartFrame cf = new ChartFrame("Ode2", "gravité");
		this.chart_h = new Chart("hauteur");
		cf.addToLineChartPane(chart_h);
			
		this.nextmessages = new HashMap<String, Double>();

	}


	@Override
	protected void trace_begin_loop() {
		this.nextmessages.clear();	
		System.out.println("begin loop, trmin="+trmin);
	}


	@Override
	protected void trace_transmit_msg(AtomicComponent<Double> c, Port<Double> p, Double msg) {
		System.out.println("[message] "+p.name+"="+msg);
	}


	@Override
	protected void trace_pre_updates() {
	}


	@Override
	protected void trace_update_pre(AtomicComponent<Double> c) {
	}


	@Override
	protected void trace_update_post(AtomicComponent<Double> c) {
		String nmsg = c.getNextTimeoutMessage();
		if(nmsg != null)
			this.nextmessages.put(nmsg, c.getTr());		
	}


	@Override
	protected void trace_end_loop() {
		
		String _trace = "t="+ (super.t - super.trmin)+"\n"; // t already updated 
		_trace += "trmin="+trmin+"\n";
		/*
		 * print future messages
		 */
		for (Entry<String, Double> e : this.nextmessages.entrySet())
			_trace += (e.getKey()+"|"+e.getValue())+"\n";
				
		_trace += ("\n");
		if (!_trace.equals(this.last_trace))
			System.out.println(_trace);
		this.last_trace = _trace;
		
		
		Double y = this.h.popY("h");
		if (y != null)
			this.chart_h.addDataToSeries(super.t - trmin, y);		
		

		System.out.println("v.tr="+this.v.getTr());
	}


	@Override
	protected void trace_end_run() {
		System.out.println("Exit scheduler");
	}


}
