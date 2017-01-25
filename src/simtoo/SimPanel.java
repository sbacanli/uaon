package simtoo;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.*;
import javax.swing.*;

import java.util.*;
import javax.swing.Timer;

import routing.*;
import simtoo.Lib;

public class SimPanel extends JPanel implements MouseListener{

	Timer timer;
	
	ArrayList<Node> nodes;
	ArrayList<RoutingNode>  routingNodes;
	Uav uav;
	RoutingNode uavRoutingNode;
	
	
	int numberOfNodes;
	private final int UPDATE_RATE = 5;
	int time;
	
	
	Random r;
	
	Datas data;
	int numrecord=1000;
	int height,width;
	boolean israndom;
	double COMMDIST;
	int numberOfRoutesCompleted;
	
	public SimPanel(boolean israndom, int numberOfNodes,final double COMMDIST){
		super();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		height= screenSize.height-10;
		width = screenSize.width-10;
		
		this.israndom=israndom;
		this.COMMDIST=COMMDIST;
		time=0;
		numberOfRoutesCompleted=0;
		
		if(!israndom){
			data=new Datas(numrecord,height,width);
			Datas.calculateMaxes("NewYork/maxes.txt");
		}
			
		
		setPreferredSize(new Dimension(width,height));
		setBackground(Color.LIGHT_GRAY);
		
		
		addMouseListener(this);

		r=new Random();
		
		
		this.numberOfNodes=numberOfNodes;
		nodes=new ArrayList<Node>(numberOfNodes);
		routingNodes = new ArrayList<RoutingNode>(numberOfNodes);
		
		for(int i=0;i<numberOfNodes;i++){
			nodes.add(new Node(i+1,height,width));
			routingNodes.add(new RoutingNode(i+1));
			if(!israndom){
				nodes.get(i).setPoints(data.readRealDataForNode("NewYork\\NewYork_30sec_0"+(i+1)+".txt"));
			}else{
				nodes.get(i).fillRandomPositions(35);
			}
		}
		
		//create uav with speed 900 and id=1
		uav=new Uav(1,2900,width/2,height/2,width,height);
		uav.fillPath(5);
		
		uavRoutingNode=new RoutingNode(-1);
		
		
		TimerListener tm=new TimerListener(this);
		Timer timer = new Timer(1000/UPDATE_RATE, tm);
        timer.start();
        //ArrayList<Position> pts=uav.getPoints();
        /*
        for(int i=0;i<pts.size();i++){
        	Lib.p(pts.get(i).getScreenX()+" "+pts.get(i).getScreenY());
        }
        */
     
	}
		
	private void drawImage(String img1,Graphics2D g2d,double x,double y){
		Image img11 = Toolkit.getDefaultToolkit().getImage(img1);
		
        /* draw ball image to the memory image with transformed x/y double values */
        AffineTransform t = new AffineTransform();
        t.translate(x, y); // x/y set here, ball.x/y = double, ie: 10.33
        t.scale(0.1, 0.1); // scale = 1 
        g2d.drawImage(img11,t, null);
      
	}
	
	
	private void drawFigures(Graphics2D g2){
		int nodesize=15;
		
		//Drawing the nodes
        for(int i=0;i<nodes.size();i++){
        	double x=0;
        	double y=0;
        	x=nodes.get(i).getScreenPosition().getX();
        	y=nodes.get(i).getScreenPosition().getY();		
	        	
        	
        	Shape node = new Ellipse2D.Double(x, y, nodesize, nodesize); 
            g2.draw (node);
        }
        
        //draw the UAV
        double xuav=uav.getScreenPosition().getX();
        double yuav=uav.getScreenPosition().getY();
        
        /*
        if(xuav==width/2 && yuav==height/2){
    		System.out.println("Time is this one "+time);
    		System.out.println("length is "+uav.positionsLength());
        	
    		//uav.clearPositions();
    		//uav.fillPath(500);
    	}
        */
        //*
        	if(uav.isRouteFinished()){
        		uav.clearPositions();        		
        		uav.fillPath(100);
        		ArrayList<Position> pts=uav.getPoints();
                /*
                for(int i=0;i<pts.size();i++){
                	Lib.p(pts.get(i).getScreenX()+" "+pts.get(i).getScreenY());
                }
                //*/
                uav.routeFinished=false;
        		numberOfRoutesCompleted++;
        	}
        	
        //*/
        
        drawImage("drone.png", g2,xuav, yuav);
        
	}
	
	public int getNumberOfRoutesCompleted(){
		return numberOfRoutesCompleted;
	}
	
	public void simulationEnded(){
		Computer.run(nodes, routingNodes, uav, uavRoutingNode);
	}
	
	public void checkNodesDistances(){
		double xuav=uav.getRealPosition().getX();
        double yuav=uav.getRealPosition().getY();
        
		   for(int i=0;i<nodes.size();i++){
			    RoutingNode r1=routingNodes.get(i);
	        	double x=nodes.get(i).getRealPosition().getX();
	        	double y=nodes.get(i).getRealPosition().getY();
	        	for(int j=0;j<i;j++){
	        		
					RoutingNode r2=routingNodes.get(j);
	        		if(i != j){
						double otherx=nodes.get(j).getRealPosition().getX();
						double othery=nodes.get(j).getRealPosition().getY();
						if(Lib.distance(x, y, otherx, othery) <= COMMDIST){
							
							//if they are not in contact let us make them in contact
							if(!r2.isInContactWith(routingNodes.get(i).getId())){
								r2.addContact(r1.getId(), time); 
								r1.addContact(r2.getId(), time);								
								r2.addEncounter(r1.getId(), time); 
								r1.addEncounter(r2.getId(), time);
								
								
								//first touch happened
								Simulator.nodeRoute(r1,r2,time+"");
							}//else it means they are still in contact from last time
							//this  is a continueing contact
							
							
							
							Lib.p("nodes encountered");
							
						}else{
							if(r2.isInContactWith(r1.getId())){
								r2.removeContact(r1.getId());
								r1.removeContact(r2.getId());
								r1.finishEncounter(r2.getId(), time);
								r2.finishEncounter(r1.getId(), time);
							}
							//if they are far and not in contact, no need to do anything.
						
						}
						
		        	}
	        	}//end of node comparison with each other
	        	
	        	if(Lib.distance(xuav, yuav, x, y) <= COMMDIST){
	        		
	        	//uAV nin routing node kars�l�g� olmal�
	        		
	        		if(!uavRoutingNode.isInContactWith(routingNodes.get(i).getId())){
	        		
	        			uav.encounterWithNode(time,nodes.get(i).getId());
	      			
	      				r1.addContact(uavRoutingNode.getId(), time);
	      				r1.addEncounter(uavRoutingNode.getId(), time);
	      			
	      				uavRoutingNode.addContact(r1.getId(), time);
	      				uavRoutingNode.addEncounter(r1.getId(), time);
	      			
	      				Simulator.uavRoute(uavRoutingNode,r1,time+"");
	      				Lib.p("Uav encountered");
	        		}
	        		
	      		}else{
	      			if(r1.isInContactWith(uavRoutingNode.getId())){
	      				r1.removeContact(uavRoutingNode.getId());
	      				r1.finishEncounter(uavRoutingNode.getId(), time);
	      				uavRoutingNode.finishEncounter(r1.getId(), time);
	      				uavRoutingNode.removeContact(r1.getId());
	      			}
	      			
	      		}
	        }
	}
	
	private void doDrawing(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        drawFigures(g2);

        checkNodesDistances();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
        time++;
    }
    
    
    public void mousePressed(MouseEvent e) {
      
    }

    public void mouseReleased(MouseEvent e) {
       Lib.p(e.getX()+" coordinates   "+e.getY());
       System.exit(-1);
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    	
    }

    
    public class TimerListener implements ActionListener{
    	private SimPanel parent;
    	int routeLimit;
    	
    	public TimerListener(SimPanel parent){
    		this.parent=parent;
    		routeLimit=5;
    	}
    	
        public void actionPerformed(ActionEvent e) {
            if ( parent.getNumberOfRoutesCompleted() > routeLimit) {
                ((Timer)e.getSource()).stop();
                Lib.p("Simulation stopped as number of rotations reached to "+routeLimit);
                parent.simulationEnded();
            }
          
            parent.repaint();
        }        
    }//end of inner class TimerListener
    
}
