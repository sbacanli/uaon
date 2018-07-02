
#include <math.h>
extern "C"
__global__ void relative(double x1, double y1, double x2, double y2,double* result )
{
    int index = blockIdx.x * blockDim.x + threadIdx.x;
	double returned=0;
	double sum=(x1-x2)*(x1-x2)+(y1-y2)*(y1-y2);
    returned=sqrt(sum);
	if (index == 0) result[0] = returned;
}

extern "C"
__global__ void real(double lat1,double lon1,double lat2,double lon2,double* result )
{
    int index = blockIdx.x * blockDim.x + threadIdx.x;
	
	double returned=0;
	
    double R = 6371; // Radius of the earth in km
    double dLat = (lat2-lat1)*3.14159265359/180;  // deg2rad below
    double dLon = (lon2-lon1)*3.14159265359/180; 
    double a = 
    		sin(dLat/2) * sin(dLat/2) +
    		cos((lat1*3.14159265359/180)) * cos((lat2*3.14159265359/180)) * 
    		sin(dLon/2) * sin(dLon/2); 
    double c = 2 * atan2(sqrt(a), sqrt(1-a)); 
    double d = R * c; // Distance in km
    returned=d*1000;
	
	if (index == 0) result[0] = returned;
}

/*
double deg2rad(double deg) {
	double pi=3.14159265359;
	return deg * (pi/180);
}
*/

