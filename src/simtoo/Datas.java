package simtoo;

import java.util.*;

import routing.LibRouting;

import java.io.*;

enum LocationType{
	REAL,RELATIVE,SCREEN;
}
public class Datas {

	private Random r;
	private int linenumlimit=0;
	static double widthOfScreen;
	static double heightOfScreen;
	private ArrayList<ArrayList<Double>> arr;
	private double minx;
	private double miny;
	private double maxy;
	private double maxx;
	private long maxtime;
	private long mintime;
	private boolean isGPS;
	private int precision;
	private LocationType op;
	
	public Datas(double h,double w){
		linenumlimit=1000;
		//width and height of the screen
		widthOfScreen=w;
		heightOfScreen=h;
		//real,relative or screen
		
		r=new Random();
		arr=new ArrayList<ArrayList<Double>>();
		minx=Double.MAX_VALUE;
		miny=Double.MAX_VALUE;
		maxx=Double.MIN_VALUE;
		maxy=Double.MIN_VALUE;
		maxtime=Long.MIN_VALUE;
		mintime=Long.MAX_VALUE;
		isGPS=false;
		precision=6;
		op=null;
	}
	
	public boolean isGPS(){
		return isGPS;		
	}
	
	public void setGPS(boolean b){
		isGPS=b;
	}
	
	public void setLoc(LocationType l) {
		op=l;
	}
	
	public LocationType getLoc() {
		return op;
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
	
	public ArrayList<File> getDataFiles(String folderName){
		ArrayList<File> arr=new ArrayList<File>();
		File f=new File(folderName);
		File[] files=f.listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].getName().contains(".txt") && files[i].length() != 0){
				arr.add(files[i]);
			}
		}
		return arr;
	}
	
	//the read file should be ordered according to time. Ascending order
	//it finds the maximum and minimum coordinates of the file
	private void readData(String fname){
		BufferedReader br=null;
		String line=null;
		//boolean firstread=false;
		double xcord,ycord=0;
		long time=0l;
		StringTokenizer st=null;
		try{
			br=new BufferedReader(new FileReader(new File(fname)));
			while( (line=br.readLine()) !=null ){

				st=new StringTokenizer(line);
				time=Long.parseLong(st.nextToken());
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
				if(time>maxtime){
					maxtime= time;
				}
				if(time<mintime){
					mintime=time;
				}
				st=null;
			}
			st=null;
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}	
	}	
	//*/
	
	public String FindData(String folderName,String searched){
		File f=new File(folderName);
		File[] files=f.listFiles();
		for(int i=0;i<files.length;i++){
			String res=findDataInDataSet(files[i].getPath(),searched);
			if(res != null) {
				return "In file "+files[i].getPath()+" line "+res;
			}
		}
		return "Not Found";
	}
	
	private String findDataInDataSet(String fname,String searched){
		BufferedReader br=null;
		String line=null;
		//boolean firstread=false;
		double xcord,ycord=0;
		long time=0l;
		StringTokenizer st=null;
		try{
			br=new BufferedReader(new FileReader(new File(fname)));
			while( (line=br.readLine()) !=null ){

				st=new StringTokenizer(line);
				time=Long.parseLong(st.nextToken());
				xcord=Double.parseDouble(st.nextToken());
				ycord=Double.parseDouble(st.nextToken());
				if(searched.contains(".") || searched.contains(",")) {
					double d1=Double.parseDouble(searched);
					if(d1==xcord || d1==ycord) {
						return line;
					}
				}else {
					if(time== Long.parseLong(searched)) {
						return line;
					}
				}
				st=null;
				
			}
			st=null;
			br.close();
		
		}catch( IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
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
		/*
		maxx=LibRouting.prec(maxx, precision);
		minx=LibRouting.prec(minx, precision);
		miny=LibRouting.prec(miny, precision);
		minx=LibRouting.prec(minx, precision);
		//*/
	}
	
	public void makeAllEqual(){
		widthOfScreen=getMaxX()-getMinX();
		heightOfScreen=getMaxY()-getMinY();
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
	
	public ArrayList<Position> readPortion(String fileName, long dataLineStart,int numberOfLinesToBeRead){
		BufferedReader br;
		StringTokenizer st;
		String line=null;
		int count=0;
		long linecount=0;

		ArrayList<Position> arrposition=new ArrayList<Position>();
		try{
			br=new BufferedReader(new FileReader(fileName));
			while(linecount<dataLineStart){
				line=br.readLine();
				linecount++;
			}
			
			while(count<numberOfLinesToBeRead && (line=br.readLine())!= null){
				st=new StringTokenizer(line);
				long time=Long.parseLong(st.nextToken());
				double xcord=Double.parseDouble(st.nextToken());
				double ycord=Double.parseDouble(st.nextToken());
				double calcx=convertToScreenX(xcord);
				double calcy=convertToScreenY(ycord);
				arrposition.add(new Position(time,calcx,calcy,xcord,ycord));
				count++;
				
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		st=null;
		/*
		BufferedWriter bw;
		try{
			bw=new BufferedWriter(new FileWriter(fileName,false));
			bw.write(all);
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		*/
		return arrposition;
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
				maxx=LibRouting.prec(maxx, precision);
				minx=LibRouting.prec(minx, precision);
				miny=LibRouting.prec(miny, precision);
				minx=LibRouting.prec(minx, precision);
	}
	
	
	//reads the real data for the node position to Position ArrayList
	//the data structure will be like that
	//timeAsNumberofSeconds Xcord YCord
	public ArrayList<Position> readRealDataForNode(File fname){
		BufferedReader br=null;
		String line=null;
		int i=0;
		ArrayList<Position> arrposition=new ArrayList<Position>();
		try{
			br=new BufferedReader(new FileReader(fname));
			while( (line=br.readLine()) !=null ){
				StringTokenizer st=new StringTokenizer(line);
				long time=Long.parseLong(st.nextToken());
				double xcord=Double.parseDouble(st.nextToken());
				double ycord=Double.parseDouble(st.nextToken());
				xcord=LibRouting.prec(xcord, precision);
				ycord=LibRouting.prec(ycord, precision);
				
				if(i>0){
					
					long prevtime=arrposition.get(arrposition.size()-1).getTime();
					double prevxcord=arrposition.get(arrposition.size()-1).getRealX();
					double prevycord=arrposition.get(arrposition.size()-1).getRealY();
					prevxcord=LibRouting.prec(prevxcord, precision);
					prevycord=LibRouting.prec(prevycord, precision);
					
					int differenceTime=(int)(time-prevtime);
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
						
						realx=LibRouting.prec(realx, precision);
						realy=LibRouting.prec(realy, precision);
						double calcx=convertToScreenX(realx);
						double calcy=convertToScreenY(realy);
						calcx=LibRouting.prec(calcx, precision);
						calcy=LibRouting.prec(calcy, precision);
						
						arrposition.add(new Position(
										prevtime+h,// updated time
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
			//Lib.p("Geo X is not in the limit geoX "+geoX+" maxX "+getMaxX()+"  minX "+getMinX());
			if(geoX>getMaxX()){
				geoX=getMaxX();
			}
			if(geoX<getMinX()){
				geoX=getMinX();
			}
			
			/*
			try{
				Exception e=new Exception();
				throw e;
			}catch(Exception e){
				e.printStackTrace();
				System.exit(-1);
			}
			*/
		}
		
		if(Double.isNaN(geoX)){
			Lib.p("ERROR:Datas' convertToScreenX is NaN");
			System.exit(-1);
		}
		double createdPos=((geoX-minx) * (double)(widthOfScreen) )/(maxx-minx);
		if(createdPos>getWidth()){
			//Lib.p("This can not HAPPEN for datas.java at convertToscreenX");
			createdPos=Math.floor(createdPos);
			Lib.p("This can not HAPPEN for datas.java at convertToscreenX "+createdPos+"  "+getWidth());
		}
		return createdPos;
	}
	
	public double convertToScreenSpeed(double realSpeed){
		return realSpeed*getWidth()/(getXDiff());
	}
	
	public double getXDiff() {
		if(getLoc()==LocationType.RELATIVE) {
			return Lib.relativeDistance(getMinX(), (getMinY()+getMaxY())/2, getMaxX(), (getMinY()+getMaxY())/2);
		}else if(getLoc()==LocationType.REAL){
			return Lib.realdistance(getMinX(), (getMinY()+getMaxY())/2, getMaxX(), (getMinY()+getMaxY())/2);
		}else if(getLoc()==LocationType.SCREEN) {
			return Lib.screenDistance(getMinX(), (getMinY()+getMaxY())/2, getMaxX(), (getMinY()+getMaxY())/2);
		}else {
			Lib.p("getXDiff datas.java");
		}
		return 0;
	}
	
	public double getYDiff() {
		if(getLoc()==LocationType.RELATIVE) {
			return Lib.relativeDistance((getMaxX()-getMinX())/2, getMinY(), (getMaxX()-getMinX())/2, getMaxY());
		}else if(getLoc()==LocationType.REAL){
			return Lib.realdistance((getMaxX()-getMinX())/2, getMinY(), (getMaxX()-getMinX())/2, getMaxY());
		}else if(getLoc()==LocationType.SCREEN) {
			return Lib.screenDistance((getMaxX()-getMinX())/2, getMinY(), (getMaxX()-getMinX())/2, getMaxY());
		}else {
			Lib.p("getYDiff datas.java");
		}
		return 0;
	}
	
	public double convertToScreenY(double geoY){
		if(geoY>getMaxY() || geoY<getMinY()){
			//Lib.p("Geo Y is not in the limit");
			if(geoY>getMaxY()){
				geoY=getMaxY();
			}
			if(geoY<getMinY()){
				geoY=getMinY();
			}
		}
		
		if(Double.isNaN(geoY) || Double.isNaN(maxx) || Double.isNaN(miny) || Double.isNaN(maxy) || Double.isNaN(minx)){
			Lib.p("ERROR:Datas' convertToScreenY is NaN");
			System.exit(-1);
		}
		double createdPos= ((geoY-miny) * (double)(heightOfScreen) )/(maxy-miny);
		createdPos=LibRouting.prec(createdPos, precision);
		if(createdPos>getHeight()){
			Lib.p("This can not HAPPEN for datas.java at convertToscreen");
			//createdPos=Math.floor(createdPos);
			Lib.p("geoy "+geoY);
			Lib.p("maxx "+maxx);
			Lib.p("minx "+minx);
			Lib.p("maxy "+maxy);
			Lib.p("miny "+miny);
			Lib.p("getMinY "+getMinY());
			Lib.p("getMaxY "+getMaxY());
			Lib.p("createdPos "+createdPos);
			Lib.p("getHeight "+getHeight());
		}
		return createdPos;
	}
	
	public double convertToRealX(double screenX){
		if(screenX>getWidth() || screenX<0){
			double oldscreenX=screenX;
			screenX=getWidth();
			Lib.p("screen coordinate converted from "+oldscreenX+" to "+screenX+" limit is "+getWidth());
			try{
				Exception e=new Exception();
				throw e;
			}catch(Exception e){
				e.printStackTrace();
				System.exit(-1);
			}
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
	
	
	//similarity ratio of two rectangles is the square root of their areas
	public double VirtualToRealDistance(int distScr){
		double MapArea=0;double ScreenArea=0;
		MapArea=calculateMapArea();
		ScreenArea=calculateScreenArea();
		
		double AreaRatio=Math.sqrt(MapArea/ScreenArea);
		return  (distScr * AreaRatio);
	}
	
	public double RealToVirtualDistance(double speedreal){
		double MapArea=0;double ScreenArea=0;
		MapArea=calculateMapArea();
		ScreenArea=calculateScreenArea();
		
		double AreaRatio=Math.sqrt(MapArea/ScreenArea);
		return  (speedreal / AreaRatio);
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
		+"\r\n mintime "+mintime+" maxtime "+maxtime+"\r\n"+"Simulation duration: "+(maxtime-mintime);
	}
	
	public long getMinTime(){
		return mintime;
	}
	
	public long getMaxTime(){
		return maxtime;
	}	
	
	private double calculateMapArea() {
		double MapArea=0;
		if(getLoc()==LocationType.RELATIVE) {
			MapArea=Lib.relativeDistance(getMaxX(),getMaxY(),getMinX(),getMaxY()) * Lib.relativeDistance(getMaxX(),getMaxY(),getMaxX(),getMinY());
		}else if(getLoc()==LocationType.REAL) {
			MapArea=Lib.realdistance(getMaxX(),getMaxY(),getMinX(),getMaxY()) * Lib.realdistance(getMaxX(),getMaxY(),getMaxX(),getMinY());
		}
		return MapArea;
	}
	
	private double calculateScreenArea() {
		return widthOfScreen*heightOfScreen;
	}
	
}