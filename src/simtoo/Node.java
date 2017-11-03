package simtoo;

public class Node extends Positionable{


	private String datafile;
	
	public Node(int nid,boolean isGPS){
		super(nid,isGPS);
		setScreenSpeed(10);
		setRealSpeed(1);
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
