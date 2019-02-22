package simtoo;

import java.io.File;
import java.util.ArrayList;

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
	//private int GridXDistance,GridYDistance;
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
	private double spiralAconverted;

	public Simulator(Options op,Datas datagiven)
	{
		data = datagiven;

		air = new Air();

		numberOfNodes = op.getParamInt("numberOfNodes");

		char seperator = File.separatorChar;

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
			dataFolder = op.getParamString("dataFolder");
			foldername = (System.getProperty("user.dir") + seperator + "datasets" + seperator + dataFolder + seperator + "processedData");
			datafiles = data.getDataFiles(foldername);

			if (numberOfNodes == -1) {
				numberOfNodes = datafiles.size();
			}
			data.calculateMaxes(foldername);
			if (!isVisible) {
				data.makeAllEqual();
			}
		} else {
			data.calculateMaxesForScreen();
		}
		data.calculateAreaRatio();
		data.setMaxTime(maxTime);

		COMMDISTANCE = data.RealToVirtualDistance(realDistance);

		messageLifeInSeconds = op.getParamInt("MessageLifeInSeconds");
		
		double internodesprob = op.getParamDouble("interNodesProbability");
		if (internodesprob == -1) {
			nodeRouting=processRoutingParameter(op.getParamString("NodeRouting"),messageLifeInSeconds);
		} else {
			nodeRouting = new Probabilistic(air, internodesprob);
		}
		
		if (numberOfUAVs > 0) {
			messageTimesForUAVs = op.getParamInt("MessageTimesForUAVs");
			messageErrorTimesForUAVs = op.getParamInt("MessageErrorTimesForUAVs");
			altitude = op.getParamInt("Altitude");
			chargeOn=op.getParamBoolean("chargeOn");
			altitudeconverted = data.RealToVirtualDistance(altitude);
			speeduavReal = op.getParamInt("SpeedOfUAVs");
			double interUAVProb=op.getParamDouble("interUavsProbability");
			if (interUAVProb == -1) {
				uavRouting=processRoutingParameter(op.getParamString("UAVRouting"),messageLifeInSeconds);
			} else {
				uavRouting = new UAONRouting(air, interUAVProb);
				
				((UAONRouting)uavRouting).setEncounterHistoryExchange(op.getParamBoolean("encounterHistoryExchange"));
			}
			
			double internodeUAVProb=op.getParamDouble("interProbability");
			if (internodeUAVProb == -1) {
				interRouting=processRoutingParameter(op.getParamString("UAVandNodeRouting"),messageLifeInSeconds);
			} else {
				//routing between UAV and nodes
				interRouting = new Probabilistic(air, internodeUAVProb);
			}
			
		}else {//no UAV exists
			uavRouting=new Probabilistic(air,0);
			interRouting=new Probabilistic(air,0);
		}
		
		messageTimesForNodes = op.getParamInt("MessageTimesForNodes");
		messageErrorTimesForNodes = op.getParamInt("MessageErrorTimesForNodes");
		

		//GridXDistance = op.getParamInt("GridXDistance");
		//GridYDistance = op.getParamInt("GridYDistance");

		shapeUAV = op.getParamString("Shape");

		nodes = new ArrayList<Node>(numberOfNodes);
		routingNodes = new ArrayList<RoutingNode>(numberOfNodes);
		uavs = new ArrayList<Uav>(numberOfUAVs);
		routingNodeUavs = new ArrayList<RoutingNode>(numberOfUAVs);

		height = data.getHeight();
		width = data.getWidth();

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
		}


		ClusterParam cp = null;
		ClusterTechnique ct = null;

		for (int i = 1; i <= numberOfUAVs; i++) {
			
			Shapes.Shape s = null;
			cp = new ClusterParam();
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
				if (i % 2 == 1) {
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
					ct = ClusterTechnique.DBSCAN;
					maxDistanceForDBSCAN=op.getParamDouble("MaxDistanceForDBSCAN");
					maxDistanceForDBSCAN=data.RealToVirtualDistance(maxDistanceForDBSCAN);
				} else if (clusterTechique.toLowerCase().equals("kmeans")) {
					ct = ClusterTechnique.KMEANS;
				} else {
					Lib.p("Undefined Cluster technique at config file");
					System.exit(-1);
				}
					
				setSpiralParameters(op.getParamInt("SpiralA"),op.getParamDouble("MaxSpiralRadius"));
				
				s = new Shapes.Special(spiralAconverted, maxSpiralRadius, 
						aRectconverted, bRectconverted, data.getWidth(), data.getHeight(),op.getParamInt("TourLimit"));
				cp = new ClusterParam(ct, numberofClusters, clusterRadiusCoefficient,maxDistanceForDBSCAN);
			} else if (shapeUAV.toLowerCase().equals("linecluster")) {
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
					ct = ClusterTechnique.DBSCAN;
					maxDistanceForDBSCAN=op.getParamDouble("MaxDistanceForDBSCAN");
					maxDistanceForDBSCAN=data.RealToVirtualDistance(maxDistanceForDBSCAN);
					
				} else if (clusterTechique.toLowerCase().equals("kmeans")) {
					ct = ClusterTechnique.KMEANS;
				} else {
					Lib.p("Undefined Cluster technique at config file");
					System.exit(-1);
				}
				
				
				setSpiralParameters(Integer.MIN_VALUE,Double.MIN_VALUE);//maximum spiral radius set very small
				
				cp = new ClusterParam(ct, numberofClusters, clusterRadiusCoefficient,maxDistanceForDBSCAN);
				//setting maximum spiral radius as 1 (as small as possible) to create line effect
				s = new Shapes.ClusterLine(spiralAconverted, maxSpiralRadius, 
						aRectconverted, bRectconverted, data.getWidth(), data.getHeight(),op.getParamInt("LimitCountForCluster"));

			}else {
				Lib.p("Unknown Shape at config file");
				System.exit(-1);
			}
			encounterTimeLimit = op.getParamInt("EncounterTimeLimit");//in terms of seconds

			Uav u = new Uav(-1 * i, s, speeduavReal, altitude, data, rn, shapeUAV, encounterTimeLimit, cp);
			//u.setGriderParams(GridXDistance, GridYDistance);
			/*
			if(isCharging) {
				
			}
			*/
			uavs.add(u);
			
		}



		routing.Reporter.init(toString());

	}

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
		}
		System.out.println("UNKNOWN ROUTING TYPE at PARAMETER "+parameterName);
		return null;
	}
	
	
	/*
	 * 
	 * Routing between Nodes
	 */
	public static void nodeRoute(RoutingNode r1,RoutingNode r2, String time){
		nodeRouting.setSender(r1);
		nodeRouting.setReceiver(r2);
		nodeRouting.send(time);
	}

	/*
	 * 
	 * Routing between UAVs
	 */
	public static void uavRoute(RoutingNode uav1,RoutingNode uav2, String time){
		uavRouting.setSender(uav1);
		uavRouting.setReceiver(uav2);
		uavRouting.send(time);
	}

	/*
	 * 
	 * Routing between UAV and Nodes
	 */
	public static void uavNodeRoute(RoutingNode uav,RoutingNode r2, String time){
		interRouting.setSender(uav);
		interRouting.setReceiver(r2);
		interRouting.send(time);
	}

	public double getCommDist(){
		return COMMDISTANCE;
	}

	public String toString(){
		//text+="_gx_"+GridXDistance+"_gy_"+GridYDistance;
		String text=getSimulationName()+"_"+dataFolder;
		/*
		if(shapeUAV.equals("Spiral")|| shapeUAV.equals("spiral")){
			text+="_spiralR_"+spiralRadiusInitial;
		}else if(shapeUAV.equals("Rectangle")|| shapeUAV.equals("rectangle")){
			text+="_arect_"+aRect+"_brect_"+bRect;
		}else{

		}
		if(randomGrid){
			text+="_Random";
		}
		 */
		return text;
	}
}
