package routing;


public abstract class Routing {

		private Air air;
		private RoutingNode sender,receiver;
		
		Routing(Air ag){
			air=ag;
			sender=null;
			receiver=null;
		}
		
		public void setSender(RoutingNode gs){
			sender=gs;
		}
		
		public void setReceiver(RoutingNode gr){
			receiver=gr;
		}
		
		
		public RoutingNode getSender(){
			if(sender==null){
				System.out.println("sender null");
			}
			return sender;
		}
		
		public RoutingNode getReceiver(){
			if(receiver==null){
				System.out.println("receiver null");
			}
			return receiver;
		}
		
		public Air getAir(){
			if(air==null){
				System.out.println("Air is null");
			}
			return air;
		}
		
		public abstract void send(String time);
		
		//what to do if sender encounters receiver
		//this method normally called once for sender as sender
		//once for receiver as sender in send(String time) method
		public abstract void communicate(RoutingNode Sender,RoutingNode Receiver,String time);
	}


