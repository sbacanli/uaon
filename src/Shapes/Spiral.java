package Shapes;

import random.Random;
import simtoo.PointP;


public class Spiral extends Shape{
	double a;
	int radiusChange;
	double maxradius;
	
	public Spiral(double a,double maxradius,double xlim,double ylim){
		super(xlim,ylim);
		this.a=a;
		radiusChange=100;
		this.maxradius=maxradius;
	}
	
	@Override
	public PointP initialPoint() {
		double xpos=Random.nextDouble()*getXlim();
		double ypos=Random.nextDouble()*getYlim();
		return new PointP(xpos,ypos);
	}

	public double getA(){
		return a;
	}
	
	//xstart and ystart are screen coordinates
	private PointP equation(double t,double xstart,double ystart){
		//x(t) = at cos(t), y(t) = at sin(t)
		double x=a*t*Math.cos(t)+xstart;
		double y=a*t*Math.sin(t)+ystart;
		//*
		if(x>getXlim() || y>getYlim() || y<0 || x<0){
			//Lib.p("Limits done for spiral");
			return null;
		}
		//*/
		return new PointP(x,y);
	}
	
	//xstart and ystart are screen coordinates
	public void fill(double xstart,double ystart){
		clearPositions();
		double degree=Math.PI/180*10;
		PointP p=new PointP(0,0);
		for(double y=degree;p!=null;y=y+degree){
			p=equation(y,xstart,ystart);
			
			if(p!=null) {
				if(distance(p.getX(),p.getY(),xstart,ystart)>maxradius) {
					break;
				}else {
					addPoint(p.getX(),p.getY());
				}					
			}
		}
		
	}
	
	private double distance(double x,double y,double x2,double y2) {
		return Math.sqrt(  (x-x2) *(x-x2)-(y-y2)*(y-y2) );
	}
	
	public void setRadius(double c){
		a=c;
	}
	
	public double getRadius(){
		return a;
	}
	
	public void increaseRadius(double num){
		a=a+num;
		if(a>=1000) {
			a=2*radiusChange;
		}
	}
	
	public void decreaseRadius(double num){
		a=a-num;
		if(a<=100){
			setRadius(radiusChange);
		}
	}
	
	public void updateFail(){
		increaseRadius(radiusChange);
	}
	
	public void updateFail(double n){
		setRadius(n);
	}
	
	public void updateSuccess(){
		decreaseRadius(radiusChange);
	}

	@Override
	public void fill() {
		clearPositions();
		// TODO Auto-generated method stub
		
	}
}
