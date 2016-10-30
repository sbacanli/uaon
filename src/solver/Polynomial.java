package solver;

public class Polynomial {
	
	double x2=0;
	double x=0;
	double c=0;//constant
	double xr1;
	double xr2;
	
	public Polynomial(double x2,double x,double c) {
		this.x2=x2;
		this.x=x;
		this.c=c;
	}
	
	public void setx2(double f){
		x2=f;
	}
	
	public void setx(double f){
		x=f;
	}

	public void setc(double f){
		c=f;
	}
	
	public void calculate(){
		double discrimant = (x * x - 4 * x2 * c);
		
		
		if(discrimant>0){
			double temp1=Math.sqrt(discrimant);
			xr1 = (-1*x +  temp1) / (2*x2) ;
			xr2 = (-1*x -  temp1) / (2*x2) ;
			if(xr1<0){
				xr1=-1;
			}
			if(xr2<0){
				xr2=-1;
			}
			
		}else if(discrimant==0){
			xr1 = -1*x / (2 * x2);
            xr2 = -1*x / (2 * x2);
		}else{
			xr1=-1;
			xr2=-1;
		}        
	}
	
	public double getr1(){
		return xr1;
	}
	
	public double getr2(){
		return xr2;
	}
	
}
