package simtoo;

import Cluster.Cluster;
import Cluster.KMeans;
import DBSCAN.DBSCANClusterer;
import DBSCAN.myDistanceMetric;
import Shapes.*;
import grider.Grider;

import java.util.ArrayList;
import routing.*;

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
	private ClusterTechnique clusterTechnique;
	private int radiusCoefficient;
	private int numberOfClusters;
	private String shapename;
	private double maxDistanceForDBSCAN;
	
	Uav(int uid, Shape sg, double speedreal, int altitudegiven, 
			Datas givendata, RoutingNode rn, String shapeName, int encounterTimeLimit, ClusterParam cparam){
		super(uid,givendata);
		setRealSpeed(speedreal);
		setScreenSpeed(givendata.RealToVirtualDistance(speedreal));
		this.rn=rn;
		s=sg;
		PointP initialPoint=s.initialPoint();
		initialX=initialPoint.getX();
		initialY=initialPoint.getY();
		shapename = shapeName;
		
		//adding initial screen position
		Position posGen=getData().getPositionWithScreen(initialX, initialY);
		posGen.setTime(getData().getMinTime());
		setCurrentPosition(posGen);
		
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
	
		if(s instanceof Spiral){
			initialParamSpiral=((Spiral) s).getA();
			fillPath(initialX, initialY);
			
		}
		this.encounterTimeLimit=encounterTimeLimit;
		oldpoints=new ArrayList<PointP>();
		numberOfRoutesCompleted=0;
		
		radiusCoefficient = cparam.getRadiusCoefficient();
		clusterTechnique = cparam.getTechnique();
		numberOfClusters = cparam.getNumberOfClusters();
		maxDistanceForDBSCAN=cparam.getMaxDistance();
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
	

	
	/**given screen positions, path will be generated according to the shape
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
		s.setRandomRadius();
		//Lib.p("PARAMETERS ARE "+xpos+" "+ypos);
		s.fill(xpos,ypos);
		arr=s.getPoints();
		
		/*
		for(int i=0;i<arr.size();i++){
			System.out.println( (i+1)+"   "+arr.get(i).getX()+" "+ arr.get(i).getY());
		}
		//*/
		
		
		if(arr==null || arr.isEmpty()){
			s.fill(getData().getWidth()/2,getData().getHeight()/2);
			arr=s.getPoints();
			if(arr==null || arr.isEmpty()){
				Lib.p("-----------Still empty at UAV----------------");
				Lib.p(getData().getWidth()/2+" "+getData().getHeight()/2);
				
				
				Lib.p("-----------POSITIONS GOT EMPTY AT UAV FILLPATH---------------");
				Lib.p("coordinates " + xpos + ", " + ypos + " sccreenwitdth " + getData().getWidth() + 
				        " screenheight " + getData().getHeight() + " type " + s.getClass().getName());
				      Lib.p("Class name " + s.getClass() + " ");
				System.exit(-1);
			}
			
			
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
	
	
	public PointP getRandomScreenLocationFromEncounters(ArrayList<Encounter> ars) {
		int randomLoc = random.Random.get(ars.size());
		return ((Encounter)ars.get(randomLoc)).getPosition().getScreenPoint();
	}
	
	public PointP getRandomScreenLocation() {
		return getData().getRandomScreenLocation();
	}
	
	/*
	public PointP getOldScreenRandomLocation(){
		PointP p1=null;
		
		int sizeold=oldpoints.size();
		int pos=random.Random.get(sizeold+1);
		if(pos==sizeold){
			p1=getRandomScreenLocation();
		}else{
			p1=oldpoints.get(pos);
		}		
		return p1;
	}
	*/
	
	public void reRoute(long currentTime){
		if (shapename.contains("spiralcluster")) {
			clusterReroute(currentTime);
		} else {
			otherReroute(currentTime);
		}
	}
	
	
	//DBSSCAN clustering,
	private ArrayList<Cluster> DBSCAN(ArrayList<Encounter> rnct)
	{
		ArrayList<ArrayList<Encounter>> resultcluster = null;

		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		

		boolean isReal = getData().getLoc() == LocationType.REAL;
		try {
			DBSCANClusterer<Encounter> cl1 = new DBSCANClusterer<Encounter>(rnct, numberOfClusters, maxDistanceForDBSCAN, new myDistanceMetric(isReal));
			resultcluster = cl1.performClustering();


			for (int i = 0; i < resultcluster.size(); i++) {
				PointP centropoint = centerOf(resultcluster.get(i));


				Cluster x = new Cluster(i);
				x.setCentroid(centropoint);


				for (int j = 0; j < resultcluster.get(i).size(); j++) {
					PointP p1 = new PointP(resultcluster.get(i).get(j).getPosition().getScreenPoint()  );
					x.addPointP(p1);
				}
				clusters.add(x);
			}
		}
		catch (DBSCAN.DBSCANClusteringException e1) {
			Lib.p(e1);
			e1.printStackTrace();
		}
		return clusters;
	}

	private ArrayList<Cluster> kmeans(ArrayList<Encounter> rnct)
	{
	    ArrayList<Cluster> clusters = new ArrayList<Cluster>();
	    ArrayList<PointP> pts = new ArrayList<PointP>();
	     
	    boolean isReal = getData().getLoc() == LocationType.REAL;
	    KMeans kmeans = new KMeans(pts, numberOfClusters, isReal);
	    
	    kmeans.calculate();
	    clusters = kmeans.getClusters();
	    
	    return clusters;
	}
	
	public double getFurthestXPoint(ArrayList<Encounter> earr) {
		double maxxpoint=earr.get(0).getPosition().getScreenX();
		for(int i=1;i<earr.size();i++) {
			double tpoint=earr.get(i).getPosition().getScreenX();
			if(tpoint>maxxpoint) {
				maxxpoint=tpoint;
			}
		}
		return maxxpoint;
	}
	
	public double getClosestXPoint(ArrayList<Encounter> earr) {
		double minxpoint=earr.get(0).getPosition().getScreenX();
		for(int i=1;i<earr.size();i++) {
			double tpoint=earr.get(i).getPosition().getScreenX();
			if(tpoint<minxpoint) {
				minxpoint=tpoint;
			}
		}
		return minxpoint;
	}
	
	
	public void clusterReroute(long currentTime) {
		PointP p;
		double newx=0;
		double newy=0;
		//Lib.p(s.getNumberOfTours());Special)s).isMeandering()
		if(((Special)s).isMeandering()) {
			initialX=newx;
			initialY=newy;
			fillPath(newx,newy);
		}else {
			ArrayList<Encounter> rnc=rn.uniqueEncounters();
			if(rnc.size()<2) {
				//Lib.p("Size less than 2");
				PointP tempp=getRandomScreenLocation();
				newx=tempp.getX();
				newy=tempp.getY();
				((Special)s).resetRadius();
				fillPath(newx,newy);
			}else {
				double xmax=getFurthestXPoint(rnc);
				//y variable is useless here
				((Special)s).setClusterBorderForMaxX(xmax,0);
				
				double xmin=getClosestXPoint(rnc);
				//y variable is useless here
				((Special)s).setClusterBorderForMinX(xmin,0);
				
				ArrayList<Cluster> clusterResult = null;
				if (clusterTechnique == ClusterTechnique.KMEANS) {
					clusterResult = kmeans(rnc);
				} else if (clusterTechnique == ClusterTechnique.DBSCAN) {
					clusterResult = DBSCAN(rnc);
				}

				if (clusterResult.isEmpty()) {
					PointP tempp = getRandomScreenLocationFromEncounters(rnc);
					newx = tempp.getX();
					newy = tempp.getY();
					fillPath(newx, newy);
				} else {
					int sizeResult=clusterResult.size();
					if(sizeResult>numberOfClusters) {
						sizeResult=numberOfClusters;
					}
					for (int j = 0; (j < sizeResult); j++) {
						s.setMaxRadius(getRadiusOfCluster(clusterResult.get(j).getPointPs()) / radiusCoefficient);
						fillPath((clusterResult.get(j)).getCentroid().getX(), ((Cluster)clusterResult.get(j)).getCentroid().getY());
						//Lib.p("fill number "+j+" "+sizeResult+" clustersize "+clusterResult.size()+" numberofCluster "+numberOfClusters);
					}
				}
			}
			s.incNumberOfTours();
			rn.clearContacts();
			rn.clearAllEncounters();
		}
		//Lib.p(s.getNumberOfTours()+" Tours");
		numberOfRoutesCompleted += 1;
	}
	
	private double getRadiusOfCluster(ArrayList<PointP> points)
	{
		double maxX = ((PointP)points.get(0)).getX();
		double maxY = ((PointP)points.get(0)).getY();
		double minX = ((PointP)points.get(0)).getX();
		double minY = ((PointP)points.get(0)).getY();
		double ptsx = 0.0D;
		double ptsy = 0.0D;

		for (int i = 1; i < points.size(); i++) {
			ptsx = ((PointP)points.get(i)).getX();
			ptsy = ((PointP)points.get(i)).getY();

			if (ptsx > maxX) {
				maxX = ptsx;
			}
			if (ptsx < minX) {
				minX = ptsx;
			}
			if (ptsy > maxY) {
				maxY = ptsy;
			}
			if (ptsy < minY) {
				minY = ptsy;
			}
		}

		return Lib.screenDistance(maxX, maxY, minX, minY);
	}

	
	public PointP centerOfOld(ArrayList<Encounter> encs) {
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
	
	public PointP centerOf(ArrayList<Encounter> encs)
	{
		if (encs.isEmpty()) {
			Lib.p("The encounters to be found centers are empty at uav.java");
			System.exit(-1);
		}

		double screenx = 0.0D;
		double screeny = 0.0D;
		for (int i = 0; i < encs.size(); i++) {
			screenx += (encs.get(i)).getPosition().getScreenX();
			screeny += (encs.get(i)).getPosition().getScreenY();
		}
		double midx = screenx / encs.size();
		double midy = screeny / encs.size();
		return new PointP(midx, midy);
	}
	
	public double mydistance(Position p1,Position p2) {
		return Lib.relativeDistance(p1.real.getX(),p1.real.getY(),p2.real.getX(),p2.real.getY());
	}
	
	/**
	 * @param currentTime
	 */
	public void otherReroute(long currentTime) {
		double newx=0;
		double newy=0;
		
		PointP p = getRandomScreenLocation();
		newx = p.getX();
	    newy = p.getY();
	    
	    rn.clearEncountersWithLimit(encounterTimeLimit, currentTime);
	    
	    initialX = newx;
	    initialY = newy;
	    
	    fillPath(newx, newy);
	    
	    numberOfRoutesCompleted += 1;
	    s.clearPositions();
	}

	/*
	public void spiralReroute(long currentTime) {
		double newx=0;
		double newy=0;
		
		PointP p = getRandomScreenLocation();

	    newx = p.getX();
	    newy = p.getY();
	    
	    rn.clearEncountersWithLimit(encounterTimeLimit, currentTime);
	    
	    //newx = getData().convertToScreenX(newx);
	    //newy = getData().convertToScreenY(newy);
	    //Lib.p(newx+" "+newy+" CREATED POSITIONS\n");
	    
	    initialX = newx;
	    initialY = newy;
	    
	    fillPath(newx, newy);
	    
	    numberOfRoutesCompleted += 1;
	    s.clearPositions();
	}
	*/
	
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
			Lib.p("Problem getCurrentPositionWithTime! in UAV.java\nPositions are empty");
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
				//Lib.p("Reroute needed "+giventime);
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
		}else {//position length is more than 1
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
