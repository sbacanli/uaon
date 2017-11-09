package routing;

import java.util.*;

import simtoo.Lib;
import simtoo.Node;
import simtoo.Uav;

public class Computer {

	public static void run(ArrayList<Node> nodes, ArrayList<RoutingNode> routingNodes, 
			ArrayList<Uav> uav, ArrayList<RoutingNode> uavRouting,int numberofMessages){

	
		ArrayList<RoutingNode> arrcopy = new ArrayList<RoutingNode>(routingNodes);
		arrcopy.addAll(uavRouting);
		
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
		
		if(Reporter.getNumberOfSentByNodes() !=0 || Reporter.getNumberOfSentByUAVs() !=0){
			Reporter.writePacketsSents();
		}
		
		Lib.p(Reporter.PacketInfo());
		//Lib.p(Reporter.getDistanceTravelled(uav));
		Reporter.writeDistanceTravelled(uav);
	}
	
	

}
