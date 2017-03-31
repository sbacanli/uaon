package simtoo;

public class Node extends Positionable{

	public int nodeId;	
	
	public Node(int nid){
		super();
		setScreenSpeed(10);
		setRealSpeed(1);
		nodeId=nid;
	}
		
	public int getId(){
		return nodeId;
	}
	
	
}
