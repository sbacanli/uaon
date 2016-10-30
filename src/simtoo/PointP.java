package simtoo;

public class PointP {

	private double x;
	private double y;
	
	public PointP(double x,double y){
		this.x=x;
		this.y=y;
	}

	public double getX() {
		return x;
	}
	
	public double getY() {
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
	
	
}
