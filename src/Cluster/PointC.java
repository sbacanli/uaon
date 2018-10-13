package Cluster;

import java.util.ArrayList;
import java.util.Random;


public class PointC
{
	private double x = 0.0D;
	private double y = 0.0D;
	private int cluster_number = 0;

	public PointC(double x, double y)
	{
		setX(x);
		setY(y);
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getX() {
		return x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getY() {
		return y;
	}

	public void setCluster(int n) {
		cluster_number = n;
	}

	public int getCluster() {
		return cluster_number;
	}

	protected static double distance(PointC p, PointC centroid)
	{
		return Math.sqrt(Math.pow(centroid.getY() - p.getY(), 2.0D) + Math.pow(centroid.getX() - p.getX(), 2.0D));
	}

	protected static PointC createRandomPointC(int min, int max)
	{
		Random r = new Random();
		double x = min + (max - min) * r.nextDouble();
		double y = min + (max - min) * r.nextDouble();
		return new PointC(x, y);
	}

	protected static ArrayList<PointC> createRandomPointCs(int min, int max, int number) {
		ArrayList<PointC> points = new ArrayList<PointC>(number);
		for (int i = 0; i < number; i++) {
			points.add(createRandomPointC(min, max));
		}
		return points;
	}

	public String toString() {
		return "(" + x + "," + y + ")";
	}
}