package simtoo;
import routing.*;


import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import java.util.*;
import javax.swing.Timer;




public class invisibleRunner{

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
	int messageErrorTimesForNodes;
	int messageErrorTimesForUAVs;
	
	JFrame f;
	boolean isvisible;
	boolean clearpos;
	boolean isGPS;
	double btwdistance;//comm distance between node and uav 
	
	//The current positions of the nodes and uavs if they are on the map at that time
	// we are asking positions 2 places
	//one for drawing the nodes/auvs at the current positions
	//one for comparing the positions
	//if we ask the position it is dequeued after the call so we are keeping track of the positions
	//to be used by the distanceComparing functions
	Position[] nodesPositions;
	Position[] uavsPositions;
	
	public invisibleRunner(Simulator simulator,Datas datagiven,boolean isvisible){
		
		super();
		this.isvisible=isvisible;
		r=new Random();
		mydata=datagiven;
		height= mydata.getHeight();
		width = mydata.getWidth();
		isGPS=simulator.isGPS();
		
		//Extracting Simulator Parameters
		nodes=simulator.getNodes();
		routingNodes=simulator.getRoutingNodes();
		uavs=simulator.getUavs();
		uavRoutingNodes=simulator.getUavRoutingNodes();
		
		
		nodesPositions=new Position[nodes.size()];
		uavsPositions=new Position[uavs.size()];
		
		//Virtual distance
		COMMDIST=simulator.getCommDist(); 
		messageLifeInSeconds=simulator.getMessageLifeInSeconds();
		btwdistance=Math.sqrt(COMMDIST*COMMDIST-uavs.get(0).getAltitude()*uavs.get(0).getAltitude());
		
		
		//Message Scheduling 
		messageTimesForNodes = simulator.getMessageTimesForNodes();
		messageTimesForUAVs = simulator.getMessageTimesForUAVs();
		messageErrorTimesForNodes = simulator.getMessageErrorTimesForNodes();
		messageErrorTimesForUAVs = simulator.getMessageErrorTimesForUAVs();
	
		
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
        Lib.p("Number of nodes: "+nodes.size());
	}
	
	public void processTime() {
		while(!reachedMaxTime()) {
			updatePositions();
    		increaseTime();
		}
		simulationEnded();
	}
		
	public void addScheduleForNodes(){
		int timems=0;
		//will add messages in every messageTimesForNodes +- mesGenError
		while(timems<mydata.getMaxTime()){
			int neg=LibRouting.getUniform(2);//if plus or minus
			int mesGenError=LibRouting.getUniform(messageErrorTimesForNodes);
			if(neg==1){
				timems+=(messageTimesForNodes+mesGenError);
			}else{
				timems+=(messageTimesForNodes-mesGenError);
			}
			int idOfNode=LibRouting.getUniform(routingNodes.size());
			schedule.put(new Integer(timems),new Integer(idOfNode));
		}
	}
	
	public void addScheduleForUAVs(){
		int timems=0;
		while(timems<mydata.getMaxTime()){
			int neg=LibRouting.getUniform(2);//if plus or minus
			int mesGenError=LibRouting.getUniform(messageErrorTimesForUAVs);
			if(neg==1){
				timems+=(messageTimesForUAVs+mesGenError);
			}else{
				timems+=(messageTimesForUAVs-mesGenError);
			}
			//id should be negative as the UAV ids are negative
			int idOfUAVNode=LibRouting.getUniform(uavRoutingNodes.size())*-1;
			schedule.put(new Integer(timems),new Integer(idOfUAVNode));
		}
	}
	
	public boolean reachedMaxTime(){
		return mydata.getMaxTime()==time;
	}
	
	private int numberOfMessagesCreated(){
		return numberofCreatedMessages;
	}
	
	private int getNumberOfRoutesCompleted(){
		return numberOfRoutesCompleted;
	}

	public void simulationEnded(){
		try{
			Lib.p("Simulation ended at time "+time);
			Lib.p("Number or routes completed "+getNumberOfRoutesCompleted());
			Lib.p("Number of created messages total "+numberOfMessagesCreated());
			Lib.p("Number of created messages by UAVs "+numberOfMessagesCreatedByUavs);
			Lib.p("Number of created messages by Nodes "+numberOfMessagesCreatedByNodes);
			if(numberOfMessagesCreated() !=0){
				Computer.run(nodes, routingNodes, uavs, uavRoutingNodes,numberOfMessagesCreated());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		Reporter.finish();
		System.exit(1);
	}
	
	//this is called after drawing everything
	//anything here will be done once for every time.
	public void increaseTime(){
		
		//adding messages
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
		
		checkAllDistances();
		time++;
	}
	
	private void updatePositions() {
		//routing checks for UAVs	
		for(int i=0;i<uavs.size();i++){
			Uav uav=uavs.get(i);
			if(uav.isRouteFinished()){
	    		uav.reRoute(time);
	    		numberOfRoutesCompleted++;
	    	}
		}
		
		//the datas are read portion by portion in order to handle long files
		for(int i=0;i<nodes.size();i++){
			if(nodes.get(i).positionsLength()==0){
				///TODO:Check this line below
				nodes.get(i).readData(mydata.getMinTime());	
			}
		}
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
	

	public void checkNodesDistances(){
		for(int i=0;i<routingNodes.size();i++){
			RoutingNode rn1=routingNodes.get(i);
			Node n1=nodes.get(i);
			
        	Position encountern1 =nodesPositions[i];
        	//if the node is not in the scenes now
        	if(encountern1 != null){
	        	double x1=encountern1.getScreenX();
	        	double y1=encountern1.getScreenY();
	        	
	        	for(int j=0;j<i;j++){
					RoutingNode rn2=routingNodes.get(j);
					Node n2=nodes.get(j);
					
		        	Position encountern2 =nodesPositions[j];
			        if(encountern2!=null){
		        		double x2=encountern2.getScreenX();
			        	double y2=encountern2.getScreenY();
			        	double distancecalc=Lib.screenDistance(x1, y1, x2, y2);
			        	if(isGPS){
			        		distancecalc=Lib.realdistance(y1,x1,y2,x2);
			        	}
		
			        	
			        	//Distance comparison should be done on virtual distances
			        	//COMMDIST is virtual
			        	if(distancecalc <= COMMDIST){
							
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
								
								//since UAV clears encounters,According to preference it might!, the node may still have that record
								//whereas UAV doesn't or maybe the reverse
								//In that case the one that is not null will be written.
								if(e1==null && e2==null){
									Lib.p("Encounters are null between Nodes in SimPanel CHECK THIS AT SimPanel.java");
								}else{
									Reporter.writeEncounters(e1,e2,"encounterNodes.txt");
								}
								
								
							}
							//if they are far and not in contact, no need to do anything.
						
						}//end of else
					
	        		}//end of if position null
				}//end of for loop
        	
			}//end of if check position is null
		}//end of outer for lop
	}
	
	public void checkUavDistances(){
		for(int i=0;i<uavs.size();i++){
			Uav u1=uavs.get(i);
			RoutingNode ru1=uavRoutingNodes.get(i);
			
			Position encounteru1 =uavsPositions[i];
			//if the node is not in the scenes now
			if(encounteru1 != null){
	        	double x1=encounteru1.getScreenX();
	        	double y1=encounteru1.getScreenY();
	        	
				for(int j=0;j<i;j++){
					Uav u2=uavs.get(j);
					RoutingNode ru2=uavRoutingNodes.get(j);
					
					Position encounteru2 =uavsPositions[j];
					if(encounteru2!=null){
			        	double x2=encounteru2.getScreenX();
			        	double y2=encounteru2.getScreenY();
			        	
			        	//Lib.p(Lib.relativeDistance(x1, y1, x2, y2)+"  UAVS  "+COMMDIST);
			        	double distancecalc=Lib.screenDistance(x1, y1, x2, y2);
			        	if(isGPS){
			        		distancecalc=Lib.realdistance(y1,x1,y2,x2);
			        	}
			        	
			        	if(distancecalc <= COMMDIST){
							
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
								
								if(e1==null && e2==null){
									Lib.p("Encounters are null between UAVs in SimPanel CHECK THIS AT SimPanel.java");
								}else{
									Reporter.writeEncounters(e1,e2,"encounterUavs.txt");
								}
							}
							//if they are far and not in contact, no need to do anything.
						
						}//end of else
		        	
					}//end of if for position null
				}//end of inner for
			
			}//if encounteru1 is not null
		}//end of outer for
	}
	
	public void checkUavNodeDistances(){
		for(int i=0;i<uavs.size();i++){
			Uav u=uavs.get(i);
        	RoutingNode ruav=uavRoutingNodes.get(i);
        	
        	Position encounterUav = uavsPositions[i];
        	if(encounterUav!=null){
        		//Lib.p(u.getPosition(0).toString());
        		//Lib.p("SIMPANEL checkUAvNodeDistance Problem "+u.positionsLength()+"  "+time);
        	
	        	double xuav=encounterUav.getScreenX();
	        	double yuav=encounterUav.getScreenY();
	        	
				for(int j=0;j<nodes.size();j++){
					Node n=nodes.get(j);
		        	RoutingNode rnode=routingNodes.get(j);
		        	
		        	Position encounterNode = nodesPositions[j];
		        	
		        	if(encounterNode!=null){
			        	double xnode=encounterNode.getScreenX();
			        	double ynode=encounterNode.getScreenY();
			        	//Lib.p(Lib.relativeDistance(xuav, yuav, xnode, ynode)+"  BETWEEN  "+COMMDIST);
			        	double distancecalc=0;
			        	if(isGPS){
			        		distancecalc=Lib.realdistance(yuav,xuav,ynode,xnode);
			        	}else{
			        		distancecalc=Lib.screenDistance(xuav, yuav, xnode, ynode);
			        	}
			        	
			        	if(distancecalc <= btwdistance){
		
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
								
								if(e1==null && e2==null){
									Lib.p("Encounters are null between UAV and Nodes in SimPanel CHECK THIS AT SimPanel.java");
								}else{
									Reporter.writeEncounters(e1,e2,"encountersUavNodes.txt");
								}
							}
							//if they are far and not in contact, no need to do anything.
						
						}//end of else
			        	
		        	}//encounter node if else check
				}//end of inner for
			
        	}//end of if
        	
		}//end of outer for
	}
	
	private void addContactsEncounters(RoutingNode n1,RoutingNode n2,Position p1,Position p2,int timegiven){
		n2.addContact(n1.getId(),p2, timegiven); 
		n1.addContact(n2.getId(),p1, timegiven);								
		n2.addEncounter(n1.getId(),p2, timegiven); 
		n1.addEncounter(n2.getId(),p1, timegiven);
	}
    
    
}
