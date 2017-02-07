package simtoo;

public class Position {
	
	int time;
	PointP screenp;
	PointP real;
	
	
	public Position(int time,double screenx,double screeny,double xx,double yy){
		if(Double.isNaN(screenx) || Double.isNaN(screeny) || Double.isNaN(xx) || Double.isNaN(yy)){
			Lib.p("Position's screenx,screeny,realx,realy is NaN");
			System.exit(-1);
		}
		this.time=time;
		screenp=new PointP(screenx,screeny);
		real=new PointP(xx,yy);
	}
	
	public Position(int time,PointP scrp,PointP rp){
		this.time=time;
		screenp=new PointP(scrp.getX(),scrp.getY());
		real=new PointP(rp.getX(),rp.getY());
	}
	
	private Position(){
		time=-1;
		screenp=null;
		real=new PointP(0,0);
	}
	
	public PointP getScreenPoint(){
		return screenp;
	}
	
	public PointP getRealPoint(){
		return real;
	}
	
	public double getScreenX(){
		return screenp.getX();
	}
	
	public double getScreenY(){
		return screenp.getY();
	}
	
	public double getRealX(){
		return real.getX();
	}
	
	public double getRealY(){
		return real.getY();
	}
	
	public int getTime(){
		return time;
	}
}
