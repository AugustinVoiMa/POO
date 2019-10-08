package ODE1;

import java.util.HashMap;
import java.util.Map.Entry;

import api.AtomicComponent;
import api.Port;
import api.Scheduler;
import chart.Chart;
import chart.ChartFrame;

public class BOUNCE extends Scheduler<Double> {

	public static void main(String[] args) {
		new BOUNCE(40).run();
	}


	private Constante g;
	private IntegrateurEDiscret v;
	private IntegrateurEDiscret h;
	private Chart chart_h;
	private HashMap<String, Double> nextméssages;
	private String last_trace;
	private Chart chart_v;
	private Double vitesse;
	private Double hauteur;
	private Double old_v;
	private Double old_h;

	
	protected BOUNCE(double tend) {
		super(tend);
		
		this.g = new Constante(-9.81, "g");
		this.v = new IntegrateurEDiscret(-.01, 5, "g", "v");
		this.h = new IntegrateurEDiscret(-.01, 1000, "v", "h", 0d);
		
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
		this.chart_v = new Chart("vitesse");
		cf.addToLineChartPane(chart_v);
			
		this.nextméssages = new HashMap<String, Double>();

	}


	@Override
	protected void trace_begin_loop() {
		this.nextméssages.clear();	
//		System.out.println("begin loop, trmin="+trmin);
	}


	@Override
	protected void trace_transmit_msg(AtomicComponent<Double> c, Port<Double> p, Double msg) {
//		System.out.println("[message] "+p.name+"="+msg);
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
			this.nextméssages.put(nmsg, c.getTr());
	}


	@Override
	protected void trace_end_loop() {
		
		String _trace = "t="+ (super.t - super.trmin)+"\n"; // t already updated 
		_trace += "trmin="+trmin+"\n";
		/*
		 * print future messages
		 */
		for (Entry<String, Double> e : this.nextméssages.entrySet())
			_trace += (e.getKey()+"|"+e.getValue())+"\n";
				
		_trace += ("\n");
//		if (!_trace.equals(this.last_trace))
//			System.out.println(_trace);
		this.last_trace = _trace;
		
		hauteur = h.popY("h");
		if (hauteur != null){
			this.chart_h.addDataToSeries(super.t - trmin, hauteur);
			this.old_h = hauteur;
		}
		
		if (vitesse != null){
			this.chart_v.addDataToSeries(super.t - trmin, vitesse);
			this.old_v = vitesse;
		}
		
//		System.out.println(hauteur+" ___"+vitesse);
		if (old_v != null && old_h !=null && old_h <= Double.MIN_VALUE){
			this.h.forceSetValueCDEGEU(3*Double.MIN_VALUE);
			this.v.forceSetValueCDEGEU(-.9*old_v);
//			System.err.println("rebond");
			old_h=null;
			old_v=null;
		}

//		System.out.println("v.tr="+this.v.getTr());
	}


	@Override
	protected void trace_end_run() {
		System.out.println("Exit scheduler");
	}
	

	@Override
	protected void trace_pre_transmit() {			
		C.forEach(c->{
			if (c instanceof IntegrateurEDiscret){
				if (((IntegrateurEDiscret)c).nomsortie.equals("v")){
					this.vitesse = v.getY("v");
					
				} else if (((IntegrateurEDiscret)c).nomsortie.equals("h")){
					this.hauteur= v.getY("h");			
				}
			}
		});
	}


}
