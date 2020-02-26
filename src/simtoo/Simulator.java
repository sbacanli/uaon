package simtoo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

import Shapes.*;
import routing.*;

public class Simulator {

	final double COMMDISTANCE;
	int numberOfNodes;
	int numberOfUAVs;
	boolean isRandomMobility;
	double height;
	double width;
	private Air air;
	static Routing nodeRouting;
	static Routing uavRouting;
	static Routing interRouting;
	String simulationName;
	int realDistance;
	Datas data;
	ArrayList<Node> nodes;
	ArrayList<RoutingNode> routingNodes;
	ArrayList<Uav> uavs;
	ArrayList<RoutingNode> routingNodeUavs;

	private double speeduavReal;
	private int messageLifeInSeconds;
	private int numberOfPositions;
	private int spiralRadiusInitial;
	private String dataFolder; 
	private int messageTimesForNodes;
	private int messageTimesForUAVs;
	private int messageErrorTimesForNodes;
	private int messageErrorTimesForUAVs;
	private boolean isVisible;
	private String shapeUAV;
	private int aRect;
	private int bRect;
	private String foldername;
	private int altitude;
	//this is for the time limit for deleting the encounters after each route finish/
	private int encounterTimeLimit;
	private LocationType loctype;
	private double altitudeconverted;
	private int numberOfLinesToBeRead;
	private double maxSpiralRadius;
	private long maxTime;
	private String clusterTechique;
	private int clusterRadiusCoefficient;
	private int numberofClusters;
	private double maxDistanceForDBSCAN;
	private boolean chargeOn;
	private PointP[] screenChargingLocations;
	private int numberOfChargingLocations;
	private double spiralAconverted;
	private int batteryLife=-1;
	private String chargingLocationNames;


	public Simulator(Options op,Datas datagiven)
	{
		data = datagiven;

		air = new Air();

		numberOfNodes = op.getParamInt("numberOfNodes");

		String loctypestr = op.getParamString("LocationType");
		if (loctypestr.equals("SCREEN")) {
			loctype = LocationType.SCREEN;
		} else if (loctypestr.equals("REAL")) {
			loctype = LocationType.REAL;
		} else if (loctypestr.equals("RELATIVE")) {
			loctype = LocationType.RELATIVE;
		} else {
			Lib.p("UNDEFINED LOCATION TYPE");
		}
		data.setLoc(loctype);
		data.setTimeDifference(op.getParamInt("ProjectionTimeLimit"));


		realDistance = op.getParamInt("CommDistance");
		numberOfUAVs = op.getParamInt("numberOfUAVs");

		simulationName = op.getParamString("SimulationName");
		isRandomMobility = op.getParamBoolean("randomMobility");
		maxTime = op.getParamLong("MaxSimulationTime");
		isVisible = op.getParamBoolean("Visible");
		numberOfLinesToBeRead = op.getParamInt("NumberOfDataLines");
		data.setNumberOfDataLines(numberOfLinesToBeRead);
		

		ArrayList<File> datafiles = null;
		if (!isRandomMobility) {
			
			foldername = op.getParamString("dataFolder");
			String processedFolderName=Datas.filePath(foldername);
			//foldername = (System.getProperty("user.dir") + separator + "datasets" + separator + dataFolder + separator + "processedData");
			datafiles = data.getDataFiles(processedFolderName);
			
			if (numberOfNodes == -1) {
				numberOfNodes = datafiles.size();
			}
			data.calculateMaxes(foldername);
			if (!isVisible) {
				data.makeAllEqual();
			}
		} else {
			data.calculateMaxesForScreen();
			if (!isVisible) {
				data.makeAllEqual();
			}
		}
		data.calculateAreaRatio();
		data.setMaxTime(maxTime);
		height = data.getHeight();
		width = data.getWidth();

		COMMDISTANCE = data.RealToVirtualDistance(realDistance);

		messageLifeInSeconds = op.getParamInt("MessageLifeInSeconds");
		
		if (numberOfUAVs != 0) {
			messageTimesForUAVs = op.getParamInt("MessageTimesForUAVs");
			messageErrorTimesForUAVs = op.getParamInt("MessageErrorTimesForUAVs");
			altitude = op.getParamInt("Altitude");
			chargeOn=op.getParamBoolean("chargeOn");
			altitudeconverted = data.RealToVirtualDistance(altitude);
			speeduavReal = op.getParamInt("SpeedOfUAVs");
			
			
			if(isChargeOn()) {
				batteryLife=op.getParamInt("batteryLife");
				numberOfChargingLocations=op.getParamInt("NumberOfChargingLocations");
				if(numberOfChargingLocations==-1) {
					chargingLocationNames=op.getParamString("chargingLocationsFile");
					PointP[] realChargingLocations=readLocations(chargingLocationNames);
					screenChargingLocations=new PointP[realChargingLocations.length];
					
					for(int iloc=0;iloc<screenChargingLocations.length;iloc++) {
						screenChargingLocations[iloc]=new PointP( data.convertToScreenX(realChargingLocations[iloc].getX()), data.convertToScreenY(realChargingLocations[iloc].getY()) );
					}
					
					//Do not use real charging locations after
					Random rit=new Random();
					//shuffle the screen Charging locations
					int numIterations=screenChargingLocations.length;
					for(int b=0;b<numIterations;b++) {
						int swapPos=rit.nextInt(numIterations);
						PointP tempP=null;
						//swap positions
						tempP=screenChargingLocations[b];
						screenChargingLocations[b]=screenChargingLocations[swapPos];
						screenChargingLocations[swapPos]=tempP;
					}
					
				}else {
					screenChargingLocations=new PointP[numberOfChargingLocations];
					
					for(int iloc=0;iloc<numberOfChargingLocations;iloc++) {
						screenChargingLocations[iloc]=data.getRandomScreenLocation();					}
				}
				//numberofUAv can be given as -1 if and only if chargeOn is yes
				if(numberOfUAVs==-1) {
					numberOfUAVs=numberOfChargingLocations;
				}
				
			}///end of if chargeOn
			
			if(numberOfUAVs>1) {
				double interUAVProb=op.getParamDouble("interUavsProbability");
				if (interUAVProb == -1) {
					uavRouting=processRoutingParameter(op.getParamString("UAVRouting"),messageLifeInSeconds);
				} else {
					uavRouting = new UAONRouting(air, interUAVProb);
					((UAONRouting)uavRouting).setEncounterHistoryExchange(op.getParamBoolean("encounterHistoryExchange"));
				}
			}else {
				uavRouting=new Probabilistic(air,0);
			}
			
			
			//Since there is a UAV, UAV and Node routing should be decided
			double internodeUAVProb=op.getParamDouble("interProbability");
			if (internodeUAVProb == -1) {
				interRouting=processRoutingParameter(op.getParamString("UAVandNodeRouting"),messageLifeInSeconds);
			} else {
				//routing between UAV and nodes
				interRouting = new Probabilistic(air, internodeUAVProb);
			}
			/////////////////////////////////////////////////////////////////
			
			
		}else {//no UAV exists
			uavRouting=new Probabilistic(air,0);
			interRouting=new Probabilistic(air,0);
		}
		
		
		
		messageTimesForNodes = op.getParamInt("MessageTimesForNodes");
		messageErrorTimesForNodes = op.getParamInt("MessageErrorTimesForNodes");

		shapeUAV = op.getParamString("Shape");

		nodes = new ArrayList<Node>(numberOfNodes);
		routingNodes = new ArrayList<RoutingNode>(numberOfNodes);
		uavs = new ArrayList<Uav>(numberOfUAVs);
		routingNodeUavs = new ArrayList<RoutingNode>(numberOfUAVs);

		

		for (int i = 1; i <= numberOfNodes; i++) {
			RoutingNode rn = new RoutingNode(i);
			routingNodes.add(rn);
			Node currentNode = new Node(i, data);
			nodes.add(currentNode);
			if (!isRandomMobility) {
				currentNode.setDataFile(((File)datafiles.get(i - 1)).getAbsolutePath());
				currentNode.readData(data.getMinTime());
			} else {
				//Random Mobility
				//might need to be checked!
				currentNode.setScreenSpeed(15);
				currentNode.setRealSpeed(1);
				numberOfPositions = 2;
				ArrayList<PointP> path = data.fillRandomPositions(numberOfPositions);
				currentNode.addPathsWithPoints(path, data, LocationType.SCREEN);
			}
		}//end of for of nodes initialization
		
		
		//Routing between nodes. Nodes each other
		////////////////////////////////////////////////////////////////////////////////
		double internodesProb=op.getParamDouble("interNodesProbability");
		if (internodesProb == -1) {
			nodeRouting=processRoutingParameter(op.getParamString("NodeRouting"),messageLifeInSeconds);
		} else {
			//routing between UAV and nodes
			nodeRouting = new Probabilistic(air, internodesProb);
		}
		/////////////////////////////////////////////////////////////////////////////
		
		ClusterParam clusterparam = null;
		ClusterTechnique clustertech = null;

		//boolean isStartUAV=false;
		for (int i = 1; i <= numberOfUAVs; i++) {
			
			Shapes.Shape s = null;
			clusterparam = new ClusterParam();
			RoutingNode rn = new RoutingNode(i * -1);
			routingNodeUavs.add(rn);
			
			if (shapeUAV.toLowerCase().equals("rectangle")) {
				
				aRect = op.getParamInt("RectangleWidth");
				bRect = op.getParamInt("RectangleHeight");
				double aRectconverted = data.RealToVirtualDistance(aRect);
				double bRectconverted = data.RealToVirtualDistance(bRect);
				if ((aRect <= 0) || (bRect <= 0)) {
					Lib.p("aRect or bRect should be greater than 0");
					System.exit(-1);
				}
				if (i % 2 == 0) {
					s = new Rectangle(true, aRectconverted, bRectconverted, data.getWidth(), data.getHeight());
				} else {
					s = new Rectangle(false, aRectconverted, bRectconverted, data.getWidth(), data.getHeight());
				}
			}
			else if (shapeUAV.toLowerCase().equals("spiral")) {
				setSpiralParameters(op.getParamInt("SpiralA"),op.getParamDouble("MaxSpiralRadius"));		
				
				s = new Shapes.Spiral(spiralAconverted, maxSpiralRadius, data.getWidth(), data.getHeight());

			} else if (shapeUAV.toLowerCase().equals("spiralcluster")) {
				numberofClusters = op.getParamInt("numberOfClusters");
				clusterRadiusCoefficient = op.getParamInt("RadiusCoefficient");

				aRect = op.getParamInt("RectangleWidth");
				bRect = op.getParamInt("RectangleHeight");
				double aRectconverted = data.RealToVirtualDistance(aRect);
				double bRectconverted = data.RealToVirtualDistance(bRect);

				clusterTechique = op.getParamString("ClusterTechnique");
				if ((aRect <= 0) || (bRect <= 0)) {
					Lib.p("aRect or bRect should be greater than 0");
					System.exit(-1);
				}

				if (clusterTechique.toLowerCase().equals("dbscan")) {
					clustertech = ClusterTechnique.DBSCAN;
					maxDistanceForDBSCAN=op.getParamDouble("MaxDistanceForDBSCAN");
					maxDistanceForDBSCAN=data.RealToVirtualDistance(maxDistanceForDBSCAN);
				} else if (clusterTechique.toLowerCase().equals("kmeans")) {
					clustertech = ClusterTechnique.KMEANS;
				} else {
					Lib.p("Undefined Cluster technique at config file");
					System.exit(-1);
				}
					
				setSpiralParameters(op.getParamInt("SpiralA"),-1);
				
				s = new Special(spiralAconverted, maxSpiralRadius, 
						aRectconverted, bRectconverted, data.getWidth(), data.getHeight(),op.getParamInt("TourLimit"));
					((Special)s).setIsStart(i%2==0);
					//((Special)s).setIsStart(true);
				
				clusterparam = new ClusterParam(clustertech, numberofClusters, clusterRadiusCoefficient,maxDistanceForDBSCAN);
			} else {
				Lib.p("Unknown Shape at config file");
				System.exit(-1);
			}
			encounterTimeLimit = op.getParamInt("EncounterTimeLimit");//in terms of seconds

			
			Uav u = new Uav(-1 * i, s, speeduavReal, altitude, data, rn, shapeUAV, encounterTimeLimit, clusterparam,screenChargingLocations,batteryLife);
			
			
			/*
			for(int b=0;b<chargingLocations.length;b++) {
				Lib.p(chargingLocations[b]);
			}
			*/
			/*
			if(isCharging) {
				
			}
			*/
			uavs.add(u);
			
		}//end of UAVs for
		
		
		routing.Reporter.init(toString());

	}//end of Simulator constructor

	public double getConvertedAltitude() {
		if (numberOfUAVs > 0) {
			return altitudeconverted;
		}
		Lib.p("No UAV exists to get the altitude");
		return -1;
	}

	public int getMessageLifeInSeconds(){
		return messageLifeInSeconds;
	}

	public void setNodeRouting(Routing rout){
		nodeRouting=rout;
	}

	public void setUavRouting(Routing rout){
		uavRouting=rout;
	}

	public boolean isVisible(){
		return isVisible;
	}

	public String getSimulationName(){
		return simulationName;
	}

	public boolean isChargeOn() {
		return chargeOn;
	}

	public int getMessageErrorTimesForNodes(){
		return messageErrorTimesForNodes;
	}

	public int getMessageErrorTimesForUAVs(){
		return messageErrorTimesForUAVs;
	}

	public int getMessageTimesForNodes(){
		return messageTimesForNodes;
	}

	public int getMessageTimesForUAVs(){
		return messageTimesForUAVs;
	}

	public int getNumberOfNodes(){
		return numberOfNodes;
	}

	public boolean isRandomMobility(){
		return isRandomMobility;
	}

	public void setRandomMobility(){
		isRandomMobility=true;
	}

	public void unSetRandom(){
		isRandomMobility=false;
	}	

	public void setRealDistance(int dist){
		realDistance=dist;
	}

	public int getRealDistance(){
		return realDistance;
	}


	public ArrayList<Node> getNodes(){
		return nodes;
	}

	public ArrayList<RoutingNode> getRoutingNodes(){
		return routingNodes;
	}

	public ArrayList<Uav> getUavs(){
		return uavs;
	}

	public ArrayList<RoutingNode> getUavRoutingNodes(){
		return routingNodeUavs;
	}

	public void setSpiralParameters(int spiralAA,double maxsp) {
		spiralAconverted = data.RealToVirtualDistance(spiralAA);
		
		maxSpiralRadius = maxsp;
		if(maxSpiralRadius>0) {
			maxSpiralRadius = data.RealToVirtualDistance(maxSpiralRadius);
		}
	}	
	
	public Routing processRoutingParameter(String parameterName,double messageLife) {
		//String UAVparam = op.getParamString(parameterName);
		//String[] exploded = UAVparam.split(" ");
		String[] exploded=parameterName.split(" ");
		if (exploded[0].equals("SCR")) {
			return new SCRouting(air, Double.parseDouble(exploded[1]), 
					Double.parseDouble(exploded[2]), Double.parseDouble(exploded[3]), Double.parseDouble(exploded[4]));
		}else if(exploded[0].equals("VOI")) {
			return new VOIRouting(air, Double.parseDouble(exploded[1]),messageLife);
		}else if(exploded[0].equals("VOI2")) {
			return new VOIRouting(air, Double.parseDouble(exploded[1]),messageLife);
		}else if(exploded[0].equals("ChargingR")) {
			if(isChargeOn()) {
				return new ChargeRouting(air, screenChargingLocations,data.RealToVirtualDistance(Double.parseDouble(exploded[1])) ) ;
			}else {
				Lib.p("Charge on set to no but the internode routing is charging based. PROBLEM! Error!");
				Lib.createException("Simulator.java in processRoutingParameter method");
			}
		}
		System.out.println("UNKNOWN ROUTING TYPE at PARAMETER "+parameterName);
		return null;
	}
	
	//reads the locations from the specified file name and returns the array of Locations
	public static PointP[] readLocations(String locsF) {
		ArrayList<PointP> locs=new ArrayList<PointP>();
		BufferedReader br;
		String line;
		int cnt=0;
		try {
			br=new BufferedReader(new FileReader(locsF));
			while((line=br.readLine())!=null) {
				int p1=line.indexOf(",");
				String xstr=line.substring(0,p1).trim();
				String ystr=line.substring(p1+2).trim();
				
				int xdot=xstr.indexOf(".");
				int ydot=ystr.indexOf(".");
				if(xdot+8 < xstr.length()) {
					xstr=xstr.substring(0,xdot+8);
				}
				if(ydot+8 < ystr.length()) {
					ystr=ystr.substring(0,ydot+8);
				}
				double xloc = Double.parseDouble(xstr);
				double yloc = Double.parseDouble(ystr);
				locs.add(new PointP(xloc,yloc));
				cnt++;
			}			
			br.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		PointP[] res=new PointP[cnt];
		for(int i=0;i<cnt;i++) {
			res[i]=new PointP(locs.get(i));
		}
		locs.clear();
		locs=null;
		
		return res;
	}
	
	/**
	 * 
	 * @param RoutingNode r1,RoutingNode r2, Node senderNode1, Node receiverNode2
	 * @param String time
	 */
	public static void nodeRoute(RoutingNode r1,RoutingNode r2, Positionable senderNode1, Positionable receiverNode2, String time){
		nodeRouting.setSender(r1);
		nodeRouting.setSenderNode(senderNode1);
		nodeRouting.setReceiver(r2);
		nodeRouting.setReceiverNode(receiverNode2);
		nodeRouting.send(time);
	}

	/**
	 * 
	 * @param RoutingNode uav1,RoutingNode uav2,  Node senderNodeuav1, Node receiverNodeuav2
	 * @param String time
	 */
	public static void uavRoute(RoutingNode uav1,RoutingNode uav2,  Positionable senderNodeuav1, Positionable receiverNodeuav2, String time){
		uavRouting.setSender(uav1);
		uavRouting.setSenderNode(senderNodeuav1);
		uavRouting.setReceiver(uav2);
		uavRouting.setReceiverNode(receiverNodeuav2);
		uavRouting.send(time);
	}

	/**
	 * 
	 * @param RoutingNode uav,RoutingNode r2, Node uavNode, Node receiverNode
	 * @param String time
	 * 
	 */
	public static void uavNodeRoute(RoutingNode uav,RoutingNode r2, Positionable uavNode, Positionable receiverNode, String time){
		interRouting.setSender(uav);
		interRouting.setSenderNode(uavNode);
		interRouting.setReceiver(r2);
		interRouting.setReceiverNode(receiverNode);
		interRouting.send(time);
	}

	public double getCommDist(){
		return COMMDISTANCE;
	}

	public String toString(){
		String text="";
		/*if(chargeOn) {
			text=chargingLocationNames;
		}
		*/
		text=getSimulationName()+text;//+"_"+dataFolder;
		/*
		if(shapeUAV.equals("Spiral")|| shapeUAV.equals("spiral")){
			text+="_spiralR_"+spiralRadiusInitial;
		}else if(shapeUAV.equals("Rectangle")|| shapeUAV.equals("rectangle")){
			text+="_arect_"+aRect+"_brect_"+bRect;
		}else{

		}
		 */
		return text;
	}
}
