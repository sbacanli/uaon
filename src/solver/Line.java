package solver;

public class Line {

	double m;
	double a;
	
	public Line(double m,double a){
		this.m=m;
		this.a=a; 
	}
	
	public double getM(){
		return m;
	}
	
	public double getA(){
		return a;
	}
	
	public Line(double x,double y,double x1,double y1){
		m=(x-x1)/(y-y1);
		a=-1*m*x+y;
	}
	
}
