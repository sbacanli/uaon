package simtoo;

enum ClusterTechnique
{
    KMEANS,DBSCAN;
}

public class ClusterParam
{
  int numberOfClusters = 5;
  ClusterTechnique mytech;
  int radiusCoef;
  double maxDistance;
  
  /*
   * @param ClusterTechnique, numberOfClusters,radiusCoefficient, maxDistanceForDBSCAN
   * <>
   * ClusterTechnique as Enum KMEANS or DBSCAN
   * numberOfClusters used in KMEANS clustering.
   * radiusCoefficient used in all clustering techniques. The maximum distance between 2 points in the same cluster is
   * divided by that number to create a spiral with that result radius size. 
   * maxDistanceForDBSCAN DBSCAN algorithm does not take number of clusters are parameter 
   * but the maximum distance between 2 arbitrary nodes
   * </p>
   */
  public ClusterParam(ClusterTechnique ct, int numberOfClusters, int radiusCoefficient,double maxDistanceForDBSCAN)
  {
    this.numberOfClusters = numberOfClusters;
    mytech = ct;
    radiusCoef = radiusCoefficient;
    maxDistance=maxDistanceForDBSCAN;
  }
  
  /* 
   * @param
   * This is implemented but never to be used. The parameters are dummy. NOT SUGGESTED TO BE USED
   * The properties should be set later!
   */
  public ClusterParam()
  {
    numberOfClusters = 0;
    mytech = null;
    radiusCoef = 0;
    maxDistance=1000;
  }
  
  public ClusterTechnique getTechnique() {
    return mytech;
  }
  
  public int getNumberOfClusters() {
    return numberOfClusters;
  }
  
  public int getRadiusCoefficient() {
    return radiusCoef;
  }
  
  public double getMaxDistance() {
	  return maxDistance;
  }
}
