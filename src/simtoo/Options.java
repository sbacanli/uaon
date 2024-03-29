package simtoo;

import java.io.*;
import java.util.*;

import routing.Reporter;

public class Options {

	HashMap<String,String> hm;
	String conf;
	
	Options(String conf){
		hm=new HashMap<String,String>();
		this.conf=conf;
		readConfigFile();
	}
	
	private void readConfigFile(){
		try (BufferedReader br = new BufferedReader(new FileReader(conf))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	// // is commenting in the config file
		    	if(!line.startsWith("//") && line.trim().length()!=0){
		    		//the first occurence of the semicolon is the option preference.
		    		//the remainin charactes are all the option
		    		//this is necessary to define a web page or local path like c:\ etc..
		    		int p1=line.indexOf(':');
		    		String s1=line.substring(0, p1);
		    		String s2=line.substring(p1+1);
		    		hm.put(s1, s2);
		    	}
		    	// lines starting with // are comment lines and they are ignored
		    }
		    br.close();
		}catch(Exception e){
			Lib.p("Problem in reading config file "+conf+" "+e.getMessage());
			e.printStackTrace();
			System.exit(-1);
			
		}
	}

    public void printConfigFile(){
    	String s="";
    	for (String key : hm.keySet()) {
    		String value=hm.get(key);
    		s=s+key+":"+value+"\r\n";
    	}
    	Reporter.writeToFile("options_created.txt", s);
    }
    
    public double getParamDouble(String s){
    	if(hm.isEmpty()){
    		Lib.p("ERROR: config file empty for call getParamDouble in class Options for option "+s);
    		return -999.01;
    	}
    	
    	if(hm.get(s) !=null){
    		s=s.trim();
    		return Double.parseDouble(hm.get(s));
    	}
    	Lib.p("ERROR: config file empty for call getParamDouble in class Options for option "+s);
    	return -999.01;
    }
    
    public int getParamInt(String s){
    	if(hm.isEmpty()){
    		Lib.p("ERROR: config file empty for call getParamInt in class Options for option "+s);
    		return -999;
    	}
    	
    	if(hm.get(s) !=null){
    		s=s.trim();
    		return Integer.parseInt(hm.get(s));
    	}
    	//made this number specifically like that  so that it will be easy to debug. just search the number.
    	Lib.p("ERROR: config file empty for call getParamInt in class Options for option "+s);
    	return -999;
    }
    
    public long getParamLong(String s){
    	
    	if(hm.isEmpty()){
    		Lib.p("ERROR: config file empty ");
    		return -999;
    	}
    	
    	if(hm.get(s) !=null){
    		s=s.trim();
    		return Long.parseLong(hm.get(s));
    	}
    	Lib.p("ERROR: config file empty for call getParamLong in class Options for option "+s);
    	return -999;
    }
    
    public boolean getParamBoolean(String s){
    	if(hm.isEmpty()){
    		Lib.p("ERROR: config file empty for call getParamBoolean in class Options for option "+s);
    		return false;
    	}
    	
    	if(hm.get(s) !=null){
    		s=s.trim();
    		return hm.get(s).equalsIgnoreCase("yes");
    	}

		Lib.p("ERROR: Entry not found for call getParamBoolean in class Options for option "+s);
    	return false;
    }
    
    public String getParamString(String s){
    	if(hm.isEmpty()){
    		Lib.p("ERROR: config file empty for call getParamString in class Options for option "+s);
    		return null;
    	}
    	
    	if(hm.get(s) !=null){
    		s=s.trim();
    		return hm.get(s);
    	}

		Lib.p("ERROR: Entry not found for call getParamString in class Options for option "+s);
    	return null;
    }
    
    
}
