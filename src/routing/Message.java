package routing;

import java.util.ArrayList;
import java.util.StringTokenizer;

import simtoo.Lib;


/**
 * @author Kara
 *
 */
public class Message {

	private int receiverId;
	private int senderId;
	private String message;
	private static int staticmesid=0;
	private int packetId;
	
	private int messageId;
	//the given messageId which is given in constructor
	
	private String time;
	private int tts;//number of packets to be sent as setted
	private int remaining;// remaining number of packets to be sent
	private String expirationTime;// expiration time for the message
	
	private int hopCount;// hop count for the message
	//how many times it has been sent
	private int prevPacketId;
	private int sprayAndWaitNumber;
	
	
	//this is used in sending the vector packet in epidemic routing
	//also for asking to send the message again
	public Message(int senderId,int receiverId,String mes,int messageid,String timegiven){
		prevPacketId=-1;
		this.receiverId=receiverId;
		this.senderId=senderId;
		message=mes;
		time=timegiven;
		staticmesid++;
		packetId=staticmesid;
		messageId=messageid;
		remaining=-1;
		tts=-1;
		expirationTime="-1";//this is the number in seconds
		//that number states the expiration time of the message
		hopCount=0;
	}
	
	/*//empty vector message
	public Message(int sender,int receiver,String timegiven){
		this(sender,receiver,"Empty Vector",-2,timegiven);
	}*/
	
	//real message
	public Message(int gprevPacketId,int senderId,int receiverId,String mes,int messageid,
			String timegiven,int sprayAndWaitNumberGiven,int ttsgiven,String expirationgiven,int hopcount){
		prevPacketId=gprevPacketId;
		this.receiverId=receiverId;
		this.senderId=senderId;
		message=mes;
		time=timegiven;
		staticmesid++;
		packetId=staticmesid;
		messageId=messageid;
		tts=ttsgiven;
		sprayAndWaitNumber=sprayAndWaitNumberGiven;
		//TTL Value is not changed but remaining is being changed.
		//this is for the reason that if TTL values are to be set differently
		//for the newly messages we would like to have data of the setted TTL
		//and the remaining tts for each of the messages
		remaining=tts;
		expirationTime=expirationgiven;
		hopCount=hopcount;
	}
	
	public int getSprayAndWaitNumber() {
		return sprayAndWaitNumber;
	}
	
	public void decreaseSprayAndWaitNumber() {
		sprayAndWaitNumber--;
	}
	
	public String getMessageText(){
		return message;
	}
	
	
	/**
	 * Message Creation Time
	 * @return time as String
	 */
	public String getTime(){
		return time;
	}
	
	/**
	 * Receiver node Id
	 * @return receiverId as String
	 */
	public int getReceiver(){
		return receiverId;	
	}
	
	/**
	 * Sender node Id
	 * @return senderId as String
	 */
	public int getSender(){
		return senderId;
	}
	
	public int getId(){
		return messageId;
	}
	
	public int getHopCount(){
		return hopCount;
	}
	
	/**
	 * The time when the message will be expired
	 * @return expirationTime as String
	 */
	public String getExpiration(){
		return expirationTime;
	}
	
	public int getTTS(){
		return tts;
	}
	
	public boolean isProtocolMessage(){
		return getId()<0;
	}
	
	///remaining times to send
	//updated dynamically after sending every time (giving to air)
	public void setRemaining(int remnew){
		remaining=remnew;
	}
	
	public int getRemaining(){
		if(remaining<0 && tts != -1)
				System.out.println("remaining value is less than 0 but tts is not infinite, PROBLEM in message.java");
		return remaining;
	}
	
	public boolean isTTSEnabled(){
		return tts>0;
	}
	
	public void incHop(){
		hopCount++;
	}
	
	public int getPacketId(){
		return packetId;
	}
	
	public int getPrevPacketId(){
		return  prevPacketId;
	}
	
	//decrease tts till 0
	//returns true if it decreased which means the message can be sent
	// if this returns false it means message can not be sent.
	public void decRemaining(){
		//only the messages who are created the node itself will get TTS decreased 
		//this is for sprayAndWait fix.
		if(isTTSEnabled() && getSender()==getReceiver()){
			if(getRemaining()>0){
				remaining--;
			}else{
				System.out.println("Remaining is less than or equal to 0");
			}
		}
		//if TTS is not enabled two possibilities
		//the message has no TTS value
		//the message is a protocol message
	}
	
	//if remaining is more than 0 and isnotexpired
	//then it means message is sendable
	public boolean isSendable(String timegiven){
		if(!isExpired(timegiven)){
			if(isTTSEnabled()){
				if(getRemaining()>0){
						return true;
				}				
			}else{
				//if tts is disabled or we have copies then it is sendable
				if(getSprayAndWaitNumber()>0 || getSprayAndWaitNumber()==-1) {
					return true;
				}
				//not expired and tts is not greater than 0 but we have no sprayandwait copies left
			}
		}
		return false;
	}
	
	//returns true if the message is expired so that it wont be sent
	public boolean isExpired(String timegiven){
		if(getExpiration().equals("-1")){
			//it will never expire
			return false;
		}else{
			int current=Integer.parseInt(timegiven);
			int expirationTime=Integer.parseInt(getExpiration());
			if(current>expirationTime){
				return true;
			}
			
		}
		
		return false;
	}
	
	//Overriden method for to String
	public String toString(){
		return "sender "+getSender()+"\r\nreceiver " +getReceiver()+"\r\nid "+getId()+
				"\r\nmessage "+getMessageText()+"\r\nhop Count "+getHopCount()+"\r\ntts "+getTTS()+
				"\r\nExpiration Date "+getExpiration()+"\r\nCreation Time "+getTime();
	}
	
	//returns true if this message and Message m is identical
	public boolean equals(Message m){
		if(m.getMessageText()==getMessageText() && m.getReceiver()==getReceiver() 
				&& m.getSender()==getSender() && m.getId()==getId() && m.getTTS()==getTTS() 
				&& m.getExpiration()==getExpiration() && m.getPacketId()==getPacketId()){
			return true;
		}
		return false;
	}
	
	//if given the message
	//returns true if the message texts are same
	public boolean messageTextSame(Message m){
		if(m.getMessageText().equals(getMessageText()) ){
			return true;
		}
		return false;
	}
	
	//takes the message text that contains vector of message ids
	// that will return the ids in the int array
	//that message is a protocol message that contains only message ids
	//it is used in session communication between nodes
	public ArrayList<Integer> getVectorArray(){
		String s=getMessageText();
		ArrayList<Integer> ret=new ArrayList<Integer>();
		
		//if there are no messages in the text it means sender has no messages
		if(s.equals("all")){
			return null;
		}

		StringTokenizer st=new StringTokenizer(s,":");
		while(st.hasMoreTokens()){
			ret.add(Integer.valueOf(Integer.parseInt(st.nextToken())));
		}
		st=null;
		s=null;
		return ret;
	}
}
