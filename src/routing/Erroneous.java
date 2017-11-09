package routing;

public final class Erroneous {
	
	private static double errorrate=0.1;
	//error rate will be 10%
	//which means 10% of the messages will have an error
	
	//dont create an instance of this class!
	private Erroneous(){
		errorrate=0.1;
	}
	
	public static boolean noError(){

		double d=Math.random();
		if(d<=errorrate){
			return false;
		}
		return true;
	}

	
}
