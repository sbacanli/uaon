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
		private double timecalc;
		private double distancetravelled;
		
		private double xa=0;
		private double ya=0;
		private double num=0;
		private ArrayList<Position> pointsp;
		private int pointsiterator;
		private boolean routeFinished;
		private Comparator<Position> positionComparator;
		private boolean isGPS;
		private boolean isOnTheMap;
		int positionsTraced;
		
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
			pointsiterator=0;
			setRouteFinished(false);
			distancetravelled=0;
			
			positionComparator = new Comparator<Position>()
	        {
	            public int compare(Position u1, Position u2)
	            {
	                return u1.getTime() - u2.getTime();
	            }
	        };	 	
	        isOnTheMap=false;
	        positionsTraced=0;
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
		
		public void addPathsWithRealCoordinatesAll(ArrayList<PointP> path,Datas mydata){
			for(int i=0;i<path.size();i++){
				PointP p=path.get(i);
				addPathWithRealCoordinates(p.getX(),p.getY(),mydata);
			}
		}
		
		public void addPathsWithScreenCoordinatesAll(ArrayList<PointP> path,Datas mydata){
			for(int i=0;i<path.size();i++){
				PointP p=path.get(i);
				addPathWithScreenCoordinates(p.getX(),p.getY(),mydata);
			}
		}
		
		public void addPathWithScreenCoordinates(double xcalc,double ycalc,Datas mydata){
			if(positionsLength() !=0){
				int lasttime=0;
				int lastpos=positionsLength()-1;
				lastposx=pointsp.get(lastpos).getScreenX();
				lastposy=pointsp.get(lastpos).getScreenY();
				lasttime=pointsp.get(lastpos).getTime();	
				
				double distance=Lib.screenDistance(lastposx, lastposy, xcalc, ycalc);
				timecalc=(distance/screenspeed);// meter/ meter/sec =sec 
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
				
			}
			
			if(xcalc >mydata.getWidth() || xcalc < 0 || ycalc <0 || ycalc > mydata.getHeight()){
				Lib.p("This can not happen");
				Lib.p(xa+" "+ya+" "+mydata.getWidth()+" "+mydata.getHeight()+" "+xcalc+" "+ycalc+" "+lastposx+" "+lastposy);

			}else{
				Position p=mydata.getPositionWithScreen(xcalc,ycalc);	
				p.setTime(0);
				pointsp.add(p);
			}
		}
		
		//since the area will be very small latitude and longitude may be used like cartesian coordinate system
		public void addPathWithRealCoordinates(double xreal,double yreal,Datas mydata){
			if(positionsLength() !=0){
				int lasttime=0;
				int lastpos=positionsLength()-1;
				lastposx=pointsp.get(lastpos).getRealX();
				lastposy=pointsp.get(lastpos).getRealY();
				lasttime=pointsp.get(lastpos).getTime();	
				
				double distance=Lib.relativeDistance(lastposx, lastposy, xreal, yreal);
				timecalc=(distance/speed);
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
				
			}//end of if
			
			if(xreal >mydata.getMaxX() || xreal < mydata.getMinX() || yreal <mydata.getMinY() || yreal > mydata.getMaxY()){
				Lib.p("This can not happen:addPathWithRealCoordinates");
			}else{
				Position p=mydata.getPositionWithReal(xreal,yreal);		
				p.setTime(0);
				pointsp.add(p);
			}
			
			
		}		
			
		public PointP getScreenPosition(){
			if(pointsiterator<positionsLength()){
				setRouteFinished(false);
			}else{
				setRouteFinished(true);
			}
			
			if(pointsp==null)
			{
				Lib.p("Points Arraylist is null on Positionable.java");
			}
			if(pointsp.isEmpty())
			{
				Lib.p("Points Arraylist is empty on Positionable.java");
				Lib.p("Node id: "+nodeId+" ");
				//some reports
				Lib.p("--------------------------");
				Lib.p("Report starts");
				Lib.p("ID of node is "+nodeId+" ");
				Lib.p("is route finished "+routeFinished);
				Lib.p("points iterator "+pointsiterator);
				Lib.p("size of arrayList "+positionsLength());
				Lib.p("--------------------------");
				System.exit(1);
			}
			
			if(pointsiterator>=positionsLength()){
				return pointsp.get(positionsLength()-1).getScreenPoint();
			}
			
			pointsiterator++;
			
			
			if(pointsiterator>1){
				double lat1=pointsp.get(pointsiterator-1).getRealX();
				double lon1=pointsp.get(pointsiterator-1).getRealY();
				
				double lat2=pointsp.get(pointsiterator-2).getRealX();
				double lon2=pointsp.get(pointsiterator-2).getRealY();
				//Lib.p("distrance Positionable");
				if(!isGPS){
					addDistanceTravelled(Lib.relativeDistance(lat1, lon1, lat2, lon2));
				}else{
					addDistanceTravelled(Lib.realdistance(lat1, lon1, lat2, lon2));
				}
			}
			return pointsp.get(pointsiterator-1).getScreenPoint();
		}

		
		//for every time getScreenPosition or getRealPosiiton is called pointsiterator increased.
		//If we just want the current position we should call this method		
		public Position getCurrentPosition(){
			if(pointsiterator==0){
				return pointsp.get(0);
			}
			if(pointsiterator>=positionsLength()){
				return pointsp.get(positionsLength()-1);
			}
			return pointsp.get(pointsiterator-1);
		}
		
		/***************************************************/
		public PointP getScreenPositionWithTime(int giventime){
			PointP returned=null;
			
			if(pointsiterator<positionsLength()){
				setRouteFinished(false);
			}else{
				setRouteFinished(true);
				return pointsp.get(positionsLength()-1).getScreenPoint();
			}
			
			if(pointsp==null)
			{
				Lib.p("Points Arraylist is null on Positionable.java");
			}
			if(pointsp.isEmpty())
			{
				Lib.p("Points Arraylist is empty on Positionable.java");
				Lib.p("Node id: "+nodeId+" ");
				//some reports
				Lib.p("--------------------------");
				Lib.p("Report starts");
				Lib.p("ID of node is "+nodeId+" ");
				Lib.p("is route finished "+routeFinished);
				Lib.p("points iterator "+pointsiterator);
				Lib.p("size of arrayList "+positionsLength());
				Lib.p("--------------------------");
				System.exit(1);
			}
			
			if(pointsp.get(pointsiterator).time == giventime){
				returned=pointsp.get(pointsiterator).getScreenPoint();
				pointsiterator++;
			}else{
				returned=null;
			}
			
			
			
			if(pointsiterator>1){
				double lat1=pointsp.get(pointsiterator-1).getRealX();
				double lon1=pointsp.get(pointsiterator-1).getRealY();
				
				double lat2=pointsp.get(pointsiterator-2).getRealX();
				double lon2=pointsp.get(pointsiterator-2).getRealY();
				
				//Lib.p("distrance Positionable");
				if(!isGPS){
					addDistanceTravelled(Lib.relativeDistance(lat1, lon1, lat2, lon2));
				}else{
					addDistanceTravelled(Lib.realdistance(lat1, lon1, lat2, lon2));
				}
			}
			
			return returned;
		}

		
		//for every time getScreenPosition or getRealPosiiton is called pointsiterator increased.
		//If we just want the current position we should call this method		
		public Position getCurrentPositionWithTime(int giventime){
			Position returned=null;
			
			if(positionsLength()==0){
				
				setRouteFinished(true);
				//Lib.p("routefinished for nodeId "+nodeId);
			}else{
				/*
				if(giventime==getPosition(positionsLength()-1).time){
					setRouteFinished(true);
					Lib.p("HEY");
					isOnTheMap=true;
					return pointsp.get(positionsLength()-1);
					
					
				}*/		
				if(isOnTheMap){
					returned=new Position(getPosition(0));
					pointsp.remove(0);
					setRouteFinished(false);
					//Lib.p("route not finished for nodeId "+nodeId);
				}else{
					if(getPosition(0).time==giventime)
					{
						returned=new Position(getPosition(0));
						pointsp.remove(0);
						setRouteFinished(false);
					}else{
						isOnTheMap=false;
					}
				}
			}
				
			return returned;
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
			if(!pointsp.isEmpty()){
				pointsp.subList(0,pointsp.size()-1).clear();
				pointsiterator=0;
			}
		}
		
		public int positionsLength(){
			return pointsp.size();
		}
	
}
