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
		if(elems.isEmpty()){
			Lib.p("success rates are empty");
		}else{
			Reporter.writeArrayListToFile(elems,"success_rates.txt");
		}
		
		
		double[] arr=SimLib.MessageDelayArray(numberofMessages, arrcopy);
		if(arr.length==0){
			Lib.p("Message Delays are empty");
		}else{
			Reporter.writeArrayToFile(arr, "message_delays.txt");
		}
		
		if(Reporter.getNumberOfSentByNodes() !=0 || Reporter.getNumberOfSentByUAV() !=0){
			Reporter.writePacketsSents();
		}
		
		Lib.p(Reporter.PacketInfo());
		
	}
	
	

}
