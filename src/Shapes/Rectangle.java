package Shapes;


import simtoo.Lib;
import simtoo.PointP;

public class Rectangle extends Shape{
	
	private double a,b;
	private boolean isStart;
	private int counter;
	
	
	public Rectangle(boolean isStart,double a,double b,double xlim,double ylim){
		super(xlim,ylim);
		this.isStart=false;//isStart;
		this.a=a;
		this.b=b;
		counter=1;
		
	}
	
	public PointP initialPoint() {
		PointP[] p1=null;
		if(isStart) {
			p1=getPointsFillStart(1);
		}else {
			p1=getPointsFillEnd(1);
		}
		return p1[0];
	}
	
	public void setA(double ag){
		a=ag;
	}
	
	public void setB(double bg){
		b=bg;
	}
	
	public double getA(){
		return a;
	}
	
	public double getB(){
		return b;
	}
	
	public void decreaseA(double num){
		a=a-num;
		if(a<=0){
			setA(10);
		}
	}
	
	public void increaseA(double num){
		a=a+num;
	}
	
	public void decreaseB(double num){
		b=b-num;
		if(b<=0){
			setB(10);
		}
	}
	
	public void increaseB(double num){
		b=b+num;
	}

	public void fill(){
		if(!isPositionsEmpty()) {
			Lib.p("PROBLEM IN RECTANGLE");
		}
		PointP[] ar2=null;
		
		if(isStart){
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
	
	/*
	protected void fillstart(){
		boolean chk=true;
		PointP one,two=null;
		for(double i=a/2;i<xlim;i=i+a){
			if(chk){
				//add bottom
				one=new PointP(i,ylim-b/2);
				two=new PointP(i,b/2);
				chk=false;
			}else{
				//add top
				one=new PointP(i,b/2);
				two=new PointP(i,ylim-b/2);
				chk=true;
			}

			arr.add(one);
			arr.add(two);
		}
	}
	*/
	
	/**
	 * <p>This is for filling the points from start for route</p>
	 * @param counter will be used by the caller method should be greater than 1 
	 * @return the array of points to be added to points(projection should be done)
	 * @see PointP
	 */
	private PointP[] getPointsFillStart(int counter) {
		PointP[] ar2=new PointP[2];
		if(counter<1) {
			System.out.println("counter is zero for getpointsfillstart at rectangle");
			return null;
		}
		double firstTerm=((2*counter)-1)*getA()/2;

		if(firstTerm<getXlim()) {
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
	
	/**
	 * <p>This is for filling the points from end for route. Will be necessary for second UAV</p>
	 * @param counter will be used by the caller method should be greater than 1 
	 * @return the array of points to be added to points(projection should be done)
	 * @see PointP
	 */
	private PointP[] getPointsFillEnd(int counter) {
		PointP[] ar2=new PointP[2];
		if(counter<1) {
			System.out.println("counter is zero for getpointsfillend at rectangle");
			return null;
		}
		double firstTerm=getXlim()-(2*counter-1)*getA()/2;
		if(firstTerm>=getA()/2) {
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
		
		return ar2;		
	}
	
	
	public void updateSuccess(){

	}

	public void updateFail(){

	}
	
	@Override
	public void updateFail(double n) {
		//nothing will be done
	}
	
	@Override
	public void fill(double xpos,double ypos){
		fill();
	}

	protected int getCounter(){
		return counter;
	}
	
	protected void incCounter(){
		counter++;
	}
	
	protected void setCounter() {
		counter=0;
	}
}
