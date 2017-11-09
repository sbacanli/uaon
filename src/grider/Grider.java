package grider;
import routing.*;
import simtoo.PointP;

import java.util.ArrayList;
import random.Random;

public class Grider {

	protected int numx;
	protected int numy;
	protected double xmin,ymin,xmax,ymax;
	protected int[][] grid;
	protected double xc,yc;
	protected int comms;
	/*
	public static void main(String[] args) {
		ArrayList<Encounter> arr=new ArrayList<Encounter>();
		for(int i=0;i<10;i++){
			Position p=new Position(i, i*4, i*5, i*20, i*50);
			Encounter e=new Encounter(1, 1, p, 1);
			arr.add(e);
		}
		Position p=new Position(1, 100, -100, 200, 500);
		Encounter e=new Encounter(1, 1, p, 1);
		arr.add(e);
		Grider g=new Grider(10,10,-100,200,-100,500,arr);
		g.process();
		g.pri();
		
	}
	//*/
	
	public Grider(int numx,int numy,double xmin,double xmax,double ymin,double ymax){
		this.numx=numx;
		this.numy=numy;
		this.xmin=xmin;
		this.ymin=ymin;
		this.xmax=xmax;
		this.ymax=ymax;
		grid=new int[numx][numy];
		xc=(xmax-xmin)/numx;
		yc=(ymax-ymin)/numy;
	}
	
	//processes the 
	public void process(ArrayList<Encounter> arr){

		for(int i=0;i<arr.size();i++){
			double x=arr.get(i).getPosition().getRealX()-xmin;
			double y=arr.get(i).getPosition().getRealY()-ymin;
			int xpos=(int)(x/xc);
			int ypos=(int)(y/yc);
			if(xpos>numx-1){
				xpos--;
			}
			if(ypos>numy-1){
				ypos--;
			}
			grid[xpos][ypos]++;
		}
	}
	
	public PointP max(){
		int max=-1;
		int mi=0;
		int mj=0;
		for(int i=0;i<grid.length;i++){
			for(int j=0;j<grid[0].length;j++){
				if(grid[i][j]>max){
					max=grid[i][j];
					mi=i;
					mj=j;
				}
			}
		}
		return new PointP(mj*xc+(xc/2)+xmin,mi*yc+(yc/2)+ymin);
	}
	
	public PointP randomLoc(){
		int mi=Random.nextInt(grid.length);
		int mj=Random.nextInt(grid[0].length);
		return new PointP(mj*xc+(xc/2)+xmin,mi*yc+(yc/2)+ymin);
	}
	
	private void pri(){
		for(int i=0;i<grid.length;i++){
			for(int j=0;j<grid[0].length;j++){
				System.out.print(grid[i][j]+" ");
			}
			System.out.println();
		}
	}

}
