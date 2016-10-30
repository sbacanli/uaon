package simtoo;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

public class Simulator {

	final double COMMDISTANCE;
	final int nodesSize;
	boolean random;
	int height;
	int width;
	
	public Simulator()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		height= screenSize.height-10;
		width = screenSize.width-10;
		COMMDISTANCE=Datas.RealToVirtualDistance(10);
		nodesSize=39;
		//random=true;
		random=false;
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
	
	public void checkNodesDistances(Uav uavt,ArrayList<Node> nodesg, int time){
		double xuav=uavt.getRealPosition(time).getX();
        double yuav=uavt.getRealPosition(time).getY();
        
		   for(int i=0;i<nodesg.size();i++){
	        	double x=nodesg.get(i).getRealPosition(time).getX();
	        	double y=nodesg.get(i).getRealPosition(time).getY();
	        	for(int j=0;j<i;j++){
		        	if(i != j){
						double otherx=nodesg.get(j).getRealPosition(time).getX();
						double othery=nodesg.get(j).getRealPosition(time).getY();
						if(Lib.distance(x, y, otherx, othery) <= COMMDISTANCE){
							nodesg.get(j).encounter(time, nodesg.get(i).getId());
							nodesg.get(i).encounter(time, nodesg.get(j).getId());
							
						}
		        	}
	        	}
	        	
	        	if(Lib.distance(xuav, yuav, x, y) <= COMMDISTANCE){
	      			uavt.encounterWithNode(time,nodesg.get(i).getId());
	      		}
	        }
	}
	
	public double getCommDist(){
		return COMMDISTANCE;
	}
}
