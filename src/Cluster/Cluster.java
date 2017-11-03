package Cluster;


import java.util.ArrayList;
import simtoo.PointP;

public class Cluster {
	
	public ArrayList<PointP> points;
	public PointP centroid;
	public int id;
	
	//Creates a new Cluster
	public Cluster(int id) {
		this.id = id;
		this.points = new ArrayList<PointP>();
		this.centroid = null;
	}

	public ArrayList<PointP> getPointPs() {
		return points;
	}
	
	public void addPointP(PointP point) {
		points.add(point);
	}

	public void setPointPs(ArrayList<PointP> points) {
		this.points = points;
	}

	public PointP getCentroid() {
		return centroid;
	}

	public void setCentroid(PointP centroid) {
		this.centroid = centroid;
	}

	public int getId() {
		return id;
	}
	
	public void clear() {
		points.clear();
	}
	
	public void plotCluster() {
		System.out.println("[Cluster: " + id+"]");
		System.out.println("[Centroid: " + centroid + "]");
		System.out.println("[PointPs: \n");
		for(PointP p : points) {
			System.out.println(p);
		}
		System.out.println("]");
	}

	public int getNumberOfPoints() {
		return points.size();
	}
	
}