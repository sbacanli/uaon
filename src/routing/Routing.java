package routing;

import simtoo.Lib;
import simtoo.Positionable;

public abstract class Routing {

		private Air air;
		private RoutingNode sender,receiver;
		private Positionable senderNode,receiverNode;
		//The node objects are also added so that location information can be used in routing decisions
		
		Routing(Air ag){
			air=ag;
			sender=null;
			receiver=null;
			senderNode=null;
			receiverNode=null;
		}
		
		public void setSender(RoutingNode gs){
			if(gs==null){
				Lib.p("sender null in Routing.java setSender");
			}
			sender=gs;
		}
		
		public void setReceiver(RoutingNode gr){
			if(gr==null){
				Lib.p("Receiver null in Routing.java setReceiver");
			}
			receiver=gr;
		}
		
		
		public RoutingNode getSender(){
			if(sender==null){
				Lib.p("sender null in Routing.java");
			}
			return sender;
		}
		
		public RoutingNode getReceiver(){
			if(receiver==null){
				Lib.p("receiver null in Routing.java");
			}
			return receiver;
		}
		
		
		public void setSenderNode(Positionable gs){
			if(gs==null){
				System.out.println("sender Node null in Routing.java setSenderNode");
			}
			senderNode=gs;
		}
		
		public void setReceiverNode(Positionable gr){
			if(gr==null){
				Lib.p("Receiver Node null in Routing.java setReceiverNode");
			}
			receiverNode=gr;
		}
		
		
		public Positionable getSenderNode(){
			if(senderNode==null){
				Lib.p("Sender Node null in Routing.java");
			}
			return senderNode;
		}
		
		public Positionable getReceiverNode(){
			if(receiverNode==null){
				Lib.p("Receiver Node null in Routing.java");
			}
			return receiverNode;
		}
		
		
		public Air getAir(){
			if(air==null){
				Lib.p("Air is null in Routing.java");
			}
			return air;
		}
		
		public abstract void send(String time);
		
		//what to do if sender encounters receiver
		//this method normally called once for sender as sender
		//once for receiver as sender in send(String time) method
		protected abstract void communicate(RoutingNode Sender,RoutingNode Receiver,String time);
	}


