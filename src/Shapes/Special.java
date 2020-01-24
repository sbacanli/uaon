package Shapes;

import java.util.ArrayList;

import javafx.scene.chart.PieChart.Data;
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
	
	@Override
	public PointP initialPoint() {
		counter=1;
		PointP[] p1=null;
		if(getIsStart()) {			
			p1=getPointsFillStart(1);
		}else {
			p1=getPointsFillEnd(1);
		}
		return p1[0];
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
				if(ar2[0]==null && ar2[1]==null) {
					//The frame limit is too small to cruise around
					//we will increase the limits again here
					resetLimits();	
					ar2=getPointsFillStart(counter);
				}
			}
			
		}else{
			
			ar2=getPointsFillEnd(counter);
			
			if(ar2[0]==null && ar2[1]==null) {
				counter=1;
				incNumberOfTours();
				ar2=getPointsFillEnd(counter);		
				if(ar2[0]==null && ar2[1]==null) {
					//The frame limit is too small to cruise around
					//we will increase the limits again here
					resetLimits();	
					ar2=getPointsFillEnd(counter);	
				}
			}
		}
		counter=counter+1;
		addPoint(ar2[0]);
		addPoint(ar2[1]);
	}
	/*
	 * (starts from right most top corner on the screen)
	 * The first UAV starts from there
	 */
	@Override
	protected PointP[] getPointsFillStart(final int counter) {
		PointP[] ar2=new PointP[2];
		if(counter<1) {
			System.out.println("counter is zero for getpointsfillstart at special.java");
			return null;
		}
		double firstTerm=((2*counter)-1)*getA()+getMinimumPoint().getX();

		if(firstTerm < getMaximumPoint().getX()) {
			double yterm=getYlim()-getB();
			double bterm=getB();
			PointP p1=null;
			PointP p2=null;
			if(((int)counter)%2==1) {
				p1=new PointP(firstTerm,yterm);
				p2=new PointP(firstTerm,bterm);
			}else {
				p1=new PointP(firstTerm,bterm);
				p2=new PointP(firstTerm,yterm);
			}
			ar2[0]=p1;
			ar2[1]=p2;
			/*Lib.p("A value "+getA()+" B value "+getB());
			Lib.p("Everything perfect!");
			Lib.p("getPointsFillStart method firstterm is "+firstTerm+" getXlim() "+getXlim()+" getmaxPoint "+getMaximumPoint());
			Lib.p("minimumPoint " +getMinimumPoint());
			Lib.p("counter "+counter);
			Lib.p(ar2[0]);
			Lib.p(ar2[1]);
			*/
		}else {
			/*
			Lib.p("PROBLEM HERE MAYBE?");
			Lib.p("A value "+getA()+" B value "+getB());
			Lib.p("getPointsFillStart method firstterm is "+firstTerm+" getXlim() "+getXlim()+" getmaxPoint "+getMaximumPoint());
			Lib.p("minimumPoint " +getMinimumPoint());
			Lib.p("counter "+counter);
			*/
		}
		
		return ar2;				
	}
	
	@Override
	protected PointP[] getPointsFillEnd(final int counter) {
		PointP[] ar2=new PointP[2];
		if(counter<1) {
			System.out.println("counter is zero for getpointsfillend at special.java");
			return null;
		}
		
		double firstTerm=getMaximumPoint().getX()-((2*counter)-1)*getA();
		if(firstTerm > getMinimumPoint().getX()) {
			double yterm=getYlim()-getB();
			double bterm=getB();
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
		pnts=null;
	}
	
	private void resetLimits() {
		setMaximumPoint(getXlim(), getYlim());
		setMinimumPoint(getA(), getYlim());
		counter=1;
		incNumberOfTours();
	}
	
	//these are screen points
	public void setClusterBorderForMaxX(double xlim2, double ylim2) {
		if( (getNumberOfTours()+1)%limitcounter == 0) {
			setMaximumPoint(getXlim(), getYlim());
		}else if ( (getNumberOfTours()+1)%limitcounter == 1){
			setMaximumPoint(xlim2, ylim2);
		}
	} 
	
	//these are screen points
	public void setClusterBorderForMinX(double xlim2, double ylim2) {
		if( (getNumberOfTours()+1)%limitcounter == 0) {
			setMinimumPoint(0, getYlim());
			/*getA()*/
		}else if ( (getNumberOfTours()+1)%limitcounter == 1){
			setMinimumPoint(xlim2, ylim2);
		}
	}
}
