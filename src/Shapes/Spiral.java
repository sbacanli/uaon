package Shapes;

//import random.Random;
import java.util.Random;
import simtoo.Lib;
import simtoo.PointP;


public class Spiral extends Shape{
	double a;
	int radiusChange;
	double maxradius;
	Random r;
	boolean randomGenerate;
	double minOf;
	
	public Spiral(double a,double maxradius,double xlim,double ylim){
		super(xlim,ylim);
		this.a=a;
		radiusChange=100;
		this.maxradius=maxradius;
		randomGenerate=false;
		if(maxradius == -1) {
			randomGenerate=true;
		}
		minOf=xlim;
		if(minOf>ylim) {
			minOf=ylim;
		}
		r=new Random(System.currentTimeMillis());
	}
	
	@Override
	public PointP initialPoint() {
		
		//double xpos=r.nextDouble()*getXlim();
		//double ypos=r.nextDouble()*getYlim();
		//return new PointP(xpos,ypos);
		return new PointP(getXlim()/2,getYlim()/2);
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
			//Lib.p("Limits done for spiral x "+x+" y "+y+"xlim "+getXlim()+" getYLim "+getYlim()+" xstart "+xstart+" ystart"+ystart);
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
		if(randomGenerate) {
			maxradius=r.nextInt((int)minOf)+1;
		}
		for(double y=degree;p!=null;y=y+degree){
			p=equation(y,xstart,ystart);
			if(p!=null) {
				double distan=distance(p.getX(),p.getY(),xstart,ystart);
				if (maxradius==-2 || distan<maxradius) {
					addPoint(p.getX(),p.getY());
				}else {
					//Lib.p("maxradius " +maxradius+" distance "+distan+" degree "+degree+" y "+y);
					break;
				}			
			}else {
				//Lib.p("It is null at spiral " +xstart+" "+ystart+" "+y);
			}//end of if p null check
		}//end of for
		
		if(getPoints().size()==0) {
			//Lib.p("Still empty "+getXlim()+" "+getYlim()+" "+xstart+" "+ystart+" in Spiral.java");
			addPoint(xstart,ystart);
		}else {
		
		}
	}
	
	private double distance(double x,double y,double x2,double y2) {
		return Math.sqrt(  (x-x2) *(x-x2)+(y-y2)*(y-y2) );
	}
	
	public void setRadius(double c){
		a=c;
	}
	
	public void setRandomRadius()
	{
		if (getXlim() > getYlim()) {
			setMaxRadius(r.nextDouble() * (getYlim() / 2.0D));
		} else {
			setMaxRadius(r.nextDouble() * (getXlim() / 2.0D));
		}
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
