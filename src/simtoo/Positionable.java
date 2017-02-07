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
		final int MAXHEIGHT,MAXWIDTH;
		private ArrayList<Position> pointsp;
		int pointsiterator;
		boolean routeFinished;

		public Positionable(int maxh,int maxw){
			r=new Random();
			//fillPositions();
			time=0;
			speed=1;
			screenspeed=10;
			MAXHEIGHT=maxh;
			MAXWIDTH=maxw;
			pointsp=new ArrayList<Position>();
			pointsiterator=0;
			routeFinished=false;
		}
		
		public boolean isRouteFinished(){
			return routeFinished;
		}
		
		public int getMAXWIDTH(){
			return MAXWIDTH;
		}
		
		public int getMAXHEIGHT(){
			return MAXHEIGHT;
		}
		
		public ArrayList<Position> getPoints(){
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
		
		public void fillRandomPositions(int numberOfPoints){
			double xgen=0;
			double ygen=0;
			int count=1;
			double xgenfirst=r.nextDouble()*MAXWIDTH;
			double ygenfirst=r.nextDouble()*MAXHEIGHT;
			addPathWithScreenCoordinates(xgenfirst,ygenfirst);
			while(count<numberOfPoints){
				xgen=r.nextDouble()*MAXWIDTH;
				ygen=r.nextDouble()*MAXHEIGHT;
				addPathWithScreenCoordinates(xgen,ygen);
				System.gc();
				count++;
			}
			addPathWithScreenCoordinates(xgenfirst,ygenfirst);
			//this one makes a loop 
		}
		
		public void addPathWithScreenCoordinates(double xcalc,double ycalc){
			if(positionsLength() !=0){
				int lastpos=positionsLength()-1;
				lastposx=pointsp.get(lastpos).getScreenX();
				lastposy=pointsp.get(lastpos).getScreenY();

				
				distance=Lib.distance(lastposx, lastposy, xcalc, ycalc);
				timecalc=(distance/screenspeed);
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
					
					
					addPositionWithScreen(xa,ya);
					
				
				}//end of for
				addPositionWithScreen(xcalc,ycalc);
			}//end of if
			else{
				addPositionWithScreen(xcalc,ycalc);			
			}
			
		}
		
		//since the area will be very small latitude and longitude may be used like cartesian coordinate system
		public void addPathWithRealCoordinates(double xreal,double yreal){
			if(positionsLength() !=0){
				int lastpos=pointsp.size()-1;
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
					
					
					addPositionWithReal(xa,ya);
					
				}//end of for
				addPositionWithReal(xreal,yreal);
			}//end of if
			else{
				addPositionWithReal(xreal,yreal);			
			}
			
		}
		
		//given screen coordinates it will add real position to positions array
		public void addPositionWithScreen(double xscreengiven,double yscreengiven){
			if(Double.isNaN(xscreengiven) || Double.isNaN(yscreengiven)){
				Lib.p("Positionable addPositionWithScreen one of the screen variables are NaN");
				Lib.p("screengivenx"+xscreengiven+"screengiveny"+yscreengiven);
				System.exit(-1);
			}
			double xrealgiven=Datas.convertToRealX(xscreengiven,MAXWIDTH);
			double yrealgiven=Datas.convertToRealY(yscreengiven,MAXHEIGHT);
			if(Double.isNaN(xrealgiven) || Double.isNaN(yrealgiven)){
				Lib.p("Positionable addPositionWithScreen one of the generated real variables are NaN");
				Lib.p("xrealgiven "+xrealgiven+" yrealgiven "+yrealgiven);
				System.exit(-1);
			}
			pointsp.add(new Position(positionsLength()+1,xscreengiven,yscreengiven,xrealgiven,yrealgiven));				
		}
		
		public void addPositionWithReal(double xrealgiven,double yrealgiven){
			if(Double.isNaN(xrealgiven) || Double.isNaN(yrealgiven)){
				Lib.p("Positionable addPositionWithReal one of the real variables are NaN");
				Lib.p("xrealgiven "+xrealgiven+" yrealgiven "+yrealgiven);
				System.exit(-1);
			}
			double xscreengiven=Datas.convertToScreenX(xrealgiven,MAXWIDTH);
			double yscreengiven=Datas.convertToScreenY(yrealgiven,MAXHEIGHT);
			if(Double.isNaN(xscreengiven) || Double.isNaN(yscreengiven)){
				Lib.p("Positionable addPositionWithReal one of the generated screen variables are NaN");
				Lib.p("screengivenx"+xscreengiven+"screengiveny"+yscreengiven);
				System.exit(-1);
			}
			pointsp.add(new Position(positionsLength()+1,xscreengiven,yscreengiven,xrealgiven,yrealgiven));	
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
			
			PointP p=null;
			if(pointsiterator>=positionsLength()){
				p = new PointP(pointsp.get(positionsLength()-1).getRealX(),
						pointsp.get(positionsLength()-1).getRealY());
			}else{
				p = new PointP(pointsp.get(pointsiterator).getRealX(),
						pointsp.get(pointsiterator).getRealY());
			}				
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
