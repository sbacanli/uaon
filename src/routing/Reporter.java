package routing;

import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import simtoo.Lib;
import simtoo.Uav;
//e.printStackTrace lines exist in this file.



public class Reporter {

	private static String fname;
	private static BufferedWriter bwriter;
	//number of received messages
	private static int numberOfReceivedByUAVs=0;
	private static int numberOfAcksByUAVs=0;
	//this is because of the error ratio in Erroneous class
	private static int numberOfDroppedByUAVs=0;

	//number of messages which are added to buffer!
	// not all messages are added to buffer
	//a node may receive a message but it might be in the buffer already
	private static int numberOfAddedToBufferByUAVs=0;

	
	private static int numberOfReceivedByNodes=0;
	//number of acknowledgements
	private static int numberOfAcksByNodes=0;
	//number of dropped messages
	//this is because of the error ratio in Erroneous class
	private static int numberOfDroppedByNodes=0;


	//number of messages which are added to buffer!
	// not all messages are added to buffer
	//a node may receive a message but it might be in the buffer already
	private static int numberOfAddedToBufferByNodes=0;

	private static int numberOfSentBetweenUAVs=0;
	
	private static int numberOfSentBetweenNodes=0;
	
	private static int numberOfSentByUAVToNodes=0;
	
	private static int numberOfSentByNodeToUAVs=0;
	
	//Output folder name set to Outputs
	private static String foldername="";
	private static Date runstart;
	
	//the written filename will be in fact the parameters of the simulation
	//that will be someVariable_Value_someVariable_Value etc...
	public static void init(String params){
		foldername=params;
		File fold=new File(foldername);
		if(!fold.exists()  || !fold.isDirectory()){
			fold.mkdir();
		}
		//set all of the parameters to Zero
		resetNumbers();
		Lib.p("***************************************************************");
		runstart=getCurrentTime();
		/*
		//deleting the report generated by Lib.p(String) function
		File del=new File("Report.txt");
		if(del.exists()){
			del.delete();
		}
		*/
		
	}
	
	//prints the time difference between program start and end
	public static void printExecutionTime(){
		Date now=getCurrentTime();
        long duration  = now.getTime() - runstart.getTime();
    	long diff = TimeUnit.MILLISECONDS.toSeconds(duration);
    	long diffSeconds = diff;
    	long diffMinutes = diff / 60;
    	//long diffHours = diff / 3600;

    	Lib.p("Execution took "+diffMinutes+" minutes "+(diffSeconds-(60*diffMinutes))+" secs");

	}
	
	public static Date getCurrentTime(){
		    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		    Calendar cal = Calendar.getInstance();
		    return cal.getTime();
	}
	  
	//set all of the parameters to Zero
	public static void resetNumbers(){
		numberOfAddedToBufferByNodes=0;
		numberOfDroppedByNodes=0;
		numberOfReceivedByNodes=0;
		numberOfAcksByNodes=0;
		numberOfAddedToBufferByUAVs=0;
		numberOfDroppedByUAVs=0;
		numberOfReceivedByUAVs=0;
		numberOfAcksByUAVs=0;
		numberOfSentBetweenUAVs=0;
		numberOfSentBetweenNodes=0;
		numberOfSentByUAVToNodes=0;
		numberOfSentByNodeToUAVs=0;
	}
	
	public static int getNumberOfSentBetweenUAVs(){
		return numberOfSentBetweenUAVs;
	}
	
	public static int getNumberOfSentBetweenNodes(){
		return numberOfSentBetweenNodes;
	}
	
	
	public static int getNumberOfSentByUAVs(){
		return numberOfSentBetweenUAVs+numberOfSentByUAVToNodes;
	}
	
	public static int getNumberOfSentByNodes(){
		return numberOfSentBetweenNodes+numberOfSentByNodeToUAVs;
	}
	
	public static int getNumberOfSent(){
		return getNumberOfSentByNodes()+getNumberOfSentByUAVs();
			
	}

	public static void writePacketsSents(){
		String s1=getNumberOfSentByUAVs()+"\r\n";
		writeToFile("NumberOfPacketsSentBy_UAVs.txt",s1);
		String s2=getNumberOfSentByNodes()+"\r\n";
		writeToFile("NumberOfPacketsSentBy_Nodes.txt",s2);
	}
	//the sender receiver and time is given to these methods but they are not used
	//For future implementations these values can be found and this class can be further extended
	public static void addPacketSent(int sender,int receiver,String time){
		//Lib.p("Reporter addPacketSent is working");
		if(sender <0 ){
			
			if(receiver<0){
				numberOfSentBetweenUAVs++;
			}else{
				numberOfSentByUAVToNodes++;
			}
		}else{
			if(receiver>0){
				numberOfSentBetweenNodes++;
			}else{
				numberOfSentByNodeToUAVs++;
			}
		}
		//Lib.p("Reporter class: Number of sents");
	}
	
	public static void addPacketAddedToBuffer(int sender,int receiver,String time){
		//Lib.p("Reporter addPacketaddedTobuffer is working");
		if(receiver <0 ){
			numberOfAddedToBufferByUAVs++;
		}else{
			numberOfAddedToBufferByNodes++;
		}
		
	}
	
	public static void addPacketDropped(int sender,int receiver,String time){
		//Lib.p("Reporter addPacketDropped is working");
		if(sender <0 ){
			numberOfDroppedByUAVs++;
		}else{
			numberOfDroppedByNodes++;
		}
	}
	
	public static void addPacketReceived(int sender,int receiver,String time){
		//addAckSent(sender,receiver,time);
		//Lib.p("Reporter addPacketReceived is working");
		if(receiver <0 ){
			numberOfReceivedByUAVs++;
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
	
	public static void finish(){
		//close the file writer
		try{
			if(bwriter!=null) {
				bwriter.close();
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
		//print the number of seconds/minutes of run time
		printExecutionTime();
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
			Lib.p("Reporter can not write to file "+foldername+"/"+fileName);
			e.printStackTrace();
		}
	}	
	
	//returns the string representation of the parameters
	public static String PacketInfo(){
		String res="************STATS************\r\n"+
		(numberOfReceivedByUAVs+numberOfReceivedByNodes)+" packets received totally\r\n"+
		"\t"+numberOfReceivedByNodes+" packets received by Nodes\r\n"+
		"\t"+numberOfReceivedByUAVs+" packets received by UAVs\r\n"+
		(numberOfAddedToBufferByNodes+numberOfAddedToBufferByUAVs)+" packets added to Buffer totally\r\n"+
		"\t"+numberOfAddedToBufferByNodes+" packets added to Buffer by Nodes\r\n"+
		"\t"+numberOfAddedToBufferByUAVs+" packets added to Buffer by UAVs\r\n"+
		numberOfDroppedByNodes+" packets dropped  totally\r\n"+
		getNumberOfSent()+" packets sent  totally\r\n"+
		"\t"+numberOfSentByNodeToUAVs+" packets sent to UAVs from nodes\r\n"+
		"\t"+numberOfSentByUAVToNodes+" packets sent to nodes from UAVs\r\n"+
		"\t"+numberOfSentBetweenUAVs+" packets sent between UAVs\r\n"+
		"\t"+numberOfSentBetweenNodes+" packets sent between Nodes\r\n"+
		//numberOfAcks+" packets sent\r\n"+
		"*****************************";
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
	
	public static String getDistanceTravelled(ArrayList<Uav> uavs){
		String all="";
		double distances=0;
		for(int i=0;i<uavs.size();i++){
			//all=all+"UAV with id "+uavs.get(i).getId()+" travelled "+uavs.get(i).getDistanceTravelled()+" meters \r\n";
			distances=distances+uavs.get(i).getDistanceTravelled();
		}
		all="UAV distances is "+distances;
		return all;
		
		
	}
	
	public static void writeDistanceTravelled(ArrayList<Uav> uavs){
		String all="";
		double distances=0;
		for(int i=0;i<uavs.size();i++){
			distances+=uavs.get(i).getDistanceTravelled();
		}

        
		all=distances+"\r\n";
		writeToFile("UAVDistances.txt", all);
	}
	
	//this is used in SimPanel.java
	//Precondition: the encounters are not both null!
	public static void writeEncounters(Encounter e1,Encounter e2,String fname){
		//since UAV clears encounters,According to preference it might!, the node may still have that record
		//whereas UAV doesn't or maybe the reverse
		//In that case the one that is not null will be written.
		//At least one of them is not null
		if(e1!=null){
			//write e1
			writeToFile(fname, e1.toString());
		}else{
			//write e2
			writeToFile(fname, e2.toString());
		}
		
	}
	
}
