package simtoo;
import routing.*;

import java.util.ArrayList;

public class Uav extends Positionable{

	ArrayList<Encounter> encounterHistory;

	double initialX,initialY;
	int uid;

	
	Uav(int uid,double speedx,double xpos,double ypos,int xlim,int ylim){
		super(100000000,ylim,xlim);
		super.setScreenSpeed(speedx);
		encounterHistory=new ArrayList<Encounter>();
		initialX=xpos;
		initialY=ypos;
		this.uid=uid;
	}
	
	public int getId(){
		return uid;
	}
	
	public void fillPath(){
		Spiral s=new Spiral(5,initialX,initialY,getMAXWIDTH(),getMAXHEIGHT());
		s.fill();
		ArrayList<PointP> arr=s.getPoints();
		if(arr==null){
			Lib.p("STOPPED ");
		}
		for(int i=0;i<arr.size();i++){
			addPathWithScreenCoordinates(arr.get(i).getX(),arr.get(i).getY());
		}
	}
	
	public void encounterWithNode(int time,int nodeid){
		encounterHistory.add(new Encounter(nodeid,getId(),time));
		Lib.p(nodeid+" node encountered with UAV");
	}
		
		
}
