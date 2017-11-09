package simtoo;

public class Node extends Positionable{

	Datas data;
	private String datafile;
	long dataLineStart;
	int numberOfDataLines;

	
	public Node(int nid,boolean isGPS,Datas data){
		super(nid,isGPS);
		this.data=data;
		//Speeds will be set according to the dataset
		//this is for random mobility
		setScreenSpeed(15);
		setRealSpeed(1);
		dataLineStart=0;
		numberOfDataLines=10;

	}
		

	public void setDataFile(String s){
		datafile=s;
	}
	
	public String getDataFile(){
		return datafile;
	}
	
	public long getDataLineStart(){
		return dataLineStart;
	}
	
	public int getNumberOfDataLines(){
		return numberOfDataLines;
	}
	
	public void readData(){
		if(positionsLength()==0){
			setPoints(data.readPortion(datafile,dataLineStart,numberOfDataLines));
			positionsTraced=0;
		}
		dataLineStart=dataLineStart+numberOfDataLines;
	}
	
	public String toString(){
		return "Node id: "+getId()+ " datafile: "+getDataFile();
	}
	
}
