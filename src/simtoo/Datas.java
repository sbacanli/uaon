package simtoo;

import java.util.*;
import java.io.*;

public class Datas {

	Random r;
	int linenumlimit=0;
	static int width;
	static int height;
	ArrayList<ArrayList<Double>> arr;
	static double minx;
	static double miny;
	static double maxy;
	static double maxx;
	
	
	public Datas(int linen,int h,int w){
		linenumlimit=linen;
		width=w;
		height=h;
		r=new Random();
		arr=new ArrayList<ArrayList<Double>>();
		minx=-1;
		miny=-1;
		maxx=10000;
		maxy=10000;
		
	}
	
	/*
	public static void readData(String fname){
		BufferedReader br=null;
		String line=null;
		int i=0;
		try{
			br=new BufferedReader(new FileReader(new File(fname)));
			while( (line=br.readLine()) !=null ){
				arr.add(new ArrayList<Double>());
				StringTokenizer st=new StringTokenizer(line," ");
				double time=Double.parseDouble(st.nextToken());
				double xcord=Double.parseDouble(st.nextToken());
				double ycord=Double.parseDouble(st.nextToken());
				arr.get(i).add(time);
				arr.get(i).add(xcord);
				arr.get(i).add(ycord);
				if(i==0){
					maxx=xcord;
					maxy=ycord;
					minx=xcord;
					miny=ycord;
				}
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
				
				i++;
			}
			
		}catch(Exception e){
			Lib.p(e.toString());
		}
		
	}
	*/
	
	
	public static void calculateMaxes(String fname){
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
			}
			br.close();
		}catch(Exception e){
			Lib.p(e.toString());
		}
	}
	
	
	//reads the real data for the node position to Position ArrayList
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
			e.printStackTrace();
			System.out.println(e.getCause());
		}
		return arrposition;
	}	
	
	private static double convertToScreen(double screendifference,double geodifference,double geo){
		return (geo * (screendifference) )/(geodifference);
	}
	
	public static double convertToScreenX(double geoX){
		return (geoX * (width) )/(maxx-minx);
	}
	
	public static double convertToScreenY(double geoY){
		return (geoY * (height) )/(maxy-miny);
	}
	
	public static double convertToRealX(double screenX){
		return (screenX * (maxx-minx) )/(width);
	}
	
	public static double convertToRealY(double screenY){
		return (screenY * (maxy-miny) )/(height);
	}
	
	public ArrayList<PointP> dataLine(int line){
		
		if(line>linenumlimit)
			return null;
		
		ArrayList<PointP> p=new ArrayList<PointP>();
		for(int i=0;i<10;i++){
			int x1=r.nextInt(width);
			int y1=r.nextInt(height);
			p.add(new PointP(x1+0.0,y1+0.0));
		}
		return p;
	}

	public void setWidth(int w) {
		if(w<1){
			Lib.p("PROBLEM in setting width");
			System.exit(-1);
		}
		width=w;
	}

	public void setHeight(int h) {
		if(h<1){
			Lib.p("PROBLEM in setting height");
			System.exit(-1);
		}
		height=h;
	}
	
	public static  double VirtualToRealDistance(int dist){
		Datas.calculateMaxes("NewYork/maxes.txt");
		return  dist*(Datas.getMaxX()-Datas.getMinX())/width;
		
	}
	
	
	public static double RealToVirtualDistance(int dist){
		Datas.calculateMaxes("NewYork/maxes.txt");
		return  (dist * width)/(Datas.getMaxX()-Datas.getMinX());

	}
	
	
	public static double getMinX(){
		return minx;
	}
	
	public static double getMinY(){
		return miny;
	}
	
	public static double getMaxX(){
		return maxx;
	}
	
	public static double getMaxY(){
		return maxy;
	}
	
	
}
