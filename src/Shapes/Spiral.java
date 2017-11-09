package Shapes;

import simtoo.PointP;


public class Spiral extends Shape{
	double a;
	int radiusChange;
	
	public Spiral(double a,double xlim,double ylim){
		super(xlim,ylim);
		this.a=a;
		radiusChange=100;
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
		if(x>xlim || y>ylim || y<0 || x<0){
			//Lib.p("Limits done for spiral");
			return null;
		}
		//*/
		return new PointP(x,y);
	}
	
	//xstart and ystart are screen coordinates
	public void fill(double xstart,double ystart){
		double degree=Math.PI/180*10;
		PointP p=new PointP(0,0);
		for(double y=degree;p!=null;y=y+degree){
			p=equation(y,xstart,ystart);
			if(p!=null)
				addPoint(p.getX(),p.getY());
		}
		
	}
	
	public void setRadius(double c){
		a=c;
	}
	
	public double getRadius(){
		return a;
	}
	
	public void increaseRadius(double num){
		a=a+num;
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
		// TODO Auto-generated method stub
		
	}
}
