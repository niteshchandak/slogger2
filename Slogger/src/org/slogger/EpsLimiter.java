/**
 * 
 */
package org.slogger;

/**
 * Class which limits the EPS to the specified value
 * @author preetham
 *
 */
public class EpsLimiter extends Thread {
	
	private volatile int token = 0;	
	private int epsRate = 0;
	private boolean run = true;
	private boolean noRateLimit = false; 
	
	public EpsLimiter(int epsRate) {
		this.epsRate = epsRate;
		if (epsRate == 0)
			noRateLimit = true;
		else {
			noRateLimit = false;
			this.start();
		}
		
	}
	
	@Override
	public void run() {
		while (run) {
			this.token = epsRate/3;
			try {
				Thread.sleep(333);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
	
	public boolean ok() {
		return (noRateLimit || --token > 0);
	}
	
	public void stopLimiter() {
		run = false;
	}
	

}
