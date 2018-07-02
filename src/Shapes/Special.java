package Shapes;

import java.util.ArrayList;

import simtoo.Lib;
import simtoo.PointP;

public class Special extends Rectangle {
	
	/**
	 * @param a: width of the rectangle
	 * @param b: height of the rectangle
	 * @param xlim: maximum x coordinate
	 * @param ylim: maximum y coordinate
	 */
	double spiralA,maxRadius;
	
	public Special(double spiralA, double maxRadius, double a, double b, double xlim, double ylim) {
		super(true, a, b, xlim, ylim);
		this.spiralA=spiralA;
		this.maxRadius=maxRadius;
	}
	
	public double getSpiralA() {
		return spiralA;
	}
	
	public double getMaxRadius() {
		return maxRadius;
	}
	
	@Override
	public void fill(double xpos,double ypos){
		if(getNumberOfTours()%2==0){
			super.fill(xpos, ypos);
			//Lib.p("super called");
		}else {
			fillAsSpiral(spiralA,maxRadius,xpos,ypos);
			//Lib.p("spiral called");
		}
		/*
		PointP[] ar2=getPointsFillEnd(getCounter());
		//it means the limit is reached
		if(ar2==null) {
			Lib.p(" It is null at special");
			System.exit(-1);
		}
		if(ar2[0]==null) {
			setCounter();
			incNumberOfTours();
			ar2=getPointsFillEnd(getCounter());
		}
		if(getNumberOfTours()>0) {
			addPoint(xpos, ypos);
		}else {
			addPoint(ar2[0]);
			addPoint(ar2[1]);
			incCounter();
		}*/
	}
	
	//@Override
	//public void fill(){
		//this.fill(0.0,0.0);
		/*
		PointP[] ar2=getPointsFillEnd(getCounter());
		//it means the limit is reached
		
		if(ar2==null) {
			Lib.p(" It is null at special");
			System.exit(-1);
		}
		if(ar2[0]==null) {
			setCounter();
			incNumberOfTours();
			ar2=getPointsFillEnd(getCounter());
		}
		if(getNumberOfTours()==0) {
			addPoint(ar2[0]);
			addPoint(ar2[1]);
			incCounter();
		}
		**/
	//}
	
	private void fillAsSpiral(double spiralA, double maxradius,double xpos,double ypos) {
		Spiral s=new Spiral(spiralA,maxradius,getXlim(),getYlim());
		s.fill(xpos,ypos);
		ArrayList<PointP> pnts=s.getPoints();
		if(pnts.isEmpty() || pnts==null) {
			Lib.p("fillasspiral problem "+spiralA+" "+maxradius);
		}
		for(int i=0;i<pnts.size();i++) {
			addPoint(pnts.get(i));
		}
		//pnts.clear();
		s=null;		
	}
}
