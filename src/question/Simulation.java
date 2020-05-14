package question;

import javax.swing.JFrame;

/**
 * This class contains the main program for a warehouse simulation.
 * 
 * @author CAB302
 * @version 1.0
 */
public class Simulation {

	/*
	 * Program entry point.
	 * 
	 * Creates the GUI, ready for users to begin managing
	 * the warehouse.
	 * 
	 */
	public static void main(String[] args) {  	
    	// Create a warehouse simulation
	 	SimulationFrame warehouse = new SimulationFrame();
	 	// Terminate the program if the user closes the frame
        warehouse.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Make the simulation visible
        warehouse.setVisible(true);
	}
}
