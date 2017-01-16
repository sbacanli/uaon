package simtoo;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

import routing.*;

public class Simulator {

	final double COMMDISTANCE;
	final int nodesSize;
	boolean random;
	int height;
	int width;
	Air air;
	Routing nodeRouting;
	Routing uavRouting;
	
	public Simulator()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		height= screenSize.height-10;
		width = screenSize.width-10;
		COMMDISTANCE=Datas.RealToVirtualDistance(100);
		nodesSize=39;
		air=new Air();
		nodeRouting=new Probabilistic(air,0.1);
		uavRouting=new Probabilistic(air,1);
		//random=true;
		random=false;
	}
	
	public void setNodeRouting(Routing rout){
		nodeRouting=rout;
	}

	public void setUavRouting(Routing rout){
		uavRouting=rout;
	}
	
	public int getNodesSize(){
		return nodesSize;
	}
	
	public boolean getRandom(){
		return random;
	}
	
	public void setRandom(){
		random=true;
	}
	
	public void unSetRandom(){
		random=false;
	}
	
	public void checkNodesDistances(Uav uavt,RoutingNode uavr,ArrayList<Node> nodesg,ArrayList<RoutingNode> routingNodes, int time){
		double xuav=uavt.getRealPosition(time).getX();
        double yuav=uavt.getRealPosition(time).getY();
        
		   for(int i=0;i<nodesg.size();i++){
			    RoutingNode r1=routingNodes.get(i);
	        	double x=nodesg.get(i).getRealPosition(time).getX();
	        	double y=nodesg.get(i).getRealPosition(time).getY();
	        	for(int j=0;j<i;j++){
	        		
					RoutingNode r2=routingNodes.get(j);
	        		if(i != j){
						double otherx=nodesg.get(j).getRealPosition(time).getX();
						double othery=nodesg.get(j).getRealPosition(time).getY();
						if(Lib.distance(x, y, otherx, othery) <= COMMDISTANCE){
							
							//if they are not in contact let us make them in contact
							if(!r2.isInContactWith(routingNodes.get(i).getId())){
								r2.addContact(r1.getId(), time); 
								r1.addContact(r2.getId(), time);								
								r2.addEncounter(r1.getId(), time); 
								r1.addEncounter(r2.getId(), time); 
							}
							
							nodeRoute(r1,r2,time+"");
							
							Lib.p("nodes encountered");
							
						}else{
							if(r2.isInContactWith(r1.getId())){
								r2.removeContact(r1.getId());
								r1.removeContact(r2.getId());
								r1.finishEncounter(r2.getId(), time);
								r2.finishEncounter(r1.getId(), time);
							}
							//if they are far and not in contact, no need to do anything.
						
						}
						
		        	}
	        	}
	        	
	        	if(Lib.distance(xuav, yuav, x, y) <= COMMDISTANCE){
	        		
	        	//uAV nin routing node karsýlýgý olmalý
	        		
	      			uavt.encounterWithNode(time,nodesg.get(i).getId());
	      			
	      			r1.addContact(uavr.getId(), time);
	      			r1.addEncounter(uavr.getId(), time);
	      			
	      			uavr.addContact(r1.getId(), time);
	      			uavr.addEncounter(r1.getId(), time);
	      			
	      			uavRoute(uavr,r1,time+"");
	      			Lib.p("Uav encountered");
	      			
	      		}else{
	      			if(r1.isInContactWith(uavr.getId())){
	      				r1.removeContact(uavr.getId());
	      				r1.finishEncounter(uavr.getId(), time);
	      				uavr.finishEncounter(r1.getId(), time);
	      				uavr.removeContact(r1.getId());
	      			}
	      			
	      		}
	        }
	}
	
	public double getCommDist(){
		return COMMDISTANCE;
	}
	
	public void nodeRoute(RoutingNode r1,RoutingNode r2, String time){
		nodeRouting.setSender(r1);
		nodeRouting.setReceiver(r2);
		nodeRouting.send(time);
	}
	
	public void uavRoute(RoutingNode uav,RoutingNode r2, String time){
		uavRouting.setSender(uav);
		uavRouting.setReceiver(r2);
		uavRouting.send(time);
	}
}
