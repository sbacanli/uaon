package routing;

/*
 * This class is for holding information for encounters
 * who has encountered with who when and how many times!
 */
public class Encounter {

	//these integers are for the id of the nodes
	private int sender,receiver;
	private int time;

	
	public Encounter(){
		sender=-1;
		receiver=-1;
		time=-1;
	}
	
	public Encounter(int senderg,int receiverg,int timegiven){
		sender=senderg;
		receiver=receiverg;
		time=timegiven;
	}
	
	//copy constructor
	public Encounter(Encounter e){
		sender=e.sender;
		receiver=e.receiver;
		time=e.time;
	}
	
	public int getTime(){
		return time;
	}
	
	public void setTime(int timet){
		time=timet;
	}
	
	public int getSenderId(){
		return sender;
	}
	
	public int getReceiverId(){
		return receiver;
	}
	
}