package simtoo;
import routing.*;


import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.util.*;
import javax.swing.Timer;

public class SimPanel extends JPanel implements MouseListener{
	TimerListener tm;
	Timer timer;
	
	ArrayList<Node> nodes;
	ArrayList<RoutingNode> routingNodes;
	ArrayList<Uav> uavs;
	ArrayList<RoutingNode> uavRoutingNodes;
	

	private final int UPDATE_RATE = 1000;
	long time;
	
	Random r;
	
	Datas mydata;
	double height,width;
	
	double COMMDIST;
	int numberOfMessagesCreatedByNodes;
	int numberOfMessagesCreatedByUavs;
	int messageLifeInSeconds;
	int sprayAndWaitNumber;
	
	int poissonVar;
	int numberofCreatedMessages;
	int numberOfNodes;
	int routeLimit;
	int numberOfUavs;
	int messageTimes;
	HashMap<Long,Integer> schedule;
	
	int messageTimesForNodes;
	int messageTimesForUAVs;
	int messageErrorTimesForNodes;
	int messageErrorTimesForUAVs;
	
	JFrame f;
	boolean isvisible;
	boolean clearpos;
	boolean isGPS;
	double btwdistance;//comm distance between node and uav 
	boolean chargeOn;
	
	//this is related with java drawing.
	//repaaint method is called fist. The thing is at that time we dont have data in the Position[] arrays
	//so I am keeping track of if it is first time
	boolean firsttime;
	
	
	//The current positions of the nodes and uavs if they are on the map at that time
	// we are asking positions 2 places
	//one for drawing the nodes/auvs at the current positions
	//one for comparing the positions
	//if we ask the position it is dequeued after the call so we are keeping track of the positions
	//to be used by the distanceComparing functions
	Position[] nodesPositions;
	Position[] uavsPositions;
	
	int secondsToWait;
	PointP[] chargingLocations;
	
	public SimPanel(Simulator simulator,Datas datagiven,boolean isvisible){
		
		super();
		secondsToWait=30;
		firsttime=true;
		this.isvisible=isvisible;
		r=new Random();
		mydata=datagiven;
		height= mydata.getHeight();
		width = mydata.getWidth();
		
		//Extracting Simulator Parameters
		nodes=simulator.getNodes();
		routingNodes=simulator.getRoutingNodes();
		uavs=simulator.getUavs();
		uavRoutingNodes=simulator.getUavRoutingNodes();
		
		
		nodesPositions=new Position[nodes.size()];
		uavsPositions=new Position[uavs.size()];
		
		chargeOn=simulator.isChargeOn();
		//Virtual distance
		COMMDIST=simulator.getCommDist();
		double altitude=simulator.getConvertedAltitude();
		messageLifeInSeconds=simulator.getMessageLifeInSeconds();
		sprayAndWaitNumber=simulator.getSprayAndWaitNumber();
		btwdistance=Math.sqrt(COMMDIST*COMMDIST+altitude*altitude);
		
		
		//Message Scheduling 
		messageTimesForNodes = simulator.getMessageTimesForNodes();
		messageTimesForUAVs = simulator.getMessageTimesForUAVs();
		messageErrorTimesForNodes = simulator.getMessageErrorTimesForNodes();
		messageErrorTimesForUAVs = simulator.getMessageErrorTimesForUAVs();
	
		
		schedule=new HashMap<Long,Integer>();
		if(messageTimesForNodes ==0 && messageTimesForUAVs ==0){
			System.out.println("MessageTimes are all 0 \r\nPlease check config file ");
			System.exit(-1);
		}
		if(messageTimesForNodes != 0 && !nodes.isEmpty()){
			addScheduleForNodes();
		}
		if(messageTimesForUAVs != 0 && !uavs.isEmpty()){
			addScheduleForUAVs();
		}
		
		setTime(mydata.getMinTime());
		
		numberOfNodes=nodes.size();
		numberOfUavs=uavs.size();
		
		setPreferredSize(new Dimension((int)width,(int)height));
		setBackground(Color.LIGHT_GRAY);
				
		addMouseListener(this);
		setBorder(BorderFactory.createLineBorder(Color.black));
		
		numberOfMessagesCreatedByNodes=0;
		numberOfMessagesCreatedByUavs=0;
		numberofCreatedMessages=0;
		
		//Information is written
        
        Lib.p(simulator.toString());
        Lib.p(mydata.toString());
        Lib.p("Number of nodes: "+nodes.size());
        
        if(!uavs.isEmpty()) {
        	chargingLocations=uavs.get(0).getChargingLocations();
        }
	}
		
	public void startTimer() {
		tm =new TimerListener(this);
		timer = new Timer(1000/UPDATE_RATE, tm);
        timer.start();
	}
	
	public void setTime(long timegiven) {
		time=timegiven;
	}
	
	public void addScheduleForNodes(){
		long timems=mydata.getMinTime();
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
			schedule.put(new Long(timems),new Integer(idOfNode));
		}
	}
	
	public void addScheduleForUAVs(){
		long timems=mydata.getMinTime();
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
			schedule.put(new Long(timems),new Integer(idOfUAVNode));
		}
	}
	
	private void drawImage(String img1,Graphics2D g2d,double x,double y){
		Image img11 = Toolkit.getDefaultToolkit().getImage(img1);
		
        /* draw ball image to the memory image with transformed x/y double values */
        AffineTransform t = new AffineTransform();
        t.translate(x, y); // x/y set here, ball.x/y = double, ie: 10.33
        t.scale(0.1, 0.1); // scale = 1 
        g2d.drawImage(img11,t, null);
      
	}
	
	public static Image iconToImage(Icon icon) {
		if(icon instanceof ImageIcon) {
			return ((ImageIcon)icon).getImage();
		}else{
			int w = icon.getIconWidth();
			int h = icon.getIconHeight();
			GraphicsEnvironment ge = 
			GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gd = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gd.getDefaultConfiguration();
			BufferedImage image = gc.createCompatibleImage(w, h);
			Graphics2D g = image.createGraphics();
			icon.paintIcon(null, g, 0, 0);
			g.dispose();
			return image;
		}
	}
	
	public boolean reachedMaxTime(){
		return mydata.getMaxTime()==time;
	}
	
	private int numberOfMessagesCreated(){
		return numberofCreatedMessages;
	}
	
	private void drawFigures(Graphics2D g2){
		int nodesize=15;
		//Drawing the nodes
        for(int i=0;i<nodesPositions.length;i++){
        	double x=0;
        	double y=0; 
        	
        	Position nodepos= nodesPositions[i];
        	
	        if(nodepos!=null){
	        	x=nodepos.getScreenX();
	        	y=nodepos.getScreenY();		
	        	
	        	Shape node = new Ellipse2D.Double(x, y, nodesize, nodesize); 
	            int rval=(9*i+55)%256;
	        	int gval=(6*i+35)%256;
	        	int bval=(7*i+15)%256;
	        	g2.setPaint(new Color(0, 0, 255)); // a dull blue-green
	            g2.fill(node);
	            g2.draw (node);
        	}else {
        		//Lib.p("Empty for node "+nodes.get(i).getDataFile());
        	}
        }
        
        for(int i=0;i<uavsPositions.length;i++){
        	Uav uav=uavs.get(i);
        	/*
        	for(int b=0;b<uav.getPositions().size();b++){
        		System.out.println(uav.getPosition(b).toString());
        	}
        	*/
        	Position uavpos=uavsPositions[i];
        	
	        if(uavpos!=null){
	        	
	        	double xuav=uavpos.getScreenX();
	        	double yuav=uavpos.getScreenY();
	        	//if(uav.isRouteFinished()){
	        	drawImage("drone.png", g2,xuav, yuav);
        	}else {
        		//*
        		Lib.p("PROBLEM!!!! in simpanel.java UAV can not be null");
        		Lib.p("Length of the positions " +uav.getPositions().size());
            	Lib.p("time is "+time);
        		Lib.p(uav.getPosition(0));
        		//*/
        	}
        }
        if(!(uavs.isEmpty()) && chargeOn) {
        	for(int i=0;i<chargingLocations.length;i++) {
        		drawImage("energy.png",g2,chargingLocations[i].getX(),chargingLocations[i].getY());
        	}
		}
	}
	

	private int getNumberOfRoutesCompletedForUAVs(){
		int sum=0;
		for(int i=0;i<uavs.size();i++) {
			sum=uavs.get(i).getNumberOfRoutesCompleted()+sum;
		}
		return sum;
	}

	
	public void simulationEnded(){
		try{
			Lib.p("Simulation ended at time "+time);
			Lib.p("Number or routes completed "+getNumberOfRoutesCompletedForUAVs());
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
		if(schedule.containsKey(new Long(time))){
			Integer messageGenerator=schedule.get(new Long(time));
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
		//It was all here

		onlyUpdatePositions(time);
		
		for(int i=0;i<uavs.size();i++){
	        //WARNING!!!!!
	        //UAV.getCurrentPositionWithTime should be called only once. After here use only currentPosition()
			Uav uav=uavs.get(i);
			
			int pos=uav.getId()*(-1) - 1;
			uavsPositions[pos]=uav.getCurrentPosition();
			Position uavpos=uavsPositions[pos];
			
			if(uav.isRouteFinished() && uav.positionsLength()!=0) {
				Lib.p("PROBLEM in simpanel");
				uav.writePositions();
			}
						
			if(chargeOn) {
				
				PointP closestChargingLocation=uav.getClosestChargingLocation();
				if(!uav.isWaiting() && !uav.isOnAChargingPos()) {
					uav.goToCharging(closestChargingLocation,time);
					Reporter.increaseLandTimesForUAV();
				}
				
				if(uav.isWaiting() && uav.isOnAChargingPos()) {
					uav.chargeBattery();
					uav.chargeRouted=false;
					//Lib.p("Wait route added at time "+time);
					//Lib.p("************************");
					//Lib.p("reached to Charging pos "+uavpos);
					//Lib.p("************************");
				}else if(uav.isBatteryEmpty() && !uav.isWaiting()){
					Lib.p("UAV Fall down at position "+uav.getCurrentPosition().screenp+" Charging Location: "+closestChargingLocation+" at time "+time+" battery: "+uav.remainingFlightTime());
					Lib.p("Distance "+mydata.VirtualToRealDistance(  (int)(Lib.screenDistance(closestChargingLocation, uavpos.screenp))  ) );
					Lib.p("seconds to reach "+uav.secondsToReach(closestChargingLocation)+" real speed "+uav.getRealSpeed());
					Lib.p("previous position "+uav.getPreviousPosition());
					Lib.p("Is it charge Routed? "+uav.chargeRouted);
					if(uav.isWaiting()) {
						Lib.p("UAV Waiting at location "+uavpos);
					}
					//uav.writePositions();
					System.exit(-1);
				}///end of if battery drained check
				
				/*
				if(uav.isCharging()) {
					Lib.p("UAV charging at time "+time+" with battery "+uav.getBattery());
				}else if(uav.getBattery()<1) {
					Lib.p("Battery low but flying "+uav.getBattery()+" chargeRouted "+uav.chargeRouted+" at time "+time);
				}
				//*/
				uav.batteryConsume();
				if(uav.isBatteryEmpty() && !uav.isOnAChargingPos()) {
					Lib.p(closestChargingLocation+" "+uavpos+" UAV DOWN"+" battery "+uav.getBattery()+" isRouted "+uav.chargeRouted);
					System.exit(-1);
				}
				
			}//end of if chargeOn check
			
			/*
			if(uav.isWaiting()) {
				Lib.p("X UAV Waiting at location "+uavpos+" UAV Battery "+uav.getBattery());
			}
			*/
			
			/*
			int farthestChargingLocPos=uav.getFarthestChargingLocation();
			PointP farthestChargingLocation=locations[closestChargingLocPos];
			*/
			//not much battery left. Need to charge
			//Uav will route to charging station if battery is low.
			//All the checks will be done in that method.
			
		}
		//Nothing should be written after this
		
	}
	
	private void onlyUpdatePositions(long utime) {

		//routing checks for UAVs	
		for(int uavcounter=0;uavcounter<uavs.size();uavcounter++)
		{
			Uav uav=uavs.get(uavcounter);
			
			int pos=uav.getId()*(-1) - 1;
			uavsPositions[pos]=uav.getCurrentPositionWithTime(utime);
			Position uavpos=uavsPositions[pos];
        	
	        if(uavpos==null){
	    		//*
        		Lib.p("time is "+time+" in update positions");
        		Lib.p(uav.getPosition(0));
        		Lib.p("This part can not be null");
        		System.exit(-1);
        		//*/
        	}
		}
		
		for(int i=0;i<nodes.size();i++) {
			Node node=nodes.get(i);
			int pos=node.getId()-1;
			nodesPositions[pos]=node.getCurrentPositionWithTime(utime);
		}
	}
	
	
	public void addMessage(RoutingNode n,long timegiven){
		//48 hours Expiration time
		int messageID=numberofCreatedMessages+1;
		String expiration=(timegiven+messageLifeInSeconds)+"";
		int prevPacketId=-1;
		int hopcount=0;
		int senderId=0;
		int tts=-1;//tts is disabled
		int sprayAndWait=sprayAndWaitNumber;
		//as the message is generated by the node the sender will be -1
		//receiver will be the node itself.
		int receiverId=n.getId(); //message generating nodeid
		Message createdMessage=null;
		
		if(n.getId()<0 && uavs.isEmpty()) {
			//A UAV is creating a message but there are no UAVs
			Lib.p("No UAV exits but UAV is creating a message, problem in SimPanel.java ");
			System.exit(-1);
		}
		if(n.getId()>0 && nodes.isEmpty()) {
			//A Node is creating a message but there are no Nodes
			Lib.p("No node exits but node is creating a message, problem in SimPanel.java ");
			System.exit(-1);
		}
		
		createdMessage=new Message(prevPacketId, senderId,receiverId, 
				"This is The Message "+messageID,
				messageID, timegiven+"",sprayAndWait,tts,expiration,hopcount);
		
		n.addtoBuffer(createdMessage, time+"");
		numberofCreatedMessages++;
	}
	
	public void checkAllDistances(){
		checkNodesDistances();
		if(uavs.size()>1) {
			checkUavDistances();
		}
		if(uavs.size()>0) {
			checkUavNodeDistances();
		}
	}
	
	public void checkNodesDistances(){
		for(int i=0;i<routingNodes.size();i++){
			RoutingNode rn1=routingNodes.get(i);
			Node n1=nodes.get(i);
			
        	Position encountern1 =nodesPositions[i];
        	//if the node is not in the scenes now
        	if(n1.isVisible() && encountern1 != null){
	        	double x1=encountern1.getScreenX();
	        	double y1=encountern1.getScreenY();
	        	
	        	for(int j=0;j<i;j++){
					RoutingNode rn2=routingNodes.get(j);
					Node n2=nodes.get(j);
					
		        	Position encountern2 =nodesPositions[j];
			        if(n2.isVisible() && encountern2!=null){
		        		double x2=encountern2.getScreenX();
			        	double y2=encountern2.getScreenY();
			        	double distancecalc=Lib.screenDistance(x1, y1, x2, y2);
			        			
			        	
			        	//Distance comparison should be done on virtual distances
			        	//COMMDIST is virtual
			        	if(distancecalc <= COMMDIST){
							
							//if they are not in contact let us make them in contact
							if(!rn2.isInContactWith(rn1.getId())){
								addContactsEncounters(rn2,rn1,encountern2,encountern1,time);
								
								//first contact happened
								
								Simulator.nodeRoute(rn1,rn2,n1,n2,time+"");
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
									//Reporter.writeEncounters(e1,e2,"encounterNodes.txt");
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
			//if uav is charging it can not communicate at all
			if(!u1.isCharging()) {
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
				        	
				        	if(distancecalc <= COMMDIST){
								
								//if they are not in contact let us make them in contact
								if(!ru2.isInContactWith(ru1.getId())){
									addContactsEncounters(ru2,ru1,encounteru2,encounteru1,time);
									
									//first touch happened
									Simulator.uavRoute(ru1,ru2,u1,u2,time+"");
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
										//Reporter.writeEncounters(e1,e2,"encounterUavs.txt");
									}
								}
								//if they are far and not in contact, no need to do anything.
							
							}//end of else
			        	
						}//end of if for position null
					}//end of inner for
				
				}//if encounteru1 is not null
			}//if uav is not charging
		}//end of outer for
	}
	
	public void checkUavNodeDistances(){
		for(int i=0;i<uavs.size();i++){
			Uav u=uavs.get(i);
			//if uav is charging it can not communicate at all
			if(!u.isCharging()) {
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
			        	
			        	if(n.isVisible() && encounterNode!=null){
				        	double xnode=encounterNode.getScreenX();
				        	double ynode=encounterNode.getScreenY();
				        	//Lib.p(Lib.relativeDistance(xuav, yuav, xnode, ynode)+"  BETWEEN  "+COMMDIST);
				        	double distancecalc=0;
				        	distancecalc=Lib.screenDistance(xuav, yuav, xnode, ynode);
				        	
				        	if(distancecalc <= btwdistance){
			
								//if they are not in contact let us make them in contact
								if(!rnode.isInContactWith(ruav.getId())){
									addContactsEncounters(rnode,ruav,encounterNode,encounterUav,time);
									
									//first touch happened
									Simulator.uavNodeRoute(ruav,rnode,u,n,time+"");
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
										//Reporter.writeEncounters(e1,e2,"encountersUavNodes.txt");
									}
								}
								//if they are far and not in contact, no need to do anything.
							
							}//end of else
				        	
			        	}//encounter node if else check
					}//end of inner for
				
	        	}//end of if encounter is not null
	        	
			}//end of if uav is charging
		}//end of outer for
	}
	
	private void addContactsEncounters(RoutingNode n1,RoutingNode n2,Position p1,Position p2,long timegiven){
		n2.addContact(n1.getId(),p2, timegiven); 
		n1.addContact(n2.getId(),p1, timegiven);								
		n2.addEncounter(n1.getId(),p2, timegiven); 
		n1.addEncounter(n2.getId(),p1, timegiven);
	}
	
	private void doDrawing(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if(firsttime) {
			updatePositions();
			drawFigures(g2);
			increaseTime();
			firsttime=false;
		}else {			
			drawFigures(g2);
		}
		g2.drawLine(0,(int)mydata.getHeight(), (int)mydata.getWidth(), (int)mydata.getHeight() );
        g2.drawLine((int)mydata.getWidth(), 0,  (int)mydata.getWidth(), (int)mydata.getHeight());
        
    }

    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
        doDrawing(g);
        
    }
    
    private boolean allEmpty(Position[] arr) {
    	for(int i=0;i<arr.length;i++) {
    		if(arr[i]!=null) {
    			return false;
    		}
    	}
    	return true;
    }
    
    
    public void mousePressed(MouseEvent e) {
      
    }

    public void mouseReleased(MouseEvent e) {
       Lib.p(e.getX()+" coordinates   "+e.getY());
       /*
       double mindist=100000000;
       String nodepath="";
       for(int i=0;i<nodesPositions.length;i++) {
    	   double newdist2=Lib.screenDistance(nodesPositions[i].screenp.getX(),nodesPositions[i].screenp.getY(),e.getX(),e.getY());
    	   if(mindist<newdist2) {
    		   mindist=newdist2;
    		   nodepath=nodes.get(i).getDataFile();
    	   }
    	   
       }
       Lib.p(nodepath);
       */
       simulationEnded();
    }
    
    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    	
    }

	public void processTime() {
		while(!reachedMaxTime()) {
			updatePositions();
    		increaseTime();
		}
		simulationEnded();
	}
    
    public class TimerListener implements ActionListener{
    	private SimPanel parent;

    	
    	public TimerListener(SimPanel parent){
    		this.parent=parent;
    	}
    	
        public void actionPerformed(ActionEvent e) {
        	if(parent.reachedMaxTime()){
        		((Timer)e.getSource()).stop();
                Lib.p("Simulation stopped as it reached max time");
                parent.simulationEnded();
        	}else {
        		updatePositions();
        		
        		parent.repaint();
        		increaseTime();
        	}
        	/*else {
        		updater();  
        	}	*/
        }//end of actionPerformed      
    }//end of inner class TimerListener
    
}
