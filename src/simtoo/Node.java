package simtoo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

import routing.*;

public class Node extends Positionable{

	public int nodeId;	
	
	public Node(int nid,int maxh,int maxw){
		super(maxh,maxw);
		setScreenSpeed(10);
		setRealSpeed(1);
		nodeId=nid;
	}
		
	public int getId(){
		return nodeId;
	}
	
	
}
