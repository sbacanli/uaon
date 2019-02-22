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
	private double spiralA,maxRadius;
	private PointP maxPoint,minPoint;
	private double initRadius;
	private int limitcounter;
	
	public Special(double spiralA, double maxRadius, double a, double b, double xlim, double ylim,int limitcounter) {
		//isStart is true as we have only one UAV for now
		super(true, a, b, xlim, ylim);
		this.spiralA=spiralA;
		this.maxRadius = maxRadius;
		
		maxPoint=new PointP(getXlim(),getYlim());
		minPoint=new PointP(getA()/2,getYlim());
		this.limitcounter=limitcounter;
		initRadius=maxRadius;
	}
	
	public double getSpiralA() {
		return spiralA;
	}	
	
	public double getMaxRadius() {
		return maxRadius;
	}
	
	public boolean isMeandering() {
	    return getNumberOfTours() % 2 == 0;
	  }

	public void resetRadius() {
		maxRadius=initRadius;
	}
	
	public void setMaximumPoint(double x1,double y1) {
		maxPoint=new PointP(x1,y1);
	}
	
	public PointP getMaximumPoint() {
		return maxPoint;
	}
	
	public void setMinimumPoint(double x1,double y1) {
		minPoint=new PointP(x1,y1);
	}
	
	public PointP getMinimumPoint() {
		return minPoint;
	}
	
	@Override
	public void fill(double xpos,double ypos){
		if(isMeandering()){
			fillAsMeander(xpos, ypos);
			//Lib.p("super called");
		}else {
			fillAsSpiral(spiralA,maxRadius,xpos,ypos);
		}
	}
	
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
				Lib.p("Tours increased");
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
		double firstTerm=((2*counter)-1)*getMinimumPoint().getX();

		if(firstTerm<getXlim() && firstTerm < getMaximumPoint().getX()) {
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
		for(int i=0;i<pnts.size();i++) {
			addPoint(pnts.get(i));
		}
		s=null;		
	}
	
	
	public void setClusterBorderForMaxX(double xlim2, double ylim2) {
		if( (getNumberOfTours()+1)%limitcounter == 0) {
			setMaximumPoint(getXlim(), getYlim());
		}else if ( (getNumberOfTours()+1)%limitcounter == 1){
			setMaximumPoint(xlim2, ylim2);
		}
	} 
	
	public void setClusterBorderForMinX(double xlim2, double ylim2) {
		if( (getNumberOfTours()+1)%limitcounter == 0) {
			setMinimumPoint(getA()/2, getYlim());
		}else if ( (getNumberOfTours()+1)%limitcounter == 1){
			setMinimumPoint(xlim2, ylim2);
		}
	}
}
