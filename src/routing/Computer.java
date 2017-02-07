package routing;

import java.util.*;

import simtoo.Lib;
import simtoo.Node;
import simtoo.Uav;

public class Computer {

	public static void run(ArrayList<Node> nodes, ArrayList<RoutingNode>  routingNodes, Uav uav, RoutingNode uavRouting,int numberofMessages){
		Lib.p("Simulation ended");
	
		ArrayList<RoutingNode> arrcopy = new ArrayList<RoutingNode>(routingNodes);
		arrcopy.add(uavRouting);
		
		ArrayList<Double> elems=SimLib.successRate(numberofMessages,arrcopy);
		Reporter.writeArrayListToFile(elems,"success_rates.txt");
		
		double[] arr=SimLib.MessageDelayArray(numberofMessages, arrcopy);
		Reporter.writeArrayToFile(arr, "message_delays.txt");
		
		Reporter.writePacketsSents();
		
	}
	
	

}
