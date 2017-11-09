package Shapes;


public class RandomPoints extends Shape{
	
	
	public RandomPoints(double xlim,double ylim){
		super(xlim,ylim);
	}
	
	public void fill(double xpos,double ypos){
		addPoint(xpos,ypos);
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

	
	@Override
	public void fill() {
		// TODO Auto-generated method stub
		
	}
	
	
}
