package simtoo;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

public class Positionable {

	
		public int nodeId;
		double speed;//   in terms of  m/sec
		double screenspeed;//in terms of pixels
		int time;
		Random r;
		int numRecords;
		int lastpos;
		double lastposx;
		double lastposy;
		double timecalc;
		double distance;
		
		double xa=0;
		double ya=0;
		double num=0;
		private ArrayList<Position> pointsp;
		int pointsiterator;
		boolean routeFinished;

		public Positionable(){
			r=new Random();
			//fillPositions();
			time=0;
			speed=1;
			screenspeed=10;
			pointsp=new ArrayList<Position>();
			pointsiterator=0;
			routeFinished=false;
		}
		
		public boolean isRouteFinished(){
			return routeFinished;
		}
		
		public ArrayList<Position> getPositions(){
			return pointsp;
		}
		
		public void setRealSpeed(double s){
			speed=s;
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
				int lastpos=positionsLength()-1;
				lastposx=pointsp.get(lastpos).getScreenX();
				lastposy=pointsp.get(lastpos).getScreenY();

				
				distance=Lib.distance(lastposx, lastposy, xcalc, ycalc);
				timecalc=(distance/screenspeed);// meter/sec 
				double xdistance=Math.abs(lastposx-xcalc);
				double ydistance=Math.abs(lastposy-ycalc);
				
				
				for(int k=1;k<=timecalc;k++){
					num=k/timecalc;
					if(lastposx>xcalc && lastposy>ycalc){
						xa=lastposx-num*xdistance;
						ya=lastposy-num*ydistance;		
					}else if(lastposx>xcalc && lastposy<ycalc){
						xa=lastposx-num*xdistance;
						ya=lastposy+num*ydistance;
					}else if(lastposx<xcalc && lastposy>ycalc){
						xa=lastposx+num*xdistance;
						ya=lastposy-num*ydistance;
					}else{//lastposx<x %% lastposy<y
						xa=lastposx+num*xdistance;
						ya=lastposy+num*ydistance;
					}
					
					Position p=mydata.getPositionWithScreen(xa,ya);
					addPosition(p);
					
				}//end of for
				
			}
			
			Position p=mydata.getPositionWithScreen(xcalc,ycalc);	
			addPosition(p);
			
		}
		
		//since the area will be very small latitude and longitude may be used like cartesian coordinate system
		public void addPathWithRealCoordinates(double xreal,double yreal,Datas mydata){
			if(positionsLength() !=0){
				int lastpos=positionsLength()-1;
				lastposx=pointsp.get(lastpos).getRealX();
				lastposy=pointsp.get(lastpos).getRealY();

				
				distance=Lib.realdistance(lastposx, lastposy, xreal, yreal);
				timecalc=(distance/speed);
				double xdistance=Math.abs(lastposx-xreal);
				double ydistance=Math.abs(lastposy-yreal);
				
				for(int k=1;k<=timecalc;k++){
					num=k/timecalc;
					if(lastposx>xreal && lastposy>yreal){
						xa=lastposx-num*xdistance;
						ya=lastposy-num*ydistance;		
					}else if(lastposx>xreal && lastposy<yreal){
						xa=lastposx-num*xdistance;
						ya=lastposy+num*ydistance;
					}else if(lastposx<xreal && lastposy>yreal){
						xa=lastposx+num*xdistance;
						ya=lastposy-num*ydistance;
					}else{//lastposx<x %% lastposy<y
						xa=lastposx+num*xdistance;
						ya=lastposy+num*ydistance;
					}
					
					
					Position p=mydata.getPositionWithReal(xa,ya);
					addPosition(p);
				}//end of for
				
			}//end of if
			
			Position p=mydata.getPositionWithReal(xreal,yreal);			
			addPosition(p);
		}
		
		public void addPosition(Position p){
			p.setTime(positionsLength()+1);
			pointsp.add(p);
		}
			
		public PointP getScreenPosition(){
			if(pointsp==null)
			{
				Lib.p("Points Arraylist is null on Positionable.java");
			}
			if(pointsp.isEmpty())
			{
				Lib.p("Points Arraylist is empty on Positionable.java");
			}
			
			if(pointsiterator<positionsLength()-1){
				routeFinished=false;
			}else{
				routeFinished=true;
			}
			
			if(pointsiterator>=positionsLength()){
				return new PointP(pointsp.get(positionsLength()-1).getScreenX(),
						pointsp.get(positionsLength()-1).getScreenY());
			}
			
			PointP p=new PointP(pointsp.get(pointsiterator).getScreenX(),
					pointsp.get(pointsiterator).getScreenY());
	
			pointsiterator++;
			return p;
		}
		
		public PointP getRealPosition(){
			if(pointsp==null)
			{
				Lib.p("Points Arraylist is null");
			}
			if(pointsp.isEmpty())
			{
				Lib.p("Points Arraylist is empty");
			}
			
			if(pointsiterator<positionsLength()-1){
				routeFinished=false;
			}else{
				routeFinished=true;
			}
			
			PointP p=null;
			if(pointsiterator>=positionsLength()){
				return new PointP(pointsp.get(positionsLength()-1).getRealX(),
						pointsp.get(positionsLength()-1).getRealY());
			}
			p = new PointP(pointsp.get(pointsiterator).getRealX(),pointsp.get(pointsiterator).getRealY());
				
			pointsiterator++;
			return p;
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
				pointsp.clear();
				pointsiterator=0;
			}
		}
		
		public int positionsLength(){
			return pointsp.size();
		}
	
}
