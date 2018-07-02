package Shapes;


public class Line extends Shape {

	public Line(double xlim,double ylim){
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
		//nothing here
		clearPositions();
	}

	@Override
	public void fill(double xpos, double ypos) {
		clearPositions();
		addPoint(xpos, ypos);
		
	}

}
