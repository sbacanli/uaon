package DBSCAN;

public class DistanceMetricNumbers implements DistanceMetric<Number>{

	@Override
	public double calculateDistance(Number val1, Number val2) {
		return Math.abs(val1.doubleValue() - val2.doubleValue());
	}

}