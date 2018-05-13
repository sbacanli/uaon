package simtoo;

import java.util.ArrayList;

public class Node extends Positionable{

	private String datafile;
	private long dataLineStart;
	//how many times the data file is read
	private int rounds;
	
	private boolean isVisible;
	int numberOfDataLines;
	
	public Node(int nid,Datas data){
		super(nid,data);
		dataLineStart=0;
		numberOfDataLines=getData().getNumberOfDataLines();
		isVisible=true;
		setCurrentPosition(null);
		setPreviousPosition(null);
		rounds=0;
	}
		

	private void addRounds() {
		rounds++;
	}
	
	private int getRounds() {
		return rounds;
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
	
	public void readData(long gtime){
		if(dataLineStart==-1) {
			isVisible=false;
		}else {
			if(positionsLength()==0){
				long timebias=0;
				if(rounds!=0) {
					timebias=rounds*getData().getMaxFTime();
				}
				
				
				ArrayList<Position> positionsCreated=getData().readPortion(datafile,dataLineStart,numberOfDataLines,timebias);
				
				if(positionsCreated==null || positionsCreated.isEmpty()) {
					//Lib.p("POSITIONS EMPTY at node.java");
					//Lib.p("length "+positionsLength()+"  "+dataLineStart+"  "+numberOfDataLines+" "+datafile);
					dataLineStart=0;//data to be read is finished!
					addRounds();
					timebias=rounds*getData().getMaxFTime();
					positionsCreated=getData().readPortion(datafile,dataLineStart,numberOfDataLines,timebias);
					//System.exit(-1);
					//it means that node data is finished
				}
				
				
			
				/*
				Lib.p("CREATED for node "+getId()+" "+getDataFile());
				if(getId()==12) {
					writePositions();
				}*/
				
				//Lib.p(getId()+" Node.java "+getDataFile());
				if(positionsCreated.size()<numberOfDataLines) {
					dataLineStart=0;//reread the data from beginning
					addRounds();
				}else {
					dataLineStart=dataLineStart+numberOfDataLines;
				}//end of positions.size check
				
				addPathsWithPositions(positionsCreated,getData(),getData().getLoc());				
			
			}//end of length==0
			
		}//end of if dataline==-1 check		
	}
	
	public boolean isVisible() {
		return isVisible;
	}
	
	public String toString(){
		return "Node id: "+getId()+ " datafile: "+getDataFile();
	}
	
	@Override
	public Position getCurrentPositionWithTime(long giventime){
		Position returned=null;
		//Lib.p("TIME for node "+giventime);
		if(!isVisible()) {
			//Lib.p(getId()+ " not visible");
			return null;
		}
		if(positionsLength()==0) {
			//it means no positions in the list but we still have data to read.
			readData(giventime);
			//Lib.p("data read in node.java for node "+getId());
		}
		//here it means we have data in the list if the time has come
		//else we can not see the node as it is not in the map at that time
		
		int lengthfirst=positionsLength();
		//Lib.p(getId()+" id length "+lengthfirst);
		if(positionsLength()!=0 && getPosition(0).getTime()==giventime)
		{	
			//returned=new Position(getPosition(0));
			returned=dequeuePosition();
			setPreviousPosition(getCurrentPosition());
			setCurrentPosition(returned);
			//Lib.p("Dequued");
			calculateDistance();
			int lengthlast=positionsLength();
			if(lengthlast==lengthfirst) {
				Lib.p("Position list dequeue not done in Node.java");
			}
			
		}else {
			//This means the node is not active now. Its data is for future and time hasnt come yet for it.
			//so it will be null
			//Lib.p(getId()+" giventime "+giventime+" positions Length "+positionsLength()+" "+getPosition(0).toString());
			//writePositions();
		}
		
		return returned;
	}
	
	
}
