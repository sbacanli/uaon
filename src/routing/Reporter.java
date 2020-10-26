package routing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import simtoo.Lib;
import simtoo.Uav;

public class Reporter
{
  private static String fname;
  private static BufferedWriter bwriter;
  private static int numberOfAcksByNodes = 0;
  private static int numberOfAcksByUAVs = 0;
  
  private static int numberOfDroppedByUAVs = 0;
  private static int numberOfDroppedByNodes = 0;
  
  private static int numberOfNonProtocolReceivedByUAVs = 0;
  private static int numberOfNonProtocolReceivedByNodes = 0;
  private static int numberOfProtocolReceivedByUAVs = 0;
  private static int numberOfProtocolReceivedByNodes = 0;
  
  private static int numberOfAddedToBufferByNodes = 0;
  private static int numberOfAddedToBufferByUAVs = 0;
  
  private static int numberOfProtocolSentByUAVToUAVs = 0;
  private static int numberOfProtocolSentByNodeToNodes = 0;
  private static int numberOfProtocolSentByUAVToNodes = 0;
  private static int numberOfProtocolSentByNodeToUAVs = 0;
  
  private static int numberOfNonProtocolSentByNodeToUAVs = 0;
  private static int numberOfNonProtocolSentByNodeToNodes = 0;
  private static int numberOfNonProtocolSentByUAVToUAVs = 0;
  private static int numberOfNonProtocolSentByUAVToNodes = 0;
  
  private static int numberOfLandTakeOffByUAV=0;
  



  private static String foldername = "";
  private static Date runstart;
  
  public Reporter() {}
  
	  //the written filename will be in fact the parameters of the simulation
		//that will be someVariable_Value_someVariable_Value etc...
	  public static void init(String params) {
	    foldername = params;
	    File fold = new File(foldername);
	    if ((!fold.exists()) || (!fold.isDirectory())) {
	      fold.mkdir();
	    }
	    
	    resetNumbers();
	    Lib.p("***************************************************************");
	    runstart = getCurrentTime();
	  }
	  








  public static void printExecutionTime()
  {
    Date now = getCurrentTime();
    long duration = now.getTime() - runstart.getTime();
    long diff = TimeUnit.MILLISECONDS.toSeconds(duration);
    long diffSeconds = diff;
    long diffMinutes = diff / 60L;
    

    Lib.p("Execution took " + diffMinutes + " minutes " + (diffSeconds - 60L * diffMinutes) + " secs");
  }
  
  public static Date getCurrentTime()
  {
    //DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Calendar cal = Calendar.getInstance();
    return cal.getTime();
  }
  

  public static void resetNumbers()
  {
    numberOfAcksByNodes = 0;
    numberOfAcksByUAVs = 0;
    
    numberOfDroppedByUAVs = 0;
    numberOfDroppedByNodes = 0;
    
    numberOfNonProtocolReceivedByUAVs = 0;
    numberOfNonProtocolReceivedByNodes = 0;
    numberOfProtocolReceivedByUAVs = 0;
    numberOfProtocolReceivedByNodes = 0;
    
	//number of messages which are added to buffer!
	// not all messages are added to buffer
	//a node may receive a message but it might be in the buffer already
    //NOT SURE ABOUT THE STATEMENT ABOVE
    numberOfAddedToBufferByNodes = 0;
    numberOfAddedToBufferByUAVs = 0;
    
    numberOfProtocolSentByUAVToUAVs = 0;
    numberOfProtocolSentByNodeToNodes = 0;
    numberOfProtocolSentByUAVToNodes = 0;
    numberOfProtocolSentByNodeToUAVs = 0;
    
    numberOfNonProtocolSentByNodeToUAVs = 0;
    numberOfNonProtocolSentByNodeToNodes = 0;
    numberOfNonProtocolSentByUAVToUAVs = 0;
    numberOfNonProtocolSentByUAVToNodes = 0;
    
    numberOfLandTakeOffByUAV=0;
  }
  
  public static int getNumberOfSentBetweenUAVs() {
    return numberOfNonProtocolSentByUAVToUAVs + numberOfProtocolSentByUAVToUAVs;
  }
  
  public static int getNumberOfSentBetweenNodes() {
    return numberOfNonProtocolSentByNodeToNodes + numberOfProtocolSentByNodeToNodes;
  }
  
  public static int getNumberOfSentNodesToUAVs()
  {
    return numberOfNonProtocolSentByNodeToUAVs + numberOfProtocolSentByNodeToUAVs;
  }
  
  public static int getNumberOfSentUAVToNodes() {
    return numberOfNonProtocolSentByUAVToNodes + numberOfProtocolSentByUAVToNodes;
  }
  
  public static int getNumberOfSentByUAVs() {
    return numberOfNonProtocolSentByUAVToNodes + numberOfProtocolSentByUAVToNodes + 
      numberOfNonProtocolSentByUAVToUAVs + numberOfProtocolSentByUAVToUAVs;
  }
  
  public static int getNumberOfSentByNodes() {
    return numberOfNonProtocolSentByNodeToNodes + numberOfProtocolSentByNodeToNodes + 
      numberOfNonProtocolSentByNodeToUAVs + numberOfProtocolSentByNodeToUAVs;
  }
  

  public static int getNumberOfReceivedByUAVs()
  {
    return numberOfNonProtocolReceivedByUAVs + numberOfProtocolReceivedByUAVs;
  }
  
  public static int getNumberOfReceivedByNodes() {
    return numberOfNonProtocolReceivedByNodes + numberOfProtocolReceivedByNodes;
  }
  
  public static double addedToBufferPercentageByNodes() {
	  double numberRatioTemp=numberOfAddedToBufferByNodes;
	  if(getNumberOfReceivedByNodes()==0) {//this is not likely but possible if there is no node
		  Lib.p("NumberofmessagesReceivedByNodes in Reporter is zero!");
		  return -1;
	  }
	  return numberRatioTemp / getNumberOfReceivedByNodes();
  }
  
  public static double addedToBufferPercentageByUAVs() {
    	double numberRatioTemp=numberOfAddedToBufferByUAVs;
    	if(getNumberOfReceivedByNodes()==0) {//this is not likely but possible if there is no node
  		  Lib.p("NumberofmessagesReceivedByNodes in Reporter is zero!");
  	  		return -1;
    	}
    	return numberRatioTemp / getNumberOfReceivedByNodes();
  }
  
  public static double nonProtocolMessagesPercentageSentByNodes() {
	//If the messages sent by Nodes is 0
	  if(getNumberOfSentByNodes()==0) {
		  return -1;
	  }
	  double numberRatioTemp=getNumberOfSentByNodes();
	  return (numberOfNonProtocolSentByNodeToNodes+numberOfNonProtocolSentByNodeToUAVs)/numberRatioTemp;
  }
  
  public static double nonProtocolMessagesPercentageSentByUAVs() {
	  //If the messages sent by UAVs is 0
	  if(getNumberOfSentByUAVs()==0) {
		  return -1;
	  }
	  double numberRatioTemp=getNumberOfSentByUAVs();
	  return (numberOfNonProtocolSentByUAVToNodes+numberOfNonProtocolSentByUAVToUAVs)/numberRatioTemp;
  }
  
  
  public static double nonProtocolMessagesPercentageReceivedByNodes() {
	//If the messages sent by UAVs is 0
	  if(getNumberOfReceivedByNodes()==0) {
		  return -1;
	  }
	  double numberRatioTemp=getNumberOfReceivedByNodes();
	  return numberOfNonProtocolReceivedByNodes/numberRatioTemp;
  }
  
  public static double nonProtocolMessagesPercentageReceivedByUAVs() {
	  //If the messages sent by UAVs is 0
	  if(getNumberOfReceivedByUAVs()==0) {
		  return -1;
	  }
	  double numberRatioTemp=getNumberOfReceivedByUAVs();
	  return (numberOfNonProtocolReceivedByUAVs)/numberRatioTemp;
  }
  
  
  public static int getNumberOfSent() {
    return getNumberOfSentByNodes() + getNumberOfSentByUAVs();
  }
  
  public static int getNumberOfLandTakeOffByUAV() {
	  return numberOfLandTakeOffByUAV;
  }
  
  public static void increaseLandTimesForUAV() {
	  numberOfLandTakeOffByUAV++;
  }
  
  public static void writePacketsSents() {
    String s1 = getNumberOfSentByUAVs() + "\r\n";
    writeToFile("NumberOfPacketsSentBy_UAVs.txt", s1);
    String s2 = getNumberOfSentByNodes() + "\r\n";
    writeToFile("NumberOfPacketsSentBy_Nodes.txt", s2);
  }
  

  	//the sender receiver and time is given to these methods but they are not used
	//For future implementations these values can be found and this class can be further extended
  public static void addPacketSent(int sender, int receiver, String time, boolean isProtocol)
  {
	  if (isProtocol) {
		    if (sender < 0) {
		        //sender is UAV
		        if (receiver < 0) {
		            //receiver is UAV
		            numberOfProtocolSentByUAVToUAVs += 1;
		        } else {
		            //receiver is node
		            numberOfProtocolSentByUAVToNodes += 1;
		        } //if receiver < 0
		        
		    } //if sender <0
		    else if (receiver < 0) {
		    	//receiver is UAV sender is node 
		        numberOfProtocolSentByNodeToUAVs += 1;
		    } else {
		    	//sender is node receiver is node
		        numberOfProtocolSentByNodeToNodes += 1;
		    }

		} else {
			//non protocol messages
			if (sender < 0) {
			    if (receiver < 0) {
			        numberOfNonProtocolSentByUAVToUAVs += 1;
			    } else {
			    	//receiver is node
			        numberOfNonProtocolSentByUAVToNodes += 1;
			    }
			} else if (receiver < 0) {
				//receiver is UAV sender is node 
				numberOfNonProtocolSentByNodeToUAVs += 1;
			} else {
				//sender is node receiver is nodeAV
			    numberOfNonProtocolSentByNodeToNodes += 1;
			}
			
		}//if protocol check
	  
  }//end of addPacketSent()
  

  public static void addPacketReceived(int sender, int receiver, String time, boolean isProtocol)
  {
    if (isProtocol) {
        if (receiver < 0) {
        	//receiver is UAV
          numberOfProtocolReceivedByUAVs += 1;
        } else {
        	//receiver is Node
          numberOfProtocolReceivedByNodes += 1;
        }
    }else {
    	//non protocol messages
    	if (receiver < 0) {
    		//receiver is UAV
    	    numberOfNonProtocolReceivedByUAVs += 1;
    	} else {
    		//receiver is Node
    	    numberOfNonProtocolReceivedByNodes += 1;
    	}
    }
  }
  
  
  public static void addPacketAddedToBuffer(int sender, int receiver, String time)
  {
    if (receiver < 0) {
      numberOfAddedToBufferByUAVs += 1;
    } else {
      numberOfAddedToBufferByNodes += 1;
    }
  }
  

  public static void addPacketDropped(int sender, int receiver, String time)
  {
    if (sender < 0) {
      numberOfDroppedByUAVs += 1;
    } else {
      numberOfDroppedByNodes += 1;
    }
  }
  
  
//reads the fname(exact path should be given) to String ArrayList
	//each element will be line
  public static ArrayList<String> readTrace(String fname)
  {
    ArrayList<String> a = new ArrayList<String>();
    try
    {
      BufferedReader br = new BufferedReader(new java.io.FileReader(fname));
      String line; while ((line = br.readLine()) != null) { 
        a.add(line);
      }
      br.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return a;
  }
  
  public static void finish()
  {
    try {
      if (bwriter != null) {
        bwriter.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    printExecutionTime();
  }
  
  /*File writing methods below*/
  
  public static String PacketInfo()
  {
    String res = "************STATS************\r\n" + (
      getNumberOfReceivedByUAVs() + getNumberOfReceivedByNodes()) + " packets received totally\r\n" + 
      "\t" + getNumberOfReceivedByNodes() + " packets received by Nodes\r\n" + 
      "\t" + getNumberOfReceivedByUAVs() + " packets received by UAVs\r\n" + (
      numberOfAddedToBufferByNodes + numberOfAddedToBufferByUAVs) + " packets added to Buffer totally\r\n" + 
      "\t" + numberOfAddedToBufferByNodes + " packets added to Buffer by Nodes\r\n" + 
      "\t" + numberOfAddedToBufferByUAVs + " packets added to Buffer by UAVs\r\n" + 
      numberOfDroppedByNodes + " packets dropped  totally\r\n" + 
      getNumberOfSent() + " packets sent  totally\r\n" + 
      "\t" + getNumberOfSentNodesToUAVs() + " packets sent Nodes to UAVs\r\n" + 
      "\t" + getNumberOfSentUAVToNodes() + " packets sent to UAVs to Nodes\r\n" + 
      "\t" + getNumberOfSentBetweenUAVs() + " packets sent between UAVs\r\n" + 
      "\t" + getNumberOfSentBetweenNodes() + " packets sent between Nodes\r\n" + 
      
      "*****************************";
    return res;
  }

  
  	public static void writeTrace(ArrayList<String> s, String fname) {
	    try {
	      bwriter = new BufferedWriter(new FileWriter(fname));
	      for (int i = 0; i < s.size(); i++) {
	        StringTokenizer st = new StringTokenizer((String)s.get(i));
	        while (st.hasMoreTokens()) {
	          String nt = st.nextToken();
	          
	          bwriter.write(nt + "\t");
	        }
	        
	        bwriter.write("\r\n");
	      }
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	  }
	  
	  public static void writeToFile(String fileName, String s)
	  {
		  try
		  {
			  bwriter = new BufferedWriter(new FileWriter(foldername + "/" + fileName, true));
			  bwriter.write(s);
			  bwriter.close();
		  } catch (Exception e) {
			  Lib.p("Reporter can not write to file " + foldername + "/" + fileName);
			  e.printStackTrace();
		  }
	  }
	  
	//the key method
	//writes the results (message delay or success rates to the specified file)
  public static void writeArrayToFile(double[] arr, String s)
  {
    String all = "";
    
    for (int i = 0; i < arr.length; i++)
    {
		//it is possible that message may not be broadcasted further
			//at this time the delay will be 0
			//don't write the message if delay is 0

      if (arr[i] != 0.0D) {
        all = all + LibRouting.precstr(LibRouting.prec(arr[i], 4), 4) + "\r\n";
      }
    }
    
    writeToFile(s, all);
  }
  
  //This method is used to write the metric results. The results have 4 precision
  public static void writeArrayListToFile(ArrayList<Double> srate, String fname) {
    String all = "";
    for (int i = 0; i < srate.size(); i++) {
      all = all + LibRouting.precstr(LibRouting.prec(((Double)srate.get(i)).doubleValue(), 4), 4) + "\r\n";
    }
    
    writeToFile(fname, all);
  }
  
  public static String getDistanceTravelled(ArrayList<Uav> uavs) {
    String all = "";
    double distances = 0.0D;
    for (int i = 0; i < uavs.size(); i++)
    {
      distances += ((Uav)uavs.get(i)).getDistanceTravelled();
    }
    all = "UAV distances is " + distances;
    return all;
  }
  

  public static void writeDistanceTravelled(ArrayList<Uav> uavs)
  {
    String all = "";
    double distances = 0.0D;
    for (int i = 0; i < uavs.size(); i++) {
      distances += ((Uav)uavs.get(i)).getDistanceTravelled();
    }
    

    all = distances + "\r\n";
    writeToFile("UAVDistances.txt", all);
  }
  
  public static void writeNumberOfAddedToBuffer() {
    String all = "";
    all = numberOfAddedToBufferByNodes + "\r\n";
    writeToFile("NumberOfAddedToBufferByNodes.txt", all);
    
    all = "";
    all = numberOfAddedToBufferByUAVs + "\r\n";
    writeToFile("NumberOfAddedToBufferByUAVs.txt", all);
  }
  
  public static void writeMessagesReceived() {
    String all = "";
    all = getNumberOfReceivedByNodes() + "\r\n";
    writeToFile("NumberOfReceivedByNodes.txt", all);
    
    all = getNumberOfReceivedByUAVs() + "\r\n";
    writeToFile("NumberOfReceivedByUAVs.txt", all);
  }
  
  public static void writeAddedToBufferPercentage() {
    String all = "";
    all = addedToBufferPercentageByNodes() + "\r\n";
    writeToFile("AddedToBufferPercentageByNodes.txt", all);
    
    all = addedToBufferPercentageByUAVs() + "\r\n";
    writeToFile("AddedToBufferPercentageByUAVs.txt", all);
    all=null;
  }
  
  	public static void writeNonProtocolMessagesReceivedPercentage() {
	    String all = "";
	    double ratio=nonProtocolMessagesPercentageReceivedByNodes();
	    if(ratio>=0) {
	    	all = ratio+ "\r\n";
		    writeToFile("nonProtocolMessagesPercentageReceivedByNodes.txt", all);
	    }
	    
	    ratio=nonProtocolMessagesPercentageReceivedByUAVs();
	    if(ratio >=0) {
	    	all = ratio + "\r\n";
	    	writeToFile("nonProtocolMessagesPercentageReceivedByUAVs.txt", all);
  		}
	    all=null;
 	}
	  
  	public static void writeNonProtocolMessagesSentPercentage() {
	    String all = "";
	    double ratio=nonProtocolMessagesPercentageSentByNodes();
	    if(ratio>=0) {
	    	all = ratio+ "\r\n";
		    writeToFile("nonProtocolMessagesPercentageSentByNodes.txt", all);
	    }
	    
	    ratio=nonProtocolMessagesPercentageSentByUAVs();
	    if(ratio >=0) {
	    	all = ratio + "\r\n";
	    	writeToFile("nonProtocolMessagesPercentageSentByUAVs.txt", all);
  		}
	    all=null;
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
	}//end of write encounters
	
	
		//that method is for reporting the cases where the receiver's buffer is full
		//that method is not called in the simulator now
		//for further development that case might be used!
	  public static void bufferFull(int id, String time) {
		  /*
			String line="bufferFull for node "+id+" time "+time; 
			try{
				bw.write(line+"\r\n");
			}catch(Exception e){
				e.printStackTrace();
			}
			//*/
		  
	  }
}
