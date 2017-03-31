package routing;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

import simtoo.Lib;
//e.printStackTrace lines exist in this file.


public class Reporter {

	private static String fname;
	private static BufferedWriter bwriter;
	//number of received messages
	private static int numberOfReceivedByUAV=0;
	private static int numberOfAcksByUAV=0;
	//this is because of the error ratio in Erroneous class
	private static int numberOfDroppedByUAV=0;
	//number of messages sent!
	//number of messages sent=number of received+dropped+acks
	private static int numberOfSentByUAV=0;
	//number of messages which are added to buffer!
	// not all messages are added to buffer
	//a node may receive a message but it might be in the buffer already
	private static int numberOfAddedToBufferByUAV=0;

	
	private static int numberOfReceivedByNodes=0;
	//number of acknowledgements
	private static int numberOfAcksByNodes=0;
	//number of dropped messages
	//this is because of the error ratio in Erroneous class
	private static int numberOfDroppedByNodes=0;
	//number of messages sent!
	//number of messages sent=number of received+dropped+acks
	private static int numberOfSentByNodes=0;
	//number of messages which are added to buffer!
	// not all messages are added to buffer
	//a node may receive a message but it might be in the buffer already
	private static int numberOfAddedToBufferByNodes=0;

	
	//Output folder name set to Outputs
	private static String foldername="Outputs";
	
	
	//the written filename will be in fact the parameters of the simulation
	//that will be someVariable_Value_someVariable_Value etc...
	public static void init(String params){
		foldername=foldername+"_"+params;
		File fold=new File(foldername);
		if(!fold.exists()  || !fold.isDirectory()){
			fold.mkdir();
		}
		//set all of the parameters to Zero
		resetNumbers();
	}
	
	//set all of the parameters to Zero
	public static void resetNumbers(){
		numberOfAddedToBufferByNodes=0;
		numberOfDroppedByNodes=0;
		numberOfReceivedByNodes=0;
		numberOfSentByNodes=0;
		numberOfAcksByNodes=0;
		numberOfAddedToBufferByUAV=0;
		numberOfDroppedByUAV=0;
		numberOfReceivedByUAV=0;
		numberOfSentByUAV=0;
		numberOfAcksByUAV=0;
	}
	
	public static int getNumberOfSentByUAV(){
		return numberOfSentByUAV;
	}
	
	public static int getNumberOfSentByNodes(){
		return numberOfSentByNodes;
	}

	public static void writePacketsSents(){
		String s1=getNumberOfSentByUAV()+"\r\n";
		writeToFile("NumberOfPacketsSentBy_UAV.txt",s1);
		String s2=getNumberOfSentByNodes()+"\r\n";
		writeToFile("NumberOfPacketsSentBy_Nodes.txt",s2);
	}
	//the sender receiver and time is given to these methods but they are not used
	//For future implementations these values can be found and this class can be further extended
	public static void addPacketSent(int sender,int receiver,String time){
		Lib.p("Reporter addPacketSent is working");
		if(sender <0 ){
			numberOfSentByUAV++;
		}else{
			numberOfSentByNodes++;
		}
		Lib.p("Reporter class: Number of sents");
	}
	
	public static void addPacketAddedToBuffer(int sender,int receiver,String time){
		Lib.p("Reporter addPacketaddedTobuffer is working");
		if(receiver <0 ){
			numberOfAddedToBufferByUAV++;
		}else{
			numberOfAddedToBufferByNodes++;
		}
		
	}
	
	public static void addPacketDropped(int sender,int receiver,String time){
		Lib.p("Reporter addPacketDropped is working");
		if(sender <0 ){
			numberOfDroppedByUAV++;
		}else{
			numberOfDroppedByNodes++;
		}
	}
	
	public static void addPacketReceived(int sender,int receiver,String time){
		//addAckSent(sender,receiver,time);
		Lib.p("Reporter addPacketReceived is working");
		if(receiver <0 ){
			numberOfReceivedByUAV++;
		}else{
			numberOfReceivedByNodes++;
		}
		
	}

	
	//that method is for reporting the cases where the receiver's buffer is full
	//that method is not called in the simulator now
	//for further development that case might be used!
	public static void bufferFull(int id,String time){
		/*
		String line="bufferFull for node "+id+" time "+time; 
		try{
			bw.write(line+"\r\n");
		}catch(Exception e){
			e.printStackTrace();
		}
		//*/
	}
	
	//reads the fname(exact path should be given) to String ArrayList
	//each element will be line
	public static ArrayList<String> readTrace(String fname){
		ArrayList<String> a=new ArrayList<String>();
		String line;
		try{
			BufferedReader br = new BufferedReader(new FileReader(fname));
			while ((line = br.readLine()) != null) {
			   a.add(line);
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return a;
	}
	
	public static void closeFile(){
		try{
			bwriter.close();
		}catch(Exception e){
			Lib.p(e.toString());
		}
	}
	
	public static void writeTrace(ArrayList<String> s,String fname){
		try{
			bwriter=new BufferedWriter(new FileWriter(fname));
			for(int i=0;i<s.size();i++){
				StringTokenizer st=new StringTokenizer(s.get(i));
				while(st.hasMoreTokens()){
					String nt=st.nextToken();
					//System.out.print(nt+" ");
					bwriter.write(nt+"\t");
				}
				
				bwriter.write("\r\n");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//Writes the String to the filename
	public static void writeToFile(String fileName,String s){

		try{
			bwriter=new BufferedWriter(new FileWriter(foldername+"/"+fileName,true));
			bwriter.write(s);
			bwriter.close();
		}catch(Exception e){
			Lib.p(e.toString());
		}
	}	
	
	//returns the string representation of the parameters
	public static String PacketInfo(){
		String res="************STATS************\r\n"+
		numberOfReceivedByNodes+" packets received\r\n"+
		numberOfAddedToBufferByNodes+" packets added to Buffer\r\n"+
		numberOfDroppedByNodes+" packets dropped\r\n"+
		numberOfSentByNodes+" packets sent\r\n"+
		//numberOfAcks+" packets sent\r\n"+
		"*****************************\r\n";
		return res;
	}
	
	//writes message_delays and success rates
	//writes to text file called all.txt
	//with a tab between delay and success rate result paths
	public static void writeAllTextFile(){
		String fnameDelay=foldername+"/Message_Delays.txt";
		String fnameSuccess=foldername+"/success_rate.txt";
		File allf=new File("all.txt");
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(allf, true)))) {
		    fnameDelay="./"+fnameDelay;
		    fnameSuccess="./"+fnameSuccess;
		    //fnameDelay=fnameDelay.replace("/", "//");
		    //fnameSuccess=fnameSuccess.replace("/", "//");
			out.println(fnameDelay+"\r\n"+fnameSuccess);
			//System.out.println(fnameDelay+"\r\n"+fnameSuccess);
		}catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	//the key method
	//writes the results (message delay or success rates to the specified file)
	public static void writeArrayToFile(double[] arr,String s){
		//String all="Message Delay Time for each message \r\n";
		String all="";
		
		for(int i=0;i<arr.length;i++){
			//it is possible that message may not be broadcasted further
			//at this time the delay will be 0
			//don't write the message if delay is 0
			if(arr[i] != 0){
				all=all+LibRouting.precstr(LibRouting.prec(arr[i], 4),4)+"\r\n";
			}
		}
				
		writeToFile(s, all);
	}
	
	public static void writeArrayListToFile(ArrayList<Double> srate,String fname){
		String all="";
		for(int i=0;i<srate.size();i++){
			all += LibRouting.precstr(LibRouting.prec(srate.get(i).doubleValue(),4),4)+"\r\n";
		}
		
		writeToFile(fname, all);
	}
	
}
