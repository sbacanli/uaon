package simtoo;
import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

public class RightPanel extends JFrame {
	JLabel inf;
	Simulator sim;
	
	public RightPanel(Simulator sim) {
		super();
		this.sim=sim;
		setBackground(Color.green);
		
		inf=new JLabel();
		this.getContentPane().add(inf);
				
	}
	

    public void some() {
        String text="";
        ArrayList<Uav> uavnodes=sim.getUavs();
        for(int i=0;i<uavnodes.size();i++){
        	text +=uavnodes.get(i).isRouteFinished()+"\n";
        }
        inf.setText(text);
        
    }

}
