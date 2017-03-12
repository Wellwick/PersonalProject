import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.BorderLayout;

public class HelpPane extends JPanel {
    //class to show help
    public HelpPane()  {
	super(new GridLayout(1,1));
	JTabbedPane tabbedPane = new JTabbedPane();
	
	tabbedPane.addTab("Bayesian Logic", bayesRule());
	tabbedPane.addTab("Navigation", navigation());
	tabbedPane.addTab("Making a Network", networking());
	add(tabbedPane);
	
	
    }
    
    //explains some bayes
    private JComponent bayesRule() {
	JPanel panel = new JPanel(false); //doesn't need double buffering!
	JTextArea text = new JTextArea(
	    "Bayes rule is \n" +
	    "P(A|B) = P(A∩B) / P(B)\n" +
	    "\n" +
	    "Using this equation, it is possible to derive event and conditional probabilities across a Bayesian network"
	);
	text.setEditable(false);
	text.setLineWrap(true);
	text.setWrapStyleWord(true);
	panel.setPreferredSize(new Dimension(390, 450));
	text.setPreferredSize(new Dimension(390, 450));
	panel.add(text);
	return panel;
    }
    
    //how to navigate through the program
    private JComponent navigation() {
	JPanel panel = new JPanel(false); //doesn't need double buffering!
	JTextArea text = new JTextArea(
	    "File allows you to\n" +
	    "  - Start a new network\n" + 
	    "  - Load an existing network\n" + 
	    "  - Save a created network\n" + 
	    "  - Quit the program\n" +
	    "\n" +
	    "Fallacies allow you to access the library of logical fallacies packaged with the program.\n" +
	    "Click on one and follow the steps to learn about a logical fallacy."
	);
	text.setEditable(false);
	text.setLineWrap(true);
	text.setWrapStyleWord(true);
	panel.setPreferredSize(new Dimension(390, 450));
	text.setPreferredSize(new Dimension(390, 450));
	panel.add(text);
	return panel;
    }
    
    //show how to make a network
    private JComponent networking() {
	JPanel panel = new JPanel(false); //doesn't need double buffering!
	JTextArea text = new JTextArea(
	    "Time to make your own Bayesian network!\n" +
	    "\n"
	);
	text.setEditable(false);
	text.setLineWrap(true);
	text.setWrapStyleWord(true);
	panel.setPreferredSize(new Dimension(390, 450));
	text.setPreferredSize(new Dimension(390, 450));
	panel.add(text);
	return panel;
    }
    
}