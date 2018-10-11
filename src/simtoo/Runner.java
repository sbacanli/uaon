package simtoo;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


import routing.LibRouting;


public class Runner {

	public static void main(String[] args) {
		
		if(args.length==0){
			run("config.txt");
		}else if(args.length==1){
			String conffile=args[0];
			if(conffile.toLowerCase().contains("config")) {
				run(conffile);
			}else {
				System.out.println("Config file name should contain 'config' word");
			}
			
		}		
	}
	
	public static void run(String confFile){
		//read the options from the file
		Options op=new Options(confFile);
		
		String reportfile=confFile.replace("config", "Report");
		Lib.init(reportfile);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double height= screenSize.height;
		double width = screenSize.width;

		
		LibRouting.init();
		Datas data=new Datas(height,width);
		Simulator simulator=new Simulator(op,data);
		
		SimPanel simpanel=new SimPanel(simulator,data,simulator.isVisible());
		//RightPanel rp=new RightPanel(simulator);
		if(simulator.isVisible()) {
			simpanel.startTimer();
			JFrame j=new JFrame(simulator.getSimulationName());	
			
			j.setLayout(new BorderLayout());
			j.add(simpanel, BorderLayout.CENTER);
			//j.add(rp, BorderLayout.EAST);
			//j.add(simpanel);
			//j.add(rp);
			
			j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			j.addWindowListener(new WindowAdapter() {
				  public void windowClosing(WindowEvent we) {
					  j.dispose();
					  Lib.p("DONE");
					  System.exit(0);
				  }
			});
			
			//j.setPreferredSize(new Dimension(700,600));
			j.pack();
			j.setExtendedState(JFrame.MAXIMIZED_BOTH); 
			j.setVisible(simulator.isVisible());
			
		}else {
			simpanel.processTime();
		}
				
	}

	public static void runinvisible(String confFile){
		//read the options from the file
				Options op=new Options(confFile);
				
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				double height= screenSize.height;
				double width = screenSize.width;

				Lib.p("SCREEN "+height+" "+width);
				
				Datas data=new Datas(height,width);
				Simulator simulator=new Simulator(op,data);
				SimPanel simpanel=new SimPanel(simulator,data,simulator.isVisible());
				simpanel.processTime();
	}
}
