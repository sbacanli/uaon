package simtoo;

import java.awt.*;
import javax.swing.*;


public class Simtoo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Simulator sim=new Simulator();
		sim.unSetRandom();
		
		JFrame j=new JFrame("Simtoo");	
		j.setLayout(new BorderLayout());
		SimPanel simp=new SimPanel(sim);
		j.add(simp, BorderLayout.CENTER);

		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//j.setPreferredSize(new Dimension(700,600));
		j.pack();
		j.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		j.setVisible(true);
		
	}

}
