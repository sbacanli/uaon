package simtoo;

import java.io.File;
import java.util.ArrayList;

import Shapes.RandomPoints;
import Shapes.Rectangle;
import Shapes.Shape;
import Shapes.Spiral;
import routing.*;

public class Simulator {

	final double COMMDISTANCE;
	int numberOfNodes;
	int numberOfUAVs;
	boolean isRandomMobility;
	double height;
	double width;
	Air air;
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
	private int GridXDistance,GridYDistance;
	private boolean isVisible;
	private String shapeUAV;
	private int aRect;
	private int bRect;
	private String foldername;
	private boolean randomGrid;
	private int altitude;
	//this is for the time limit for deleting the encounters after each route finish/
	private int encounterTimeLimit;
	private boolean isGPS;
	
	
	public Simulator(Options op,Datas datagiven)
	{
		data=datagiven;
		
		air=new Air();
		
		numberOfPositions=2;
		
		numberOfNodes=op.getParamInt("numberOfNodes");
		dataFolder=op.getParamString("dataFolder");
		String seperator="\\";
		foldername=System.getProperty("user.dir")+seperator+"datasets"+seperator+dataFolder+seperator+"processedData";
		
		//getting the datafiles arraylist to be used for filling the node's movement data
		ArrayList<File> datafiles=data.getDataFiles(foldername);
		if(numberOfNodes==-1){
			numberOfNodes=datafiles.size();
		}
		
		isGPS=op.getParamBoolean("isGPS");
		realDistance=op.getParamInt("CommDistance");
		numberOfUAVs=op.getParamInt("numberOfUAVs");
		nodeRouting=new Probabilistic(air,op.getParamDouble("interNodesProbability"));
		uavRouting=new Probabilistic(air,op.getParamDouble("interUavsProbability"));
		interRouting=new Probabilistic(air,op.getParamDouble("interProbability"));
		simulationName=op.getParamString("SimulationName");
		isRandomMobility=op.getParamBoolean("randomMobility");
		messageLifeInSeconds=op.getParamInt("MessageLifeInSeconds");
		speeduavReal=op.getParamInt("SpeedOfUAVs");
		altitude=op.getParamInt("Altitude");
		messageTimesForUAVs=op.getParamInt("MessageTimesForUAVs");
		messageTimesForNodes=op.getParamInt("MessageTimesForNodes");
		messageErrorTimesForUAVs=op.getParamInt("MessageErrorTimesForUAVs");
		messageErrorTimesForNodes=op.getParamInt("MessageErrorTimesForNodes");
		randomGrid=op.getParamBoolean("RandomGrid");
		encounterTimeLimit=op.getParamInt("EncounterTimeLimit");
		
		
		GridXDistance=op.getParamInt("GridXDistance");
		GridYDistance=op.getParamInt("GridYDistance");
		isVisible=op.getParamBoolean("Visible");
		shapeUAV=op.getParamString("Shape");
		if(shapeUAV.equals("Rectangle") || shapeUAV.equals("rectangle")){
			aRect=op.getParamInt("RectangleWidth");
			bRect=op.getParamInt("RectangleHeight");
			if(aRect<=0 || bRect <=0){
				Lib.p("aRect or bRect should be greater than 0");
				System.exit(-1);
			}
			
		}else if(shapeUAV.equals("spiral") || shapeUAV.equals("Spiral")){
			spiralRadiusInitial=op.getParamInt("InitialSpiralRadius");
		}else if(shapeUAV.equals("random") || shapeUAV.equals("Random")){
			randomGrid=true;
		}else{
			Lib.p("Unknown shape type in the config file entry: Shape\r\nIt should be spiral/rectangle/random");
			System.exit(-1);
		}
		
		nodes=new ArrayList<Node>(numberOfNodes);
		routingNodes = new ArrayList<RoutingNode>(numberOfNodes);
		uavs=new ArrayList<Uav>(numberOfUAVs) ;
		routingNodeUavs=new ArrayList<RoutingNode>(numberOfUAVs);
		
		
		if(!isRandomMobility){
			data.calculateMaxes(foldername);
			if(!isVisible){
				data.makeAllEqual();
			}
		}else{	
			data.calculateMaxesForScreen();
		}
		
		/*
		Lib.p("maxtime " +data.FindData(foldername, data.getMaxTime()+""));
		Lib.p("mintime " +data.FindData(foldername, data.getMinTime()+""));
		Lib.p("maxx " +data.FindData(foldername, data.getMaxX()+""));
		Lib.p("maxy " +data.FindData(foldername, data.getMaxY()+""));
		Lib.p(data.getMinX()+" minx " +data.FindData(foldername, data.getMinX()+"" ));
		Lib.p(data.getMinY()+" miny " +data.FindData(foldername, data.getMinY()+""));
		//*/
		
		height=data.getHeight();
		width=data.getWidth();
		data.setGPS(isGPS);
		
		
		//the parameters are for real coordinates, conversion should be done to virtual
		//so that the classes can calculate
		double aRectconverted=data.RealToVirtualDistance(aRect);
		double bRectconverted=data.RealToVirtualDistance(bRect);
		double spiralRadiusInitialconverted=data.RealToVirtualDistance(spiralRadiusInitial);
		
		for(int i=1;i<=numberOfNodes;i++){
			RoutingNode rn=new RoutingNode(i);
			routingNodes.add(rn);
			Node currentNode=new Node(i,isGPS,data);
			nodes.add(currentNode);
			if(!isRandomMobility){
				currentNode.setDataFile(datafiles.get(i-1).getAbsolutePath());
				currentNode.readData(data.getMinTime());
			}else{
				ArrayList<PointP> path=data.fillRandomPositions(numberOfPositions);
				currentNode.addPathsWithPoints(path,data,"screen");
				
			}
			
		}//end of for
		
		
		
		for(int i=1;i<=numberOfUAVs;i++){
			Shape s=null;
			RoutingNode rn=new RoutingNode(i*-1);
			routingNodeUavs.add(rn);
			
			double initialX=LibRouting.getUniform((int)width)-1;
			double initialY=LibRouting.getUniform((int)height)-1;
			
			if(shapeUAV.equals("spiral") || shapeUAV.equals("Spiral")){
				s=new Spiral(spiralRadiusInitialconverted,data.getWidth(),data.getHeight());
			}else if(shapeUAV.equals("Rectangle") || shapeUAV.equals("rectangle")){
				//one of the UAV will start from beginning, the other will start from end
				if(i%2==0){
					s=new Rectangle(true,aRectconverted,bRectconverted,data.getWidth(),data.getHeight());
				}else{
					s=new Rectangle(false,aRectconverted,bRectconverted,data.getWidth(),data.getHeight());
				}
				
			}else if(shapeUAV.equals("random") || shapeUAV.equals("Random")){
				s=new RandomPoints(data.getWidth(),data.getHeight());
			}
			
			
			Uav u=new Uav(-1*i,s,speeduavReal,altitude,initialX,initialY,data,rn,randomGrid,encounterTimeLimit);
			u.setGriderParams(GridXDistance, GridYDistance);
			u.fillPath(u.getInitialX(),u.getInitialY(),data.getMinTime());
			
			//Lib.p("filled path "+u.positionsLength()+" in simulator");
			//u.writePositions();
			uavs.add(u);
			
		}
		
		COMMDISTANCE=data.RealToVirtualDistance(realDistance);
		
		Reporter.init(toString());
	
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
	
	public boolean isGPS(){
		return isGPS;
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
	
	public static void uavRoute(RoutingNode uav,RoutingNode r2, String time){
		uavRouting.setSender(uav);
		uavRouting.setReceiver(r2);
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
		if(shapeUAV.equals("Spiral")|| shapeUAV.equals("spiral")){
			text+="_spiralR_"+spiralRadiusInitial;
		}else if(shapeUAV.equals("Rectangle")|| shapeUAV.equals("rectangle")){
			text+="_arect_"+aRect+"_brect_"+bRect;
		}else{
			
		}
		if(randomGrid){
			text+="_Random";
		}
		return text;
		
		
	}
}
