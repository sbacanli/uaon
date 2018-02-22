package simtoo;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Positionable {

	
		private int nodeId;
		private double speed;//   in terms of  m/sec
		private double screenspeed;//in terms of pixels
		
		private double lastposx;
		private double lastposy;
		private double distancetravelled;
		
		private ArrayList<Position> pointsp;
		private boolean routeFinished;
		private Comparator<Position> positionComparator;
		private boolean isGPS;
		
		public Positionable(int nodeId,boolean isGPS){

			//fillPositions();
			this.isGPS=isGPS;
			this.nodeId=nodeId;
			if(nodeId==0){
				Lib.p("NodeId is 0");
				System.exit(-1);
			}

			speed=1;//used in random mobility, will be changed if not random mobility
			screenspeed=10;//used in random mobility, will be changed if not random mobility
			pointsp=new ArrayList<Position>();
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
		
		public boolean isRouteFinished(){
			return routeFinished;
		}
		
		public void setRouteFinished(boolean a){
			routeFinished=a;
		}
		
		public List<Position> getPositions(){
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
		
		public void setPoints(ArrayList<Position> arr){
			pointsp=arr;
		}
		
		public void addPathsWithPoints(ArrayList<PointP> path,Datas mydata,String op){
			for(int i=0;i<path.size();i++){
				PointP p=path.get(i);
				if(op.equals("real")) {
					addPathWithRealCoordinates(p.getX(),p.getY(),mydata);
				}else if(op.equals("screen")) {
					addPathWithScreenCoordinates(p.getX(),p.getY(),mydata);
				}else {
					Lib.p("No such option for addPathsWithPoints");
				}
				
			}
		}
		
		//option will be screen or real
		// positions are added based on the time data so the speed is constant
		//according to the distance and time between the positions
		public void addPathsWithPositions(ArrayList<Position> path,Datas mydata,String op){
			for(int i=0;i<path.size();i++){
				Position p1=path.get(i);
				if(op.equals("real")) {
					addPathWithRealCoordinatesWithPos(p1,mydata);
				}else if(op.equals("screen")) {
					addPathWithScreenCoordinatesWithPos(p1,mydata);
				}else {
					Lib.p("No such option for addPathsWithPositions");
				}
				
			}
		}		
		
		private void addPathWithScreenCoordinates(double xcalc,double ycalc,Datas mydata){
			double xa=0;
			double ya=0;
			long timecalc;
			double num;
			if(positionsLength() !=0){
				int lasttime=0;
				int lastpos=positionsLength()-1;
				lastposx=pointsp.get(lastpos).getScreenX();
				lastposy=pointsp.get(lastpos).getScreenY();
				lasttime=(int)(pointsp.get(lastpos).getTime());	
				
				double distance=Lib.screenDistance(lastposx, lastposy, xcalc, ycalc);
				timecalc=(long) (distance/screenspeed);// meter/ meter/sec =sec 
				double xdistance=Math.abs(lastposx-xcalc);
				double ydistance=Math.abs(lastposy-ycalc);
				
				
				for(int k=1;k<=timecalc;k++){
					num=((double)k)/timecalc;
					if(lastposx>=xcalc && lastposy>=ycalc){
						xa=lastposx-num*xdistance;
						ya=lastposy-num*ydistance;		
					}else if(lastposx>=xcalc && lastposy<=ycalc){
						xa=lastposx-num*xdistance;
						ya=lastposy+num*ydistance;
					}else if(lastposx<=xcalc && lastposy>=ycalc){
						xa=lastposx+num*xdistance;
						ya=lastposy-num*ydistance;
					}else{//lastposx<x %% lastposy<y
						xa=lastposx+num*xdistance;
						ya=lastposy+num*ydistance;
					}
					
					Position p=mydata.getPositionWithScreen(xa,ya);
					lasttime++;
					p.setTime(lasttime);
					pointsp.add(p);
					
				}//end of for
				
			}else{
				Position p=mydata.getPositionWithScreen(xcalc,ycalc);	
				p.setTime(0);
				pointsp.add(p);
			}
			
			if(xcalc >mydata.getWidth() || xcalc < 0 || ycalc <0 || ycalc > mydata.getHeight()){
				Lib.p("This can not happen");
				Lib.p(xa+" "+ya+" "+mydata.getWidth()+" "+mydata.getHeight()+" "+xcalc+" "+ycalc+" "+lastposx+" "+lastposy);
			}
		}
		
		//since the area will be very small latitude and longitude may be used like cartesian coordinate system
		private void addPathWithRealCoordinates(double xreal,double yreal,Datas mydata){
			double xa=0;
			double ya=0;
			long timecalc;
			double num;
			if(positionsLength() !=0){
				long lasttime=0;
				int lastpos=positionsLength()-1;
				lastposx=pointsp.get(lastpos).getRealX();
				lastposy=pointsp.get(lastpos).getRealY();
				lasttime=(pointsp.get(lastpos).getTime());	
				
				double distance=Lib.relativeDistance(lastposx, lastposy, xreal, yreal);
				timecalc=(long) (distance/speed);
				double xdistance=Math.abs(lastposx-xreal);
				double ydistance=Math.abs(lastposy-yreal);
				
				for(int k=1;k<=timecalc;k++){
					num=((double)k)/timecalc;
					if(lastposx>=xreal && lastposy>=yreal){
						xa=lastposx-num*xdistance;
						ya=lastposy-num*ydistance;		
					}else if(lastposx>=xreal && lastposy<=yreal){
						xa=lastposx-num*xdistance;
						ya=lastposy+num*ydistance;
					}else if(lastposx<=xreal && lastposy>=yreal){
						xa=lastposx+num*xdistance;
						ya=lastposy-num*ydistance;
					}else{//lastposx<x %% lastposy<y
						xa=lastposx+num*xdistance;
						ya=lastposy+num*ydistance;
					}
					
					
					Position p=mydata.getPositionWithReal(xa,ya);
					lasttime++;
					p.setTime(lasttime);
					pointsp.add(p);
				}//end of for
				
			}else{
				Position p=mydata.getPositionWithReal(xreal,yreal);		
				p.setTime(0);
				pointsp.add(p);
			}
			//end of if
			
			if(xreal >mydata.getMaxX() || xreal < mydata.getMinX() || yreal <mydata.getMinY() || yreal > mydata.getMaxY()){
				Lib.p("This can not happen:addPathWithRealCoordinates");
			}			
		}				
		
		//the position will contain the time information
		//screen part of the position will be used to put the coordinates to pointsp
		private void addPathWithScreenCoordinatesWithPos(Position p,Datas mydata){
			long timeDifference=0;
			double xcalc=p.getScreenX();
			double ycalc=p.getScreenY();
			
			double xa=0;
			double ya=0;
			double num;
			if(positionsLength() !=0){
				int lasttime=0;
				int lastpos=positionsLength()-1;
				lastposx=pointsp.get(lastpos).getScreenX();
				lastposy=pointsp.get(lastpos).getScreenY();
				lasttime=(int)(pointsp.get(lastpos).getTime());	
				timeDifference=p.getTime()-lasttime;
				
				
				//double distance=Lib.screenDistance(lastposx, lastposy, xcalc, ycalc);
				double xdistance=Math.abs(lastposx-xcalc);
				double ydistance=Math.abs(lastposy-ycalc);
				
				for(int k=1;k<=timeDifference;k++){
					num=((double)k)/timeDifference;
					if(lastposx>=xcalc && lastposy>=ycalc){
						xa=lastposx-num*xdistance;
						ya=lastposy-num*ydistance;		
					}else if(lastposx>=xcalc && lastposy<=ycalc){
						xa=lastposx-num*xdistance;
						ya=lastposy+num*ydistance;
					}else if(lastposx<=xcalc && lastposy>=ycalc){
						xa=lastposx+num*xdistance;
						ya=lastposy-num*ydistance;
					}else{//lastposx<x %% lastposy<y
						xa=lastposx+num*xdistance;
						ya=lastposy+num*ydistance;
					}
					
					Position pCreated=mydata.getPositionWithScreen(xa,ya);
					lasttime++;
					pCreated.setTime(lasttime);
					pointsp.add(pCreated);
					
				}//end of for
				
			}else{
				//this is only done if the arraylist is empty. Arraylist will be empty at the beginning of the
				//simulation only.
				p=mydata.getPositionWithScreen(p.getScreenX(), p.getScreenY());
				p.setTime(mydata.getMinTime());
				pointsp.add(p);
			}
			
			if(xcalc >mydata.getWidth() || xcalc < 0 || ycalc <0 || ycalc > mydata.getHeight()){
				Lib.p("This can not happen");
				Lib.p(xa+" "+ya+" "+mydata.getWidth()+" "+mydata.getHeight()+" "+xcalc+" "+ycalc+" "+lastposx+" "+lastposy);

			}
		}
		
		//the position will contain the time information
		//real part of the position will be used to put the coordinates to pointsp
		private void addPathWithRealCoordinatesWithPos(Position p,Datas mydata){
			double xreal=p.getRealX();
			double yreal=p.getRealY();
			double xa=0;
			double ya=0;
			double num;
			if(positionsLength() !=0){
				long lasttime=0;
				int lastpos=positionsLength()-1;
				lastposx=pointsp.get(lastpos).getRealX();
				lastposy=pointsp.get(lastpos).getRealY();
				lasttime=(pointsp.get(lastpos).getTime());	
				long timeDifference=p.time-lasttime;
				
				
				//double distance=Lib.relativeDistance(lastposx, lastposy, xreal, yreal);
				double xdistance=Math.abs(lastposx-xreal);
				double ydistance=Math.abs(lastposy-yreal);
				
				for(int k=1;k<=timeDifference;k++){
					num=((double)k)/timeDifference;
					if(lastposx>=xreal && lastposy>=yreal){
						xa=lastposx-num*xdistance;
						ya=lastposy-num*ydistance;		
					}else if(lastposx>=xreal && lastposy<=yreal){
						xa=lastposx-num*xdistance;
						ya=lastposy+num*ydistance;
					}else if(lastposx<=xreal && lastposy>=yreal){
						xa=lastposx+num*xdistance;
						ya=lastposy-num*ydistance;
					}else{//lastposx<x %% lastposy<y
						xa=lastposx+num*xdistance;
						ya=lastposy+num*ydistance;
					}
					
					Position pCreated=mydata.getPositionWithReal(xa,ya);
					lasttime++;
					pCreated.setTime(lasttime);
					pointsp.add(pCreated);
				}//end of for
				
			}else{
				p=mydata.getPositionWithReal(p.getRealX(), p.getRealY());
				p.setTime(mydata.getMinTime());
				pointsp.add(p);
			}	
			//end of if
			
			if(xreal >mydata.getMaxX() || xreal < mydata.getMinX() || yreal <mydata.getMinY() || yreal > mydata.getMaxY()){
				Lib.p("This can not happen:addPathWithRealCoordinates");
			}		
		}
		
		
		
		/***************************************************/
		//for every time getScreenPosition or getRealPosiiton is called pointsiterator increased.
		//If we just want the current position we should call this method		
		public Position getCurrentPositionWithTime(long giventime){
			Position returned=null;
			
			if(positionsLength()==0){
				setRouteFinished(true);
			}else if(getPosition(0).time==giventime)
			{
				returned=new Position(getPosition(0));
				setRouteFinished(false);
				calculateDistance();
				pointsp.remove(0);
			}	
			return returned;
		}
		
		public void calculateDistance() {
			if(!isGPS){
				double lat1=getPosition(0).getRealX();
				double lon1=getPosition(0).getRealY();
				
				double lat2=getPosition(1).getRealX();
				double lon2=getPosition(1).getRealY();
				addDistanceTravelled(Lib.relativeDistance(lat1, lon1, lat2, lon2));
			}else{
				double lat1=getPosition(0).getRealX();
				double lon1=getPosition(0).getRealY();
				
				double lat2=getPosition(1).getRealX();
				double lon2=getPosition(1).getRealY();
				addDistanceTravelled(Lib.realdistance(lat1, lon1, lat2, lon2));
			}
		}
		
		public Position getPosition(int t){
			return pointsp.get(t);
		}
		
		/******************************************************/
		
		
		public double getDistanceTravelled(){
			return distancetravelled;
		}
		
		public void addDistanceTravelled(double d){
			distancetravelled+=d;
		}
		
		public Position getLastPosition(){
			return pointsp.get(positionsLength()-1);
		}
		
		///writes the positions in the pointsp arraylist
		public void writeFile(String s){
			BufferedWriter bw=null;
			try{
				bw=new BufferedWriter(new FileWriter(s));
				for(int j=0;j<pointsp.size();j++){
					bw.write(	pointsp.get(j).getTime()+"\t"+ 
								pointsp.get(j).getScreenX()+"\t"+ 
								pointsp.get(j).getScreenY()+"\t"+
								pointsp.get(j).getRealX()+"\t"+
								pointsp.get(j).getRealY()
							);
				}
				bw.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		public void clearPositions(){
			pointsp.clear();
			/*
			if(!pointsp.isEmpty()){
				pointsp.subList(0,pointsp.size()-1).clear();
			}
			//*/
		}
		
		public Position dequeuePosition() {
			//Position p=new Position(getPosition(0));
			try {
			return pointsp.remove(0);
			}catch(Exception e) {
				e.printStackTrace();
				//System.exit();
			}
			return null;
			
		}
		
		public int positionsLength(){
			return pointsp.size();
		}
		
		public void writePositions() {
			Lib.p("Writing positions in Positionable.java");
			for(int i=0;i<pointsp.size();i++) {
				Lib.p(getPosition(i).toString());
			}
		}
		
}
