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

public class SimPanel extends JPanel implements MouseListener{

	Timer timer;
	
	ArrayList<Node> nodes;
	ArrayList<RoutingNode>  routingNodes;
	int nodesSize;
	private final int UPDATE_RATE = 5;
	int time;
	
	Uav uav;
	RoutingNode uavRouting;
	Random r;
	
	Datas data;
	int numrecord=1000;
	int height,width;
	boolean israndom;
	Simulator sim;
	
	public SimPanel(Simulator simg){
		super();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		height= screenSize.height-10;
		width = screenSize.width-10;
		
		sim=simg;
		israndom=sim.getRandom();
		
		
		data=new Datas(numrecord,height,width);
		Datas.calculateMaxes("NewYork/maxes.txt");
			
		
		
		setPreferredSize(new Dimension(width,height));
		setBackground(Color.LIGHT_GRAY);
		
		time=0;
		setNumRecord(numrecord);
		addMouseListener(this);

		r=new Random();
		nodesSize=sim.getNodesSize();
		nodes=new ArrayList<Node>(nodesSize);
		routingNodes = new ArrayList<RoutingNode>(nodesSize);
		
		for(int i=0;i<nodesSize;i++){
			nodes.add(new Node(i+1,height,width));
			routingNodes.add(new RoutingNode(i+1));
			if(!israndom){
				nodes.get(i).setPoints(data.readRealDataForNode("NewYork\\NewYork_30sec_0"+(i+1)+".txt"));
			}else{
				nodes.get(i).fillRandomPositions(35);
			}
		}
		
		/*
		data.readData("NewYork/all.txt");
		Lib.p("Min X "+data.getMinX());
		Lib.p("Min Y "+data.getMinY());
		Lib.p("Max X "+data.getMaxX());
		Lib.p("Max Y "+data.getMaxY());
		*/
		
		//create uav with speed 900 and id=1
		uav=new Uav(1,2900,width/2,height/2,width,height);
		uav.fillPath(5);
		
		uavRouting=new RoutingNode(-1);
		
		/*
		uav.addPath(0,100);
		uav.addPath(120,200);
		uav.addPath(250,480);
		uav.addPath(320,400);
		uav.addPath(140.01,100.09);
		*/
		System.out.println(numrecord+" SET EDILEN  DEGER *************************");
		TimerListener tm=new TimerListener(numrecord,this);
		Timer timer = new Timer(1000/UPDATE_RATE, tm);
        timer.start();
        //ArrayList<Position> pts=uav.getPoints();
        /*
        for(int i=0;i<pts.size();i++){
        	Lib.p(pts.get(i).getScreenX()+" "+pts.get(i).getScreenY());
        }
        */
     
	}
	
	public int getNumRecord(){
		return  numrecord;
	}
	
	public void setNumRecord(int rc){
		numrecord=rc;
	}
	
	public ArrayList<Node> getNodes(){
		return nodes;
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
        		System.out.println("Time is this one "+time);
        		uav.clearPositions();        		
        		uav.fillPath(100);
        		ArrayList<Position> pts=uav.getPoints();
                //*
                for(int i=0;i<pts.size();i++){
                	Lib.p(pts.get(i).getScreenX()+" "+pts.get(i).getScreenY());
                }
                //*/
                uav.routeFinished=false;
        		System.out.println("F�LLED");	
        	}
        	
        //*/
        
        drawImage("drone.png", g2,xuav, yuav);
        
        
	}
	
	private void doDrawing(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        drawFigures(g2);

        sim.checkNodesDistances(uav,uavRouting ,nodes,routingNodes,time);
      

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
    	private int currentdataline;
    	private int recordnum;
    	private SimPanel parent;
    	
    	public TimerListener(int recordnum,SimPanel parent){
    		currentdataline=0;
    		this.parent=parent;
    		this.recordnum=recordnum;
    		System.out.println(recordnum+" recornum burda******************");
    	}
    	
    	public int getCurrentDataLine(){
    		return currentdataline;
    	}
    	
    	public void setCurrentDataLine(int g){
    		currentdataline=g;
    	}
    	
        public void actionPerformed(ActionEvent e) {
            currentdataline++;
            if (currentdataline > recordnum) {
                ((Timer)e.getSource()).stop();
                Lib.p("Simulation stopped as record number reached to "+recordnum+" "+currentdataline);
            }
          
            parent.repaint();
        }
        
        
    }
}
