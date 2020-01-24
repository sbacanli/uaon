
#include <math.h>
extern "C"
__global__ void relative(float x1, float y1, float x2, float y2,float* result )
{
    int index = blockIdx.x * blockDim.x + threadIdx.x;
	float returned=0;
	float sum=(x1-x2)*(x1-x2)+(y1-y2)*(y1-y2);
    returned=(float)sqrt((float)sum);
	if (index == 0) result[0] = returned;
}

extern "C"
__global__ void real(float lat1,float lon1,float lat2,float lon2,float* result )
{
    int index = blockIdx.x * blockDim.x + threadIdx.x;
	
	float returned=0;
	
    float R = 6371; // Radius of the earth in km
    float dLat = (lat2-lat1)* (3.14159265359/180);  // deg2rad below
    float dLon = (lon2-lon1)* (3.14159265359/180); 
    float a = 
    		sinf(dLat/2) * sinf(dLat/2) +
    		cosf(lat1* (3.14159265359/180)) * cosf(lat2* (3.14159265359/180)) * 
    		sinf(dLon/2) * sinf(dLon/2); 
    float c = 2 * atan2f(sqrt(a), sqrt(1-a)); 
    float d = R * c; // Distance in km
    returned=d*1000;
	
	if (index == 0) result[0] = returned;
}
