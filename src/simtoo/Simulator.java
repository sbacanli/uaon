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
	//private boolean isCharging;

	public Simulator(Options op,Datas datagiven)
	{
		data = datagiven;

		air = new Air();

		numberOfPositions = 2;

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
		//isCharging=op.getParamBoolean("isCharging");
		

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

		altitude = op.getParamInt("Altitude");
		if (numberOfUAVs > 0) {
			altitudeconverted = data.RealToVirtualDistance(altitude);
		}

		double internodesprob = op.getParamDouble("interNodesProbability");
		if (internodesprob == -1) {
			String nrparam = op.getParamString("NodeRouting");
			String[] exploded = nrparam.split(" ");

			if (exploded[0].equals("SCR")) {
				nodeRouting = new SCRouting(air, Double.parseDouble(exploded[1]), 
						Double.parseDouble(exploded[2]), Double.parseDouble(exploded[3]), Double.parseDouble(exploded[4]));
			}
		} else {
			nodeRouting = new Probabilistic(air, internodesprob);
		}

		uavRouting = new routing.UAONRouting(air, op.getParamDouble("interUavsProbability"), op.getParamBoolean("encounterHistoryExchange"));
		interRouting = new Probabilistic(air, op.getParamDouble("interProbability"));


		messageTimesForUAVs = op.getParamInt("MessageTimesForUAVs");
		messageTimesForNodes = op.getParamInt("MessageTimesForNodes");
		messageErrorTimesForUAVs = op.getParamInt("MessageErrorTimesForUAVs");
		messageErrorTimesForNodes = op.getParamInt("MessageErrorTimesForNodes");
		messageLifeInSeconds = op.getParamInt("MessageLifeInSeconds");

		encounterTimeLimit = op.getParamInt("EncounterTimeLimit");



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
				currentNode.setScreenSpeed(15);
				currentNode.setRealSpeed(1);
				ArrayList<PointP> path = data.fillRandomPositions(numberOfPositions);
				currentNode.addPathsWithPoints(path, data, LocationType.SCREEN);
			}
		}


		ClusterParam cp = null;
		ClusterTechnique ct = null;

		for (int i = 1; i <= numberOfUAVs; i++) {
			speeduavReal = op.getParamInt("SpeedOfUAVs");

			


			


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
				spiralRadiusInitial = op.getParamInt("InitialSpiralRadius");
				double spiralRadiusInitialconverted = data.RealToVirtualDistance(spiralRadiusInitial);
				maxSpiralRadius = op.getParamDouble("MaxSpiralRadius");

				if(maxSpiralRadius>0) {
					maxSpiralRadius = data.RealToVirtualDistance(maxSpiralRadius);
				}
				
				
				s = new Shapes.Spiral(spiralRadiusInitialconverted, maxSpiralRadius, data.getWidth(), data.getHeight());
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
				spiralRadiusInitial = op.getParamInt("InitialSpiralRadius");
				double spiralRadiusInitialconverted = data.RealToVirtualDistance(spiralRadiusInitial);
				
				
				
				maxSpiralRadius = op.getParamDouble("MaxSpiralRadius");
				maxSpiralRadius = data.RealToVirtualDistance(maxSpiralRadius);
				
				
				s = new Shapes.Special(spiralRadiusInitialconverted, maxSpiralRadius, 
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
				spiralRadiusInitial = op.getParamInt("InitialSpiralRadius");
				double spiralRadiusInitialconverted = data.RealToVirtualDistance(spiralRadiusInitial);
				
				cp = new ClusterParam(ct, numberofClusters, clusterRadiusCoefficient,maxDistanceForDBSCAN);
				s = new Shapes.ClusterLine(spiralRadiusInitialconverted, maxSpiralRadius, 
						aRectconverted, bRectconverted, data.getWidth(), data.getHeight(),op.getParamInt("LimitCountForCluster"));

			}
			else if (shapeUAV.toLowerCase().equals("random")) {
				s = new Shapes.RandomPoints(data.getWidth(), data.getHeight());
			} else {
				Lib.p("Unknown Shape at config file");
				System.exit(-1);
			}


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

	public static void nodeRoute(RoutingNode r1,RoutingNode r2, String time){
		nodeRouting.setSender(r1);
		nodeRouting.setReceiver(r2);
		nodeRouting.send(time);
	}

	public static void uavRoute(RoutingNode uav1,RoutingNode uav2, String time){
		uavRouting.setSender(uav1);
		uavRouting.setReceiver(uav2);
		uavRouting.send(time);
	}

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
