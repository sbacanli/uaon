package simtoo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;


public class Lib {

	
	public static double relativeDistance(double x,double y,double x1,double y1){
		return Math.sqrt((x-x1)*(x-x1)+(y-y1)*(y-y1));
	}
	
	public static double screenDistance(double x,double y,double x1,double y1){
		return Math.sqrt((x-x1)*(x-x1)+(y-y1)*(y-y1));
	}
	
    public static void p(Object x){
		System.out.println(x.toString());
		BufferedWriter bwriter=null;
		try{
			bwriter=new BufferedWriter(new FileWriter("Report.txt",true));
			bwriter.write(x+"\r\n");
			bwriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
    
    public static double distance(PointP p1,PointP p2){
		return Math.sqrt(
				(  p1.getX()-p2.getX() )  *  (  p1.getX()-p2.getX()   )
				+ ( p1.getY()-p2.getY() ) *  (  p1.getY()-p2.getY()   )
				);
	}
	
    public static double realdistance(double lat1,double lon1,double lat2,double lon2) {
    	//*
    	double R = 6371; // Radius of the earth in km
    	double dLat = deg2rad(lat2-lat1);  // deg2rad below
    	double dLon = deg2rad(lon2-lon1); 
    	double a = 
    			Math.sin(dLat/2) * Math.sin(dLat/2) +
    			Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
    			Math.sin(dLon/2) * Math.sin(dLon/2); 
    	double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
    	double d = R * c; // Distance in km
    	return d*1000;
    	//*/
    }
    

    //This method is used in real GPS distance calculation
	private static double deg2rad(double deg) {
	  return deg * (Math.PI/180);
	}
	    
	public static void printPositions(String fname,ArrayList<Position> arr){
		BufferedWriter bw;
		try{
			bw=new BufferedWriter(new FileWriter(fname));
			
			for(int i=0;i<arr.size();i++){
				String str=arr.get(i).realString();
				bw.write(str);
				bw.newLine();
			}
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
