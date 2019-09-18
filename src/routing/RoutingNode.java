package routing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import simtoo.Lib;
import simtoo.Position;

public class RoutingNode
{
	private int id;
	private ArrayList<Message> messageBuffer;
	private ArrayList<Encounter> contacts;
	private ArrayList<Encounter> encounterHistory;
	private ArrayList<Integer> sents;
	private int limit;
	private boolean isIdle;
	private String lastEnc;
	private String mempolicy = "lrr";

	private boolean gotNewPacket;

	private double probToSend;

	private ArrayList<Encounter> encounterHistoryWithNodes;

	private ArrayList<Encounter> last3WithNodes;

	private HashMap<Integer, Encounter> uniqueEncsWithNodes;
	private ArrayList<Encounter> ExtendedEncounters;

	public RoutingNode(int givenid)
	{
		id = givenid;
		contacts = new ArrayList<Encounter>();
		encounterHistory = new ArrayList<Encounter>();
		encounterHistoryWithNodes = new ArrayList<Encounter>();
		ExtendedEncounters = new ArrayList<Encounter>();
		messageBuffer = new ArrayList<Message>();
		isIdle = false;
		sents = new ArrayList<Integer>();
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

	public ArrayList<Message> getMessageBuffer()
	{
		return messageBuffer;
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
		if (searchBufferMessageId(message.getId()) == -1) {
			if (isBufferFull())
			{
				for (int i = 0; i < messageBuffer.size(); i++) {
					Message t = (Message)messageBuffer.get(i);
					if (t.isExpired(time)) {
						deleteMessageFromBuffer(t.getId());
					}
				}

				//after attempt to delete old messages, if it is still full
				if (isBufferFull())
				{
					Reporter.bufferFull(getId(), time);
					String policy = getMemPolicy();
					if (policy.equals("lrr"))
						messageBuffer.remove(0); else {
							policy.equals("random");
						}
				}
			}
			messageBuffer.add(message);
		}


		return true;
	}


	public boolean deleteMessageFromBuffer(int messageid)
	{
		if (isBufferEmpty()) {
			System.out.println("Message buffer is empty, PROBLEM in Node.java ");
			return false;
		}

		for (int i = 0; i < messageBuffer.size(); i++) {
			Message t = (Message)messageBuffer.get(i);
			if (t.getId() == messageid) {
				messageBuffer.remove(i);

				return true;
			}
		}
		return false;
	}

	public Message getMessageFromBuffer(int messageid)
	{
		if (isBufferEmpty()) {
			return null;
		}

		for (int i = 0; i < messageBuffer.size(); i++) {
			Message t = (Message)messageBuffer.get(i);
			if (t.getId() == messageid) {
				return t;
			}
		}
		return null;
	}

	public int searchBufferMessageId(int messageid) {
		if (isBufferEmpty()) {
			return -1;
		}
		for (int i = 0; i < messageBuffer.size(); i++) {
			Message t = (Message)messageBuffer.get(i);
			if (t.getId() == messageid) {
				return i;
			}
		}
		return -1;
	}

	public String getAllBuffer()
	{
		String sum = "";
		if (messageBuffer.isEmpty()) {
			Lib.p("Message Buffer Empty for Node " + getId());
			return null;
		}

		for (int i = 0; i < messageBuffer.size(); i++) {
			sum = sum + ((Message)messageBuffer.get(i)).toString() + "\r\n***************\r\n";
		}


		return sum;
	}



	public String allids()
	{
		String all = "";
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int y = 0; y < messageBuffer.size(); y++) {
			int idm = ((Message)messageBuffer.get(y)).getId();
			if (!a.contains(new Integer(idm))) {
				a.add(new Integer(idm));
				all = all + "," + idm;
			}
		}
		if (messageBuffer.size() != a.size()) {
			System.out.println("RoutingNode.java allids method problem");
		}
		return all;
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
			if (searchBufferMessageId(mid) == -1) {
				ret.add(new Integer(mid));
			}
		}
		return ret;
	}

	public static String getStringFromMessageVector(ArrayList<Integer> v)
	{
		if (v.isEmpty()) {
			return "all";
		}

		String all = "";
		for (int i = 0; i < v.size(); i++) {
			int el = ((Integer)v.get(i)).intValue();
			all = all + el + ":";
		}

		all = all.substring(0, all.length() - 1);
		return all;
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
					tts, 
					found.getExpiration(), 
					found.getHopCount());

			sent.incHop();
			sendMessage(a, sent, time);
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
						found.getTTS(), 
						found.getExpiration(), 
						found.getHopCount());

				sent.incHop();
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
			System.out.println("air null problem in Routingnode.java");
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
		for (int i = 0; i < messageBuffer.size(); i++) {
			int el = ((Message)messageBuffer.get(i)).getId();
			Integer intel = new Integer(el);
			if ((!v.contains(intel)) && (((Message)messageBuffer.get(i)).isSendable(time))) {
				v.add(intel);
			}
		}

		return getStringFromMessageVector(v);
	}

	public String getCreatedMessageVector(String time)
	{
		if (isBufferEmpty()) {
			return "all";
		}
		ArrayList<Integer> v = new ArrayList<Integer>();
		for (int i = 0; i < messageBuffer.size(); i++) {
			int el = ((Message)messageBuffer.get(i)).getId();
			if (((Message)messageBuffer.get(i)).getPrevPacketId() == -1)
			{
				Integer intel = new Integer(el);
				if ((!v.contains(intel)) && (((Message)messageBuffer.get(i)).isSendable(time))) {
					v.add(intel);
				}
			}
		}


		return getStringFromMessageVector(v);
	}






	public void addContact(int idcon, Position p, long time)
	{
		for (int i = 0; i < contacts.size(); i++) {
			if (((Encounter)contacts.get(i)).getReceiverId() == idcon)
			{




				return;
			}
		}


		Encounter e = new Encounter(getId(), idcon, p, time);
		contacts.add(e);
	}

	public void removeContact(int idcon) {
		if (!contacts.isEmpty()) {
			for (int i = 0; i < contacts.size(); i++) {
				if (((Encounter)contacts.get(i)).getReceiverId() == idcon)
				{
					contacts.remove(i);
					return;
				}
			}
		}
	}















	public boolean isInContactWith(int idcon)
	{
		for (int i = 0; i < contacts.size(); i++) {
			if (((Encounter)contacts.get(i)).getReceiverId() == idcon)
			{
				return true;
			}
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
		Encounter e = null;
		for (int i = encounterHistory.size() - 1; i > -1; i--) {
			e = (Encounter)encounterHistory.get(i);


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
		for (int i = 0; i < encounterHistory.size(); i++) {
			e = (Encounter)encounterHistory.get(i);
			if (e.getReceiverId() == nodeId)
			{
				num++;
			}
		}
		return num;
	}

	public Encounter finishEncounter(int nodeId, long time) {
		Encounter e = null;
		for (int i = 0; i < encounterHistory.size(); i++) {
			e = (Encounter)encounterHistory.get(i);
			if ((e.getReceiverId() == nodeId) && (!e.isFinished()))
			{
				e.setFinishingTime(time);
				if (nodeId < 0) {
					Encounter e1 = null;


					for (int j = 0; j < encounterHistoryWithNodes.size(); j++) {
						e1 = (Encounter)encounterHistoryWithNodes.get(j);
						if ((e1.getReceiverId() == nodeId) && (!e1.isFinished())) {
							e1.setFinishingTime(time);
						}
						if ((uniqueEncsWithNodes.containsKey(Integer.valueOf(e1.getReceiverId()))) && (!((Encounter)uniqueEncsWithNodes.get(Integer.valueOf(e1.getReceiverId()))).isFinished())) {
							e1.setFinishingTime(time);
							uniqueEncsWithNodes.put(Integer.valueOf(e1.getReceiverId()), e1);
						}
					}
				}

				return e;
			}
		}
		return e;
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
		for (int i = encounterHistory.size() - 1; i > 0; i--) {
			Encounter e = (Encounter)encounterHistory.get(i);

			if ((!e.isFinished()) || (currentTime - e.getFinishingTime() > encounterTimeLimit)) {
				encounterHistory.remove(i);
			}
		}

		for (int i = encounterHistoryWithNodes.size() - 1; i >= 0; i--) {
			Encounter e = (Encounter)encounterHistoryWithNodes.get(i);
			if ((!e.isFinished()) || (currentTime - e.getFinishingTime() > encounterTimeLimit)) {
				encounterHistoryWithNodes.remove(i);
			}
		}

		Iterator<Map.Entry<Integer, Encounter>> it = uniqueEncsWithNodes.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<Integer, Encounter> e = (Map.Entry<Integer, Encounter>)it.next();
			Encounter currentEnc = (Encounter)e.getValue();
			if ((!currentEnc.isFinished()) || (currentTime - currentEnc.getFinishingTime() > encounterTimeLimit)) {
				it.remove();
			}
		}
	}

	public void clearAllEncounters() {
		encounterHistory.clear();
		encounterHistoryWithNodes.clear();
		uniqueEncsWithNodes.clear();
	}

	public ArrayList<Encounter> getEncounterHistory() {
		return encounterHistory;
	}

	public ArrayList<Encounter> getEncounterHistoryWithNodes() {
		return encounterHistoryWithNodes;
	}

	public int getEncounterCount() {
		return encounterHistory.size();
	}

	public int getEncounterCountWithNodes() {
		return encounterHistoryWithNodes.size();
	}

	public void printHist() {
		for (int i = 0; i < encounterHistoryWithNodes.size(); i++) {
			System.out.println(((Encounter)encounterHistoryWithNodes.get(i)).toString());
		}
	}











	public ArrayList<Encounter> uniqueEncounters()
	{
		ArrayList<Encounter> allencs = new ArrayList<Encounter>();
		Iterator<Map.Entry<Integer, Encounter>> it = uniqueEncsWithNodes.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<Integer, Encounter> e = (Map.Entry)it.next();
			Encounter currentEnc = (Encounter)e.getValue();
			allencs.add(currentEnc);
		}
		return allencs;
	}












	public void eavesdrop(Air air, String time)
	{
		ArrayList<Message> allmes = air.receiveEnvironmentMessages(time);
		if (allmes != null) { allmes.isEmpty();
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