package Shapes;

import java.util.ArrayList;

import simtoo.PointP;

public abstract class Shape {

	ArrayList<PointP> arr;
	double xlim,ylim;
	
	protected Shape(double xlim,double ylim){
		arr=new ArrayList<PointP>();
		this.xlim=xlim;
		this.ylim=ylim;
	}
	
	public ArrayList<PointP> getPoints(){
		return arr;
	}
	
	public double getXlim(){
		return xlim;
	}
	
	public double getYlim(){
		return ylim;
	}
	
	public void addPoint(double x,double y){
		arr.add(new PointP(x,y));
	}
	
	/*
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
	*/
	
	//fill screen locations starting point xpos and ypos
	public abstract void fill(double xpos,double ypos);
	
	//fill screen locations with some precomputed value or random
	public abstract void fill();
	
	public abstract void updateSuccess();

	public abstract void updateFail();
	
	public abstract void updateFail(double n);
}
