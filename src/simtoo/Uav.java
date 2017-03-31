package simtoo;
import routing.*;

import java.util.ArrayList;

public class Uav extends Positionable{

	ArrayList<Encounter> encounterHistory;

	double initialX,initialY;
	int uid;
	Datas mydata;
	int spiralRadius;
	
	Uav(int uid,double speedx,double xpos,double ypos,Datas givendata){
		super();
		super.setScreenSpeed(speedx);
		encounterHistory=new ArrayList<Encounter>();
		initialX=xpos;
		initialY=ypos;
		this.uid=uid;
		mydata=givendata;
		spiralRadius=50;
	}
	
	public double getInitialX(){
		return initialX;
	}
	
	public double getInitialY(){
		return initialY;
	}
	public int getId(){
		return uid;
	}
	
	public void fillPath(int a,double xpos,double ypos){
		//initial X and Y coordinate on the screen
		Spiral s=new Spiral(a,xpos,ypos,mydata.getWidth(),mydata.getHeight());
		s.fill();
		ArrayList<PointP> arr=s.getPoints();
		if(arr==null){
			Lib.p("Uav Class STOPPED ");
		}
		for(int i=0;i<arr.size();i++){
			addPathWithScreenCoordinates(arr.get(i).getX(),arr.get(i).getY(),mydata);
		}
		
		addPathWithScreenCoordinates(initialX,initialY,mydata);
		
	}
	
	//*
	public void encounterWithNode(int time,Position p,int nodeid){
		encounterHistory.add(new Encounter(nodeid,getId(),p,time));
	}
	//*/	
	
	public void reRoute(){
		clearPositions();  
		if(encounterHistory.isEmpty()){
			fillPath(spiralRadius,getInitialX(),getInitialY());
		}else{
			fillPath(spiralRadius,encounterHistory.get(0).getPosition().getScreenX(),encounterHistory.get(0).getPosition().getScreenY());
		}
		
		Lib.p("ENCOUNTERS");
		for(Encounter e:encounterHistory){
			Lib.p(e.toString());
		}
		
		//encounterHistory.clear();
		spiralRadius +=10;
        routeFinished=false;
	}
		
}
