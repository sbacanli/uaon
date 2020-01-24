package routing;

import java.util.ArrayList;
import java.util.Random;


public class VOIRouting extends Routing{

	double alpha;
	Random r;
	double MaxNum;
	
	public VOIRouting(Air ag, double alphagiven,double maxnum){
		super(ag);
		alpha=alphagiven;
		r=new Random(System.currentTimeMillis());
		MaxNum=maxnum;
		// TODO Auto-generated constructor stub
	}
	
	public boolean isOKToSend(double messageValue){
		double rnd=r.nextDouble();
		if(rnd<messageValue){
			return true;
		}
		return false;
	}
	
	@Override
	public void send(String timet) {
		communicate(getSender(),getReceiver(),timet);
		communicate(getReceiver(),getSender(),timet);
	}
	
	public void communicate(RoutingNode Sender,RoutingNode Receiver,String time){
		sendNonExistentMessage(Sender,Receiver,time);
	}
	
	public double valueOfMessage(Message m,String timet) {
		if(alpha<0) {
			return fractionalVal(m, timet);
		}else {
			return exponentialVal(m, timet);
		}
	}
	
	public double exponentialVal(Message m,String timet) {
		double lifetime=Integer.parseInt(timet)-Integer.parseInt(m.getTime());
		//lifetime is amount of time passed since creation of the message
		double result=1-Math.pow(Math.E,(-1*alpha*lifetime)/MaxNum);
		return result;
	}

	public double fractionalVal(Message m,String timet) {
		double lifetime=Integer.parseInt(timet)-Integer.parseInt(m.getTime());
		//lifetime is amount of time passed since creation of the message
		double result=1-Math.pow(lifetime/MaxNum,alpha);
		return result;
	}
	//sender is sending the ids of the messages it has
	//and receiving the messages
	private void sendNonExistentMessage(RoutingNode sender, RoutingNode receiver,String time){
		int senderid=sender.getId();
		int receiverid=receiver.getId();
		
		if(sender.isBufferEmpty()){
			//Lib.p("Epidemic: sender buffer empty for sender "+sender.getId()+" receiver "+receiver.getId());
			return; 
		}
		
		/* SENDER IS SENDING HIS MESSAGE VECTOR  **/
		String v=sender.getMessageVector(time);
		if(v.equals("all")){
			//v is empty which means all the messages are expired in this nodes buffer
			//nothing to send...
			//Lib.p("Epidemic: all the messages are expired!");
			return;
		}
		int messageid=-1;//that means the message is vector and it won't be put to buffer
		//creating message Vector
		Message sentmes=new Message(senderid,receiverid,v,messageid,time);
		//try to send the message until it is received correctly 
		Message receivedmes=null;
		do{
			receivedmes=sendreceive(sender,receiver,sentmes,time);
		}while(receivedmes == null);
		/*   SENDER SENT THE MESSAGE VECTOR, RECEIVER GOT IT */
		
		
		ArrayList<Integer> vd=receiver.vectorDifference(receivedmes);
		
		if(vd.isEmpty()){
		//If the sender has the exactly same messages with receiver then dont do anything close communication
			//Lib.p("Epidemic: If the sender has the exactly same messages with receiver then dont do anything close communication");
			return;
		}
		
		
		/* RECEIVER IS SENDING THE IDS THAT IT WANTS*/
		String mv=RoutingNode.getStringFromMessageVector(vd);
		Message replymessage=new Message(receiverid,senderid,mv,messageid,time);
		Message receivedreply=null;
		do{
			receivedreply=sendreceive(receiver,sender,replymessage,time);
		}while(receivedreply == null);
		/* RECEIVER SENT THE MESSAGE AND SENDER GOT IT */
		
		/* Sender is sending the messages ids stated in the packet */
		ArrayList<Integer> varray=receivedreply.getVectorArray();
		if(varray==null){
			///System.out.println("Epidemic: Problem in Epidemic.java: This message shouldn't be empty");
			//if the receiver has sent a message it means it needs some messages
			//if it doesn't need it wont send any message at all
		}
		
		for(int i=0;i<varray.size();i++){
			int id=varray.get(i).intValue();
			Message message1=sender.getMessageFromBuffer(id);
			double valueOfMessage=valueOfMessage(message1, time);
			
			if(isOKToSend(valueOfMessage)){
				boolean isGot=sender.sendMessageFromBuffer(getAir(), id, receiverid, time);			
				if(isGot){
					Message receivedMesTemp=receiver.receiveMessage(getAir(), time);	
				}
			}
			
		}

	}
	
	private Message sendreceive(RoutingNode sender,RoutingNode receiver,Message sentmes,String time){
		//Lib.p("Epidemic class sendreceive");
		boolean isGot=sender.sendMessage(getAir(), sentmes, time);	
		Message receivedmes=null;
		if(isGot){
			receivedmes=receiver.receiveMessage(getAir(), time);
		}
		return receivedmes;
	}

}
