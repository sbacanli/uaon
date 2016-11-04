package simtoo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

import routing.*;

public class Node extends Positionable{
	public ArrayList<Encounter> encounterHistory;
	public int nodeId;	
	
	public Node(int nid,int maxh,int maxw){
		super(maxh,maxw);
		setScreenSpeed(10);
		setRealSpeed(1);
		encounterHistory=new ArrayList<Encounter>();
		nodeId=nid;
	}
		
	public void encounter(int time,int eid){
		encounterHistory.add(new Encounter(eid,getId(),time));
		Lib.p("node id "+getId()+" is encountered with node "+eid);
	}
	
	public int getId(){
		return nodeId;
	}
	
	
}
