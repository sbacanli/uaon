package simtoo;
import routing.*;

import java.util.ArrayList;

import Shapes.*;
import grider.Grider;
import random.*;

public class Uav extends Positionable{

	double initialX,initialY;
	Datas mydata;
	int Radius;
	int xnum,ynum;
	private RoutingNode rn;
	double distance;
	double altitude;
	ArrayList<Encounter> prevEnc;
	ArrayList<Integer> uniqueReceiversPrev;
	boolean randomGrid;
	double initialParamSpiral;
	Shape s;
	Grider g;
	int encounterTimeLimit;
	ArrayList<PointP> oldpoints;
	Position prevPosition;
	
	Uav(int uid,Shape sg,double speedreal,int altitudegiven,
			double xpos,double ypos,Datas givendata,RoutingNode rn,boolean rg,int encounterTimeLimit){
		super(uid,givendata.isGPS());
		setRealSpeed(speedreal);
		setScreenSpeed(givendata.RealToVirtualDistance(speedreal));
		this.rn=rn;
		s=sg;
		initialX=xpos;
		initialY=ypos;
		mydata=givendata;
		
		//adding initial screen position
		Position posGen=mydata.getPositionWithScreen(xpos, ypos);
		posGen.setTime(mydata.getMinTime());
		ArrayList<Position> p1=new ArrayList<Position>();
		p1.add(posGen);
		addPathsWithPositions(p1,mydata,LocationType.SCREEN);
		
		///
		
		
		xnum=0;
		ynum=0;
		prevEnc=new ArrayList<Encounter>();
		altitude=givendata.RealToVirtualDistance(altitudegiven);
		uniqueReceiversPrev=new ArrayList<Integer>();
		randomGrid=rg;
		if(s instanceof Spiral){
			initialParamSpiral=((Spiral) s).getA();
		}
		this.encounterTimeLimit=encounterTimeLimit;
		oldpoints=new ArrayList<PointP>();
		prevPosition=null;
	}
	
	public void setShape(Shape sg){
		s=sg;
	}
	
	public Shape getShape(){
		return s;
	}
	
	public double getInitialX(){
		return initialX;
	}
	
	public double getInitialY(){
		return initialY;
	}
	
	//flying height of UAV
	public double getAltitude(){
		return altitude;
	}
	

	
	public void fillPath(double xpos,double ypos,long time){
		//initial X and Y coordinate on the screen
		ArrayList<PointP> arr=null;
		s.fill(xpos,ypos);
		arr=s.getPoints();
		
		
		if(arr==null || arr.isEmpty()){
			Lib.p("POSITIONS GOT EMPTY AT UAV FILLPATH");
			System.exit(-1);
		}else{
			// there will be at least one element in the list that has a time data
			// the arr coordinates are generated for screen. They will be converted to real
			addPathsWithPoints(arr,mydata,LocationType.SCREEN);	
		}
		//time++;
		/*
		for(int i=0;i<getPositions().size();i++){
			getPosition(i).setTime(time);
			time++;
		}
		//*/
	}
	
	public void setGriderParams(int xi,int yi){
		xnum=xi;ynum=yi;
		g=new Grider(xnum,ynum,mydata.getMinX(),mydata.getMaxX(),mydata.getMinY(),mydata.getMaxY());
	}
	
	public PointP getRandomLocationInGrid(){
		return g.randomLoc();
	}
	
	public PointP getOldRandomLocation(){
		PointP p1=null;
		
		int sizeold=oldpoints.size();
		int pos=Random.get(sizeold+1);
		if(pos==sizeold){
			p1=getRandomLocationInGrid();
		}else{
			p1=oldpoints.get(pos);
		}		
		return p1;
	}
	
	public void reRoute(long currentTime){
		PointP p;
		//clearPositions();
		
		double newx=0;
		double newy=0;
		
		if(randomGrid){
			//Random routing uses this technique only
			//it means whatever happens follow random next position
			//unlike the else condition this technique is not adapting itself
			
			p=getRandomLocationInGrid();
			newx=p.getX();
			newy=p.getY();
			
			//Lib.p(newx+" "+newy);
			//Lib.p("KNOWN RANDOM");
		}else{
			if(rn.getEncounterCountWithNodes()==0){
				//encountered with no one
				//random real coordinates
				
				p=getOldRandomLocation();
				newx=p.getX();
				newy=p.getY();
				
				//setting initial position will be useful for spiral only.
				//It is not used for rectangular or random. No problem at all
				//setting Radius for Spiral
				s.updateFail(initialParamSpiral);
				
				//Lib.p("UAV is random ");
				
			}else{
				g.process(rn.getEncounterHistoryWithNodes());
				p=g.max();
				//g.pri();
				//real coordinates
				newx=p.getX();
				newy=p.getY();					
				s.updateSuccess();	
				oldpoints.add(p);
			}	
			
			
		}//end of random grid check
	
		rn.clearEncounters(encounterTimeLimit,currentTime);
		
		//newX and newY are real coordinates
		newx=mydata.convertToScreenX(newx);
		newy=mydata.convertToScreenY(newy);
		//now they are screen coordinates
		//Lib.p(newx+" "+newy+" for reroute");
			
		fillPath(newx,newy,currentTime);
		
		
        //setRouteFinished(false);
	}
	
	public void updateIfnotRandom(double newx,double newy){
		//we are not using random directly
		
		//if in the previous route, no encounter took place
		if(rn.getEncounterCountWithNodes()==0){
			//encountered with no one
			//random real coordinates
			
			PointP randompoint=getRandomLocationInGrid();
			newx=randompoint.getX();
			newy=randompoint.getY();
			
			//setting initial position will be useful for spiral only.
			//It is not used for rectangular or random. No problem at all
			//setting Radius for Spiral
			s.updateFail();
			
			//Lib.p("UAV is random ");
			
		}else{
			//if in the previous route, some encounter happened
			
			if(!prevEnc.isEmpty()){
				//UAV encountered with some nodes
				// prev Encounter is also not empty
				
				ArrayList<Integer> commons=SimLib.commonNumbers(rn.uniqueReceiverIdsEncounterNodes(), uniqueReceiversPrev);
				//commons are the common nodes (Id) between previous and current encounters
				//commons can be empty but 
				//rn.uniqueReceiverIdsEncounterNodes() can not be empty we checked at the top
				//uniquereceiversPrev is also not empty as prevEnc is not empty
				
				if(commons.size()==rn.uniqueReceiverIdsEncounterNodes().size()){
					//same nodes are encountered
					PointP randompoint=getRandomLocationInGrid();
					newx=randompoint.getX();
					newy=randompoint.getY();
					s.updateFail();
					
				}else{
					//some different nodes exist
					
					g.process(rn.getEncounterHistoryWithNodes());
					PointP p=g.max();
					//g.pri();
					//real coordinates
					newx=p.getX();
					newy=p.getY();					
					s.updateSuccess();							
				}
				
			
			}else{
				//random point
				//previous encounter is empty. Current one is not empty
				g.process(rn.getEncounterHistoryWithNodes());
				PointP p=g.max();
				newx=p.getX();
				newy=p.getY();					
				s.updateSuccess();						
			}
			
			
			prevEnc.clear();
			prevEnc=new ArrayList<Encounter>(rn.getEncounterHistoryWithNodes());
			uniqueReceiversPrev.clear();
			uniqueReceiversPrev=new ArrayList<Integer>(rn.uniqueReceiverIdsEncounterNodes());
			
		}//end of else if encounterhistorywithnodes
	}
	
	@Override
	public Position getCurrentPositionWithTime(long giventime){
		int lengthBefore=positionsLength();
		boolean dequeued=false;
		//Lib.p("Length Before: "+positionsLength());
		Position returned=null;
		//Lib.p("Called once");
		if(positionsLength()==0) {
			Lib.p("Problem here! in UAV.java");
			return null;
		}
		if(positionsLength()==1) {
			if(getPosition(0).time==giventime)
			{	
				/*
				Lib.p("TIME IS "+giventime);
				Lib.p("before reroute "+positionsLength());
				writePositions();
	        	//*/
				//returned=new Position(getPosition(0));
				//THIS PART IS IMPORTANT!!!
				//we will get the first one on the queue and we will add the remaining positions
				// we dont remove the first one now because the added ones should continue from the first
				//position
				//Lib.p("dequeed for time "+giventime);
				
				reRoute(giventime);
				
				//now we need to remove the first
				returned=dequeuePosition();
				dequeued=true;
				
				/*
				Lib.p("After "+positionsLength());
				writePositions();
	        	//*/
				
			}else {
				Lib.p("Not dequeued - length is 1 time "+giventime);
				writePositions();
				System.exit(-1);
			}
		}else {
			if(getPosition(0).time==giventime)
			{			
				//returned=new Position(getPosition(0));
				returned=dequeuePosition();
				dequeued=true;
				//Lib.p("Dequed here 2");
			}else {
				Lib.p("Not dequeued - length is more");
				writePositions();
				System.exit(-1);
				//PROBLEM HERE AQ
			}
		}
		
		if(returned == null) {
			Lib.p("NULL AT UAV.java NOT POSSIBLE!!!");
			Lib.p("time is: "+giventime);
		}
		return returned;
	}
	
}
