package Cluster;

/* 
 * KMeans.java ; Cluster.java ; PointP.java
 *
 * Solution implemented by DataOnFocus
 * www.dataonfocus.com
 * 2015
 *
*/
import java.util.ArrayList;
import java.util.Random;

import simtoo.PointP;
import simtoo.Lib;

public class KMeans {

	//Number of Clusters. This metric should be related to the number of points
    //Number of PointPs
    
    //Min and Max X and Y
    private static final int MIN_COORDINATE = 0;
    private static final int MAX_COORDINATE = 150;
 
    //Number of Points
    private int NUM_POINTS = 800;
    private ArrayList<PointP> points;
    private ArrayList<Cluster> clusters;
    
    public KMeans(ArrayList<PointP> points,ArrayList<Cluster> clusters) {
    	this.points = points;
    	this.clusters = clusters;
    	NUM_POINTS=points.size();
    }
    
    public PointP getDensePoint(){
    	int max=0;
    	PointP p=null;
    	for (int i = 0; i < clusters.size(); i++) {
    		Cluster c = clusters.get(i);
    		if(c.getNumberOfPoints()>max){
    			max=c.getNumberOfPoints();
    			p=c.getCentroid();
    		}
    		System.out.println("HERE "+c.getNumberOfPoints());
    	}
    	return p;
    }
    /*
    public static void main(String[] args) {
    	ArrayList<Cluster> cl= new ArrayList<Cluster>();

    	ArrayList<PointP> pt=new ArrayList<PointP>();
    	
    	pt.clear();
    	cl.clear();
    	//KMeans kmeans = new KMeans(null,null);
    	KMeans kmeans = new KMeans(pt,cl);
    	kmeans.myinit();
    	kmeans.calculate();
    	System.out.println("Dense point is: "+kmeans.getDensePoint());
    }
    */
    
    public void myinit(){
    	Cluster c=new Cluster(0);
    	Cluster d=new Cluster(1);
    	c.setCentroid(new PointP(10,10));
    	d.setCentroid(new PointP(100,100));
    	clusters.add(c);
    	clusters.add(d);
    	
    	for (int i = 0; i < clusters.size(); i++) {
    		Cluster cluster = clusters.get(i);
    		Lib.p(cluster.centroid);
    	}
    	
    	points.add(new PointP(1,1));
    	points.add(new PointP(2,2));
    	points.add(new PointP(11,11));
    	points.add(new PointP(12,12));
    	points.add(new PointP(12,13));
    	points.add(new PointP(1,3));
    	points.add(new PointP(99,99));
    	points.add(new PointP(101,101));
    	plotClusters();
    }
    
    //Initializes the process
    public void init() {
    	/*
    	//Create PointPs
    	points = createRandomPointPs(MIN_COORDINATE,MAX_COORDINATE,NUM_POINTS);
    	
    	//Create Clusters
    	//Set Random Centroids
    	for (int i = 0; i < clusters.size();; i++) {
    		Cluster cluster = new Cluster(i);
    		PointP centroid = createRandomPointP(MIN_COORDINATE,MAX_COORDINATE);
    		cluster.setCentroid(centroid);
    		clusters.add(cluster);
    	}
    	//*/
    	//Print Initial state
    	plotClusters();
    }

	private void plotClusters() {
    	for (int i = 0; i < clusters.size(); i++) {
    		Cluster c = clusters.get(i);
    		c.plotCluster();
    	}
    }
    
	//The process to calculate the K Means, with iterating method.
    public void calculate() {
        boolean finish = false;
        int iteration = 0;
        
        // Add in new data, one at a time, recalculating centroids with each new one. 
        while(!finish) {
        	//Clear cluster state
        	clearClusters();
        	
        	ArrayList<PointP> lastCentroids = getCentroids();
        	
        	//Assign points to the closer cluster
        	assignCluster();
            
            //Calculate new centroids.
        	calculateCentroids();
        	
        	iteration++;
        	
        	ArrayList<PointP> currentCentroids = getCentroids();
        	
        	//Calculates total distance between new and old Centroids
        	double distance = 0;
        	for(int i = 0; i < lastCentroids.size(); i++) {
        		distance += distance(lastCentroids.get(i),currentCentroids.get(i));
        		System.out.println("distances "+distance);
        	}
        	System.out.println("#################");
        	System.out.println("Iteration: " + iteration);
        	System.out.println("Centroid distances: " + distance);
        	plotClusters();
        	        	
        	if(distance == 0) {
        		finish = true;
        	}
        }
    }
    
    public static double distance(PointP p1,PointP p2){
		return Math.sqrt(
				(  p1.getX()-p2.getX() )  *  (  p1.getX()-p2.getX()   )
				+ ( p1.getY()-p2.getY() ) *  (  p1.getY()-p2.getY()   )
				);
	}
    
    private void clearClusters() {
    	for(Cluster cluster : clusters) {
    		cluster.clear();
    	}
    }
    
    private ArrayList<PointP> getCentroids() {
    	ArrayList<PointP> centroids = new ArrayList<PointP>();
    	for(int i=0;i<clusters.size();i++) {
    		Cluster cluster=clusters.get(i);
    		PointP aux = cluster.getCentroid();
    		System.out.println("This one "+aux);
    		PointP point = new PointP(aux.getX(),aux.getY());
    		centroids.add(point);
    	}
    	return centroids;
    }
    
    private void assignCluster() {
        double max = Double.MAX_VALUE;
        double min = max; 
        int cluster = 0;                 
        double distance = 0.0; 
        
        for(PointP point : points) {
        	min = max;
            for(int i = 0; i < clusters.size(); i++) {
            	Cluster c = clusters.get(i);
                distance = distance(point, c.getCentroid());
                if(distance > min){
                    min = distance;
                    cluster = i;
                }
            }
            point.setCluster(cluster);
            clusters.get(cluster).addPointP(point);
        }
    }
    
    private void calculateCentroids() {
        for(Cluster cluster : clusters) {
            double sumX = 0;
            double sumY = 0;
            ArrayList<PointP> list = cluster.getPointPs();
            int n_points = list.size();
            
            for(PointP point : list) {
            	sumX += point.getX();
                sumY += point.getY();
            }
            
            PointP centroid = cluster.getCentroid();
            if(n_points > 0) {
            	double newX = sumX / n_points;
            	double newY = sumY / n_points;
                centroid.setX(newX);
                centroid.setY(newY);
            }
        }
    }
    
    //Creates random point
    protected static PointP createRandomPointP(int min, int max) {
    	Random r = new Random();
    	double x = min + (max - min) * r.nextDouble();
    	double y = min + (max - min) * r.nextDouble();
    	return new PointP(x,y);
    }
    
    protected static ArrayList<PointP> createRandomPointPs(int min, int max, int number) {
    	ArrayList<PointP> points = new ArrayList<PointP>(number);
    	for(int i = 0; i < number; i++) {
    		points.add(createRandomPointP(min,max));
    	}
    	return points;
    }
}