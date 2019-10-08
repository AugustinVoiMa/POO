package api;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Scheduler<T> implements Runnable {

	protected HashSet<AtomicComponent<T>> C;
	protected final double tend;
	protected double t;
	protected double t0 = 0;
	protected double trmin;
	protected Set<AtomicComponent<T>> imms;
	
	protected Scheduler(double tend){
		this.C = new HashSet<AtomicComponent<T>>();
		this.tend = tend;		
	}
	
	
	@Override
	public void run() {
		this.t = t0;
		this.trmin = 0;
		
		/*
		 * initialisation
		 */
		for ( AtomicComponent<T> c : this.C)
			c.init(t);
		
		/*
		 * begin loop, iterate until tend
		 */
		while(t < tend) {
			this.trace_begin_loop();
			
			final double ft = t; // declaring time of this iteration
			final double ftrmin = trmin;
			
			/*
			 * resetting ins attribute to false
			 */
			this.C.forEach(c -> c.ins = false);
			
			
			
			/*
			 * Detecting imminent components
			 * c.tr == ftrmin  not 0 
			 * because c times values not updated since last t increment 
			 */
			imms = null;
			imms = this.C.stream()
					.filter(a-> a.getTr() <= ftrmin).collect(Collectors.toSet());
			
			
			/*
			 * Get tr min
			 * /!\ select trmin in non-imminent subset of C
			 * subtracting current trmin because of non-updated time values  
			 * while on tp paper is not specified 
			 */
			trmin = this.C.stream()
					.min((a,b)-> (Double.compare(a.getTr(), b.getTr()))) // min tr
					.get().getTr() - ftrmin; // get tr val

			trmin = trmin>0 ? trmin : 0;
			
			/*
			 * performing lambda on imminent components
			 */			
			imms.forEach(a -> {
				try {
					a.lambda();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			this.trace_pre_transmit();
			/*
			 * transmitting messages through ports
			 */
			this.C.forEach(c ->{
				c.getPorts().forEach(p -> {
					/*
					 * for each port c is source of,
					 * we broadcast value in interested atomics 
					 */
					T msg = c.popY(p.name);					
					if (msg != null) {
						this.trace_transmit_msg(c , p, msg); 
						p.broadcast(msg);					
					}
				});
			});
			
			this.trace_pre_updates();
			/*
			 * Updating components
			 */
			this.C.forEach(c -> {
				trace_update_pre(c);
				
				if(imms.contains(c)) {
					if (c.ins) {
						/*
						 * ins != null in tp paper						 
						 * conflict because an imminent component 
						 * should not be waiting for an input
						 * => choosing to execute external method anyway 
						 */
						c.external(ft);						
					}else {
						/* 
						 * ins == null in tp paper
						 * this is the normal case of an imminent component
						 * executing internal method 
						 */
						try {
							c.internal(ft);
						} catch (Exception e) {
							e.printStackTrace();
						}						
					}
				}else {
					if(c.ins) {
						/*
						 * ins != null in paper
						 * normal case of a non-imminent atomic component 
						 * receiving a message
						 */
						c.external(ft);
					}else {
						/*
						 * ins == null in paper
						 * normal case of a non-imminent atomic component 
						 * receiving no message
						 * Do nothing
						 */						
					}				
				}
				/*
				 * in every cases, update time
				 */
				c.update_time(ft);
				
				trace_update_post(c);				
			});
			
			/*
			 * Skipping time to next event
			 */
			t += trmin;			
			this.trace_end_loop();
		}
		this.trace_end_run();
	}

	


	protected abstract void trace_pre_transmit();


	/**
	 * Called at the beginning of a state-update loop
	 */
	protected abstract void trace_begin_loop();
	
	/**
	 * Called when a message is broadcasted through a port
	 * @param c Source component of the transmitted message 
	 * @param p Broadcasting port (containing key of the message)
	 * @param msg the Message itself (containing the value of the message)
	 */
	protected abstract void trace_transmit_msg(final AtomicComponent<T> c, final Port<T> p, final T msg);

	/**
	 * Called before calling update loop
	 */
	protected abstract void trace_pre_updates();
	
	/**
	 * Called after updating component c
	 * @param c Updated component
	 */
	protected abstract void trace_update_pre(final AtomicComponent<T> c);
	
	/**
	 * Called before updating component c
	 * @param c Component to update
	 */
	protected abstract void trace_update_post(final AtomicComponent<T> c);
	
	
	/**
	 * Called at the end of a state-update loop
	 */
	protected abstract void trace_end_loop();
	

	/**
	 * Called at the end of run
	 */
	protected abstract void trace_end_run();
}
