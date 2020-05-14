package asgn1Question;

import javax.swing.JFrame;

/**
 * This class is the outermost container for the
 * simulation's GUI components.  It defines the outermost
 * frame (window) for the simulation.
 *
 * @author CAB302
 * @version 1.0
 */
@SuppressWarnings("serial") // We don't care about binary i/o here
public class SimulationFrame extends JFrame {

	/*
	 * Define the characteristics of the main frame 
	 */
	public SimulationFrame()
	{
		// Set the title for the main frame
		setTitle("Warehouse Simulator");
		// Add components to the frame (and start the simulation)
		getContentPane().add(new SimulationComponents());
		// Resize the frame to fit its components
		pack();
	}
}
