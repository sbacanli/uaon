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
  
  public ClusterParam(ClusterTechnique ct, int numberOfClusters, int radius)
  {
    this.numberOfClusters = numberOfClusters;
    mytech = ct;
    radiusCoef = radius;
  }
  
  public ClusterParam()
  {
    numberOfClusters = 0;
    mytech = null;
    radiusCoef = 0;
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
}
