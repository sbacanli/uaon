package simtoo;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;

import com.sun.glass.events.WindowEvent;


public class Runner {

	public static void main(String[] args) {
		
		if(args.length==0){
			run("config.txt");
		}else if(args.length==1){
			run(args[0]);
		}		
	}
	
	public static void run(String confFile){
		//read the options from the file
				Options op=new Options(confFile);
				
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				double height= screenSize.height;
				double width = screenSize.width;

				//Lib.p("SCREEN "+height+" "+width);
			
				Datas data=new Datas(height,width);
				Simulator simulator=new Simulator(op,data);
				
				
				SimPanel simpanel=new SimPanel(simulator,data,simulator.isVisible());
				//RightPanel rp=new RightPanel(simulator);
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
				
				TimerTask t=new invisibleRunner(simulator, data);
				Timer timer=new Timer(true);
				
				int period=1;
				timer.schedule(t, 1, period);
				
	}
}
