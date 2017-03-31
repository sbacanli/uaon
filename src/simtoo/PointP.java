package simtoo;

public class PointP {

	private double x;
	private double y;
	
	public PointP(double x,double y){
		if (Double.isNaN(x) || Double.isNaN(y) ){
			Lib.p("PointP's x or y is NaN in the constructor");
			System.exit(-1);
		}
		this.x=x;
		this.y=y;
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
	
	public String toString(){
		return x+", "+y;
	}
	
}
