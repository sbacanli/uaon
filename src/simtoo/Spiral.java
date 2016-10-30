package simtoo;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import simtoo.*;

public class Spiral {
	double a;
	double xstart,ystart;
	ArrayList<PointP> arr;
	int xlim,ylim;
	
	public Spiral(double a,double xstart, double ystart,int xlim,int ylim){
		this.a=a;
		this.xstart=xstart;
		this.ystart=ystart;
		arr=new ArrayList<PointP>();
		this.xlim=xlim;
		this.ylim=ylim;
	}
	
	public PointP equation(double t){
		//x(t) = at cos(t), y(t) = at sin(t)
		double x=a*t*Math.cos(t)+xstart;
		double y=a*t*Math.sin(t)+ystart;
		/*
		if(x>xlim || y>ylim){
			Lib.p("Limits done for spiral");
			return null;
		}
		*/
		return new PointP(x,y);
	}
	
	public void fill(){
		double degree=Math.PI/180*10;
		PointP p=new PointP(0,0);
		for(double y=degree;p!=null;y=y+degree){
			p=equation(y);
			if(p!=null)
				arr.add(p);
		}
		
	}
	
	public ArrayList<PointP> getPoints(){
		return arr;
	}
	
	public void writeFile(String s){
		BufferedWriter bw=null;
		try{
			bw=new BufferedWriter(new FileWriter(s));
			for(int j=0;j<arr.size();j++){
				bw.write(arr.get(j).getX()+"\t"+arr.get(j).getY()+"\r\n");
			}
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
}
