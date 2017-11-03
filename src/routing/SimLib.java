package routing;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.text.Position;

import simtoo.Lib;


public class SimLib {

	/**
	 * Simulator class's Library functions that are dealing with
	 * arraylists, searching etc...
	 */

	
	//number of received packets for that message
	public static int howManyReceived(ArrayList<RoutingNode> nodes,Message message){
		int count=0;
		for(int i=0; i<nodes.size(); i++){
			if(nodes.get(i).searchBufferMessageId(message.getId()) != -1){
				count++;
			}
		}
		return count;
		
	}
	
	//prints all the node buffers
	//for debugging only
	public static String allbuf(ArrayList<RoutingNode> nodes){
		String sum="";
		for(int i=0; i<nodes.size(); i++){
			String s=nodes.get(i).getAllBuffer();
			if(s!=null){
				sum=sum+(i+1)+" node buffer "+s+"\r\n";
			}
		}
		return sum;
	}
	
	//find the node with that id
	public static int findId(ArrayList<RoutingNode> nodes,int id){
		for(int i=0;i<nodes.size();i++){
			if(nodes.get(i).getId()==id){
				return i;
			}
		}
		return -1;
	}
	
	public static int exists(int[][] arr,int el){
		for(int i=0;i<arr.length;i++){
			if(arr[i][0]==el){
				return i;
			}
		}
		return -1;
	}
	
	// hop count of all the messages
	// hop count of a message says how many times it has been sent
	public static ArrayList<Double> hopCounts(ArrayList<RoutingNode> nodes){
		ArrayList<Double> arr=new ArrayList<Double>();
		
		for(int i=0;i<nodes.size();i++){
			ArrayList<Message> buf=nodes.get(i).getMessageBuffer();
			if(buf !=null){
				for(int j=0;j<buf.size();j++){
					int hopn=buf.get(j).getHopCount();
					if(hopn != 0){
						arr.add(new Double((double) hopn));
					}
				}
			}//if buff not null
		}
		return arr; 
	}
	
	/*** Success rate related functions      ****************/
	//for given total number of messages: the number of messages sent in the simulation for broadcasting
	//and the list of all nodes it finds the success rates of the each message
	public static ArrayList<Double> successRate(final int numberOfmessages,ArrayList<RoutingNode> nodes){
		double[] srate=new double[numberOfmessages];
		
		ArrayList<Double> sratearr=new ArrayList<Double>();
		for(int i=0;i<nodes.size();i++){
			ArrayList<Message> list=nodes.get(i).getMessageBuffer();
			if(list != null && !list.isEmpty()){
				for(int j=0;j<list.size();j++){
					srate[list.get(j).getId()-1]++;	
				}
			}
		}
		
		for(int i=0;i<srate.length;i++){
				srate[i]=srate[i]/nodes.size();
				if(srate[i]>1){
					System.out.println("PROBLEM in SimLib.java Success rate can not be greater than 1");
				}
				sratearr.add(new Double(srate[i]));
		}
		 

		Collections.sort(sratearr);
		return sratearr;		
	}
	
	public static double averageSuccessRate(ArrayList<Double> srate){
		double sum=0;
		
		for(int i=0;i<srate.size();i++){
			sum += srate.get(i).doubleValue();
		}
		return sum/srate.size();
		
	}
	
	/*********Success rate related functions  ***************/
	
	/**Message Delay related functions   *******************/
	//for given a message number: the number of messages sent in the simulation for broadcasting
	//and the list of all nodes it finds the success rates of the each message
	public static double[] MessageDelayArray(int numberOfMessages,ArrayList<RoutingNode> nodes){
		ArrayList<ArrayList<Message>> messageArr=new ArrayList<ArrayList<Message>>(); 
		for(int h=0;h<numberOfMessages;h++){
			messageArr.add(new ArrayList<Message>());
		}
		
		for(int i=0;i<nodes.size();i++){
			RoutingNode n=nodes.get(i);
			if( !n.isBufferEmpty() ){
				ArrayList<Message> m=n.getMessageBuffer();
				
				for(int j=0;j<m.size();j++){
					Message message=m.get(j);
					int pos=message.getId();
					messageArr.get(pos-1).add(message);			
				}
			}
		}
		
		
		double[] mdelay=new double[numberOfMessages];
		for(int i=0;i<messageArr.size();i++){
			ArrayList<Message> m=messageArr.get(i);
			//System.out.println(i);
			if(m != null && !m.isEmpty() && m.size() > 1){
				
				int firstPacketTime=0;
				
				boolean entered=false;
				
				//there should be at least one packet whose sender id is 0
				//negative ids are for UAV, positive ones are for nodes
				//stating that this is the first packet created
				//the others will be the sent ones
				for(int j=0;!entered && j<m.size();j++){
					if(m.get(j).getSender()==0)
					{
						firstPacketTime=Integer.parseInt(m.get(j).getTime());	
						entered=true;
						//System.out.println(i);
					}
				}
				if(!entered || firstPacketTime<0)
				{
					//packet sent time can not be less than 0
					System.out.println("PROBLEM in average Message Delay Calculation in SimLib.java");
					System.out.println("METRICS: "+entered+" "+firstPacketTime);
				}
				
				int timeDifferences=0;
				for(int j=0;j<m.size();j++){
					timeDifferences=timeDifferences+(Integer.parseInt(m.get(j).getTime()) - firstPacketTime);
				}
				
				///it is possible that time differences might be 0
				//if some message is created at time 10 and also at the same time sent to some node,
				//then the delay will be 0
				if(timeDifferences<0){
					System.out.println("s less than zero in SimLib PROBLEM!!");
					for(int j=0;j<m.size();j++){
						System.out.println(m.get(j));
					}
				}
				
				mdelay[i]=(double)timeDifferences/(double)(m.size()-1);	
				
			}//message is empty null >1 check
		}
		Arrays.sort(mdelay);
		
		return mdelay;
	}
	/**Message Delay related functions   *******************/
	
	public static RoutingNode getNode(int id,ArrayList<RoutingNode> a){
		if(a==null || a.size()==0){
			return null;
		}
		for(int i=0;i<a.size();i++){
			if(a.get(i).getId()==id){
				return a.get(i);
			}
		}
		return null;
	}
	
	public static double getAverage(ArrayList<Double> arr){
		double sum=0;
		for(int i=0;i<arr.size();i++){
			sum=sum+arr.get(i).doubleValue();
		}
		return sum/arr.size();
	}
	
	//gets remaining TTL s for the all messages for the all nodes
	public static ArrayList<Double> remainings(ArrayList<RoutingNode> nodes){
		ArrayList<Double> arr=new ArrayList<Double>();
		
		for(int i=0;i<nodes.size();i++){
			ArrayList<Message> buf=nodes.get(i).getMessageBuffer();
			if(buf !=null){
				for(int j=0;j<buf.size();j++){
					int rem=buf.get(j).getRemaining();
					arr.add(new Double((double) rem));
				}
			}//if buff not null
		}
		return arr; 
	}
	
	//finds the common numbers between ArrayList a and b
	//returns the arraylist of common numbers
	public static ArrayList<Integer> commonNumbers(ArrayList<Integer> a, ArrayList<Integer> b){
		ArrayList<Integer> commons=new ArrayList<Integer>();
		//if one of them is empty it means no common elements
		if(a.isEmpty() || b.isEmpty()){
			return commons;
		}
		
		Collections.sort(a);
		Collections.sort(b);
		
		for(int i=0;i<a.size();i++){
			for(int j=0;j<b.size();j++){
				if(a.get(i)<b.get(j)){
					break;
				}
				if(a.get(i)==b.get(j)){
					commons.add(a.get(i));
				}
			}
		}
	
		return commons;
	}
	
}
