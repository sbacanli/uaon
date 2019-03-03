package simtoo;


import java.io.*;
import java.util.*;

import routing.LibRouting;


public class Positionable {

	
		private int nodeId;
		private double speed;//   in terms of  m/sec
		private double screenspeed;//in terms of pixels
		private double distancetravelled;
		
		private Queue<Position> pointsp;
		private boolean routeFinished;
		private Comparator<Position> positionComparator;
		private Datas mydata;
		private Position prevPosition;
		private Position currentPosition;
		
		public Positionable(int nodeId,Datas gdata){

			//fillPositions();
			mydata=gdata;
			this.nodeId=nodeId;
			if(nodeId==0){
				Lib.p("NodeId is 0");
				System.exit(-1); 
			}
			
			speed=1;//used in random mobility, will be changed if not random mobility
			screenspeed=10;//used in random mobility, will be changed if not random mobility
			pointsp=new LinkedList<Position>();
			setRouteFinished(false);
			distancetravelled=0;
			
			positionComparator = new Comparator<Position>()
	        {
	            public int compare(Position u1, Position u2)
	            {
	                return (int)(u1.getTime() - u2.getTime());
	            }
	        };	 
		}
		
		public int getId(){
			return nodeId;
		}
		
		public Datas getData() {
			return mydata;
		}
		
		public boolean isRouteFinished(){
			return routeFinished;
		}
		
		public void setRouteFinished(boolean a){
			routeFinished=a;
		}
		
		
		public Queue<Position> getPositions(){
			return pointsp;
		}
		
		public void setRealSpeed(double s){
			speed=s;
		}
		
		public double getRealSpeed(){
			return speed;
		}
		
		public double getScreenSpeed(){
			return screenspeed;
		}
		
		public void setScreenSpeed(double s){
			screenspeed=s;
		}
		
		public void setPoints(Queue<Position> arr){
			pointsp=arr;
		}
		
		public void setPreviousPosition(Position p1) {
			prevPosition=p1;
		}
		
		public void setCurrentPosition(Position p1) {
			currentPosition=p1;
		}
		
		public Position getCurrentPosition() {
			return currentPosition;
		}
		
		public Position getPreviousPosition() {
			return prevPosition;
		}
		
		//UAV.fillPath uses that
		public void addPathsWithPoints(ArrayList<PointP> path,Datas mydata,LocationType op){
			for(int i=0;i<path.size();i++){
				PointP pelement=path.get(i);
				if(op==LocationType.REAL) {
					Position p=mydata.getPositionWithReal(pelement.getX(),pelement.getY());		
					addPathWithSpeed(p, mydata,op);	
				}else if(op==LocationType.SCREEN) {
					Position p=mydata.getPositionWithScreen(pelement.getX(),pelement.getY());
					addPathWithSpeed(p, mydata,op);
				}else {
					Lib.p("Option Different at Positionable!!!");
				}
				
			}
		}
		
		//option will be screen or real
		// positions are added based on the time data so the speed is constant
		//according to the distance and time between the positions
		//this is used by the nodes as in the dataset
		//we assume the speed of the nodes are constant between 2 points.
		//we just protect whaatever the speed is
		// considering that the node is at pos (1,1) at time 5 and at (5,5) at time 11 then at time 6 (2,2) etcc
		// the distance is taken with constant speed
		public void addPathsWithPositions(ArrayList<Position> path,Datas mydata,LocationType op){
			for(int i=0;i<path.size();i++){
				Position p1=path.get(i);
				addPositionWithCoordinates(p1,mydata,op);		
			}
		}		
		
		//the speed of the given data is protected
		//the position will contain the time information
		//screen part of the position will be used to put the coordinates to pointsp
		private void addPositionWithCoordinates(Position p,Datas mydata,LocationType op){
			double x = 0,y = 0;
			//the time of the position p is not important as the speed of the device will be used
			if(op==LocationType.REAL || op==LocationType.RELATIVE) {
				x=p.getRealX();
				y=p.getRealY();
			}else if(op==LocationType.SCREEN) {
				x=p.getScreenX();
				y=p.getScreenY();
			}else {
				Lib.p("Option Different at Positionable!!!");
			}
			long timeDifference=0;
			double xa=0;
			double ya=0;
			double num;
			if(positionsLength() !=0){
				int lasttime=0;
				double lastposx=0;
				double lastposy=0;
				
				int lastpos=positionsLength()-1;
				if(op==LocationType.SCREEN) {
					lastposx=getPosition(lastpos).getScreenX();
					lastposy=getPosition(lastpos).getScreenY();
				}else if(op==LocationType.REAL || op==LocationType.RELATIVE){
					lastposx=getPosition(lastpos).getRealX();
					lastposy=getPosition(lastpos).getRealY();
				}else {
					Lib.p("Option Different at Positionable!!!");
				}
				
				lasttime=(int)(getPosition(lastpos).getTime());	
				timeDifference=p.getTime()-lasttime;
				
				//if the time difference is larger than 5 minutes dont project the coordinates or
				// if the data repeats itself ignore it
				if(timeDifference > mydata.getTimeDifference() || timeDifference==0) {
					return;
				}
				
				//double distance=Lib.screenDistance(lastposx, lastposy, xcalc, ycalc);
				double xdistance=Math.abs(lastposx-x);
				double ydistance=Math.abs(lastposy-y);
				
				for(int k=1;k<=timeDifference;k++){
					num=((double)k)/timeDifference;
					if(lastposx>=x && lastposy>=y){
						xa=lastposx-num*xdistance;
						ya=lastposy-num*ydistance;		
					}else if(lastposx>=x && lastposy<=y){
						xa=lastposx-num*xdistance;
						ya=lastposy+num*ydistance;
					}else if(lastposx<=x && lastposy>=y){
						xa=lastposx+num*xdistance;
						ya=lastposy-num*ydistance;
					}else{//lastposx<x %% lastposy<y
						xa=lastposx+num*xdistance;
						ya=lastposy+num*ydistance;
					}
					
					Position pCreated = null;
					if(op==LocationType.SCREEN) {
						pCreated=mydata.getPositionWithScreen(xa,ya);
					}else if(op==LocationType.REAL || op==LocationType.RELATIVE){
						pCreated=mydata.getPositionWithReal(xa, ya);
					}else {
						Lib.p("Option Different at Positionable!!!");
					}
					lasttime++;
					pCreated.setTime(lasttime);
					pointsp.add(pCreated);
					
				}//end of for
				
			}else{
				//this is only done if the arraylist is empty. Arraylist will be empty at the beginning of the
				//simulation only.
				/*
				Position newpos=mydata.getPositionWithScreen(p.getScreenX(), p.getScreenY());
				if(giventime!=-1) {
					newpos.setTime(giventime);
					//For UAv we need that as the queue never gets empty except for the first time
					//for nodes it gets empty but filled according to the read data.
				}
				*/
				pointsp.add(p);
			}
			
		}
		
		private void addPathWithSpeed(Position p,Datas mydata,LocationType op){
			double x = 0;
			double y=0;
			//the time of the position p is not important as the speed of the device will be used
			if(op==LocationType.REAL || op==LocationType.RELATIVE) {
				x=p.getRealX();
				y=p.getRealY();
			}else if(op==LocationType.SCREEN) {
				x=p.getScreenX();
				y=p.getScreenY();
			}else {
				Lib.p("Option Different at Positionable!!!");
			}
			double lastposx=0;
			double lastposy=0;
			double xa=0;
			double ya=0;
			double num;
			if(positionsLength() !=0){
				long lasttime=0;
				int lastpos=positionsLength()-1;
				if(op==LocationType.REAL || op==LocationType.RELATIVE) {
					lastposx=getPosition(lastpos).getRealX();
					lastposy=getPosition(lastpos).getRealY();
				}else if(op==LocationType.SCREEN) {
					lastposx=getPosition(lastpos).getScreenX();
					lastposy=getPosition(lastpos).getScreenY();
				}else {
					Lib.p("Option Different at Positionable!!!");
				}
				lasttime=(getPosition(lastpos).getTime());	
				
				
				int totaltime=0;
				double distance=0;
				if(op==LocationType.REAL) {
					distance=Lib.realdistance(lastposx, lastposy, x, y);
					totaltime=(int)Math.ceil(distance/speed);
				}else if(op==LocationType.SCREEN) {
					distance=Lib.screenDistance(lastposx, lastposy, x, y);
					totaltime=(int)Math.ceil(distance/screenspeed);
				}else if( op==LocationType.RELATIVE){
					distance=Lib.relativeDistance(lastposx, lastposy, x, y);
					totaltime=(int)Math.ceil(distance/screenspeed);
				}else {
					Lib.p("Option Different at Positionable!!!");
				}
				
				//double distance=Lib.relativeDistance(lastposx, lastposy, xreal, yreal);
				double xspeed=Math.abs(lastposx-x)/totaltime;
				double yspeed=Math.abs(lastposy-y)/totaltime;
				
				for(int k=1;k<=totaltime;k++){
					num=((double)k);
					if(lastposx>=x && lastposy>=y){
						xa=lastposx-num*xspeed;
						ya=lastposy-num*yspeed;		
					}else if(lastposx>=x && lastposy<=y){
						xa=lastposx-num*xspeed;
						ya=lastposy+num*yspeed;
					}else if(lastposx<=x && lastposy>=y){
						xa=lastposx+num*xspeed;
						ya=lastposy-num*yspeed;
					}else{//lastposx<x %% lastposy<y
						xa=lastposx+num*xspeed;
						ya=lastposy+num*yspeed;
					}
					
					Position pCreated=null;				
					if(op==LocationType.REAL || op==LocationType.RELATIVE) {
						pCreated=mydata.getPositionWithReal(xa,ya);
						if(xa >mydata.getMaxX() || xa < mydata.getMinX() || ya <mydata.getMinY() || ya > mydata.getMaxY()){
							xa=LibRouting.prec(xa, 8);
							ya=LibRouting.prec(ya, 8);
							if(xa >mydata.getMaxX() || xa < mydata.getMinX() || ya <mydata.getMinY() || ya > mydata.getMaxY()){
								Lib.p("This can not happen:addPathWithRealCoordinates for real cords at positionable");
								Lib.p("xa "+xa+" width "+mydata.getWidth()+" ya "+ya+" height "+mydata.getHeight());
								try{throw new Exception();}catch(Exception e) {e.printStackTrace();}
							}							
						}
					}else if(op==LocationType.SCREEN) {
						pCreated=mydata.getPositionWithScreen(xa,ya);
						if(xa >mydata.getWidth() || xa < 0 || ya <0 || ya > mydata.getHeight()){
							xa=LibRouting.prec(xa, 8);
							ya=LibRouting.prec(ya, 8);
							if(xa >mydata.getWidth() || xa < 0 || ya <0 || ya > mydata.getHeight()){
								Lib.p("This can not happen:addPathWithRealCoordinates for screen coords at positionable.java");
								Lib.p("xa "+xa+" width "+mydata.getWidth()+" ya "+ya+" height "+mydata.getHeight());
								try{throw new Exception();}catch(Exception e) {e.printStackTrace();}
							}							
						}
					}
					lasttime++;
					pCreated.setTime(lasttime);
					pointsp.add(pCreated);
				}//end of for
				
			}else{
				pointsp.add(p);
			}	
			//end of if
			
			if(op==LocationType.REAL || op==LocationType.RELATIVE) {
				x=p.getRealX();
				y=p.getRealY();
				if(x >mydata.getMaxX() || x < mydata.getMinX() || y <mydata.getMinY() || y > mydata.getMaxY()){
					Lib.p("This can not happen:addPathWithRealCoordinates");
				}	
			}else if(op==LocationType.SCREEN) {
				x=p.getScreenX();
				y=p.getScreenY();
				if(x >mydata.getWidth() || x < 0 || y <0 || y > mydata.getHeight()){
					Lib.p("This can not happen:addPathWithRealCoordinates for screen coords at positionable.java");
				}	
			}else {
				Lib.p("Option Different at Positionable!!!");
			}
				
		}
		
		
		
		/***************************************************/
		
		/**
		* for every time getScreenPosition or getRealPosition is called pointsiterator increased.
		* If we just want the current position we should call this method
		* 
		* @param int the current time is given
		* @return Position it returns the Position from the positions queue that has screen and real coordinates 
		*/				
		public Position getCurrentPositionWithTime(long giventime){
			Position returned=null;
			
			if(positionsLength()==0){
				setRouteFinished(true);
			}else if(getPosition(0).time==giventime)
			{
				returned=new Position(getPosition(0));
				setRouteFinished(false);				
				dequeuePosition();
			}	
			return returned;
		}
		
		public void calculateDistance() {
			if(getPreviousPosition()!=null && getCurrentPosition() !=null) {
				if(getData().getLoc()==LocationType.RELATIVE){
					double lat1=getPreviousPosition().getRealX();
					double lon1=getPreviousPosition().getRealY();
					
					double lat2=getCurrentPosition().getRealX();
					double lon2=getCurrentPosition().getRealY();
					addDistanceTravelled(Lib.relativeDistance(lat1, lon1, lat2, lon2));
				}else if(getData().getLoc()==LocationType.REAL){
					double lat1=getPreviousPosition().getRealX();
					double lon1=getPreviousPosition().getRealY();
					
					double lat2=getCurrentPosition().getRealX();
					double lon2=getCurrentPosition().getRealY();
					addDistanceTravelled(Lib.realdistance(lat1, lon1, lat2, lon2));
				}else {
					Lib.p("Unknown data location type is Positionable.java");
				}
			}
		}
		
		public Position getPosition(int time){
			if(pointsp.isEmpty()) {
				return null;
			}
			
			if(time==0) {
				return pointsp.peek();
			}else {
				Iterator<Position> iterator = pointsp.iterator();
				while(iterator.hasNext() && time>0){
				  iterator.next();
				  time--;
				}
				return (Position)iterator.next();
			}
			
		}
		
		/******************************************************/
		
		
		/**
		 * @return Distance traveled by UAV
		 */
		public double getDistanceTravelled(){
			return distancetravelled;
		}
		
		public void addDistanceTravelled(double d){
			distancetravelled=d+distancetravelled;
		}
		
		public Position getLastPosition(){
			return getPosition(pointsp.size()-1);
		}
		
		///writes the positions in the pointsp arraylist
		public void writeFile(String s){
			BufferedWriter bw=null;
			try{
				bw=new BufferedWriter(new FileWriter(s));
				for(int j=0;j<pointsp.size();j++){
					bw.write(	getPosition(j).getTime()+"\t"+ 
							getPosition(j).getScreenX()+"\t"+ 
							getPosition(j).getScreenY()+"\t"+
							getPosition(j).getRealX()+"\t"+
							getPosition(j).getRealY()
							);
				}
				bw.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		public final void clearPositions(){
			pointsp.clear();
		}
		
		public final Position dequeuePosition() {
			try {
				return pointsp.remove();
			}catch(Exception e) {
				e.printStackTrace();
			}
			return null;
			
		}
		
		public final int positionsLength(){
			return pointsp.size();
		}
		
		public void writePositions() {
			Lib.p("Writing positions in Positionable.java");
			for(int i=0;i<pointsp.size();i++) {
				Lib.p(getPosition(i).toString());
			}
		}
		
}
