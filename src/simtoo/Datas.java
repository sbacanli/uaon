package simtoo;

import java.util.*;

import routing.LibRouting;

import java.io.*;

public class Datas {

	Random r;
	int linenumlimit=0;
	static double widthOfScreen;
	static double heightOfScreen;
	ArrayList<ArrayList<Double>> arr;
	double minx;
	double miny;
	double maxy;
	double maxx;
	int maxtime;
	int mintime;
	
	
	public Datas(double h,double w){
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
		maxtime=-1;
		mintime=Integer.MAX_VALUE;
		
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
	
	public void calculateMaxesByProcessing(String folderName){
		File f=new File(folderName);
		File[] files=f.listFiles();
		for(int i=0;i<files.length;i++){
			readData(files[i].getPath());
		}
	}
	
	public int getNumberOfNodes(String folderName){
		int num=0;
		File f=new File(folderName);
		File[] files=f.listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].getName().contains(".txt")){
				num++;
			}
		}
		return num;
	}
	
	//the read file should be ordered according to time. Ascending order
	//it finds the maximum and minimum coordinates of the file
	private void readData(String fname){
		BufferedReader br=null;
		String line=null;
		//boolean firstread=false;
		double xcord,ycord,time=0;
		
		try{
			br=new BufferedReader(new FileReader(new File(fname)));
			while( (line=br.readLine()) !=null ){

				StringTokenizer st=new StringTokenizer(line);
				time=Integer.parseInt(st.nextToken());
				xcord=Double.parseDouble(st.nextToken());
				ycord=Double.parseDouble(st.nextToken());
				/*
				if(!firstread){
					firstread=true;
					mintime=time;
				}*/
				
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
		if((int)time>maxtime){
			maxtime=(int) time;
		}
		if((int)time<mintime){
			mintime=(int) time;
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
		fixHeightWidth();
	}
	
	private void fixHeightWidth(){
		double datawidth=getMaxX()-getMinX();
		double dataheight=getMaxY()-getMinY();
		double A=(heightOfScreen*datawidth)/dataheight;
		double B=(widthOfScreen*dataheight)/datawidth;
		if(A<widthOfScreen){
			widthOfScreen=A;
		}else{
			heightOfScreen=B;
		}
				
		
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
				StringTokenizer st=new StringTokenizer(line);
				int time=Integer.parseInt(st.nextToken());
				double xcord=Double.parseDouble(st.nextToken());
				double ycord=Double.parseDouble(st.nextToken());
				xcord=LibRouting.prec(xcord, 2);
				ycord=LibRouting.prec(ycord, 2);
				
				if(i>0){
					
					int prevtime=arrposition.get(arrposition.size()-1).getTime();
					double prevxcord=arrposition.get(arrposition.size()-1).getRealX();
					double prevycord=arrposition.get(arrposition.size()-1).getRealY();
					prevxcord=LibRouting.prec(prevxcord, 2);
					prevycord=LibRouting.prec(prevycord, 2);
					
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
						
						realx=LibRouting.prec(realx, 2);
						realy=LibRouting.prec(realy, 2);
						double calcx=convertToScreenX(realx);
						double calcy=convertToScreenY(realy);
						calcx=LibRouting.prec(calcx, 2);
						calcy=LibRouting.prec(calcy, 2);
						
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
			Lib.p("problem in reading file "+fname);
			Lib.p(e.toString());
			e.printStackTrace();
		}
		return arrposition;
	}	
	
	public double convertToScreenX(double geoX){
		if(geoX>getMaxX() || geoX<getMinX()){
			Lib.p("Geo X is not in the limit");
			try{
				Exception e=new Exception();
				throw e;
			}catch(Exception e){
				e.printStackTrace();
				System.exit(-1);
			}
			
		}
		
		if(Double.isNaN(geoX)){
			Lib.p("ERROR:Datas' convertToScreenX is NaN");
			System.exit(-1);
		}
		double createdPos=((geoX-minx) * (double)(widthOfScreen) )/(maxx-minx);
		if(createdPos>getWidth()){
			Lib.p("This can not HAPPEN for datas.java at convertToscreenX");
			createdPos=Math.floor(createdPos);
		}
		return createdPos;
	}
	
	public double convertToScreenSpeed(double realSpeed){
		return realSpeed*getWidth()/(getMaxX()-getMinX());
	}
	
	public double convertToScreenY(double geoY){
		if(geoY>getMaxY() || geoY<getMinY()){
			Lib.p("Geo Y is not in the limit");
			try{
				Exception e=new Exception();
				throw e;
			}catch(Exception e){
				e.printStackTrace();
				System.exit(-1);
			}
			
		}
		
		if(Double.isNaN(geoY)){
			Lib.p("ERROR:Datas' convertToScreenY is NaN");
			System.exit(-1);
		}
		double createdPos= ((geoY-miny) * (double)(heightOfScreen) )/(maxy-miny);
		if(createdPos>getHeight()){
			Lib.p("This can not HAPPEN for datas.java at convertToscreen");
			createdPos=Math.floor(createdPos);
		}
		return createdPos;
	}
	
	public double convertToRealX(double screenX){
		if(screenX>getWidth() || screenX<0){
			double oldscreenX=screenX;
			screenX=getWidth();
			Lib.p("screen coordinate converted from "+oldscreenX+" to "+screenX+" limit is "+getWidth());
		}
		
		if(Double.isNaN(screenX)){
			Lib.p("ERROR:Datas' convertToRealX is NaN");
			System.exit(-1);
		}
		
		return (screenX * (maxx-minx) )/(double)(widthOfScreen) + minx;
	}
	
	public double convertToRealY(double screenY){
		if(screenY>getHeight() || screenY<0){
			
			Lib.p("Screen Y is not in the limit "+screenY+" the limit is "+getHeight());
			try{
				Exception e=new Exception();
				throw e;
			}catch(Exception e){
				e.printStackTrace();
				System.exit(-1);
			}
			
		}
		
		if(Double.isNaN(screenY)){
			Lib.p("ERROR:Datas.java convertToRealY is NaN");
			System.exit(-1);
		}
		
		return (screenY * (maxy-miny) )/(double)(heightOfScreen) + miny;
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
	
	//given real coordinates it will return a position whose time is -1
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

	public void setWidth(double w) {
		if(w<1){
			Lib.p("Datas: PROBLEM in setting width");
			System.exit(-1);
		}
		widthOfScreen=w;
	}

	public void setHeight(double h) {
		if(h<1){
			Lib.p("Datas: PROBLEM in setting height");
			System.exit(-1);
		}
		heightOfScreen=h;
	}
	
	public double VirtualToRealDistance(int distScr){
		return  (distScr * (getMaxX()-getMinX())) / (widthOfScreen);
	}
	
	
	public double RealToVirtualDistance(double speedreal){
		return  (speedreal * (double)widthOfScreen) / (getMaxX()-getMinX());
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
		return "Data maxX "+getMaxX()+" MaxY "+getMaxY()+" MinX "+getMinX()+" MinY "+getMinY()
		+"\r\n width "+getWidth()+" height "+getHeight()
		+"\r\n maxtime "+maxtime+" mintime "+mintime;
	}
	
	public int getMinTime(){
		return mintime;
	}
	
	public int getMaxTime(){
		return maxtime;
	}
	
}