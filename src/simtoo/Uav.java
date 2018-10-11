package simtoo;
import routing.*;
import DBSCAN.*;

import java.util.ArrayList;

import Shapes.*;
import grider.Grider;
import random.*;

public class Uav extends Positionable{

	private double initialX,initialY;
	private int Radius;
	private int xnum,ynum;
	private RoutingNode rn;
	private double distance;
	private double altitude;
	private ArrayList<Encounter> prevEnc;
	private ArrayList<Integer> uniqueReceiversPrev;
	private boolean randomGrid;
	private double initialParamSpiral;
	private Shape s;
	private Grider g;
	private int encounterTimeLimit;
	private ArrayList<PointP> oldpoints;
	private int numberOfRoutesCompleted;
	
	
	Uav(int uid,Shape sg,double speedreal,int altitudegiven,
			Datas givendata,RoutingNode rn,boolean rg,int encounterTimeLimit){
		super(uid,givendata);
		setRealSpeed(speedreal);
		setScreenSpeed(givendata.RealToVirtualDistance(speedreal));
		this.rn=rn;
		s=sg;
		PointP initialPoint=s.initialPoint();
		initialX=initialPoint.getX();
		initialY=initialPoint.getY();
		
		//adding initial screen position
		Position posGen=getData().getPositionWithScreen(initialX, initialY);
		setCurrentPosition(posGen);
		posGen.setTime(getData().getMinTime());
		ArrayList<Position> p1=new ArrayList<Position>();
		p1.add(posGen);
		addPathsWithPositions(p1,getData(),LocationType.SCREEN);
		
		
		setPreviousPosition(null);
		
		//after the first position the positions will be consumed and reroute will be run
		
		
		
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
		numberOfRoutesCompleted=0;
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
	

	
	/**given sceen positions, path will be generated according to the shape
	 * 
	 * @param xpos xposition for screen
	 * @param ypos ypositions for screen
	 */
	public void fillPath(double xpos,double ypos){
		//initial X and Y coordinate on the screen
		//writePositions();
		if(positionsLength()==0) {
			Lib.p("positions are empty interestingly.fillpath");
		}
		ArrayList<PointP> arr=null;
		s.fill(xpos,ypos);
		arr=s.getPoints();
		
		/*
		for(int i=0;i<arr.size();i++){
			System.out.println( (i+1)+"   "+arr.get(i).getX()+" "+ arr.get(i).getY());
		}
		//*/
		
		
		if(arr==null || arr.isEmpty()){
			Lib.p("POSITIONS GOT EMPTY AT UAV FILLPATH");
			System.exit(-1);
		}else{
			// there will be at least one element in the list that has a time data
			// the arr coordinates are generated for screen. They will be converted to real
			addPathsWithPoints(arr,getData(),LocationType.SCREEN);	
		}
		//time++;
		/*
		for(int i=0;i<getPositions().size();i++){
			getPosition(i).setTime(time);
			time++;
		}
		//*/
		//this is important! spent one day to add this line!
		//positions in the shape should be cleared so that the new positions will not be added
		// to the old positions
		s.clearPositions();
	}
	
	/**given sceen positions, path will be generated according to the shape
	 * 
	 * @param xpos xposition for screen
	 * @param ypos ypositions for screen
	 */
	public void fillPathSpiral(double xpos,double ypos){
		//initial X and Y coordinate on the screen
		//writePositions();
		ArrayList<PointP> arr=null;
		//Spiral s1=new Spiral(((Special)s).getSpiralA(), (Special)s.getMaxRadius(), s.getXlim(), s.getYlim());
		s.fill(xpos,ypos);
		arr=s.getPoints();
		
		/*
		for(int i=0;i<arr.size();i++){
			System.out.println( (i+1)+"   "+arr.get(i).getX()+" "+ arr.get(i).getY());
		}
		//*/
		
		
		if(arr==null || arr.isEmpty()){
			Lib.p("POSITIONS GOT EMPTY AT UAV FILLPATHSpiral");
			System.exit(-1);
		}else{
			// there will be at least one element in the list that has a time data
			// the arr coordinates are generated for screen. They will be converted to real
			addPathsWithPoints(arr,getData(),LocationType.SCREEN);	
		}
		//time++;
		/*
		for(int i=0;i<getPositions().size();i++){
			getPosition(i).setTime(time);
			time++;
		}
		//*/
		//this is important! spent one day to add this line!
		//positions in the shape should be cleared so that the new positions will not be added
		// to the old positions
		s.clearPositions();
	}
	
	public void setGriderParams(int xi,int yi){
		xnum=xi;ynum=yi;
		g=new Grider(xnum,ynum,getData().getMinX(),getData().getMaxX(),getData().getMinY(),getData().getMaxY());
	}
	
	public PointP getRandomLocationInGrid(){
		return g.randomLoc();
	}
	
	public PointP getRandomLocation() {
		return getData().getRandomScreenLocation();
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
		if(s instanceof Special ) {
			specialReroute(currentTime);
		}else {
			otherReroute(currentTime);
		}
	}
	
	public void specialReroute(long currentTime) {
		PointP p;
		double newx=0;
		double newy=0;
		//Lib.p(s.getNumberOfTours());
		if(s.getNumberOfTours()%2==0) {
			initialX=newx;
			initialY=newy;
			fillPath(newx,newy);
		}else {
			ArrayList<Encounter> rnc=rn.uniqueEncounters();
			
			if(rnc.size()<2) {
				//Lib.p("Size less than 2");
				PointP tempp=getRandomLocation();
				newx=tempp.getX();
				newy=tempp.getY();
				fillPath(newx,newy);
			}else {
				ArrayList<ArrayList<Encounter>> result = null;
				int minCluster = 5;
				double maxDistance = 1000;
				
				DBSCANClusterer<Encounter> clusterer = null;
				try {
					clusterer = new DBSCANClusterer<Encounter>(rnc, minCluster, maxDistance,new myDistanceMetric());
					result = clusterer.performClustering();
				} catch (DBSCANClusteringException e1) {
					Lib.p(e1);
				}
				/*
				for(Encounter e:rnc) {
					System.out.println(e);
				}*/
				if(result.isEmpty()) {
					//Lib.p("Result is empty");
					PointP tempp=getRandomLocation();
					newx=tempp.getX();
					newy=tempp.getY();
					fillPath(newx,newy);
				}else {
					//Lib.p("Result not empty");
					for(int j=0;j<result.size() && j<3;j++) {
						int max=result.get(0).size();
						int maxpos=0;
						for(int i=1;i<result.size();i++) {
							if(max<result.get(i).size()) {
								max=result.get(i).size();
								maxpos=i;
							}
						}
						
						PointP resultPoint=centerOf(result.get(maxpos));
						newx=resultPoint.getX();
						newy=resultPoint.getY();
						
						//Lib.p("paths filled "+newx+" "+newy);
						fillPath(newx,newy);
						
						result.get(maxpos).clear();
					}					
				}
				
				
			}//end of uniqueencounters size check
			
			s.incNumberOfTours();
			
			rn.clearContacts();
			rn.clearAllEncounters();
		}
		numberOfRoutesCompleted++;
		//s.clearPositions();
		
	}
	
	public PointP centerOf(ArrayList<Encounter> encs) {
		double maxx=encs.get(0).getPosition().getScreenX();
		double minx=encs.get(0).getPosition().getScreenX();
		double maxy=encs.get(0).getPosition().getScreenY();
		double miny=encs.get(0).getPosition().getScreenY();
		for(int i=1;i<encs.size();i++) {
			double screenx=encs.get(i).getPosition().getScreenX();
			double screeny=encs.get(i).getPosition().getScreenY();
			if(screenx>maxx) {
				maxx=screenx;
			}
			if(screeny>maxy) {
				maxy=screeny;
			}
			if(screenx<minx) {
				minx=screenx;
			}
			if(screeny<miny) {
				miny=screeny;
			}
		}
		double midx=(maxx-minx)/2;
		double midy=(maxy-miny)/2;
		return new PointP(midx,midy);
		
	}
	
	public double mydistance(Position p1,Position p2) {
		return Lib.relativeDistance(p1.real.getX(),p1.real.getY(),p2.real.getX(),p2.real.getY());
	}
	
	/**
	 * @param currentTime
	 */
	public void otherReroute(long currentTime) {
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
		
		rn.clearEncountersWithLimit(encounterTimeLimit,currentTime);
		
		//newX and newY are real coordinates
		newx=getData().convertToScreenX(newx);
		newy=getData().convertToScreenY(newy);
		//now they are screen coordinates
		//Lib.p(newx+" "+newy+" for reroute");
			
		initialX=newx;
		initialY=newy;
		fillPath(newx,newy);
		
		numberOfRoutesCompleted++;
		s.clearPositions();
        //setRouteFinished(false);
	}

	public int getNumberOfRoutesCompleted(){
		return numberOfRoutesCompleted;
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
				setPreviousPosition(getCurrentPosition());
				setCurrentPosition(returned);
				calculateDistance();
				
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
				setPreviousPosition(getCurrentPosition());
				setCurrentPosition(returned);
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
