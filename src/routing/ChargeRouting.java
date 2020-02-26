package routing;

import simtoo.Lib;
import simtoo.PointP;
import simtoo.Positionable;

public class ChargeRouting extends Probabilistic {
	PointP[] chargingStations;
	double distance;
	
	
	public ChargeRouting(Air ag,PointP[] chargingStations, double distance) {
		super(ag,1);
		this.distance=distance;
		//distance is already virtual
		this.chargingStations=chargingStations;
	}
	
	public double distanceToClosestChargingLocation(Positionable n1, long tm) {
		PointP nodepos=n1.getCurrentPosition().getScreenPoint();
		//Here We are making a golden handcuff
		//The current position's time and expected time should be exactly same.
		//If they are not then it is a problem!!!!
		if(n1.getCurrentPosition().getTime()!=tm) {
			Lib.p(n1.getCurrentPosition().getTime()+" "+tm);
			
		}
		
		double mindist=0;
		int minpos=0;
		
		mindist=Lib.screenDistance(chargingStations[0].getX(), chargingStations[0].getY(), nodepos.getX(), nodepos.getY());
		for(int i=1;i<chargingStations.length;i++) {
			PointP el=chargingStations[i];
			double tempmindist=Lib.screenDistance(el.getX(), el.getY(), nodepos.getX(), nodepos.getY());
			if(tempmindist<mindist) {
				mindist=tempmindist;
				minpos=i;
			}
		}
		return mindist;
		
	}
	
	public void send(String timet) {
		long ttime=Long.parseLong(timet);
		double senderDist=distanceToClosestChargingLocation(getSenderNode(), ttime);
		double receiverDist=distanceToClosestChargingLocation(getReceiverNode(), ttime);
		
		setProb(1);
		if(senderDist< distance) {
			setProb(senderDist/distance);
		}//if the sender is not close to any charging station enough, it will definitely send
			
		if(isOKToSend()){
			communicate(getSender(),getReceiver(),timet);
		}
		
		setProb(1);
		if(receiverDist< distance) {
			setProb(receiverDist/distance);
		}//if the receiver is not close to any charging station enough, it will definitely send
		
		if(isOKToSend()){
			communicate(getReceiver(),getSender(),timet);
		}
	}
}


