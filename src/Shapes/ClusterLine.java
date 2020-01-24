package Shapes;

import java.util.ArrayList;
import simtoo.PointP;

public class ClusterLine extends Special
{
	public ClusterLine(double spiralA, double maxRadius, double a, double b, double xlim, double ylim,int limitcounter)
	{
		super(spiralA, maxRadius, a, b, xlim, ylim,limitcounter);
	}


	public void fill(double xpos, double ypos)
	{
		if (isMeandering()) {
			super.fill(xpos, ypos);
		}
		else {
			fillAsLine(xpos, ypos);
		}
	}

	private void fillAsLine(double xpos, double ypos) {
		Line s = null;
		ArrayList<PointP> pnts = null;
		s = new Line(getXlim(), getYlim());
		s.fill(xpos, ypos);
		pnts = s.getPoints();

		for (int i = 0; i < pnts.size(); i++) {
			addPoint((PointP)pnts.get(i));
		}

		s = null;
	}
}