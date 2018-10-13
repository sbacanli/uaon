package DBSCAN;

import routing.Encounter;

public class myDistanceMetric implements DistanceMetric<Encounter>
{
	boolean isReal;

	public myDistanceMetric(boolean isReal)
	{
		this.isReal = isReal;
	}

	public double calculateDistance(Encounter val1, Encounter val2) throws DBSCANClusteringException
	{
		if (!isReal) {
			return Math.sqrt((val1.getPosition().getRealX() - val2.getPosition().getRealX()) * (
					val1.getPosition().getRealX() - val2.getPosition().getRealX()) + 
					(val1.getPosition().getRealY() - val2.getPosition().getRealY()) * (
							val1.getPosition().getRealY() - val2.getPosition().getRealY()));
		}
		return realdistance(val1.getPosition().getRealX(), val1.getPosition().getRealY(), 
				val2.getPosition().getRealX(), val2.getPosition().getRealY());
	}



	private static double realdistance(double lat1, double lon1, double lat2, double lon2)
	{
		double R = 6371.0D;
		double dLat = deg2rad(lat2 - lat1);
		double dLon = deg2rad(lon2 - lon1);
		double a = 
				Math.sin(dLat / 2.0D) * Math.sin(dLat / 2.0D) + 
				Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
				Math.sin(dLon / 2.0D) * Math.sin(dLon / 2.0D);
		double c = 2.0D * Math.atan2(Math.sqrt(a), Math.sqrt(1.0D - a));
		double d = R * c;
		return d * 1000.0D;
	}




	private static double deg2rad(double deg)
	{
		return deg * 0.017453292519943295D;
	}
}