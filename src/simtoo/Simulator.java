package simtoo;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

import routing.*;
import simtoo.Lib;

public class Simulator {

	final double COMMDISTANCE;
	final int nodesSize;
	boolean random;
	int height;
	int width;
	Air air;
	static Routing nodeRouting;
	static Routing uavRouting;
	String simulationName;
	int realDistance;
	
	public Simulator(Options op)
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		height= screenSize.height-10;
		width = screenSize.width-10;
		air=new Air();
		
		realDistance=op.getParamInt("CommDistance");
		nodesSize=op.getParamInt("numberOfNodes");
		nodeRouting=new Probabilistic(air,op.getParamDouble("nodeProbability"));
		uavRouting=new Probabilistic(air,op.getParamDouble("uavProbability"));
		simulationName=op.getParamString("SimulationName");
		random=op.getParamBoolean("randomMobility");
		
		COMMDISTANCE=Datas.RealToVirtualDistance(realDistance);
		Reporter.init(simulationName);
	}
	
	public void setNodeRouting(Routing rout){
		nodeRouting=rout;
	}

	public void setUavRouting(Routing rout){
		uavRouting=rout;
	}
	
	public int getNodesSize(){
		return nodesSize;
	}
	
	public boolean getRandom(){
		return random;
	}
	
	public void setRandom(){
		random=true;
	}
	
	public void unSetRandom(){
		random=false;
	}	
	
	public void setRealDistance(int dist){
		realDistance=dist;
	}
	
	public int getRealDistance(){
		return realDistance;
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
