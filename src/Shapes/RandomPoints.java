package Shapes;
import random.Random;
import simtoo.PointP;

public class RandomPoints extends Shape{
	
	
	public RandomPoints(double xlim,double ylim){
		super(xlim,ylim);
	}

	@Override
	public void updateSuccess() {
		//nothing will be done
		
	}

	@Override
	public void updateFail() {
		//nothing will be done
	}

	@Override
	public void updateFail(double n) {
		//nothing will be done
	}

	
	public void fill() {
		clearPositions();
		double xpos=Random.nextDouble()*getXlim();
		double ypos=Random.nextDouble()*getYlim();
		addPoint(xpos, ypos);
	}

	@Override
	public void fill(double xpos, double ypos) {
		clearPositions();
		fill();
		
	}
	
	@Override
	public PointP initialPoint() {
		double xpos=Random.nextDouble()*getXlim();
		double ypos=Random.nextDouble()*getYlim();
		return new PointP(xpos,ypos);
	}
	
	
}
