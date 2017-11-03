package simtoo;
import routing.*;


import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.util.*;
import javax.swing.Timer;




public class SimPanel extends JPanel implements MouseListener{

	Timer timer;
	
	ArrayList<Node> nodes;
	ArrayList<RoutingNode> routingNodes;
	ArrayList<Uav> uavs;
	ArrayList<RoutingNode> uavRoutingNodes;
	

	private final int UPDATE_RATE = 1000000;
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
	
	public SimPanel(Simulator simulator,Datas datagiven,boolean isvisible){
		
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
		
		setPreferredSize(new Dimension((int)width,(int)height));
		setBackground(Color.LIGHT_GRAY);
		
		
		addMouseListener(this);

		
		/*
		ArrayList<Position> arr=nodes.get(2).getPositions();
		Lib.p("uzunluk "+arr.size());
		for(int i=1;i<arr.size();i++){
			double lat1=arr.get(i).getRealX();
			double lon1=arr.get(i).getRealY();
			double lat2=arr.get(i-1).getRealY();
			double lon2=arr.get(i-1).getRealY();
			Lib.p(Lib.realdistance(lat1, lon1, lat2, lon2)+" is the distance");
		}
		//*/
		
		numberOfMessagesCreatedByNodes=0;
		numberOfMessagesCreatedByUavs=0;
		numberofCreatedMessages=0;
		
		TimerListener tm=new TimerListener(this);
		Timer timer = new Timer(1000/UPDATE_RATE, tm);
        timer.start();
        //Information is written
        
        Lib.p(simulator.toString());
        Lib.p(mydata.toString());
        Lib.p("Number of nodes: "+nodes.size());
	}
		
	public void addScheduleForNodes(){
		int timems=0;
		//will add messages in every messageTimesForNodes +- mesGenError
		while(timems<mydata.maxtime){
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
		while(timems<mydata.maxtime){
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
	
	private void drawImage(String img1,Graphics2D g2d,double x,double y){
		
		Image img11 = Toolkit.getDefaultToolkit().getImage(img1);
		
        /* draw ball image to the memory image with transformed x/y double values */
        AffineTransform t = new AffineTransform();
        t.translate(x, y); // x/y set here, ball.x/y = double, ie: 10.33
        t.scale(0.1, 0.1); // scale = 1 
        g2d.drawImage(img11,t, null);
      
	}
	
	public static Image iconToImage(Icon icon) {
		   if (icon instanceof ImageIcon) {
		      return ((ImageIcon)icon).getImage();
		   } 
		   else {
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
	
	public int numberOfMessagesCreated(){
		return numberofCreatedMessages;
	}
	
	private void drawFigures(Graphics2D g2){
		int nodesize=15;
		
		//Drawing the nodes
        for(int i=0;i<nodes.size();i++){
        	double x=0;
        	double y=0;
        	Position nodepos=nodes.get(i).getCurrentPositionWithTime(time);
        	x=nodepos.getScreenX();
        	y=nodepos.getScreenY();		
	        	
        	
        	Shape node = new Ellipse2D.Double(x, y, nodesize, nodesize); 
            int rval=(9*i+55)%256;
        	int gval=(6*i+35)%256;
        	int bval=(7*i+15)%256;
        	g2.setPaint(new Color(rval, gval, bval)); // a dull blue-green
            g2.fill(node);
            g2.draw (node);
        }
        
        for(int i=0;i<uavs.size();i++){
        	Uav uav=uavs.get(i);
        	Position uavpos=uav.getCurrentPositionWithTime(time);
        	double xuav=uavpos.getScreenX();
        	double yuav=uavpos.getScreenY();
        	if(uav.isRouteFinished()){
        		drawImage("drone2.png", g2,xuav, yuav);
        	}else{
        		drawImage("drone.png", g2,xuav, yuav);
        	}
        }
        
	}
	
	public int getNumberOfRoutesCompleted(){
		return numberOfRoutesCompleted;
	}

	public void simulationEnded(){
		Lib.p("Simulation ended at time "+time);
		Lib.p("Number or routes completed "+getNumberOfRoutesCompleted());
		Lib.p("Number of created messages total "+numberOfMessagesCreated());
		Lib.p("Number of created messages by UAVs "+numberOfMessagesCreatedByUavs);
		Lib.p("Number of created messages by Nodes "+numberOfMessagesCreatedByNodes);
		if(numberOfMessagesCreated() !=0){
			Computer.run(nodes, routingNodes, uavs, uavRoutingNodes,numberOfMessagesCreated());
		}
		Reporter.finish();
		System.exit(1);
	}
	/*TODO:
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
			
        	Position encountern1 =n1.getCurrentPositionWithTime(time);
        	//if the node is not in the scenes now
        	if(encountern1 != null){
	        	double x1=encountern1.getScreenX();
	        	double y1=encountern1.getScreenY();
	        	
	        	for(int j=0;j<i;j++){
					RoutingNode rn2=routingNodes.get(j);
					Node n2=nodes.get(j);
					
		        	Position encountern2 =n2.getCurrentPositionWithTime(time);
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
					
				}//end of for loop
        	
			}//end of if check position is null
		}//end of outer for lop
	}
	
	public void checkUavDistances(){
		for(int i=0;i<uavs.size();i++){
			Uav u1=uavs.get(i);
			RoutingNode ru1=uavRoutingNodes.get(i);
			
			Position encounteru1 =u1.getCurrentPositionWithTime(time);
			//if the node is not in the scenes now
			if(encounteru1 != null){
	        	double x1=encounteru1.getScreenX();
	        	double y1=encounteru1.getScreenY();
	        	
				for(int j=0;j<i;j++){
					Uav u2=uavs.get(j);
					RoutingNode ru2=uavRoutingNodes.get(j);
					
					Position encounteru2 =u2.getCurrentPositionWithTime(time);
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
		        	
		        	
				}//end of inner for
			
			}//if encounteru1 is not null
		}//end of outer for
	}
	
	public void checkUavNodeDistances(){
		for(int i=0;i<uavs.size();i++){
			Uav u=uavs.get(i);
        	RoutingNode ruav=uavRoutingNodes.get(i);
        	
        	Position encounterUav =u.getCurrentPositionWithTime(time);
        	if(encounterUav==null){
        		Lib.p(u.getPosition(0).toString());
        		Lib.p("SIMPANEL checkUAvNodeDistance Problem "+u.positionsLength()+"  "+time);
        	}
        	double xuav=encounterUav.getScreenX();
        	double yuav=encounterUav.getScreenY();
        	
			for(int j=0;j<nodes.size();j++){
				Node n=nodes.get(j);
	        	RoutingNode rnode=routingNodes.get(j);
	        	
	        	Position encounterNode =n.getCurrentPositionWithTime(time);
	        	
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
		}//end of outer for
	}
	
	private void addContactsEncounters(RoutingNode n1,RoutingNode n2,Position p1,Position p2,int timegiven){
		n2.addContact(n1.getId(),p2, timegiven); 
		n1.addContact(n2.getId(),p1, timegiven);								
		n2.addEncounter(n1.getId(),p2, timegiven); 
		n1.addEncounter(n2.getId(),p1, timegiven);
	}
	
	private void doDrawing(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        drawFigures(g2);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
        
    }
    
    
    public void mousePressed(MouseEvent e) {
      
    }

    public void mouseReleased(MouseEvent e) {
       Lib.p(e.getX()+" coordinates   "+e.getY());
       //timer.stop();
       simulationEnded();

    }
    
    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    	
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
        	}else if(!parent.isvisible){
        		//Lib.p("Time is "+parent.time);
        			//these are vital. the iterator of the nodes and uavs get changed. we are simulating drawfigures here:)
        			for(int i=0;i<nodes.size();i++){
        				PointP pos=nodes.get(i).getScreenPosition();
        				if(pos!=null){
        					double x=pos.getX();
        					double y=pos.getY();		
        				}
        				
        				
        			}
                
        			for(int i=0;i<uavs.size();i++){
        				PointP pos=nodes.get(i).getScreenPositionWithTime(time);
        				Uav uav=uavs.get(i);
        				if(pos != null){
        					double xuav=pos.getX();
        					double yuav=pos.getY();
        				}
        			}
        			
        			increaseTime();
        	}else{
        		//Drawing the nodes
                for(int i=0;i<nodes.size();i++){
                	double x=0;
                	double y=0;
                	PointP pos=nodes.get(i).getScreenPositionWithTime(time);
                	if(pos !=null){
                		x=pos.getX();
                		y=pos.getY();		
                	}
                }
                
                for(int i=0;i<uavs.size();i++){
                	Uav uav=uavs.get(i);
                	
                	PointP pos=uavs.get(i).getScreenPosition();
                	if(pos != null){
                		double xuav=pos.getX();
                		double yuav=pos.getY();
                	}
                }
                parent.repaint();
        		increaseTime();
        	}	
        }        
    }//end of inner class TimerListener
    
}
