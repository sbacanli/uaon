package simtoo;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

import routing.*;
import simtoo.Lib;

public class Simulator {

	final double COMMDISTANCE;
	final int numberOfNodes;
	boolean isRandom;
	double height;
	double width;
	Air air;
	static Routing nodeRouting;
	static Routing uavRouting;
	String simulationName;
	int realDistance;
	Datas data;
	ArrayList<Node> nodes;
	ArrayList<RoutingNode> routingNodes;
	Uav myuav;
	RoutingNode routingNodeUav;
	double speeduav;
	int messageLifeInSeconds;
	int numberOfPositions;
	int spiralRadiusInitial;
	String dataFolder; 
	
	public Simulator(Options op,Datas datagiven)
	{
		data=datagiven;
		height=data.getHeight();
		width=data.getWidth();
		air=new Air();
		
		numberOfPositions=2;
		
		realDistance=op.getParamInt("CommDistance");
		numberOfNodes=op.getParamInt("numberOfNodes");
		nodeRouting=new Probabilistic(air,op.getParamDouble("nodeProbability"));
		uavRouting=new Probabilistic(air,op.getParamDouble("uavProbability"));
		simulationName=op.getParamString("SimulationName");
		dataFolder=op.getParamString("dataFolder");
		isRandom=op.getParamBoolean("randomMobility");
		messageLifeInSeconds=op.getParamInt("MessageLifeInSeconds");
		
		nodes=new ArrayList<Node>(numberOfNodes);
		routingNodes = new ArrayList<RoutingNode>(numberOfNodes);
		
		
	
		String dataFileName="datasets"+"\\"+dataFolder+"\\"+dataFolder+"_30sec_0";
		
		if(!isRandom){
			data.calculateMaxes(dataFolder);
		}else{	
			data.calculateMaxesForScreen();
		}
		
		for(int i=0;i<numberOfNodes;i++){
			nodes.add(new Node(i+1));
			routingNodes.add(new RoutingNode(i+1));
			if(!isRandom){
				if(i+1<10){
					nodes.get(i).setPoints(data.readRealDataForNode(dataFileName+"0"+(i+1)+".txt"));
				}else{
					nodes.get(i).setPoints(data.readRealDataForNode(dataFileName+(i+1)+".txt"));
				}
			}else{
				ArrayList<PointP> path=data.fillRandomPositions(numberOfPositions);
				nodes.get(i).addPathsWithScreenCoordinatesAll(path,data);
			}
			
		}
		
		/*
		ArrayList<Position> pts=nodes.get(1).getPoints();
		for(int i=0;i<pts.size();i++){
			Lib.p(pts.get(i).toString());
		}
		//*/
		
		speeduav=30;
		spiralRadiusInitial=50;
		
		myuav=new Uav(-1,speeduav,(double)width/3,(double)height/3,data);
		//myuav=new Uav(-1,speeduav,100,100,data);
		
		routingNodeUav=new RoutingNode(-1);
		myuav.fillPath(spiralRadiusInitial,myuav.initialX,myuav.initialY);
		
		COMMDISTANCE=data.RealToVirtualDistance(realDistance);
		
		
		Reporter.init(simulationName);
	
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
	
	public String getSimulationName(){
		return simulationName;
	}
	
	public int getNumberOfNodes(){
		return numberOfNodes;
	}
	
	public boolean isRandom(){
		return isRandom;
	}
	
	public void setRandom(){
		isRandom=true;
	}
	
	public void unSetRandom(){
		isRandom=false;
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
	
	public Uav getUav(){
		return myuav;
	}
	
	public RoutingNode getUavRoutingNode(){
		return routingNodeUav;
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

	public double getCommDist(){
		return COMMDISTANCE;
	}
}
