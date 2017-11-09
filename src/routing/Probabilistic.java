package routing;

import java.util.Random;

public class Probabilistic extends Epidemic {

	double p;
	Random r;
	
	public Probabilistic(Air ag, double prob) {
		super(ag);
		p=prob;
		r=new Random();
	}
	
	public void setProb(double prob){
		p=prob;
	}
	
	public boolean isOKToSend(){
		double rnd=r.nextDouble();
		if(rnd<p){
			return true;
		}
		return false;
	}
	
	@Override
	public void send(String timet) {
		if(isOKToSend()){
			communicate(getSender(),getReceiver(),timet);
		}
		if(isOKToSend()){
			communicate(getReceiver(),getSender(),timet);
		}
	}
	
}
