package Shapes;


import simtoo.Lib;
import simtoo.PointP;

public class Rectangle extends Shape{
	
	private double a,b;
	private boolean isStart;
	private int counter;
	private int numberOfTours;
	
	public Rectangle(boolean isStart,double a,double b,double xlim,double ylim){
		super(xlim,ylim);
		this.isStart=isStart;
		this.a=a;
		this.b=b;
		counter=1;
		numberOfTours=0;
		
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
	
	public int getNumberOfTours() {
		return numberOfTours;
	}
	
	public void setB(double bg){
		b=bg;
	}
	
	public double getA(){
		return a;
	}
	
	public int getCounter(){
		return counter;
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
	
	protected void incCounter(){
		counter++;
	}
	
	public void fill(){
		if(!isPositionsEmpty()) {
			Lib.p("PROBLEM IN RECTANGLE");
		}
		PointP[] ar2=null;
		
		if(isStart){
			ar2=getPointsFillStart(counter);
			//it means the limit is reached
			if(ar2[0]==null) {
				numberOfTours++;
				counter=1;
				ar2=getPointsFillStart(counter);
			}
			
		}else{
			
			ar2=getPointsFillEnd(counter);
			
			if(ar2[0]==null) {
				counter=1;
				numberOfTours++;
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
	
	/*
	 * 
	 * x starts from 1
	 */
	protected PointP[] getPointsFillStart(int x) {
		PointP[] ar2=new PointP[2];
		//System.out.println("Nomre "+x);
		if(x<1) {
			System.out.println("x is zero");
			return null;
		}
		double firstTerm=(2*x-1)*getA()/2;
		if(firstTerm<getXlim()) {
			double yterm=getYlim()-getB()/2;
			double bterm=getB()/2;
			PointP p1=null;
			PointP p2=null;
			if((int)x%2==1) {
				p1=new PointP(firstTerm,yterm);
				p2=new PointP(firstTerm,bterm);
			}else {
				p1=new PointP(firstTerm,bterm);
				p2=new PointP(firstTerm,yterm);
			}
			ar2[0]=p1;
			ar2[1]=p2;
			//Lib.p(p1+"\n"+p2);
		}
		if(ar2==null) {
			System.out.println("AR2  null");
		}
		return ar2;		
	}
	
	protected PointP[] getPointsFillEnd(int x) {
		PointP[] ar2=new PointP[2];
		if(x<1) {
			System.out.println("x is zero");
			return null;
		}
		double firstTerm=getXlim()-(2*x-1)*getA()/2;
		if(firstTerm>=getA()/2) {
			double yterm=getYlim()-getB()/2;
			double bterm=getB()/2;
			PointP p1=null;
			PointP p2=null;
			if(x%2==0) {
				p1=new PointP(firstTerm,yterm);
				p2=new PointP(firstTerm,bterm);
			}else {
				p1=new PointP(firstTerm,bterm);
				p2=new PointP(firstTerm,yterm);
			}
			ar2[0]=p1;
			ar2[1]=p2;
			//Lib.p(p1+"\n"+p2);
		}		
		
		return ar2;		
	}
	
	/*
	protected void fillend(){
		boolean chk=true;
		PointP one,two=null;
		for(double i=xlim-a/2;i>=a/2;i=i-a){
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

}
