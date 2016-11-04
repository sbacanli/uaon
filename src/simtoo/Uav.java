package simtoo;
import routing.*;

import java.util.ArrayList;

public class Uav extends Positionable{

	ArrayList<Encounter> encounterHistory;

	double initialX,initialY;
	int uid;

	
	Uav(int uid,double speedx,double xpos,double ypos,int xlim,int ylim){
		super(ylim,xlim);
		super.setScreenSpeed(speedx);
		encounterHistory=new ArrayList<Encounter>();
		initialX=xpos;
		initialY=ypos;
		this.uid=uid;
	}
	
	public int getId(){
		return uid;
	}
	
	public void fillPath(int a){
		Spiral s=new Spiral(a,initialX,initialY,getMAXWIDTH(),getMAXHEIGHT());
		s.fill();
		ArrayList<PointP> arr=s.getPoints();
		if(arr==null){
			Lib.p("STOPPED ");
		}
		for(int i=0;i<arr.size();i++){
			addPathWithScreenCoordinates(arr.get(i).getX(),arr.get(i).getY());
		}
		addPathWithScreenCoordinates(initialX,initialY);
		
	}
	
	public void encounterWithNode(int time,int nodeid){
		encounterHistory.add(new Encounter(nodeid,getId(),time));
		Lib.p(nodeid+" node encountered with UAV");
	}
		
		
}
