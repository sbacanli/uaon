package routing;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import random.Random;


public class LibRouting {
	
	private static DecimalFormat dec;
	
	public static void init() {
		dec = new DecimalFormat("#0.000000");
	}
	
	//given the mean (lambda) it creates poisson random variables
	public static int getPoisson(double lambda) {
		double L = Math.exp(-lambda);
		double p = 1.0;
		int k = 0;
		
		do {
			k++;
			//p *= Math.random();
			p*=Random.get();
		} while (p > L);
		
		return k - 1;
	}
	
	//uniformly distributed random number generator
	public static int getUniform(int l){
		//Random r=new Random();
		//return r.nextInt(l)+1;
		//Using mersenneTwisterRandom
		return Random.nextInt(l)+1;
	}
	
	//returns message generation time and message generating node's id in the rows of array
	public static int[][] posarr(final int numberOfMessages,int lasttime,int messageGeneratingNumberOfNodes,ArrayList<RoutingNode> nodes){
		int[][] arr=new int[numberOfMessages][2]; 
		for(int i=0;i<numberOfMessages;i++){
			
			//generate message generating times
			arr[i][0]=getUniform(lasttime);
			
			//put the id of thhe node that is generating the message
			arr[i][1]=nodes.get(getUniform(messageGeneratingNumberOfNodes)-1).getId();

		}
		return arr;
	}
	
	//arithmetical mean of the array 
	public static double mean(double[] arr){
		double m=0;
		for(int i=0;i<arr.length;i++){
			m+=arr[i];
		}
		return m/arr.length;
	}
	
	//precedence corrector
	//for a given double number d,i number of numbers after the point is returned;
	/**
	 * @param d doublevalue
	 * @param i number of precision digits to preserve
	 * @return
	 */
	public static double prec(double d,int i){
		return limitPrecision(d,i);
		//return Double.parseDouble(dec.format(d));
		//return Double.parseDouble(precstr(d,i));
	}
	
	//gets the rounded number d to digit i
	public static String precstr(double d,int i){
		return String.format("%."+i+"f", d);
	}
	
	//Limit precision from 
	//https://stackoverflow.com/questions/14845937/java-how-to-set-precision-for-double-value
	private static double limitPrecision(double num, int maxDigitsAfterDecimal) {
	    int multiplier = (int) Math.pow(10, maxDigitsAfterDecimal);
	    double truncated = (double) ((long) (num * multiplier)) / multiplier;
	    return truncated;
	}
	
	//if date s1 is greater than s2 returns true else false
	public static boolean isGreater(String s1,String s2){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1=null;
        Date date2=null;
		try{
        	date1 = (Date) sdf.parse(s1);
        	date2 = (Date) sdf.parse(s2);
        	return date1.after(date2);
        }catch(ParseException e){
        	System.out.println(e);
        }
		System.out.println("Problem in comparison "+s1+" "+s2+" LibRouting.java");
		return false;
	}
	
	//The method returns the given timestamp as UTC milliseconds.
	public static int cdate(String date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cl = Calendar. getInstance();
		Date date1=null;
		try{
        	date1 = (Date) sdf.parse(date);
            cl.setTime(date1);
        }catch(ParseException e){
        	System.out.println("LibRouting.java cdate is problematic: "+e);
        }
		return (int)cl.getTimeInMillis();
	}
}
