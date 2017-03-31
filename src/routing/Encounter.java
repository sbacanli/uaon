package routing;

import simtoo.Position;

/*
 * This class is for holding information for encounters
 * who has encountered with who when and how many times!
 */
public class Encounter {

	//these integers are for the id of the nodes
	private int sender,receiver;
	private int startingTime,finishingTime;
	private Position p;
	
	//x and y are based on screen position it shuld be general position
	//TODO : fix it:
	
	public Encounter(){
		sender=-1;
		receiver=-1;
		startingTime=-1;
		finishingTime=-1;
		p=null;
	}
	
	public Encounter(int senderg,int receiverg,Position pg,int timegiven){
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
	
	public int getStartingTime(){
		return startingTime;
	}
	
	public void setStartingTime(int timet){
		startingTime=timet;
	}
	
	public int getFinishingTime(){
		return finishingTime;
	}
	
	public void setFinishingTime(int timet){
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
}