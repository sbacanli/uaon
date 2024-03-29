package simtoo;

import java.io.Serializable;
import java.util.Comparator;

public class Position implements Comparator<Position>,Serializable {
	
	long time;
	PointP screenp;
	PointP real;
	
	
	public Position(long time,double screenx,double screeny,double xx,double yy){
		if(Double.isNaN(screenx) || Double.isNaN(screeny) || Double.isNaN(xx) || Double.isNaN(yy)){
			Lib.p("Position's screenx,screeny,realx,realy is NaN");
			System.exit(-1);
		}
		this.time=time;
		screenp=new PointP(screenx,screeny);
		real=new PointP(xx,yy);
	}
	
	public Position(long time,PointP scrp,PointP rp){
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
	
	public Position(Position p){
		time=p.time;
		screenp=new PointP(p.getScreenX(),p.getScreenY());
		real=new PointP(p.getRealX(),p.getRealY());
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
	
	public long getTime(){
		return time;
	}
	
	public void setTime(long lasttime){
		time=lasttime;
	}
	
	public String toString(){
		return "Position's time is "+time+" realX "+real.getX()+" realY "+real.getY()+
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
