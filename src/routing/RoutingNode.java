package routing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import dstruct.LimitedQueue;

import simtoo.Lib;
import simtoo.Position;

public class RoutingNode
{
	private int id;
	private HashMap<Integer,Message> messageBuffer;
	//message ids as keys and messages as values in HashMap
	private HashMap<Integer,Encounter> contacts;
	//Receiver IDs as keys and Encounters as values.
	//only one contact can be done with the receiver
	private LimitedQueue<Encounter> encounterHistory;
	private int limit;
	private boolean isIdle;
	private String lastEnc;
	private String mempolicy = "lrr";// this is not used
	private boolean gotNewPacket;
	private double probToSend;
	private LimitedQueue<Encounter> encounterHistoryWithNodes;
	private HashMap<Integer, Encounter> uniqueEncsWithNodes;
	public RoutingNode(int givenid)
	{
		id = givenid;
		contacts = new HashMap<Integer,Encounter>();
		encounterHistory = new LimitedQueue<Encounter>(100);
		encounterHistoryWithNodes = new LimitedQueue<Encounter>(100);
		messageBuffer = new HashMap<Integer,Message>();
		isIdle = false;
		limit = -1;
		mempolicy = "lrr";
		lastEnc = null;
		gotNewPacket = false;
		probToSend = 1.0D;
		uniqueEncsWithNodes = new HashMap<Integer, Encounter>();
	}

	public boolean gotNewPacket()
	{
		return gotNewPacket;
	}

	public void setGotNewPacket(boolean g) {
		gotNewPacket = g;
	}

	public Collection<Message> getMessageBuffer()
	{
		return (Collection<Message>)(messageBuffer.values());
	}

	public int getId() {
		return id;
	}

	public boolean isIdle() {
		return isIdle;
	}

	public void setIdle(boolean b) {
		isIdle = b;
	}

	public String getLastEncTime() {
		return lastEnc;
	}

	public void setLastEncTime(String s) {
		lastEnc = s;
	}

	public String getMemPolicy() {
		return mempolicy;
	}

	public void setMemPolicy(String newPolicy) {
		mempolicy = newPolicy;
	}

	boolean isBufferEmpty() {
		return messageBuffer.isEmpty();
	}

	public void setProbToSend(double p) {
		probToSend = p;
	}

	public double getProbToSend() {
		return probToSend;
	}

	public boolean isBufferFull() {
		if (limit == -1)
			return false;
		if (limit >= messageBuffer.size()) {
			return true;
		}
		return false;
	}


	boolean addMessageToBuffer(Message message, String time)
	{
		
		if (searchBufferMessageId(message.getId()) ==false) {
			if (isBufferFull())
			{
				Set<Integer> mkeys=messageBuffer.keySet();
				for (Integer el:mkeys) {
					Message t = (Message)messageBuffer.get(el);
					if (t.isExpired(time)) {
						deleteMessageFromBuffer(t.getId());
					}
				}
				mkeys=null;
				//after attempt to delete old messages, if it is still full
				if (isBufferFull())
				{
					Reporter.bufferFull(getId(), time);
					String policy = getMemPolicy();
					/*
					 * This is to be implemented for future research
					 * Least recently used, not most recently used, random etc...
					 */
				}
			}
			//buffer is never full so this if is useless but I am keeping it
			messageBuffer.put(message.getId(), message);
		}

		return true;
	}

	/*This is not used! Used only if the buffer is full*/
	public boolean deleteMessageFromBuffer(int messageid)
	{
		if (isBufferEmpty()) {
			System.out.println("Message buffer is empty, PROBLEM in Node.java ");
			return false;
		}
		Message m1=messageBuffer.get(messageid);
		if(m1==null) {
			return false;
		}else {
			messageBuffer.remove(messageid);
		}
		m1=null;
		return true;
	}
	
	
	public Message getMessageFromBuffer(int messageid)
	{
		if (isBufferEmpty()) {
			return null;
		}

		return messageBuffer.get(messageid);
			
	}
	
	
	public boolean searchBufferMessageId(int messageid) {
		if (isBufferEmpty()) {
			return false;
		}
		if( messageBuffer.get(messageid) == null ) {
			return false;
		}
		return true;
	}
	
	
	public String getAllBuffer()
	{
		StringBuilder sum1=new StringBuilder("");
		if (messageBuffer.isEmpty()) {
			Lib.p("Message Buffer Empty for Node " + getId());
			return null;
		}
		
		Set<Integer> mkeys=messageBuffer.keySet();
		for (Integer el:mkeys) {
			Message t = (Message)messageBuffer.get(el);
			sum1.append(t.toString());
			sum1.append("\r\n***************\r\n");
		}
		mkeys=null;
		return sum1.toString();
	}


	//since all the messages are stored in hashmap with messageIds as keys
	//we can get all the keys and concetenate
	public String allids()
	{
		Set<Integer> allIDs=messageBuffer.keySet();
		StringBuilder all=new StringBuilder("");
		String result=null;
		for(Integer id:allIDs) {
			
			all.append(id);all.append(",");
		}
		result=all.toString();
		all=null;
		allIDs=null;
		return result;
	}
	
	public ArrayList<Double> getAllRemainingFromMessageBuffer() {
		ArrayList<Double> remain=new ArrayList<Double>();
		Set<Integer> mkeys=messageBuffer.keySet();
		Message t=null;
		for (Integer el:mkeys) {
			t = (Message)messageBuffer.get(el);
			remain.add((double) t.getRemaining());
		}
		t=null;
		mkeys=null;
		return remain;
	}

	public ArrayList<Integer> vectorDifference(Message v)
	{
		ArrayList<Integer> ret = new ArrayList<Integer>();
		ArrayList<Integer> messageIds = v.getVectorArray();

		if (isBufferEmpty())
		{
			return messageIds;
		}


		for (int i = 0; i < messageIds.size(); i++) {
			int mid = ((Integer)messageIds.get(i)).intValue();
			if (searchBufferMessageId(mid)==false) {
				ret.add(Integer.valueOf(mid));
			}
		}
		messageIds=null;
		return ret;
	}

	public static String getStringFromMessageVector(ArrayList<Integer> v)
	{
		if (v.isEmpty()) {
			return "all";
		}

		StringBuilder all=new StringBuilder("");
		for (int i = 0; i < v.size(); i++) {
			int el = ((Integer)v.get(i)).intValue();
			all.append(el);all.append(":");
		}

		all.substring(0, all.length() - 1);
		String result= all.toString();
		all=null;
		return result;
	}
	
	/*
	 * if sprayandwait number is enabled meaning not -1 it decreases the number of copies that the node has
	 * @param messageId
	 * @return returns true if it is not enabled
	 */
	public boolean decreaseSprayAndWaitNumber(int messageid)
	{
		
		if (isBufferEmpty()) {
			Lib.p("Buffer empty but decreasing spray and Wait");
			return false;
		}
		Message changed=getMessageFromBuffer(messageid);
		if(changed == null) {
			Lib.p("no such message in decreaseSprayAndWaitNumber in RoutingNode.java");
			return false;
		}
		if(changed.getSprayAndWaitNumber()>0) {
			changed.decreaseSprayAndWaitNumber();
		}else if(changed.getSprayAndWaitNumber()==0) {
			//It is zero can not decrease further
			Lib.p("This should not happen as isSendable function should not allow this to be reduced further in RoutingNode.java ");
			return false;
		}
		//else it is -1 which means not enabled
		return true;
		
	}
	
	/*
	 * This methods checks the buffer for the messageId specified and sends that message to air with tts value
	 * @param Air a, messageId
	 * 
	 * @return The message is in the air or disappeared in the air.
	 */
	public void sendMessageFromBufferDiffTTS(Air a, int messageId, int receiverId, String time, int tts)
	{
		Message found = getMessageFromBuffer(messageId);
		if (found == null) {
			System.out.println("PROBLEM in RoutingNode.java. No message with that id");

		}
		else if (found.isSendable(time)) {
			found.decRemaining();
			
			Message sent = new Message(found.getPacketId(), 
					getId(), 
					receiverId, 
					found.getMessageText(), 
					messageId, 
					time, 
					found.getSprayAndWaitNumber(),
					tts, 
					found.getExpiration(), 
					found.getHopCount());

			sent.incHop();
			sendMessage(a, sent, time);
			
			decreaseSprayAndWaitNumber(messageId);
		}
	}


	public boolean sendMessageFromBuffer(Air a, int messageId, int receiverId, String time)
	{
		Message found = getMessageFromBuffer(messageId);
		if (found == null) {
			System.out.println("PROBLEM in RoutingNode.java. No message with that id");
		}
		else
		{
			if (found.isSendable(time)) {
				found.decRemaining();
				Message sent = new Message(found.getPacketId(), 
						getId(), 
						receiverId, 
						found.getMessageText(), 
						messageId, 
						time, 
						found.getSprayAndWaitNumber(),
						found.getTTS(), 
						found.getExpiration(), 
						found.getHopCount());

				sent.incHop();
				//original message's spray and wait value is decreased not the new one's!
				decreaseSprayAndWaitNumber(messageId);
				// The node has created a new message and decreased the spray and wait number in it records. 
				// the node can not know if the message is received or not but the spray and wait number will be decreased anyway.
				return sendMessage(a, sent, time);
			}
			System.out.println("PROBLEM in RoutingNode.java. message not sendable from buffer");
		}
		return false;
	}


	public boolean sendMessage(Air air, Message m, String time)
	{
		int receiverNodeId = m.getReceiver();
		if (air == null) {
			Lib.p("air null problem in Routingnode.java");
			System.exit(-1);
		}

		Reporter.addPacketSent(getId(), receiverNodeId, time, m.isProtocolMessage());

		if (!air.addMessage(m)) {
			Reporter.addPacketDropped(getId(), receiverNodeId, time);
			return false;
		}
		return true;

	}

	public boolean sendEncounterHistory(Air air, int receiverId, String time) {
		int mesid = -2;

		String mes = Lib.toString(encounterHistoryWithNodes);
		Message message = new Message(getId(), receiverId, mes, mesid, time);
		return sendMessage(air, message, time);
	}

	public void receiveEncounterHistory(Air a, String time) {
		Message m = receiveMessage(a, time);
		if (m != null)
		{

			if (m.getId() == -2) {
				ArrayList<Encounter> encountersReceived = (ArrayList<Encounter>)Lib.fromString(m.getMessageText());
				for (int i = 0; i < encountersReceived.size(); i++) {
					int rec = ((Encounter)encountersReceived.get(i)).getReceiverId();
					if (rec < 0)
					{
						rec = ((Encounter)encountersReceived.get(i)).getSenderId();
						if (rec > 0) {
							Encounter e = new Encounter(getId(), rec, ((Encounter)encountersReceived.get(i)).getPosition(), Long.parseLong(time));
							encounterHistory.add(e);
							encounterHistoryWithNodes.add(e);
							uniqueEncsWithNodes.put(Integer.valueOf(rec), e);
						}
					} else {
						Encounter e = new Encounter(getId(), rec, ((Encounter)encountersReceived.get(i)).getPosition(), Long.parseLong(time));
						encounterHistory.add(e);
						encounterHistoryWithNodes.add(e);
						uniqueEncsWithNodes.put(Integer.valueOf(rec), e);
					}
				}
			}
			else {
				Lib.p("Message is not encounters");
				Lib.p("Message is " + m.toString());
			}
		}
	}


	public boolean addtoBuffer(Message m, String time)
	{
		if (m.isProtocolMessage())
		{

			System.out.println("PROBLEM in RoutingNode.java: Trying to add neg id message to buffer");
			return false;
		}
		
		boolean b = addMessageToBuffer(m, time);

		if (b) {
			Reporter.addPacketAddedToBuffer(m.getSender(), m.getReceiver(), time);
			
			return true;
		}
		
		return false;
	}

	public Message receiveMessage(Air air, String time) {
		Message message = air.receiveMessage(getId(), time);
		if (message == null)
		{
			return null;
		}
		receiveRealMessage(message, time);
		return message;
	}


	private void receiveRealMessage(Message message, String time)
	{
		if (!message.isProtocolMessage())
		{

			Reporter.addPacketReceived(message.getSender(), message.getReceiver(), time, false);

			boolean b = addtoBuffer(message, time);

			if (!b)
			{

				System.out.println("Can not add to buffer PROBLEM in RoutingNode.java");
			}

		}
		else
		{
			Reporter.addPacketReceived(message.getSender(), message.getReceiver(), time, true);
		}
	}

	public String getMessageVector(String time)
	{
		if (isBufferEmpty()) {
			return "all";
		}
		ArrayList<Integer> v = new ArrayList<Integer>();
		Collection<Message> messageCollection=messageBuffer.values();
		for (Message mymessage:messageCollection) {
			int el = mymessage.getId();
			Integer intel = Integer.valueOf(el);
			if ((!v.contains(intel)) && (mymessage.isSendable(time))) {
				v.add(intel);
			}
		}
		messageCollection=null;

		return getStringFromMessageVector(v);
	}

	public String getCreatedMessageVector(String time)
	{
		if (isBufferEmpty()) {
			return "all";
		}
		ArrayList<Integer> v = new ArrayList<Integer>();
		
		Collection<Message> messageCollection=messageBuffer.values();
		for (Message mymessage:messageCollection) {
			int myMessageId = mymessage.getId();
			if (mymessage.getPrevPacketId() == -1)
			{
				Integer intel = Integer.valueOf(myMessageId);
				if ((!v.contains(intel)) && (mymessage.isSendable(time))) {
					v.add(intel);
				}
			}
		}
		messageCollection=null;

		return getStringFromMessageVector(v);
	}

	/*
	 * Only one contact can be done with the same receiverId
	 */
	public void addContact(int idcon, Position p, long time)
	{
		Encounter e = new Encounter(getId(), idcon, p, time);
		if(contacts.containsKey(idcon)) {
			Lib.p("This should not happen. They are already in contact");
			Exception myEx= new Exception();
			myEx.printStackTrace();
		}else {
			contacts.put(idcon, e);
		}		
	}

	public void removeContact(int idcon) {
		if (!contacts.isEmpty()) {
			contacts.remove(idcon);
		}else {
			Lib.p("This should not happen. the contacts are empty but we are trying to remove it.");
			Exception myEx= new Exception();
			myEx.printStackTrace();
		}
	}


	public boolean isInContactWith(int idcon){
		if(!contacts.isEmpty() && contacts.containsKey(idcon)) {
			return true;
		}
		return false;
	}

	public int getNeighborCount() {
		return contacts.size();
	}

	public void clearContacts() {
		contacts.clear();
	}

	public Encounter lastEncounterWith(int nodeId)
	{
		ListIterator<Encounter> listIterator = encounterHistory.listIterator(encounterHistory.size());
		int i=encounterHistory.size()-1;
		Encounter e=null;
		//traversing reversely so that we can find it easier
		//we have 100 elements in this list. It is possible that we may not find it...
		while (listIterator.hasPrevious()) {
			e = (Encounter)listIterator.previous();

			if ((e.getReceiverId() == nodeId) && (!e.isFinished()))
			{
				Lib.p("RoutingNode class position is: " + i);
				return e;
			}
		}
		return e;
	}

	public int encounterTimesWith(int nodeId) {
		int num = 0;
		Encounter e = null;
		Iterator<Encounter> iterator = encounterHistory.iterator();
		while (iterator.hasNext()) {
			e = (Encounter)iterator.next();
			if (e.getReceiverId() == nodeId)
			{
				num++;
			}
		}
		return num;
	}

	public Encounter finishEncounter(int nodeId, long time) {
		Encounter e = null;
		Iterator<Encounter> iterator = encounterHistory.iterator();
		while (iterator.hasNext()) {
			e = (Encounter)iterator.next();
			//There should be only one encounter with that node
			if (  (e.getReceiverId() == nodeId)  &&  !(e.isFinished())   )
			{
				e.setFinishingTime(time);
				break;
			}
		}
		Encounter e1 = null;
		//if the encounter wasnt with a node no need to check encounterHistoryWithNodes and uniqueEncsWithNodes
		if (nodeId > 0) {
			//encounter with a node
			//The routing node that calls this. The caller might be uav or node
			
			iterator = encounterHistoryWithNodes.iterator();
			while (iterator.hasNext()) {
				e1 = (Encounter)iterator.next();
				if (  (e1.getReceiverId() == nodeId)   &&   !(e1.isFinished())  ) {
					e1.setFinishingTime(time);
					Integer recIdhere=Integer.valueOf(e1.getReceiverId());
					if ( uniqueEncsWithNodes.containsKey(recIdhere) && !uniqueEncsWithNodes.get(recIdhere).isFinished() ) {
						e1.setFinishingTime(time);
						uniqueEncsWithNodes.put(recIdhere, new Encounter(e1));
					}
					break;
				}
			}//end of while
			return e1;
		}
		
		return e;
		//This may return null as sometimes encounters might be cleared before real encounters finish.
	}

	public void addEncounter(int nodeId, Position p, long time) {
		Encounter e = new Encounter(getId(), nodeId, p, time);
		encounterHistory.add(e);
		if (nodeId > 0) {
			encounterHistoryWithNodes.add(e);
			uniqueEncsWithNodes.put(Integer.valueOf(nodeId), e);
		}
	}

	public void clearEncountersWithLimit(int encounterTimeLimit, long currentTime)
	{
		Encounter e=null;
		//the encounterHistory, encounterHistoryWithNodes, uniqueEncsWithNodes lists are linked list
		//the new elements are added at the end. the end contains the newest ones
		//the head contains the oldest ones.
		//the encounters are sorted already. so if I find an unexpired element, all the others are also unexpired
		// 
		
		for (Iterator<Encounter> listIterator = encounterHistory.iterator(); listIterator.hasNext();) {
		    e = listIterator.next();
		    //if ((!e.isFinished()) || (currentTime - e.getFinishingTime() > encounterTimeLimit)) {
		    if (currentTime - e.getFinishingTime() > encounterTimeLimit) {
		    	if( e.isFinished() ) {
		    		listIterator.remove();
		    	}		    	
			}else {
				break;
			}
		}
		
		e=null;
		for (Iterator<Encounter> listIterator = encounterHistoryWithNodes.iterator(); listIterator.hasNext();) {
		    e = listIterator.next();
		    //if ((!e.isFinished()) || (currentTime - e.getFinishingTime() > encounterTimeLimit)) {
		    if (currentTime - e.getFinishingTime() > encounterTimeLimit) {
		    	if( e.isFinished() ) {
		    		listIterator.remove();
		    	}		    	
			}else {
				break;
			}
		}
		e=null;
		Iterator<Map.Entry<Integer, Encounter>> it = uniqueEncsWithNodes.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<Integer, Encounter> emap = (Map.Entry<Integer, Encounter>)it.next();
			Encounter currentEnc = (Encounter)emap.getValue();
			//if ((!currentEnc.isFinished()) || (currentTime - currentEnc.getFinishingTime() > encounterTimeLimit)) {
			if ((currentEnc.isFinished()) && (currentTime - currentEnc.getFinishingTime() > encounterTimeLimit)) {
				it.remove();
			}
		}
		it=null;
	}

	public void clearAllEncounters() {
		encounterHistory.clear();
		encounterHistoryWithNodes.clear();
		uniqueEncsWithNodes.clear();
	}

	public LimitedQueue<Encounter> getEncounterHistory() {
		return encounterHistory;
	}

	public LimitedQueue<Encounter> getEncounterHistoryWithNodes() {
		return encounterHistoryWithNodes;
	}

	public int getEncounterCount() {
		return encounterHistory.size();
	}

	public int getEncounterCountWithNodes() {
		return encounterHistoryWithNodes.size();
	}

	public void printHist() {
		Iterator<Encounter> iterator = encounterHistoryWithNodes.iterator();
		while (iterator.hasNext()) {
			System.out.println(((Encounter)iterator.next()).toString());
		}
		iterator=null;
	}

	public ArrayList<Encounter> uniqueEncounters()
	{
		ArrayList<Encounter> allencs = new ArrayList<Encounter>(uniqueEncsWithNodes.values());
		/*
		ArrayList<Encounter> allencs = new ArrayList<Encounter>();
		Iterator<Map.Entry<Integer, Encounter>> it = uniqueEncsWithNodes.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<Integer, Encounter> e = (Map.Entry<Integer, Encounter>)it.next();
			Encounter currentEnc = (Encounter)e.getValue();
			allencs.add(currentEnc);
		}
		it=null;
		*/
		return allencs;
	}
	
	//This is not used
	private void eavesdrop(Air air, String time)
	{
		ArrayList<Message> allmes = null;//air.receiveEnvironmentMessages(time);
		if (allmes != null) { 
			allmes.isEmpty();
		}


		for (int i = 0; i < allmes.size(); i++) {
			Message m = (Message)allmes.get(i);

			if ((m.getReceiver() != getId()) && (m.getSender() != getId())) {
				Reporter.addPacketReceived(m.getSender(), m.getReceiver(), time, m.isProtocolMessage());

				if (m.getId() > 0)
				{
					boolean b = addtoBuffer(m, time);

					if (!b) {
						System.out.println("Can not add to buffer PROBLEM in RoutingNode.java");
					}
				}
				else
				{
					System.out.println("Problem in RoutingNode.java at method eavesdrop");
				}
			}
		}
	}

	public String toString()
	{
		return "Node id: " + id + " message buffer size " + messageBuffer.size();
	}
}