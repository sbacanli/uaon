package simtoo;

import java.util.*;

import routing.LibRouting;

import java.io.*;

enum LocationType{
	REAL,RELATIVE,SCREEN;
	
	@Override
	  public String toString() {
	    switch(this) {
	      case REAL: return "REAL";
	      case RELATIVE: return "RELATIVE";
	      case SCREEN: return "SCREEN";
	      default: throw new IllegalArgumentException();
	    }
	  }
}
public class Datas {

	private Random r;
	private int linenumlimit=0;
	private  double widthOfScreen;
	private double heightOfScreen;
	private ArrayList<ArrayList<Double>> arr;
	private double minx;
	private double miny;
	private double maxy;
	private double maxx;
	private long maxtime;
	private long maxftime;
	private long mintime;
	private int precision;
	private LocationType op;
	private double AreaRatio;
	private double LinearXDiff,LinearYDiff;
	private int timeDifference;
	private int numberOfDataLines;
	private boolean eof;
	
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
		maxftime=Long.MIN_VALUE;
		mintime=Long.MAX_VALUE;
		precision=6;
		op=null;
		AreaRatio=0;
		eof=false;
	}
	
	public boolean isEOF() {
		return eof;
	}
	
	
	public void setEOF(boolean c) {
		eof=c;
	}
	
	public void setLoc(LocationType l) {
		op=l;
	}
	
	public LocationType getLoc() {
		return op;
	}
	
	public void setNumberOfDataLines(int ns) {
		numberOfDataLines=ns;
	}
	
	public void setTimeDifference(int t) {
		timeDifference=t;
	}
	
	public int getTimeDifference() {
		return timeDifference;
	}
	
	public int getNumberOfDataLines() {
		return numberOfDataLines;
	}
	
	public long getMaxFTime() {
		return maxftime;
	}
	
	public ArrayList<PointP> fillRandomPositions(int numberOfPoints){
		ArrayList<PointP> pts=new ArrayList<PointP>();
		
		double xgen=0;
		double ygen=0;
		int count=1;
		double xgenfirst=(r.nextDouble()*(getLinearXDiff()))+minx;
		double ygenfirst=(r.nextDouble()*(getLinearYDiff()))+miny;
		pts.add(new PointP(xgenfirst,ygenfirst));
		while(count<numberOfPoints){
			xgen=(r.nextDouble()*(getLinearXDiff()))+minx;
			ygen=(r.nextDouble()*(getLinearYDiff()))+miny;
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
		//boolean firstread=false;
		double xcord,ycord=0;
		long time=0l;
		try{
			BufferedReader br=new BufferedReader(new FileReader(new File(fname)));
			 for(String line;(line = br.readLine()) != null; ){
				String[] arr=line.split("\t");
				
				time=Long.parseLong(arr[0]);
				xcord=Double.parseDouble(arr[1]);
				ycord=Double.parseDouble(arr[2]);
								
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
					maxftime= time;
					maxtime=time;
				}
				if(time<mintime){
					mintime=time;
				}
			}
			br.close();
			br=null;
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
	
	//for debugging purposes
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
		br=null;
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
	
	public void calculateAreaRatio() {
		double MapArea=0;double ScreenArea=0;
		MapArea=calculateMapArea();
		ScreenArea=calculateScreenArea();
		AreaRatio=Math.sqrt(MapArea/ScreenArea);
		LinearXDiff=getMaxX()-getMinX();
		LinearYDiff=getMaxY()-getMinY();	
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
	
	public ArrayList<Position> readPortion(String fileName, long dataLineStart,int numberOfLinesToBeRead, long timebias){
		//BufferedReader br;
		//StringTokenizer st;
		int count=0;
		long linecount=0;
		double xcord=0;
		double ycord=0;
		double calcx=0;
		double calcy=0;
		long time=0;


		ArrayList<Position> arrposition=new ArrayList<Position>();
		
		try{
			BufferedReader brdata= new BufferedReader(new FileReader(fileName)); 
		    for(String line;linecount<dataLineStart && (line = brdata.readLine()) != null; ) {
		        linecount++;
		    }
		    
		    for(String line;count<numberOfLinesToBeRead && (line = brdata.readLine()) != null; ) {
			    /*st=new StringTokenizer(line);
				time=Long.parseLong(st.nextToken());
				xcord=Double.parseDouble(st.nextToken());
				ycord=Double.parseDouble(st.nextToken());
				*/
				String[] arr=line.split("\t");
				
				time=Long.parseLong(arr[0])+timebias;
				xcord=Double.parseDouble(arr[1]);
				ycord=Double.parseDouble(arr[2]);
				calcx=convertToScreenX(xcord);
				calcy=convertToScreenY(ycord);
				arrposition.add(new Position(time,calcx,calcy,xcord,ycord));
				count++;
			
		    }
		    if( brdata.readLine() == null) {//end of file
		    	eof=true;
		    }
		    brdata.close();
		    // line is not visible here.
		}catch(Exception e){
			e.printStackTrace();
		}
		return arrposition;
	}     
	
	private void readMaxes(String fname){
		//the maxes file does not change while running
				//if it is calculated before no need to calculate again	
				BufferedReader br=null;
				
				int i=0;
				StringTokenizer st=null;
				try{
					br=new BufferedReader(new FileReader(new File(fname)));
					 for(String line;(line = br.readLine()) != null; ) {
						st=new StringTokenizer(line," ");
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
						st=null;
					}//enf of for 
					br.close();
				}catch(Exception e){
					Lib.p(e.toString());
					e.printStackTrace();
				}
				maxx=LibRouting.prec(maxx, precision);
				minx=LibRouting.prec(minx, precision);
				miny=LibRouting.prec(miny, precision);
				minx=LibRouting.prec(minx, precision);
				br=null;
	}
	
	
	//reads the real data for the node position to Position ArrayList
	//the data structure will be like that
	//timeAsNumberofSeconds Xcord YCord
	public ArrayList<Position> readRealDataForNode(File fname){
		BufferedReader br=null;
		int i=0;
		//StringTokenizer st=null;
		ArrayList<Position> arrposition=new ArrayList<Position>();
		long time=0;
		double xcord=0;
		double ycord=0;
		double prevxcord=0;
		double prevycord=0;
		long prevtime=0;
		double realx=0;
		double realy=0;
		double calcx=0;
		double calcy=0;
		int differenceTime=0;
		double differencex=0;
		double differencey=0;
		
		try{
			br=new BufferedReader(new FileReader(fname));
			 for(String line;(line = br.readLine()) != null; ){
				String[] arrtemp=line.split("\t");
				time=Long.parseLong(arrtemp[0]);
				xcord=Double.parseDouble(arrtemp[1]);
				ycord=Double.parseDouble(arrtemp[2]);
				/*
				st=new StringTokenizer(line);
				time=Long.parseLong(st.nextToken());
				xcord=Double.parseDouble(st.nextToken());
				ycord=Double.parseDouble(st.nextToken());
				*/
				xcord=LibRouting.prec(xcord, precision);
				ycord=LibRouting.prec(ycord, precision);
				
				if(i>0){
					
					prevtime=arrposition.get(arrposition.size()-1).getTime();
					prevxcord=arrposition.get(arrposition.size()-1).getRealX();
					prevycord=arrposition.get(arrposition.size()-1).getRealY();
					prevxcord=LibRouting.prec(prevxcord, precision);
					prevycord=LibRouting.prec(prevycord, precision);
					
					differenceTime=(int)(time-prevtime);
					differencex=(xcord-prevxcord)/differenceTime;
					differencey=(ycord-prevycord)/differenceTime;
					
					realy=0;
					realx=0;
					
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
						calcx=convertToScreenX(realx);
						calcy=convertToScreenY(realy);
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
				
				calcx=convertToScreenX(xcord);
				calcy=convertToScreenY(ycord);
				arrposition.add(new Position((int)time,calcx,calcy,xcord,ycord));
				i++;
				//st=null;
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
			Lib.p("Geo X is not in the limit geoX "+geoX+" maxX "+getMaxX()+"  minX "+getMinX());
			if(geoX>getMaxX()){
				geoX=getMaxX();
			}
			if(geoX<getMinX()){
				geoX=getMinX();
			}
		}
		
		if(Double.isNaN(geoX)){
			Lib.p("ERROR:Datas' convertToScreenX is NaN");
			System.exit(-1);
		}
		double createdPos=((geoX-minx) * (double)(widthOfScreen) )/(getLinearXDiff());
		if(createdPos>getWidth()){
			//Lib.p("This can not HAPPEN for datas.java at convertToscreenX");
			createdPos=Math.floor(createdPos);
			Lib.p("This can not HAPPEN for datas.java at convertToscreenX "+createdPos+"  "+getWidth());
		}
		return createdPos;
	}
	
	public double convertToScreenSpeed(double realSpeed){
		return RealToVirtualDistance(realSpeed);
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
		double createdPos= ((geoY-miny) * (double)(heightOfScreen) )/(getLinearYDiff());
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
		if(screenX>getWidth()){
			double oldscreenX=screenX;
			screenX=getWidth();
			//Lib.p("screen coordinate converted from "+oldscreenX+" to "+screenX+" limit is "+getWidth());
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
		if( screenX<0) {
			screenX=0;
		}
		if(Double.isNaN(screenX)){
			Lib.p("ERROR:Datas' convertToRealX is NaN");
			System.exit(-1);
		}
		
		return (screenX * (getLinearXDiff()) )/(double)(widthOfScreen) + minx;
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
		
		return (screenY * (getLinearYDiff()) )/(double)(heightOfScreen) + miny;
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
		
		return  (distScr * AreaRatio);
	}
	
	public double RealToVirtualDistance(double speedreal){
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
		+"\r\n mintime "+mintime+" maxtime "+maxtime+" filemax "+getMaxFTime()+"\r\n"+"Simulation duration: "+(maxtime-mintime);
	}
	
	public long getMinTime(){
		return mintime;
	}
	
	public long getMaxTime(){
		return maxtime;
	}	
	
	public void setMaxTime(long maxt) {
		if(maxt>0) {
			maxtime=maxt;
		}
	}
	
	public double getLinearXDiff() {
		if(LinearXDiff==0) {
			Lib.p("X diff not set in Datas.java");
			System.exit(-1);
		}
		return LinearXDiff;
	}
	
	public double getLinearYDiff() {
		if(LinearYDiff==0) {
			Lib.p("Y diff not set in Datas.java");
			System.exit(-1);
		}
		return LinearYDiff;
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
	
	public PointP getRandomScreenLocation() {
		//double x1=random.Random.nextDouble()*widthOfScreen*0.7+widthOfScreen*0.2;
		//double y1=random.Random.nextDouble()*heightOfScreen*0.7+heightOfScreen*0.2;
		double x1=r.nextDouble()*widthOfScreen*0.6+widthOfScreen*0.2;
		double y1=r.nextDouble()*heightOfScreen*0.6+heightOfScreen*0.2;
		return new PointP(x1, y1);
	}
}