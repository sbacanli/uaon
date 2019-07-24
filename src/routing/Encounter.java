package routing;

import simtoo.Position;

/*
 * This class is for holding information for encounters
 * who has encountered with who when and how many times!
 */
public class Encounter implements Comparable<Encounter>,java.io.Serializable {

	//these integers are for the id of the nodes
	private int sender,receiver;
	private long startingTime,finishingTime;
	private Position p;
	private static final long serialVersionUID = 1L;
	
	public Encounter(){
		sender=-1;
		receiver=-1;
		startingTime=-1;
		finishingTime=-1;
		p=null;
	}
	
	public Encounter(int senderg,int receiverg,Position pg,long timegiven){
		sender=senderg;
		receiver=receiverg;
		startingTime=timegiven;
		finishingTime=-1;
		p=pg;
	}
	
	//copy constructor
	public Encounter(Encounter e){
		sender=e.sender;
		receiver=e.receiver;
		startingTime=e.startingTime;
		finishingTime=e.finishingTime;
		p=e.getPosition();
	}
	
	public long getStartingTime(){
		return startingTime;
	}
	
	public void setStartingTime(int timet){
		startingTime=timet;
	}
	
	public long getFinishingTime(){
		return finishingTime;
	}
	
	public boolean isFinished() {
		return finishingTime !=-1;
	}
	
	public void setFinishingTime(long timet){
		finishingTime=timet;
	}
	
	public int getSenderId(){
		return sender;
	}
	
	public int getReceiverId(){
		return receiver;
	}
	
	public String toString(){
		return sender+"\t"+receiver+"\t"+startingTime+"\t"+finishingTime+"\r\n";
	}
	
	public void setPosition(Position pg){
		p=pg;
	}
	
	public Position getPosition(){
		return p;
	}
	
	public boolean checkEquality(Encounter e){
		if(e.getStartingTime()==getStartingTime() && e.getFinishingTime()==getFinishingTime()){
			if((e.getSenderId()==getSenderId()) || (e.getReceiverId()==getSenderId())){
				if((e.getSenderId()==getReceiverId()) || (e.getReceiverId()==getReceiverId())){
					if((e.getPosition().getScreenPoint()==getPosition().getScreenPoint())){
						if((e.getPosition().getRealPoint()==getPosition().getRealPoint())){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public int compareTo(Encounter o) {
		if(o.getReceiverId()==getReceiverId()) return 0;
		if(o.getReceiverId()<getReceiverId()) return -1;
		return 1;
	}
	
	public boolean equals(Encounter s){
		if(getReceiverId()==s.getReceiverId() && s.getSenderId()==getSenderId()){
			return true;
		}
		if(getReceiverId()==s.getSenderId() && s.getReceiverId()==getSenderId()){
			return true;
		}
		return false;
	}
}