package simtoo;

import routing.RoutingNode;

public class Node extends Positionable{

	private int nodeId;	
	private String datafile;
	private RoutingNode rn;
	
	public Node(int nid,RoutingNode rn){
		super(nid);
		this.rn=rn;
		setScreenSpeed(10);
		setRealSpeed(1);
		nodeId=nid;
	}
		

	public void setDataFile(String s){
		datafile=s;
	}
	
	public String getDataFile(){
		return datafile;
	}
	
	public String toString(){
		return "Node id: "+getId()+ " datafile: "+getDataFile();
	}
	
}
