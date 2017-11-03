package simtoo;

import java.util.Comparator;

public class Position implements Comparator<Position> {
	
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
	
	//This is not used and encouraged not to be used.
	private Position(){
		time=-1;
		screenp=null;
		real=null;
		Lib.p("DONT USE NO ARGUEMENT CONSTRUCTOR OF POSITION.JAVA");
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
	
	public void setTime(int timeg){
		time=timeg;
	}
	
	public String toString(){
		return "Position is "+time+" realX "+real.getX()+" realY "+real.getY()+
				" screenX "+screenp.getX()+" screenY "+screenp.getY();
	}
	
	public String realString(){
		return time+"\t"+real.getX()+"\t"+real.getY();
	}

	@Override
	public int compare(Position o1, Position o2) {
		if(o1.time<o2.time){
			return -1;
		}else if(o1.time>o2.time){
			return 1;
		}
		return 0;
	}
}
