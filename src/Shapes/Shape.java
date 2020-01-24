package Shapes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import simtoo.Lib;
import simtoo.PointP;

enum ShapeType{
	SPECIAL,RECTANGLE,SPIRAL,RANDOM,LINE;
}

public abstract class Shape
{
	private ArrayList<simtoo.PointP> arr;
	private double xlim;
	private double ylim;
	private int numberOfTours;
	private double maxRadius;
	private ShapeType stype;

	protected Shape(double xlim, double ylim)
	{
		arr = new ArrayList<PointP>();
		this.xlim = xlim;
		this.ylim = ylim;
		numberOfTours = 0;
	}

	public void setShapeType(ShapeType gtype) {
		stype=gtype;
	}
	
	public ShapeType getShapeType() {
		return stype;
	}
	
	public void setMaxRadius(double givenradius) {
		maxRadius = givenradius;
	}

	public double getMaxRadius() {
		return maxRadius;
	}

	public int getNumberOfTours()
	{
		return numberOfTours;
	}

	public void incNumberOfTours() {
		numberOfTours += 1;
	}

	public ArrayList<PointP> getPoints() {
		return arr;
	}

	public double getXlim() {
		return xlim;
	}

	public double getYlim() {
		return ylim;
	}

	public void addPoint(double x, double y) {
		arr.add(new simtoo.PointP(x, y));
	}

	public void addPoint(PointP p) {
		if(p==null) {
			System.out.println("******************ERROR*****************************");
			System.out.println("******************ERROR*****************************");
			System.out.println("******************ERROR*****************************");
			System.out.println("p is null at Shape.java at addPoint(PointP p) method!");
			Exception e=new NullPointerException();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Lib.p(sw.toString());
			System.exit(-1);
		}else {
			arr.add(p);
		}		 
	}

	public abstract void fill(double paramDouble1, double paramDouble2);

	public abstract void fill();

	public abstract void updateSuccess();

	public abstract void updateFail();

	public abstract void updateFail(double paramDouble);

	public abstract simtoo.PointP initialPoint();
	//{return null;}

	public final void clearPositions() {
		arr.clear();
	}

	public void setRandomRadius() {}

	public final boolean isPositionsEmpty()
	{
		return arr.isEmpty();
	}

	public String toString() {
		return initialPoint() + " " + xlim + " " + ylim + " " + numberOfTours;
	}

	public void resetRadius() {
		// TODO Auto-generated method stub
		
	}
}