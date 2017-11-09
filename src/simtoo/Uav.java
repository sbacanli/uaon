package simtoo;
import routing.*;

import java.util.ArrayList;
import java.util.HashMap;

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
	HashMap<String,Double> locations;
	ArrayList<PointP> oldpoints;
	
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
		locations=new HashMap<String,Double>();
		locations.put("Random", (double) 1.0);
		oldpoints=new ArrayList<PointP>();
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
	

	
	public void fillPath(double xpos,double ypos){
		//initial X and Y coordinate on the screen
		ArrayList<PointP> arr=null;
		s.fill(xpos,ypos);
		arr=s.getPoints();
		
		
		if(arr==null || arr.isEmpty()){
			Lib.p("POSITIONS GOT EMPTY AT UAV FILLPATH");
		}else{
			for(int i=0;i<arr.size();i++){
				addPathWithScreenCoordinates(arr.get(i).getX(),arr.get(i).getY(),mydata);
				/*
				if(i!=arr.size()-1){
					double ds=Lib.screenDistance(arr.get(i).getX(),arr.get(i).getY(),arr.get(i+1).getX(),arr.get(i+1).getY());
					addDistanceTravelled(mydata.VirtualToRealDistance((int)ds));
				}*/
			}
		}

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
	
	public void reRoute(int currentTime){
		PointP p;
		clearPositions();
		
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
		
			
		fillPath(newx,newy);
		
		
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
		
}
