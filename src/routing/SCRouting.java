package routing;

import java.util.ArrayList;

public class SCRouting extends Probabilistic {

	
	private final double alpha;
	private final double wantedprob;
	private final double lambda;
	private final int timelimit;
	private final double divisor;
	
	/**
	 * @param Air Alpha wantedProb Lambda Divisor GiventimeLimit
	 */
	public SCRouting(Air a, double alpha, double wantedProb, double lambda, double divisor){
		super(a,1);
	
		this.alpha=alpha;
		this.lambda=lambda;
		wantedprob=wantedProb;
		timelimit=-1;//giventimelimit;
		this.divisor=divisor;
		
	}
	
	public void setSender(RoutingNode sn){
		super.setSender(sn);
	}
	
	public void setReceiver(RoutingNode rn){
		super.setReceiver(rn);
	}
	
	public double getWantedProb(){
		return wantedprob;
	}
	
	public double getAlpha(){
		return alpha;
	}
	
	public void communicate(RoutingNode Sender,RoutingNode Receiver,String time){
		super.communicate(Sender,Receiver,time);
	}
	
	//overriden method
	@Override
	public void send(String time){
		if(alpha== -1 && wantedprob == -1){
			System.out.println("Parameters are negative in SCRouting.java");
			super.send(time);
			return;
		}
		RoutingNode s=getSender();
		RoutingNode r=getReceiver();
		
		sendToReceiver(s,r,time);
		sendToReceiver(r,s,time);
		
	}
	
	public void sendToReceiver(RoutingNode s,RoutingNode r,String time){
		
		double oldp=s.getProbToSend();
		setProb(oldp);
		
		double newprob=0;
		int timeconv=Integer.parseInt(time);
		s.setIdle(isNodeIdle(s,r, timeconv));
		if(s.isIdle()){
			newprob=f(oldp,getAlpha(),getWantedProb())*lambda;
		}else{
			newprob=f(oldp,getAlpha(),getWantedProb());	
		}
	
		s.setProbToSend(newprob);		
		setProb(newprob);
		if(isOKToSend()){
			communicate(s,r,time);
		}
		
	}
	
	public final boolean isNodeIdle(RoutingNode sender,RoutingNode receiver,int time){
		ArrayList<Encounter> encounters=sender.getEncounterHistoryWithNodes();
		if(encounters.size()>2) {
			
			ArrayList<Encounter> last3=new ArrayList<Encounter>();
			int count=0;
			int index=1;
			//getting last 3 finished encounters
			Encounter current=null;
			while(count<3 && index<=encounters.size()) {
				//starting from the last this method adds the finished encounters to last3
				current=encounters.get(encounters.size()-index);
				if(current.isFinished()) {
					last3.add(new Encounter(current));
					count++;
				}
				index++;
			}
			current=null;
			
			if(last3.size() >=3){
				Encounter last=last3.get(0);
				Encounter oneBeforeLast=last3.get(1);
				Encounter twoBeforeLast=last3.get(2);
				
				double currentlast=twoBeforeLast.getFinishingTime() -oneBeforeLast.getFinishingTime();
				if(timelimit>0 && currentlast>timelimit){
					last3.clear();last3=null;
					return false;
				}
				double lastprev=(oneBeforeLast.getFinishingTime()-last.getFinishingTime());
				if(lastprev/currentlast > divisor){
					last3.clear();last3=null;
					return true;//idle
				}//end of lastprev/currentlast > divisor check
				
			}//last3.size check
			last3.clear();last3=null;
		}//end of encounters size check
		encounters=null;
		return false;
		
	}
	
	private double f(double p1,double alpha,double wantedProb){
		return p1*alpha*alpha+(1-alpha)*wantedProb;
	}
}
