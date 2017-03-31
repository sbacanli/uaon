package simtoo;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class Lib {

	
	public static double distance(double x,double y,double x1,double y1){
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
	
    public static double realdistance(double lat1,double lon1,double lat2,double lon2) {
    	return distance(lat1,lon1,lat2,lon2);
    	/*
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
    	*/
    }

	public static double deg2rad(double deg) {
	  return deg * (Math.PI/180);
	}

}
