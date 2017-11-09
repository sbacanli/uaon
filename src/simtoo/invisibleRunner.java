package simtoo;

import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import routing.Computer;
import routing.Encounter;
import routing.LibRouting;
import routing.Message;
import routing.Reporter;
import routing.RoutingNode;

public class invisibleRunner extends TimerTask {

Timer timer;
	
	ArrayList<Node> nodes;
	ArrayList<RoutingNode> routingNodes;
	ArrayList<Uav> uavs;
	ArrayList<RoutingNode> uavRoutingNodes;
	


	public int time;
	
	Random r;
	
	Datas mydata;
	double height,width;
	
	double COMMDIST;
	int numberOfRoutesCompleted;
	int numberOfMessagesCreatedByNodes;
	int numberOfMessagesCreatedByUavs;
	int messageLifeInSeconds;
	
	int poissonVar;
	int numberofCreatedMessages;
	int numberOfNodes;
	int routeLimit;
	int numberOfUavs;
	int messageTimes;
	HashMap<Integer,Integer> schedule;
	
	int messageTimesForNodes;
	int messageTimesForUAVs;

	boolean isvisible;
	boolean clearpos;
	
	public invisibleRunner(Simulator simulator,Datas datagiven){
		
		r=new Random();
		mydata=datagiven;
		height= mydata.getHeight();
		width = mydata.getWidth();

		//Extracting Simulator Parameters
		nodes=simulator.getNodes();
		routingNodes=simulator.getRoutingNodes();
		uavs=simulator.getUavs();
		uavRoutingNodes=simulator.getUavRoutingNodes();
		
		//Virtual distance
		COMMDIST=simulator.getCommDist(); 
		messageLifeInSeconds=simulator.getMessageLifeInSeconds();
		
		//Message Scheduling 
		messageTimesForNodes = simulator.getMessageTimesForNodes();
		messageTimesForUAVs = simulator.getMessageTimesForUAVs();
		schedule=new HashMap<Integer,Integer>();
		if(messageTimesForNodes ==0 && messageTimesForUAVs ==0){
			System.out.println("MessageTimes are all 0 \r\nPlease check config file ");
			System.exit(-1);
		}
		if(messageTimesForNodes != 0){
			addScheduleForNodes();
		}
		if(messageTimesForUAVs != 0){
			addScheduleForUAVs();
		}
		
	
		time=0;
		numberOfRoutesCompleted=0;
		numberOfNodes=nodes.size();
		numberOfUavs=uavs.size();
		

		numberOfMessagesCreatedByNodes=0;
		numberOfMessagesCreatedByUavs=0;
		numberofCreatedMessages=0;

        //Information is written
        
        Lib.p(simulator.toString());
        Lib.p(mydata.toString());
	}

	public void addScheduleForNodes(){
		int timems=0;
		while(timems<mydata.maxtime){
			int mesGenTime=LibRouting.getUniform(messageTimesForNodes);
			timems=mesGenTime+timems;
			int idOfNode=LibRouting.getUniform(routingNodes.size());
			schedule.put(new Integer(timems),new Integer(idOfNode));
		}
	}
	
	public void addScheduleForUAVs(){
		int timems=0;
		while(timems<mydata.maxtime){
			int mesGenTime=LibRouting.getUniform(messageTimesForUAVs);
			timems=mesGenTime+timems;
			int idOfNode=LibRouting.getUniform(uavRoutingNodes.size())*-1;
			schedule.put(new Integer(timems),new Integer(idOfNode));
		}
	}

	
	public boolean reachedMaxTime(){
		return mydata.getMaxTime()==time;
	}
	
	public int numberOfMessagesCreated(){
		return numberofCreatedMessages;
	}
	
	public int getNumberOfRoutesCompleted(){
		return numberOfRoutesCompleted;
	}

	public void simulationEnded(){
		Lib.p("Simulation ended at time "+time);
		Lib.p("Number of created messages total "+numberOfMessagesCreated());
		Lib.p("Number of created messages by UAVs "+numberOfMessagesCreatedByUavs);
		Lib.p("Number of created messages by Nodes "+numberOfMessagesCreatedByNodes);
		if(numberOfMessagesCreated() !=0){
			Computer.run(nodes, routingNodes, uavs, uavRoutingNodes,numberOfMessagesCreated());
		}
		Reporter.finish();
	}
	/*TODO:
	 *  MEssages will be added to UAV or nodes at increase time accordingly. DONE
	 *  UAVs should be at random locations first. DONE
	 *  parametrize the number of grids to the config file. DONE
	 *  check the matlab codes and write a batch file for that.
	 *  find a clustering book and algorithm
	 *  parametrize random search or cluster to the config file
	 */

	
	//this is called after drawing everything
	//anything here will be done once for every time.
	public void increaseTime(){
		int messageGeneratorInt=0;
		
		if(schedule.containsKey(time)){
			Integer messageGenerator=schedule.get(time);
			messageGeneratorInt=messageGenerator.intValue();
			//Lib.p("Message Created at "+time+" at node "+messageGeneratorInt);
			
			if(messageGeneratorInt<0){
				//For UAV
				messageGeneratorInt=(messageGeneratorInt*(-1))-1;
				addMessage(uavRoutingNodes.get(messageGeneratorInt),time);
				numberOfMessagesCreatedByUavs++;
			}else{
				//For Nodes
				messageGeneratorInt=messageGeneratorInt-1;
				addMessage(routingNodes.get(messageGeneratorInt),time);
				numberOfMessagesCreatedByNodes++;
			}
				
		}
		
		//routing checks for UAVs	
		for(int i=0;i<uavs.size();i++){
			Uav uav=uavs.get(i);
			if(uav.isRouteFinished()){
	    		uav.reRoute(time);
	    		numberOfRoutesCompleted++;
	    	}
		}
		checkAllDistances();
		//Lib.p("The time is "+time);
		time++;
	}
	
	public void addMessage(RoutingNode n,int timegiven){
		//48 hours Expiration time
		int messageID=numberofCreatedMessages+1;
		String expiration=(timegiven+messageLifeInSeconds)+"";
		int prevPacketId=-1;
		int hopcount=0;
		int senderId=0;
		int tts=-1;//tts is disabled
		//as the message is generated by the node the sender will be -1
		//receiver will be the node itself.
		int receiverId=n.getId(); //message generating nodeid
		Message message1=new Message(prevPacketId, senderId,receiverId, 
				"This is The Message "+messageID,
				messageID, timegiven+"",tts,expiration,hopcount);
		
		n.addtoBuffer(message1, time+"");
		numberofCreatedMessages++;
	}
	
	public void checkAllDistances(){
		checkNodesDistances();
		checkUavDistances();
		checkUavNodeDistances();
	}
	
	
	//This function should bew rewirttten
	public void checkNodesDistances(){
		for(int i=0;i<routingNodes.size();i++){
			RoutingNode rn1=routingNodes.get(i);
			Node n1=nodes.get(i);
			
        	Position encountern1 =n1.getCurrentPosition();
        	double x1=encountern1.getScreenX();
        	double y1=encountern1.getScreenY();
        	
        	for(int j=0;j<i;j++){
				RoutingNode rn2=routingNodes.get(j);
				Node n2=nodes.get(j);
				
	        	Position encountern2 =n2.getCurrentPosition();
	        	double x2=encountern2.getScreenX();
	        	double y2=encountern2.getScreenY();
	        	
	        	//Distance comparison should be done on virtual distances
	        	//COMMDIST is virtual
	        	if(Lib.screenDistance(x1, y1, x2, y2) <= COMMDIST){
					
					//if they are not in contact let us make them in contact
					if(!rn2.isInContactWith(rn1.getId())){
						addContactsEncounters(rn2,rn1,encountern2,encountern1,time);
						
						//first touch happened
						Simulator.nodeRoute(rn1,rn2,time+"");
					}//else it means they are still in contact from last time
					//this  is a continueing contact
					//Lib.p("nodes encountered");
					
				}else{
					//if the distance is far and they were in contact before
					//it means the contact ended.
					if(rn2.isInContactWith(rn1.getId())){
						rn2.removeContact(rn1.getId());
						rn1.removeContact(rn2.getId());
						Encounter e1=rn1.finishEncounter(rn2.getId(), time);
						Encounter e2=rn2.finishEncounter(rn1.getId(), time);
						
						if(e1==null || e2==null)
							Lib.p("Encounters are null in SimPanel");
						
						Reporter.writeToFile("encounterNodes.txt", e1.toString());
					}
					//if they are far and not in contact, no need to do anything.
				
				}//end of else
				
			}//end of for loop
		}//end of for lop
	}
	
	public void checkUavDistances(){
		for(int i=0;i<uavs.size();i++){
			Uav u1=uavs.get(i);
			RoutingNode ru1=uavRoutingNodes.get(i);
			
			Position encounteru1 =u1.getCurrentPosition();
        	double x1=encounteru1.getScreenX();
        	double y1=encounteru1.getScreenY();
        	
			for(int j=0;j<i;j++){
				Uav u2=uavs.get(j);
				RoutingNode ru2=uavRoutingNodes.get(j);
				
				Position encounteru2 =u2.getCurrentPosition();
	        	double x2=encounteru2.getScreenX();
	        	double y2=encounteru2.getScreenY();
	        	
	        	//Lib.p(Lib.relativeDistance(x1, y1, x2, y2)+"  UAVS  "+COMMDIST);
	        	if(Lib.screenDistance(x1, y1, x2, y2) <= COMMDIST){
					
					//if they are not in contact let us make them in contact
					if(!ru2.isInContactWith(ru1.getId())){
						addContactsEncounters(ru2,ru1,encounteru2,encounteru1,time);
						
						//first touch happened
						Simulator.uavRoute(ru1,ru2,time+"");
					}//else it means they are still in contact from last time
					//this  is a continueing contact
					//Lib.p("nodes encountered");
					
				}else{
					//if the distance is far and they were in contact before
					//it means the contact ended.
					if(ru2.isInContactWith(ru1.getId())){
						ru2.removeContact(ru1.getId());
						ru1.removeContact(ru2.getId());
						Encounter e1=ru1.finishEncounter(ru2.getId(), time);
						Encounter e2=ru2.finishEncounter(ru1.getId(), time);
						
						if(e1==null || e2==null)
							Lib.p("Encounters are null in SimPanel");
						
						Reporter.writeToFile("encountersUav.txt", e1.toString());
					}
					//if they are far and not in contact, no need to do anything.
				
				}//end of else
	        	
	        	
			}
		}
	}
	
	public void checkUavNodeDistances(){
		for(int i=0;i<uavs.size();i++){
			Uav u=uavs.get(i);
        	RoutingNode ruav=uavRoutingNodes.get(i);
        	
        	Position encounterUav =u.getCurrentPosition();
        	if(encounterUav==null){
        		Lib.p("SIMPANEL checkUAvNodeDistance Problem");
        	}
        	double xuav=encounterUav.getScreenX();
        	double yuav=encounterUav.getScreenY();
        	
			for(int j=0;j<nodes.size();j++){
				Node n=nodes.get(j);
	        	RoutingNode rnode=routingNodes.get(j);
	        	
	        	Position encounterNode =n.getCurrentPosition();
	        	double xnode=encounterNode.getScreenX();
	        	double ynode=encounterNode.getScreenY();
	        	//Lib.p(Lib.relativeDistance(xuav, yuav, xnode, ynode)+"  BETWEEN  "+COMMDIST);
	        	if(Lib.screenDistance(xuav, yuav, xnode, ynode) <= COMMDIST){

					//if they are not in contact let us make them in contact
					if(!rnode.isInContactWith(ruav.getId())){
						addContactsEncounters(rnode,ruav,encounterNode,encounterUav,time);
						
						//first touch happened
						Simulator.uavNodeRoute(ruav,rnode,time+"");
					}//else it means they are still in contact from last time
					//this  is a continueing contact
					//Lib.p("nodes encountered");
					
				}else{
					//if the distance is far and they were in contact before
					//it means the contact ended.
					if(rnode.isInContactWith(ruav.getId())){
						rnode.removeContact(ruav.getId());
						ruav.removeContact(rnode.getId());
						Encounter e1=ruav.finishEncounter(rnode.getId(), time);
						Encounter e2=rnode.finishEncounter(ruav.getId(), time);
						
						if(e1==null || e2==null)
							Lib.p("Encounters are null in SimPanel");
						
						Reporter.writeToFile("encountersUavNodes.txt", e1.toString());
					}
					//if they are far and not in contact, no need to do anything.
				
				}//end of else
	        	
			}
		}
	}
	
	private void addContactsEncounters(RoutingNode n1,RoutingNode n2,Position p1,Position p2,int timegiven){
		n2.addContact(n1.getId(),p2, timegiven); 
		n1.addContact(n2.getId(),p1, timegiven);								
		n2.addEncounter(n1.getId(),p2, timegiven); 
		n1.addEncounter(n2.getId(),p1, timegiven);
	}
	
	private void doDrawing() {

		//Drawing the nodes
        for(int i=0;i<nodes.size();i++){
        	double x=0;
        	double y=0;
        	PointP tempp=nodes.get(i).getScreenPositionWithTime(time);
        	if(tempp != null){
        		x=tempp.getX();
        		y=tempp.getY();	
        	}
        }
        
        for(int i=0;i<uavs.size();i++){
        	Uav uav=uavs.get(i);
        	PointP tempp=nodes.get(i).getScreenPositionWithTime(time);
        	if(tempp!=null){	
        		double xuav=tempp.getX();
        		double yuav=tempp.getY();
        	}
        }
    }

    public void everyTime() {
    	Lib.p("time is "+time);
        doDrawing();
        increaseTime();
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(reachedMaxTime()){
			simulationEnded();
			cancel();
		}else{
			everyTime();
		}
	}
	
}
