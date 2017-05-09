package routing;


import java.util.ArrayList;

import simtoo.Lib;
import simtoo.Position;

public class RoutingNode{

	private int id;
	private ArrayList<Message> messageBuffer;
	private ArrayList<Encounter> contacts;
	private ArrayList<Encounter> encounterHistory;
	private ArrayList<Integer> sents;//not used
	private int limit;//buffer limit
	private boolean isIdle;
	private String lastEnc;
	private String mempolicy="lrr";//message removing policy in memory
	private boolean gotNewPacket;
	private double probToSend;

	//policy for deleting a message in the buffer if buffer is full
	
	
	public RoutingNode(int givenid){
		id=givenid;
		contacts=new ArrayList<Encounter>();
		encounterHistory=new ArrayList<Encounter>();
		messageBuffer=new ArrayList<Message>();
		isIdle=false;
		sents=new ArrayList<Integer>();//not used
		limit=-1;//buffer limit is infinite
		mempolicy="lrr";//least recently received
		lastEnc=null;
		gotNewPacket=false;
		probToSend=1;
	}	
	
	
	
	public boolean gotNewPacket(){
		return gotNewPacket;
	}
	
	public void setGotNewPacket(boolean g){
		gotNewPacket=g;
	}
	
	
	public ArrayList<Message> getMessageBuffer(){
		return messageBuffer;
	}
	
	public int getId(){
		return id;
	}
	
	public boolean isIdle(){
		return isIdle;
	}
	
	public void setIdle(boolean b){
		isIdle=b;
	}
	
	public String getLastEncTime(){
		return lastEnc;
	}
	
	public void setLastEncTime(String s){
		lastEnc=s;
	}
	
	public String getMemPolicy(){
		return mempolicy;
	}
	
	public void setMemPolicy(String newPolicy){
		mempolicy=newPolicy;
	}
	
	boolean isBufferEmpty(){
		return messageBuffer.isEmpty();
	}
	
	public void setProbToSend(double p){
		probToSend=p;
	}
	
	public double getProbToSend(){
		return probToSend;
	}
	
	public boolean isBufferFull(){
		if(limit==-1)
			return false;
		if(limit>=messageBuffer.size()){
			return true;
		}
		return false;
	}
	
	//cleans the expired messages in the buffer
	/* not used!
	public void cleanExpired(String timestr){
		int time=Integer.parseInt(timestr);
		for(int i=0;i<messageBuffer.size();i++){
			int exp=Integer.parseInt(messageBuffer.get(i).getExpiration());
			if(exp != -1 && time>exp){
				messageBuffer.remove(i);
			}
		}		
	}
	//*/
	//adding received message to the buffer
	//buffer message replacement policy is implemented also
	boolean addMessageToBuffer(Message message,String time){	
		if(searchBufferMessageId(message.getId()) == -1){//message is not received before
			if(isBufferFull()){
				//delete the expired messages in the buffer
				for(int i=0;i<messageBuffer.size();i++){
					Message t=messageBuffer.get(i);
					if(t.isExpired(time)){
						deleteMessageFromBuffer(t.getId());
					}
				}
				
				//after deleting the expired messages if the buffer is still full
				if(isBufferFull()){
					/*the expired messages are deleted but there is still not enough space
					 the policy is least recently received which will be the first one in the buffer
					least recently encountered gives the best performance
					but it is not implemented yet (04/17/2015) See the article below to implement
					Davis, J.A.; Fagg, A.H.; Levine, B.N.,
					"Wearable computers as packet transport mechanisms in 
					highly-partitioned ad-hoc networks," Wearable Computers, 2001. 
					Proceedings. Fifth International Symposium on , vol., no., pp.141,148, 2001
					*/
					Reporter.bufferFull(getId(), time);
					String policy=getMemPolicy();
					if(policy.equals("lrr")){
						messageBuffer.remove(0);//remove the first one which is the oldest
					}else if(policy.equals("random")){
						/*
						Random r=new Random();
						int deletedPos=r.nextInt(limit);
						messageBuffer.remove(deletedPos);
						*/
					}
					
				}//end of isBufferFull()
				
			}
			messageBuffer.add(message);
			//we managed to add it to the buffer
		}
		//message is received before so do nothing
		return true;
	}
	
	//returns true if the message is deleted
	//false if the message is not found and not deleted
	public boolean deleteMessageFromBuffer(int messageid){
		if(isBufferEmpty()){
			System.out.println("Message buffer is empty, PROBLEM in Node.java ");
			return false;
		}
		
		for(int i=0;i<messageBuffer.size();i++){
			Message t=messageBuffer.get(i);
			if(t.getId()==messageid){
				messageBuffer.remove(i);
				//break;
				return true;
			}
		}
		return false;
	}
	
	//gets the message from the buffer where messageid is given in parameter
	public Message getMessageFromBuffer(int messageid){
		if(isBufferEmpty()){
			return null;
		}
		
		for(int i=0;i<messageBuffer.size();i++){
			Message t=messageBuffer.get(i);
			if(t.getId()==messageid){
				return t;
			}
		}
		return null;
	}
	
	public int searchBufferMessageId(int messageid){
		if(isBufferEmpty()){
			return -1;
		}
		for(int i=0;i<messageBuffer.size();i++){
			Message t=messageBuffer.get(i);
			if(t.getId()==messageid){
				return i;
			}
		}
		return -1;
	}
	
	/*
	public boolean sentToBefore(int receiverid){
		int pos=sents.indexOf(new Integer(receiverid));
		if(pos==-1){//node hasn't sent packet to this receiver before
			return false;
		}
		return true;
	}
	*/
	
	//return the all messages in the buffer
	public String getAllBuffer(){
		String sum="";
		if(messageBuffer.isEmpty()){
			Lib.p("Message Buffer Empty for Node " + getId());
			return null;
		}
		
		for(int i=0;i<messageBuffer.size();i++){
			sum =sum+messageBuffer.get(i).toString()+"\r\n***************\r\n";
		}
		
		
		return sum;
	}
	
	//number of unique message ids
	//in fact the messages with same message ids are not stored, only 1 copy for each message 
	//but this function is checking the unique ones
	public String allids(){
		String all="";
		ArrayList<Integer> a=new ArrayList<Integer>();
		for(int y=0;y<messageBuffer.size();y++){
			int idm=messageBuffer.get(y).getId();
			if(!a.contains(new Integer(idm))){
				a.add(new Integer(idm));
				all=all+","+idm;
			}
		}
		if(messageBuffer.size() != a.size()){
			System.out.println("RoutingNode.java allids method problem");
		}
		return all;
	}
	
	//given a message vector this method is getting the id of the messages 
	//that doesn't exist in its buffer
	public ArrayList<Integer> vectorDifference(Message v){
		ArrayList<Integer> ret=new ArrayList<Integer>();
		ArrayList<Integer> messageIds=v.getVectorArray();
		
		if(isBufferEmpty()){
			// I have nothing in my buffer so I want all of the messages you have
			return messageIds;
		}
		
		
		for(int i=0;i<messageIds.size();i++){
			int mid=messageIds.get(i).intValue();
			if( searchBufferMessageId(mid) ==-1){
				ret.add(new Integer(mid));
			}
		}
		return ret;
	}
	
	//this method returns the string version of arraylist v with elements concatenated with :
	public static String getStringFromMessageVector(ArrayList<Integer> v){
		if(v.isEmpty()){
			return "all";
		}
	
		String all="";
		for(int i=0;i<v.size();i++){
			int el=v.get(i).intValue();
			all=all+el+":";
		}
		//remove the last ":" character from the all string
		all=all.substring(0,all.length()-1);
		return all;
	}
	
	//specify the tts with the message to be sent
	public void sendMessageFromBufferDiffTTS(Air a,int messageId,int receiverId,String time,int tts){
		Message found=getMessageFromBuffer(messageId);
		if(found == null){
			System.out.println("PROBLEM in RoutingNode.java. No message with that id");
		}else{
			//message found in buffer
			//now we will create a copy of the message if it is suitable to create a copy of it
			//TTS and expiration is checked
			if(found.isSendable(time)){
				found.decRemaining();
				Message sent=new Message(found.getPacketId(),
						this.getId(),//sender id
						receiverId,//message id
						found.getMessageText(),
						messageId,
						time,//creation time will be now which is time
						tts,
						found.getExpiration(),
						found.getHopCount()
						);
				sent.incHop();
				sendMessage(a,sent,time);
			}
		
		}
	}
	
	public boolean sendMessageFromBuffer(Air a,int messageId,int receiverId,String time){
		Message found=getMessageFromBuffer(messageId);
		if(found == null){
			System.out.println("PROBLEM in RoutingNode.java. No message with that id");
		}else{
			//message found in buffer
			//now we will create a copy of the message if it is suitable to create a copy of it
			//TTS and expiration is checked
			if(found.isSendable(time)){
				found.decRemaining();
				Message sent=new Message(found.getPacketId(),
						this.getId(),//sender id
						receiverId,//receiver id
						found.getMessageText(),
						messageId,
						time,//creation time will be now which is time
						found.getTTS(),
						found.getExpiration(),
						found.getHopCount()
						);
				sent.incHop();
				return sendMessage(a,sent,time);
			}else{
				System.out.println("PROBLEM in RoutingNode.java. message not sendable from buffer");
			}
			
		}
		return false;
	}
	
	//returns true if sent to air successfully
	//returns false if the packet is dropped
	public boolean sendMessage(Air air,Message m,String time){
		int receiverNodeId=m.getReceiver();
		if(air==null){
			System.out.println("air null problem in Routingnode.java");
		}
		
		//node sent the message. It may be dropped or received but it sent already
		
		Reporter.addPacketSent(getId(), receiverNodeId, time);
		
		if(air.addMessage(m) == false){
			Reporter.addPacketDropped(getId(), receiverNodeId, time);
			return false;
		}
	
		
		return true;
	}
	
	public boolean addtoBuffer(Message m,String time){
		if(m.isProtocolMessage()){
			//negative id messages are protocol messages.
			//they are processed and used in deciding to forward or not
			//but they are not expected to be stored.
			System.out.println("PROBLEM in RoutingNode.java: Trying to add neg id message to buffer");
			return false;
		}
		
		boolean b=addMessageToBuffer(m, time);
		//this above function should return true as it is responsible for
		//replacement policy etc..
		if(b){
			Reporter.addPacketAddedToBuffer(m.getSender(),m.getReceiver(),time);
			return true;
		}
		//there is no like that message in the air
		// or the buffer is full
		//probably problem 
		//boolean value will always return true. now this function can not return false
		//but if the addMessageToBuffer(m,time) method gets changed this is a checking mechanism
		// IT IS SUGGESTED THAT DO NOT CHANGE THIS METHOD
		//
		return false;
	}
	
	public Message receiveMessage(Air air,String time){
		Message message=air.receiveMessage(getId(),time);
		if(message==null){
			//there is no message or message dropped
			return null;
		}

		Reporter.addPacketReceived(message.getSender(),message.getReceiver(),time);
		//if the message id is not positive that condition will be handled by the below method
		// it is already integrated.
		receiveRealMessage(message,time);
		return message;
		
	}		
	
	///internal private message to be used in receiveMessage Methods
	//this is too detailed message that shouldn't be called outside
	// this message decides if the message is protocol message or not
	//to store it in buffer
	private void receiveRealMessage(Message message,String time){
		if(!message.isProtocolMessage()){
			//if the message is real message
			//it means it is not a message vector
		
			boolean b=addtoBuffer(message, time);
		
			if(!b){
				//if the message id is positive it means it is not protocol message
				//it should have been added to buffer 
				System.out.println("Can not add to buffer PROBLEM in RoutingNode.java");
				//return null;//cant add to buffer so no message
				//maybe buffer is full and replacement cant be happened
				//this case is in fact practically shouldnt be possible
			}
		}
	}
		
	//this method returns the id of the messages that the node has as a string
	public String getMessageVector(String time){
		if(isBufferEmpty()){
			return "all";
		}
		ArrayList<Integer> v=new ArrayList<Integer>();
		for(int i=0;i<messageBuffer.size();i++){
			int el=messageBuffer.get(i).getId();
			Integer intel=new Integer(el);
			if(!v.contains(intel) && messageBuffer.get(i).isSendable(time)){
				v.add(intel);
			}
		}
		//it is possible that the node's buffer is not empty but contains all expired messages
		return getStringFromMessageVector(v);
	}
	
	//this method returns the id of the messages that the node has created as a string
	public String getCreatedMessageVector(String time){
		if(isBufferEmpty()){
			return "all";
		}
		ArrayList<Integer> v=new ArrayList<Integer>();
		for(int i=0;i<messageBuffer.size();i++){
			int el=messageBuffer.get(i).getId();
			if(messageBuffer.get(i).getPrevPacketId()==-1){
				//it means the message is created by the node itself
				Integer intel=new Integer(el);
				if(!v.contains(intel) && messageBuffer.get(i).isSendable(time)){
					v.add(intel);
				}
			}
			
		}
		//it is possible that the node's buffer is not empty but contains all expired messages
		return getStringFromMessageVector(v);
	}

	/**********Contact Related Methods******************/
	/**********Contact Related Methods******************/
	/**********Contact Related Methods******************/
	/**We are using Encounter object for recording current contacts**/

	public void addContact(int idcon,Position p,int time){
		//sender is always this node
		for(int i=0;i<contacts.size();i++){
			if(contacts.get(i).getReceiverId()==idcon)
			{
				// already exists
				//it is possible as when the UAV finished route it clears everything the others not
				//System.out.println("Contact started when there was contact in RoutingNode.java"+
				//		"\r\n current node "+getId()+" contact node "+idcon);
				return;
			}
		}
		
		//it doesn't exist
		Encounter e=new Encounter(getId(),idcon,p,time);
		contacts.add(e);
	}
	
	public void removeContact(int idcon){
		if(!contacts.isEmpty()){
			for(int i=0;i<contacts.size();i++){
				if(contacts.get(i).getReceiverId()==idcon)
				{
					contacts.remove(i);
					return;
				}
			}
			//System.out.println("Contact not found in Node.java removeContact");
		}else{
			//If UAV has finished its route it will clear the contacts. The other nodes may not clear it.
			
			//System.out.println("Contact List is empty. problem in RoutingNode.java"+id+" "+idcon);
			//possibly again intersecting contacts
			//we wont give an error that will make the intersecting contact like this
			// if this is the case
			// 1   4   15    48
			//4   1   36   42
			//it will consider that this happened (taking the early finish time)
			//1   4  15  42
			//System.exit(1);
			//After AUV finihes its route it clears encounters and contacts to make them consistent.
			//In that case it is possible that some node thinks that they are still in contact.
		}
	}
	
	public boolean isInContactWith(int idcon){
		for(int i=0;i<contacts.size();i++){
			if(contacts.get(i).getReceiverId()==idcon)
			{
				return true;
			}
		}
		return false;
	}
	
	public int getNeighborCount(){
		return contacts.size();
	}
	
	public void clearContacts(){
		contacts.clear();
	}

	/**********Contact Related Methods******************/
	/**********Contact Related Methods******************/
	/**********Contact Related Methods******************/
	
	/**********Encounter Related Methods******************/
	/**********Encounter Related Methods******************/
	/**********Encounter Related Methods******************/
	
	public Encounter lastEncounterWith(int nodeId){
		Encounter e=null;
		for(int i=encounterHistory.size()-1;i>-1;i--){
			e=encounterHistory.get(i);
			
			//look for unfinished encounters
			if( (e.getReceiverId()==nodeId ) && e.getFinishingTime() == -1)
			{
				Lib.p("RoutingNode class position is: "+i);
				return e;
			}
		}
		return e;
	}
	
	public int encounterTimesWith(int nodeId){
		int num=0;
		Encounter e=null;
		for(int i=0;i<encounterHistory.size();i++){
			e=encounterHistory.get(i);
			if(e.getReceiverId()==nodeId)
			{
				num++;
			}
		}
		return num;
	}
	
	///encounter object has finishing time -1 if it hasn't finished yet
	public Encounter finishEncounter(int nodeId,int time){
		Encounter e=null;
		for(int i=0;i<encounterHistory.size();i++){
			e=encounterHistory.get(i);
			if( (e.getReceiverId()==nodeId) && e.getFinishingTime() == -1)
			{
				e.setFinishingTime(time);
				return e;
			}
		}
		return e;
	}
	
	public void addEncounter(int nodeId,Position p,int time){
		Encounter e=new Encounter(getId(),nodeId,p,time);
		encounterHistory.add(e);
	}
	
	//It will only clear the ones that are already finished.
	public void clearEncounters(){
		encounterHistory.clear();
		
		/*Encounter e=null;
		for(int i=0;i<encounterHistory.size();i++){
			e=encounterHistory.get(i);
			if(e.getFinishingTime() != -1)//finished encounter
			{
				encounterHistory.remove(i);
			}
		}
		*/
	}
	
	public ArrayList<Encounter> getEncounterHistory(){
		return encounterHistory;
	}
	
	public int getEncounterCount(){
		return encounterHistory.size();
	}
	
	/**********Encounter Related Methods******************/
	/**********Encounter Related Methods******************/
	/**********Encounter Related Methods******************/
	
	
	
	/***************************************************/
	/**********EAVESDROPPING METHODS********************/
	/***************************************************/
	//this method is for eavesdropping
	//node takes the all messages around and adds them to its buffer if they are
	//real messages not found in its buffer
	public void eavesdrop(Air air,String time){
		ArrayList<Message> allmes=air.receiveEnvironmentMessages(time);
		if(allmes==null || allmes.isEmpty()){
			//there is no message

		}
		for(int i=0;i<allmes.size();i++){
			Message m=allmes.get(i);
			//if the node itself has sent that message
			//or if the message is some message that the receiver is the actual node
			//it shouldn't eavesdrop
			if(m.getReceiver() != getId() && m.getSender() != getId()){
				Reporter.addPacketReceived(m.getSender(),m.getReceiver(),time);
				//the node will receive all messages but 
				//add only the ones that are not in its buffer
				if(m.getId() > 0 ){
					//if the message is real message
					//it means it is not a message vector
				
					boolean b=addtoBuffer(m, time);
				
					if(!b){
						System.out.println("Can not add to buffer PROBLEM in RoutingNode.java");
						//return null;//cant add to buffer so no message
						//maybe buffer is full and replacement cant be happened
						//this case is in fact practically shouldnt be possible
					}
				}else{
					//Eavesdropping should only take messages with id>0
					//if this is not the case then there is problem.
					System.out.println("Problem in RoutingNode.java at method eavesdrop");
				}
				
				
			}//check if the node is receiver or sender of that message
		}		
	}
	
	/***************************************************/
	/**********EAVESDROPPING METHODS********************/
	/***************************************************/
	
	public String toString(){
		return "Node id: "+id+" message buffer size "+messageBuffer.size();
	}
}
