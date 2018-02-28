package simtoo;

import java.util.ArrayList;

public class Node extends Positionable{

	Datas data;
	private String datafile;
	long dataLineStart;
	int numberOfDataLines;
	private boolean isVisible;
	
	
	public Node(int nid,boolean isGPS,Datas data){
		super(nid,isGPS);
		this.data=data;
		//Speeds will be set according to the dataset
		//this is for random mobility
		setScreenSpeed(15);
		setRealSpeed(1);
		dataLineStart=0;
		numberOfDataLines=10;
		isVisible=true;

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
	
	public void readData(long gtime){
		if(dataLineStart==-1) {
			isVisible=false;
		}else {
			if(positionsLength()==0){
				ArrayList<Position> positionsCreated=data.readPortion(datafile,dataLineStart,numberOfDataLines);
				if(positionsCreated==null || positionsCreated.isEmpty()) {
					Lib.p("POSITIONS EMPTY at node.java");
					Lib.p("length "+positionsLength()+"  "+dataLineStart+"  "+numberOfDataLines+" "+datafile);
					System.exit(-1);
				}
				
				addPathsWithPositions(positionsCreated,data,"real");
			
				/*
				Lib.p("CREATED for node "+getId()+" "+getDataFile());
				if(getId()==12) {
					writePositions();
				}*/
				
				//Lib.p(getId()+" Node.java "+getDataFile());
				if(positionsCreated.size()<numberOfDataLines) {
					dataLineStart=-1;//data to be read is finished!
					isVisible=false;
				}else {
					dataLineStart=dataLineStart+numberOfDataLines;
				}//end of positions.size check
								
			
			}//end of length==0
			
		}//end of if dataline==-1 check		
	}
	
	public boolean isVisible() {
		return isVisible;
	}
	
	public String toString(){
		return "Node id: "+getId()+ " datafile: "+getDataFile();
	}
	
	/*
	public Position getcurrentPositionWithTime2(long giventime){
		Position returned=null;
		if(positionsLength()==0) {
			readData();
		}
		int size=positionsLength();
		if(getPosition(0).time==giventime)
		{
			returned=dequeuePosition();
		}
		int sizenow=positionsLength();
		if(size-1!=sizenow) {
			Lib.p("Not dequeueing! at node.java"+ size+"  "+sizenow);
		}
		return returned;
	}
	*/
	
	@Override
	public Position getCurrentPositionWithTime(long giventime){
		Position returned=null;
		Lib.p("TIME for node "+giventime);
		if(!isVisible()) {
			Lib.p("Not visible");
			return null;
		}
		if(positionsLength()==0) {
			//it means no positions in the list but we still have data to read.
			readData(giventime);
			Lib.p("data read");
		}
		//here it means we have data in the list if the time has come
		//else we can not see the node as it is not in the map at that time
		
		if(positionsLength()!=0 && getPosition(0).getTime()==giventime)
		{	
			//returned=new Position(getPosition(0));
			returned=dequeuePosition();
			Lib.p("Dequued");
		}else {
			//writePositions();
		}
		
		return returned;
	}
	
	
}
