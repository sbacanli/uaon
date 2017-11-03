package Shapes;


import simtoo.PointP;

public class Rectangle extends Shape{
	
	double a,b;
	boolean isStart;
	
	public Rectangle(boolean isStart,double a,double b,double xlim,double ylim){
		super(xlim,ylim);
		this.isStart=isStart;
		this.a=a;
		this.b=b;
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
		if(isStart){
			fillstart();
		}else{
			fillend();
		}
	}
	
	private void fillstart(){
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
	
	private void fillend(){
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
