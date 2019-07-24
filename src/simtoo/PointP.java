package simtoo;

import java.io.Serializable;
import routing.LibRouting;


public class PointP implements Serializable {

	private double x;
	private double y;
	private int cluster_number;
	
	public PointP(double x,double y){
		if (Double.isNaN(x) || Double.isNaN(y) ){
			Lib.p("PointP's x or y is NaN in the constructor");
			System.exit(-1);
		}
		this.x=precPt(x);
		this.y=precPt(y);
		this.cluster_number=0;
	}
	
	public PointP(PointP screenPoint) {
		x=precPt(screenPoint.x);
		y=precPt(screenPoint.y);
	}

	public double getX() {
		if (Double.isNaN(x)) {
			Lib.p("PointP's x is NaN");
			System.exit(-1);
		}
		return x;
	}
	
	public double getY() {
		if (Double.isNaN(y)) {
			Lib.p("PointP's y is NaN");
			System.exit(-1);
		}
		return y;
	}
	
	public void setX(double xx) {
		if(xx<0){
			Lib.p("X can not be less than 0");
		}else{
			x=xx;
		}
		
	}
	
	public void setY(double yy) {
		if(yy<0){
			Lib.p("Y can not be less than 0");
		}else{
			y=yy;
		}
	}
	
    public void setCluster(int n) {
        cluster_number = n;
    }
    
    public int getCluster() {
        return cluster_number;
    }
    
    
	public String toString(){
		return x+", "+y;
	}
	
	private double precPt(double value) {
		return value; 
		//LibRouting.prec(value, 7);
	}
	
	public boolean equals(PointP p){
		//return LibRouting.prec(p.getX(),6)==LibRouting.prec(getX(),6) && LibRouting.prec(p.getY(),6)==LibRouting.prec(getY(),6);
		return precPt(p.getX())==precPt(getX()) && precPt(p.getY())==precPt(getY());
	}
	
	public boolean closeEnough(PointP p) {
			return LibRouting.prec(p.getX(),7)==LibRouting.prec(getX(),7) && LibRouting.prec(p.getY(),7)==LibRouting.prec(getY(),7);
	}
	
}
