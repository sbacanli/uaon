package DBSCAN;

import routing.Encounter;

public class myDistanceMetric implements DistanceMetric<Encounter> {

	public myDistanceMetric() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public double calculateDistance(Encounter val1, Encounter val2) throws DBSCANClusteringException {
		// TODO Auto-generated method stub
		return Math.sqrt( (val1.getPosition().getRealX()-val2.getPosition().getRealX())*
				(val1.getPosition().getRealX()-val2.getPosition().getRealX())+
				(val1.getPosition().getRealY()-val2.getPosition().getRealY())*
				(val1.getPosition().getRealY()-val2.getPosition().getRealY()) );
	}

}