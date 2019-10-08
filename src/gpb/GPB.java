package gpb;

import java.util.HashMap;
import java.util.Map.Entry;

import api.AtomicComponent;
import api.Port;
import api.Scheduler;
import chart.Chart;
import chart.ChartFrame;

public class GPB extends Scheduler<Boolean> {
	
	public static void main(String [] args) {
		Scheduler<?> gpb = new GPB();
		gpb.run();
	}

	private Generator g;
	private Buffer b;
	private Processor p;
	private HashMap<String, Double> nextmessages;
	private String last_trace;

	@SuppressWarnings("unused")
	private String imms_str;
	private ChartFrame cf;
	private Chart chart;
	
	
	public GPB() {
		super(20);
		
		this.nextmessages = new HashMap<String, Double>();
		
		this.g = new Generator();
		this.b = new Buffer("job");
		this.p = new Processor();
		
		Port<Boolean> pjob = new Port<Boolean>("job");
		pjob.addAtomicListener(this.b);
		
		Port<Boolean> preq = new Port<Boolean>("req");
		preq.addAtomicListener(this.p);
		
		Port<Boolean> pdone = new Port<Boolean>("done");
		pdone.addAtomicListener(this.b);
		
		this.g.addPort(pjob);
		this.b.addPort(preq);
		this.p.addPort(pdone);
		
		super.C.add(g);
		super.C.add(p);
		super.C.add(b);		
		
		

		this.cf = new ChartFrame("GPB", "Evolution de q");
		this.chart = new Chart("q");
		this.cf.addToLineChartPane(chart);
	}
	
	@Override
	protected void trace_begin_loop() {		
		this.nextmessages.clear();
	}
	
	@Override
	protected void trace_transmit_msg(AtomicComponent<Boolean> c, Port<Boolean> p, Boolean msg) {}

	@Override
	protected void trace_pre_updates() {
		
		this.imms_str = "";
		for(AtomicComponent<Boolean> c : super.imms)
			this.imms_str += c.name+"("+c.getStateName()+"); ";
		
						
	}

	@Override
	protected void trace_update_pre(AtomicComponent<Boolean> c) {}

	@Override
	protected void trace_update_post(AtomicComponent<Boolean> c) {
		String nmsg = c.getNextTimeoutMessage();
		if(nmsg != null)
			this.nextmessages.put(nmsg, c.getTr());		
	}

	@Override
	protected void trace_end_loop() {
		
		String _trace = "t="+ (super.t - super.trmin)+"\n"; // t already updated
		_trace += ("q="+ this.b.q )+"\n"; 
		this.chart.addDataToSeries(super.t - super.trmin, this.b.q);
		/*
		 * print future messages
		 */
		for (Entry<String, Double> e : this.nextmessages.entrySet())
			_trace += (e.getKey()+"|"+e.getValue())+"\n";
				
		_trace += ("\n");
		if (!_trace.equals(this.last_trace))
			System.out.println(_trace);
		this.last_trace = _trace;
	}

	@Override
	protected void trace_end_run() {
		System.out.println("job terminated on time t="+super.t);
	}

	@Override
	protected void trace_pre_transmit() {	
	}

}
