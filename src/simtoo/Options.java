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
	
	public void readConfigFile(){
		try (BufferedReader br = new BufferedReader(new FileReader(conf))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	if(!line.startsWith("//")){
		    		StringTokenizer st=new StringTokenizer(line,":");
		    		String s1=st.nextToken();
		    		String s2=st.nextToken();
		    		hm.put(s1, s2);
		    	}
		    	// lines starting with // are comment lines and they are ignored
		    }
		}catch(Exception e){
			Lib.p(e.toString());
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
    		return -999.01;
    	}
    	
    	if(hm.get(s) !=null){
    		return Double.parseDouble(hm.get(s));
    	}
    	return -999.01;
    }
    
    public int getParamInt(String s){
    	if(hm.isEmpty()){
    		Lib.p("ERROR: config file empty for call getParamInt in class Options for option "+s);
    		return -999;
    	}
    	
    	if(hm.get(s) !=null){
    		return Integer.parseInt(hm.get(s));
    	}
    	Lib.p("ERROR: config file empty for call getParamInt in class Options for option "+s);
    	return -999;
    }
    
    public boolean getParamBoolean(String s){
    	if(hm.isEmpty()){
    		Lib.p("ERROR: config file empty for call getParamBoolean in class Options for option "+s);
    		return false;
    	}
    	
    	if(hm.get(s) !=null){
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
    		return hm.get(s);
    	}

		Lib.p("ERROR: Entry not found for call getParamString in class Options");
    	return null;
    }
}
