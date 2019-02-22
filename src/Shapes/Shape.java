package Shapes;

import java.util.ArrayList;

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

	public ArrayList<simtoo.PointP> getPoints() {
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

	public void addPoint(simtoo.PointP p) {
		arr.add(p); 
	}




	public abstract void fill(double paramDouble1, double paramDouble2);




	public abstract void fill();




	public abstract void updateSuccess();



	public abstract void updateFail();



	public abstract void updateFail(double paramDouble);



	public simtoo.PointP initialPoint()
	{
		return null;
	}

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