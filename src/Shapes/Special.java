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
	int limitcounter;
	
	public Special(double spiralA, double maxRadius, double a, double b, double xlim, double ylim) {
		super(true, a, b, xlim, ylim);
		this.spiralA=spiralA;
		limitcounter=4;
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
			fillAsMeander(xpos, ypos);
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
	
	private void fillAsMeander(double xpos,double ypos) {
		boolean limitsDone=false;
		if(!isPositionsEmpty()) {
			Lib.p("PROBLEM IN SPECIAL");
		}
		PointP[] ar2=null;
		
		if(getIsStart()){
			ar2=getPointsFillStart(counter);
			//it means the limit is reached
			if(ar2[0]==null && ar2[1]==null) {
				incNumberOfTours();
				counter=1;
				ar2=getPointsFillStart(counter);
			}
			
		}else{
			
			ar2=getPointsFillEnd(counter);
			
			if(ar2[0]==null && ar2[1]==null) {
				counter=1;
				incNumberOfTours();
				ar2=getPointsFillEnd(counter);
				
			}
		}
		counter=counter+1;
		addPoint(ar2[0]);
		addPoint(ar2[1]);
	}
	
	protected PointP[] getPointsFillStart(int counter) {
		PointP[] ar2=new PointP[2];
		if(counter<1) {
			System.out.println("counter is zero for getpointsfillstart at rectangle");
			return null;
		}
		double firstTerm=((2*counter)-1)*getA()/2;

		if(firstTerm<getXlim() && firstTerm < getMaximumPoint().getX() && (getNumberOfTours()+1)%limitcounter < 0) {
			double yterm=getYlim()-getB()/2;
			double bterm=getB()/2;
			PointP p1=null;
			PointP p2=null;
			if((int)counter%2==1) {
				p1=new PointP(firstTerm,yterm);
				p2=new PointP(firstTerm,bterm);
			}else {
				p1=new PointP(firstTerm,bterm);
				p2=new PointP(firstTerm,yterm);
			}
			ar2[0]=p1;
			ar2[1]=p2;
		}
		if(ar2==null) {
			System.out.println("AR2  null");
		}
		return ar2;		
		
	}
	
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