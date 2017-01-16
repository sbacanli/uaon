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
		
		
		public Positionable(int maxh,int maxw){
			r=new Random();
			//fillPositions();
			time=0;
			speed=1;
			screenspeed=10;
			MAXHEIGHT=maxh;
			MAXWIDTH=maxw;
			pointsp=new ArrayList<Position>();
			
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
			if(pointsp.size() !=0){
				int lastpos=pointsp.size()-1;
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
			if(pointsp.size() !=0){
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
		public void addPositionWithScreen(double xscreen,double yscreen){
			double xreal=Datas.convertToRealX(xscreen);
			double yreal=Datas.convertToRealY(yscreen);
			pointsp.add(new Position(pointsp.size()+1,xscreen,yscreen,xreal,yreal));				
		}
		
		public void addPositionWithReal(double xreal,double yreal){
			double xscreen=Datas.convertToScreenX(xreal);
			double yscreen=Datas.convertToScreenY(yreal);
			pointsp.add(new Position(pointsp.size()+1,xscreen,yscreen,xreal,yreal));	
		}
			
		public PointP getScreenPosition(int time){
			if(pointsp==null)
			{
				Lib.p("Points Arraylist is null");
			}
			if(pointsp.isEmpty())
			{
				Lib.p("Points Arraylist is empty");
			}
			for(int i=0;i<pointsp.size();i++){
				if(pointsp.get(i).getTime()==time)
				{
					return new PointP(pointsp.get(i).getScreenX(),pointsp.get(i).getScreenY());
				}
			}	
			
			//last position will be given
			return pointsp.get(pointsp.size()-1).getScreenPoint();
		}
		
		public PointP getRealPosition(int time){
			if(pointsp==null)
			{
				Lib.p("Points Arraylist is null");
			}
			if(pointsp.isEmpty())
			{
				Lib.p("Points Arraylist is empty");
			}
			
			for(int i=0;i<pointsp.size();i++){
				if(pointsp.get(i).getTime()==time)
				{
					return new PointP(pointsp.get(i).getRealX(),pointsp.get(i).getRealY());
				}
			}
			//last position will be given
			return pointsp.get(pointsp.size()-1).getRealPoint();
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
		
		
	
}
