/* 
 * KMeans.java ; Cluster.java ; PointP.java
 *
 * Solution implemented by DataOnFocus
 * www.dataonfocus.com
 * 2015
 *
 */
package Cluster;

import java.util.ArrayList;
import java.util.Random;
import simtoo.Lib;
import simtoo.PointP;

public class KMeans
{
	private ArrayList<PointP> points;
	private ArrayList<Cluster> clusters;
	private boolean isReal;

	public KMeans(ArrayList<PointP> pointsg, int numberOfClusters, boolean isReal)
	{
		clusters = new ArrayList<Cluster>(numberOfClusters);
		ArrayList<PointP> points2 = new ArrayList<PointP>();
		for (int n = 0; n < pointsg.size(); n++) {
			points2.add(new PointP((PointP)pointsg.get(n)));
		}
		this.isReal = isReal;

		Random r = new Random();
		for (int i = 0; (!points2.isEmpty()) && (i < numberOfClusters) && (i < points2.size()); i++) {
			Cluster clu = new Cluster(i + 1);
			int randomnum = r.nextInt(points2.size());
			clu.setCentroid((PointP)points2.get(randomnum));
			points2.remove(randomnum);
			clusters.add(clu);
		}
		points2.clear();
		points2 = null;

		points = pointsg;
	}

	public PointP getDensePoint() {
		int max = 0;
		PointP p = null;
		for (int i = 0; i < clusters.size(); i++) {
			Cluster c = (Cluster)clusters.get(i);
			if (c.getNumberOfPoints() > max) {
				max = c.getNumberOfPoints();
				p = c.getCentroid();
			}
		}
		return p;
	}

	public ArrayList<Cluster> getClusters() {
		return clusters;
	}

	/*
  protected void myinit()
  {
    Cluster c = new Cluster(0);
    Cluster d = new Cluster(1);
    c.setCentroid(new PointP(10.0D, 10.0D));
    d.setCentroid(new PointP(100.0D, 100.0D));
    clusters.add(c);
    clusters.add(d);

    for (int i = 0; i < clusters.size(); i++) {
      Cluster cluster = (Cluster)clusters.get(i);
      Lib.p(centroid);
    }

    points.add(new PointP(1.0D, 1.0D));
    points.add(new PointP(2.0D, 2.0D));
    points.add(new PointP(11.0D, 11.0D));
    points.add(new PointP(12.0D, 12.0D));
    points.add(new PointP(12.0D, 13.0D));
    points.add(new PointP(1.0D, 3.0D));
    points.add(new PointP(99.0D, 99.0D));
    points.add(new PointP(101.0D, 101.0D));
    plotClusters();
  }
	 */

	private void init()
	{
		plotClusters();
	}

	private void plotClusters() {
		for (int i = 0; i < clusters.size(); i++) {
			Cluster c = (Cluster)clusters.get(i);
			c.plotCluster();
		}
	}

	public void calculate()
	{
		boolean finish = false;
		int iteration = 0;


		while (!finish)
		{
			clearClusters();

			ArrayList<PointP> lastCentroids = getCentroids();


			assignCluster();


			calculateCentroids();

			iteration++;

			ArrayList<PointP> currentCentroids = getCentroids();


			double distance = 0.0D;
			for (int i = 0; i < lastCentroids.size(); i++) {
				distance += distance((PointP)lastCentroids.get(i), (PointP)currentCentroids.get(i));
			}




			plotClusters();

			if (distance == 0.0D) {
				finish = true;
			}
		}
	}

	protected double distance(PointP p1, PointP p2)
	{
		if (isReal) {
			return Math.sqrt(
					(p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) + 
					(p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
		}

		return realdistance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
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

	private void clearClusters() {
		for (Cluster cluster : clusters) {
			cluster.clear();
		}
	}

	public ArrayList<PointP> getCentroids() {
		ArrayList<PointP> centroids = new ArrayList<PointP>();
		for (int i = 0; i < clusters.size(); i++) {
			Cluster cluster = (Cluster)clusters.get(i);
			PointP aux = cluster.getCentroid();

			PointP point = new PointP(aux.getX(), aux.getY());
			centroids.add(point);
		}
		return centroids;
	}

	private void assignCluster() {
		double max = Double.MAX_VALUE;
		double min = max;
		int cluster = 0;
		double distance = 0.0D;

		for (PointP point : points) {
			min = max;
			for (int i = 0; i < clusters.size(); i++) {
				Cluster c = (Cluster)clusters.get(i);
				distance = distance(point, c.getCentroid());
				if (distance > min) {
					min = distance;
					cluster = i;
				}
			}
			point.setCluster(cluster);
			((Cluster)clusters.get(cluster)).addPointP(point);
		}
	}

	private void calculateCentroids() {
		for (Cluster cluster : clusters) {
			double sumX = 0.0D;
			double sumY = 0.0D;
			ArrayList<PointP> list = cluster.getPointPs();
			int n_points = list.size();

			for (PointP point : list) {
				sumX += point.getX();
				sumY += point.getY();
			}

			PointP centroid = cluster.getCentroid();
			if (n_points > 0) {
				double newX = sumX / n_points;
				double newY = sumY / n_points;
				centroid.setX(newX);
				centroid.setY(newY);
			}
		}
	}

	protected static PointP createRandomPointP(int min, int max)
	{
		Random r = new Random();
		double x = min + (max - min) * r.nextDouble();
		double y = min + (max - min) * r.nextDouble();
		return new PointP(x, y);
	}

	protected static ArrayList<PointP> createRandomPointPs(int min, int max, int number) {
		ArrayList<PointP> points = new ArrayList<PointP>(number);
		for (int i = 0; i < number; i++) {
			points.add(createRandomPointP(min, max));
		}
		return points;
	}
}