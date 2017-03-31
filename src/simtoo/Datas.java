package simtoo;

import java.util.*;
import java.io.*;

public class Datas {

	Random r;
	int linenumlimit=0;
	static int widthOfScreen;
	static int heightOfScreen;
	ArrayList<ArrayList<Double>> arr;
	double minx;
	double miny;
	double maxy;
	double maxx;
	
	
	public Datas(int h,int w){
		linenumlimit=1000;
		//width and height of the screen
		widthOfScreen=w;
		heightOfScreen=h;
		
		r=new Random();
		arr=new ArrayList<ArrayList<Double>>();
		minx=0;
		miny=0;
		maxx=0;
		maxy=0;
		
	}
	
	public ArrayList<PointP> fillRandomPositions(int numberOfPoints){
		ArrayList<PointP> pts=new ArrayList<PointP>();
		
		double xgen=0;
		double ygen=0;
		int count=1;
		double xgenfirst=(r.nextDouble()*(maxx-minx))+minx;
		double ygenfirst=(r.nextDouble()*(maxy-miny))+miny;
		pts.add(new PointP(xgenfirst,ygenfirst));
		while(count<numberOfPoints){
			xgen=(r.nextDouble()*(maxx-minx))+minx;
			ygen=(r.nextDouble()*(maxy-miny))+miny;
			pts.add(new PointP(xgen,ygen));
			count++;
		}
		pts.add(new PointP(xgenfirst,ygenfirst));
		//this one makes a loop 
		
		return pts;
	}
	
	//*
	
	public void calculateMaxesByProcessing(String folderName){
		File f=new File(folderName);
		File[] files=f.listFiles();
		for(int i=0;i<files.length;i++){
			readData(files[i].getPath());
		}
	}
	
	
	private void readData(String fname){
		BufferedReader br=null;
		String line=null;

		try{
			br=new BufferedReader(new FileReader(new File(fname)));
			System.out.println(fname);
			while( (line=br.readLine()) !=null ){

				StringTokenizer st=new StringTokenizer(line," ");
				double time=Double.parseDouble(st.nextToken());
				double xcord=Double.parseDouble(st.nextToken());
				double ycord=Double.parseDouble(st.nextToken());

				
				if(xcord>maxx){
					maxx=xcord;
				}
				if(ycord>maxy){
					maxy=ycord;
				}
				if(xcord<minx){
					minx=xcord;
				}
				if(ycord<miny){
					miny=ycord;
				}
				
			}
			
		}catch(Exception e){
			Lib.p(e.toString());
		}
		
	}
	
	//*/
	
	public void calculateMaxesForScreen(){
		miny=0;
		minx=0;
		maxx=widthOfScreen;
		maxy=heightOfScreen;
	}
	
	//folder name for the dataset should be given
	public void calculateMaxes(String fname){
		calculateMaxesByProcessing(fname);
	}
	
	private void readMaxes(String fname){
		//the maxes file does not change while running
				//if it is calculated before no need to calculate again	
				BufferedReader br=null;
				String line=null;
				int i=0;
				try{
					br=new BufferedReader(new FileReader(new File(fname)));
					while( (line=br.readLine()) !=null ){
						StringTokenizer st=new StringTokenizer(line," ");
						st.nextToken();
						st.nextToken();
						if(i==0){
							//minx
							minx=Double.parseDouble(st.nextToken());
						}else if(i==1){
							//miny
							miny=Double.parseDouble(st.nextToken());
						}else if(i==2){
							//maxX
							maxx=Double.parseDouble(st.nextToken());
						}else{
							//maxY
							maxy=Double.parseDouble(st.nextToken());
						}
						
						i++;
					}//enf of while check
					br.close();
				}catch(Exception e){
					Lib.p(e.toString());
				}
	}
	
	
	//reads the real data for the node position to Position ArrayList
	//the data structure will be like that
	//timeAsNumberofSeconds Xcord YCord
	public ArrayList<Position> readRealDataForNode(String fname){
		BufferedReader br=null;
		String line=null;
		int i=0;
		ArrayList<Position> arrposition=new ArrayList<Position>();
		try{
			br=new BufferedReader(new FileReader(new File(fname)));
			while( (line=br.readLine()) !=null ){
				StringTokenizer st=new StringTokenizer(line," ");
				double time=Double.parseDouble(st.nextToken());
				double xcord=Double.parseDouble(st.nextToken());
				double ycord=Double.parseDouble(st.nextToken());
				
				if(i>0){
					
					int prevtime=arrposition.get(arrposition.size()-1).getTime();
					double prevxcord=arrposition.get(arrposition.size()-1).getRealX();
					double prevycord=arrposition.get(arrposition.size()-1).getRealY();
				
					int differenceTime=(int)time-prevtime;
					double differencex=(xcord-prevxcord)/differenceTime;
					double differencey=(ycord-prevycord)/differenceTime;
					
					double realy=0;
					double realx=0;
					
					for(int h=1;h<differenceTime;h++){
						if(prevxcord<xcord){
							realx=prevxcord+h*differencex;
						}else{
							realx=prevxcord-h*differencex;
						}
						
						if(prevycord<ycord){
							realy=prevycord+h*differencey;
						}else{
							realy=prevycord-h*differencey;
						}
						
						double calcx=convertToScreenX(realx);
						double calcy=convertToScreenY(realy);
						
						arrposition.add(new Position(
										prevtime+h*differenceTime,// updated time
										calcx,calcy, //screen coordinates
										realx,	//real X coordinate
										realy   //real Y coordinate
										));
					
					}//end of for
				
				}//end of if
				double calcx=convertToScreenX(xcord);
				double calcy=convertToScreenY(ycord);
				arrposition.add(new Position((int)time,calcx,calcy,xcord,ycord));
				i++;
			}//end of while
			br.close();
		}catch(Exception e){
			Lib.p(e.toString()+"\n\n problem in reading file "+fname);
			Lib.p(e.getCause().toString());
		}
		return arrposition;
	}	
	
	public double convertToScreenX(double geoX){
		if(Double.isNaN(geoX)){
			Lib.p("ERROR:Datas' convertToScreenX is NaN");
			System.exit(-1);
		}
		return (geoX * (double)(widthOfScreen) )/(maxx-minx);
	}
	
	public double convertToScreenY(double geoY){
		if(Double.isNaN(geoY)){
			Lib.p("ERROR:Datas' convertToScreenY is NaN");
			System.exit(-1);
		}
		return (geoY * (double)(heightOfScreen) )/(maxy-miny);
	}
	
	public double convertToRealX(double screenX){
		if(Double.isNaN(screenX)){
			Lib.p("ERROR:Datas' convertToRealX is NaN");
			System.exit(-1);
		}
		
		return (screenX * (maxx-minx) )/(double)(widthOfScreen);
	}
	
	//given screen coordinates it will return a position whose time is -1
	public Position getPositionWithScreen(double xscreengiven,double yscreengiven){
		if(Double.isNaN(xscreengiven) || Double.isNaN(yscreengiven)){
			Lib.p("Positionable addPositionWithScreen one of the screen variables are NaN");
			Lib.p("screengivenx"+xscreengiven+"screengiveny"+yscreengiven);
			System.exit(-1);
		}
		double xrealgiven=convertToRealX(xscreengiven);
		double yrealgiven=convertToRealY(yscreengiven);
		if(Double.isNaN(xrealgiven) || Double.isNaN(yrealgiven)){
			Lib.p("Positionable addPositionWithScreen one of the generated real variables are NaN");
			Lib.p("xrealgiven "+xrealgiven+" yrealgiven "+yrealgiven);
			System.exit(-1);
		}
		return new Position(-1,xscreengiven,yscreengiven,xrealgiven,yrealgiven);				
	}
	
	//gien real coordinates it will return a position whose time is -1
	public Position getPositionWithReal(double xrealgiven,double yrealgiven){
		if(Double.isNaN(xrealgiven) || Double.isNaN(yrealgiven)){
			Lib.p("Positionable addPositionWithReal one of the real variables are NaN");
			Lib.p("xrealgiven "+xrealgiven+" yrealgiven "+yrealgiven);
			System.exit(-1);
		}
		double xscreengiven=convertToScreenX(xrealgiven);
		double yscreengiven=convertToScreenY(yrealgiven);
		if(Double.isNaN(xscreengiven) || Double.isNaN(yscreengiven)){
			Lib.p("Positionable addPositionWithReal one of the generated screen variables are NaN");
			Lib.p("screengivenx"+xscreengiven+"screengiveny"+yscreengiven);
			System.exit(-1);
		}
		return new Position(-1,xscreengiven,yscreengiven,xrealgiven,yrealgiven);	
	}
	
	public double convertToRealY(double screenY){
		if(Double.isNaN(screenY)){
			Lib.p("ERROR:Datas' convertToRealY is NaN");
			System.exit(-1);
		}
		
		return (screenY * (maxy-miny) )/(double)(heightOfScreen);
	}
	/*
	public ArrayList<Position> readRealDataForNodeDateFormatted(String fname){
		BufferedReader br=null;
		String line=null;
		int i=0;
		double maxXCord=-99999;
		double minXCord=99999;
		double maxYCord=-99999;
		double minYCord=99999;
		
		ArrayList<Position> arrposition=new ArrayList<Position>();
		try{
			br=new BufferedReader(new FileReader(new File(fname)));
			while( (line=br.readLine()) !=null ){
				StringTokenizer st=new StringTokenizer(line," ");
				double nodeid=Integer.parseInt(st.nextToken());
				String date=st.nextToken();
				double xcord=Double.parseDouble(st.nextToken());
				double ycord=Double.parseDouble(st.nextToken());
				
				if(xcord>maxXCord){
					maxXCord=xcord;
				}
				if(ycord>maxYCord){
					maxYCord=xcord;
				}
				if(xcord<minXCord){
					minXCord=xcord;
				}
				if(ycord<minYCord){
					minYCord=xcord;
				}
				
				
				if(i>0){
					
					int prevtime=arrposition.get(arrposition.size()-1).getTime();
					double prevxcord=arrposition.get(arrposition.size()-1).getRealX();
					double prevycord=arrposition.get(arrposition.size()-1).getRealY();
				
					int differenceTime=(int)time-prevtime;
					double differencex=(xcord-prevxcord)/differenceTime;
					double differencey=(ycord-prevycord)/differenceTime;
					
					double realy=0;
					double realx=0;
					
					for(int h=1;h<differenceTime;h++){
						if(prevxcord<xcord){
							realx=prevxcord+h*differencex;
						}else{
							realx=prevxcord-h*differencex;
						}
						
						if(prevycord<ycord){
							realy=prevycord+h*differencey;
						}else{
							realy=prevycord-h*differencey;
						}
						
						double calcx=convertToScreenX(realx);
						double calcy=convertToScreenY(realy);
						
						arrposition.add(new Position(
										prevtime+h*differenceTime,// updated time
										calcx,calcy, //screen coordinates
										realx,	//real X coordinate
										realy   //real Y coordinate
										));
					
					}//end of for
				
				}//end of if
				double calcx=convertToScreenX(xcord);
				double calcy=convertToScreenY(ycord);
				arrposition.add(new Position((int)time,calcx,calcy,xcord,ycord));
				i++;
			}//end of while
			br.close();
		}catch(Exception e){
			Lib.p(e.toString()+"\n\n problem in reading file "+fname);
			e.printStackTrace();
			System.out.println(e.getCause());
		}
		return arrposition;
	}
	*/
	
	/*
	private static double convertToScreen(double screendifference,double geodifference,double geo){
		return (geo * (screendifference) )/(geodifference);
	}
	*/
	
	public ArrayList<PointP> dataLine(int line){
		
		if(line>linenumlimit)
			return null;
		
		ArrayList<PointP> p=new ArrayList<PointP>();
		for(int i=0;i<10;i++){
			int x1=r.nextInt(widthOfScreen);
			int y1=r.nextInt(heightOfScreen);
			p.add(new PointP(x1+0.0,y1+0.0));
		}
		return p;
	}

	public void setWidth(int w) {
		if(w<1){
			Lib.p("Datas: PROBLEM in setting width");
			System.exit(-1);
		}
		widthOfScreen=w;
	}

	public void setHeight(int h) {
		if(h<1){
			Lib.p("Datas: PROBLEM in setting height");
			System.exit(-1);
		}
		heightOfScreen=h;
	}
	
	public double VirtualToRealDistance(int dist){
		return  dist*(getMaxX()-getMinX()) / (double)widthOfScreen;
	}
	
	
	public double RealToVirtualDistance(int dist){
		return  (dist * (double)widthOfScreen) / (getMaxX()-getMinX());
	}
	
	
	public double getMinX(){
		if (Double.isNaN(minx)) {
			Lib.p("Datas's getMinX is NaN");
			System.exit(-1);
		}
		return minx;
	}
	
	public double getMinY(){
		if (Double.isNaN(miny)) {
			Lib.p("Datas's getMinY is NaN");
			System.exit(-1);
		}
		return miny;
	}
	
	public double getMaxX(){
		if (Double.isNaN(maxx)) {
			Lib.p("Datas's getMaxX is NaN");
			System.exit(-1);
		}
		return maxx;
	}
	
	public double getMaxY(){
		if (Double.isNaN(maxy)) {
			Lib.p("Datas's getMaxY is NaN");
			System.exit(-1);
		}
		return maxy;
	}
	
	public double getWidth(){
		return widthOfScreen;
	}
	
	public double getHeight(){
		return heightOfScreen;
	}
	public String toString(){
		return "Data maxX "+getMaxX()+" MaxY "+getMaxY()+" MinX "+getMinX()+" MinY "+getMinY()+" width "+getWidth()+" height "+getHeight();
	}
	
}