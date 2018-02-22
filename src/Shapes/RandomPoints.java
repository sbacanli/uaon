package Shapes;
import random.Random;

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
		double xpos=Random.nextDouble()*xlim;
		double ypos=Random.nextDouble()*ylim;
		addPoint(xpos, ypos);
	}

	@Override
	public void fill(double xpos, double ypos) {
		fill();
		
	}
	
	
}
