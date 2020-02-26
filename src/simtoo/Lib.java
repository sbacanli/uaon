package simtoo;

/*
import static jcuda.driver.JCudaDriver.*;
import jcuda.Pointer;
import jcuda.driver.*;
*/

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;



import java.nio.ByteBuffer;


public class Lib {

	static String file;
	
	public static void init(String gfile) {
		file=gfile;
	}
	
	public static double relativeDistance(double x,double y,double x1,double y1){
		
		//return cudaFunc("relative", (float)x, (float)y, (float)x1, (float)y1);
		return Math.sqrt((x-x1)*(x-x1)+(y-y1)*(y-y1));
	}
	
	private static byte[] convertToByteArray(double value) {
	      byte[] bytes = new byte[8];
	      ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
	      buffer.putDouble(value);
	      return buffer.array();

	}
	 
	private static double toDouble(byte[] bytes) {
		    return ByteBuffer.wrap(bytes).getDouble();
	}
	
	public static double screenDistance(double x,double y,double x1,double y1){
		//return cudaFunc("relative", (float)x, (float)y, (float)x1, (float)y1);
		return Math.sqrt((x-x1)*(x-x1)+(y-y1)*(y-y1));
	}
	
	public static double screenDistance(PointP a,PointP b) {
		return screenDistance(a.getX(),a.getY(),b.getX(),b.getY());
	}
	
	/*
	private static float cudaFunc(String func,float x,float y,float x1,float y1) {
		int memorySize=Float.SIZE/8;
		cuInit(0);
		CUdevice device = new CUdevice();
		cuDeviceGet(device, 0);
		CUcontext context = new CUcontext();
		cuCtxCreate(context, 0, device);

		// Load the PTX that contains the kernel.
		CUmodule module = new CUmodule();
		cuModuleLoad(module, "distance.ptx");

		// Obtain a handle to the kernel function.
		CUfunction function = new CUfunction();
		cuModuleGetFunction(function, module, func);

		// Allocate the device input data, and copy the
		// host input data to the device
		CUdeviceptr deviceData = new CUdeviceptr();
		cuMemAlloc(deviceData, memorySize);
		
		
		// Set up the kernel parameters: A pointer to an array
		// of pointers which point to the actual values.
		Pointer kernelParameters = Pointer.to(
		    Pointer.to(convertToByteArray(x)), 
		    Pointer.to(convertToByteArray(y)), 
		    Pointer.to(convertToByteArray(x1)), 
		    Pointer.to(convertToByteArray(y1)),
		    Pointer.to(deviceData)
		);
		
		cuLaunchKernel(function, 255, 0, 0, 255, 0, 0, 
			    0, null, kernelParameters, null);
			cuCtxSynchronize();

        float[] hostOutput = new float[1];
		// Copy the data back from the device to the host and clean up
		cuMemcpyDtoH(Pointer.to(hostOutput), deviceData, memorySize);
		
		float returned=hostOutput[0];//toDouble(hostOutput);
		cuMemFree(deviceData);
		return returned;
	}
	*/
	
    public static void p(Object x){
		System.out.println(x.toString());
		BufferedWriter bwriter=null;
		try{
			bwriter=new BufferedWriter(new FileWriter(file,true));
			bwriter.write(x+"\r\n");
			bwriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}		
	}    
    
    public static double realdistance(double lat1,double lon1,double lat2,double lon2) {
    	//*
    	double R = 6371; // Radius of the earth in km
    	double dLat = deg2rad(lat2-lat1);  // deg2rad below
    	double dLon = deg2rad(lon2-lon1); 
    	double a = 
    			Math.sin(dLat/2) * Math.sin(dLat/2) +
    			Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
    			Math.sin(dLon/2) * Math.sin(dLon/2); 
    	double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
    	double d = R * c; // Distance in km
    	return d*1000;
    	//*/
    	//return cudaFunc("real", (float)lat1, (float)lon1, (float)lat2, (float)lon2);
    }
    

    //This method is used in real GPS distance calculation
	private static double deg2rad(double deg) {
	  return deg * (Math.PI/180);
	}
	    
	public static void printPositions(String fname,ArrayList<Position> arr){
		BufferedWriter bw;
		try{
			bw=new BufferedWriter(new FileWriter(fname));
			
			for(int i=0;i<arr.size();i++){
				String str=arr.get(i).realString();
				bw.write(str);
				bw.newLine();
			}
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static int binarySearch(int key,ArrayList<Position> parr){
        int lo = 0;
        int hi = parr.size() - 1;
        while (lo <= hi) {
            // Key is in a[lo..hi] or not present.
            int mid = lo + (hi - lo) / 2;
            if      (key < parr.get(mid).getTime()) hi = mid - 1;
            else if (key > parr.get(mid).getTime()) lo = mid + 1;
            else return mid;
        }
        return -1;
	}
	
	/** Read the object from Base64 string. */
	public static Object fromString( String s ) {
		   try {
				byte [] data = Base64.getDecoder().decode( s );
				ObjectInputStream ois = new ObjectInputStream( 
				                                new ByteArrayInputStream(  data ) );
				Object o  = ois.readObject();
				ois.close();
				return o;
		   }catch(Exception e) {
			   e.printStackTrace();
		   }
		   return null;
	   }

	    /** Write the object to a Base64 string. */
	public static String toString( Serializable o ){
    	try {
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		ObjectOutputStream oos = new ObjectOutputStream( baos );
    		oos.writeObject( o );
    		oos.close();
    		return Base64.getEncoder().encodeToString(baos.toByteArray()); 
    	}catch(Exception e) {
		   e.printStackTrace();
    	}
	    return null;
	}
	
	public static void createException(String mesg) {
		try{
			IllegalArgumentException e=new IllegalArgumentException(mesg);
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
