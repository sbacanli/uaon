package routing;

import java.util.LinkedList;
import java.util.ListIterator;

import simtoo.Lib;

public class Air {

	//private LinkedList<Message> messageInTheAir;
	private Message currentMessage;
	int maxSize;
	
	//Air object constructor
	//Air will have messages
	public Air(){
		//messageInTheAir=new LinkedList<Message>();
		currentMessage=null;
		//maxSize=10;
	}
	
	//adding message to air
	public boolean addMessage(Message mes){
		
		if(Erroneous.noError()){
			currentMessage=mes;
			/*
			messageInTheAir.addFirst(mes);
			if(messageInTheAir.size()>maxSize) {
				messageInTheAir.removeLast();
			}*/
			return true;
		}	
		currentMessage=null;
		return false;
		
	}
	
	//receive the message that is sent to id
	public Message receiveMessage(int id,String time){
		Message ret=null;
		if(currentMessage.getTime().equals(time) &&
				currentMessage.getReceiver()==id ){//messages sent newly
				return currentMessage;
		}
		/*
		ListIterator<Message> listIterator = messageInTheAir.listIterator();
		while (listIterator.hasNext()) {
			Message tempmessage=listIterator.next();
			if(tempmessage.getTime().equals(time) &&
					tempmessage.getReceiver()==id ){//messages sent newly
					ret=tempmessage;
			}
		}
		*/
		
		return ret;
	}

	//Eavesdropping message retrieval
	//only used for eavesdropping!
	private Message receiveEnvironmentMessages(String time){
		//ArrayList<Message> ret=new ArrayList<Message>();
		/*for(int i=0;i<messages.size();i++){
			if(messages.get(i).getTime().equals(time) && messages.get(i).getId() != -1){
				//messages sent newly and also the ones that are 
				//real messages (not protocol related)
				ret.add(messages.get(i));
				
			}
		}*/
		//if we want eavesdropping property we shouldnt remove the packet

		return null;
	}
	
}
