import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.BadLocationException;
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
	tabbedPane.addTab("Performing calculations", calculate());
	
	add(tabbedPane);
	
	
    }
    
    //explains some bayes
    private JComponent bayesRule() {
	JPanel panel = new JPanel(false); //doesn't need double buffering!
	JTextPane textPane = new JTextPane();
	StyledDocument doc = (StyledDocument) textPane.getDocument();
	
	Style style = doc.addStyle("Info", null);
	try {
	    doc.insertString(doc.getLength(),
		"Bayes rule is \n" +
		"P(A|B) = P(A∩B) / P(B)\n" +
		"\n" +
		"This equation means the probability of A given that B has occurred is equal to the " +
		"probability of A and B occuring simultaneously divided by the prior probability of B\n" +
		"\n" +
		"Using this equation, it is possible to derive event and conditional probabilities across a Bayesian network. " +
		"Below are some useful derived equations.\n" +
		"\n" +
		"P(B) = P(B|A)*P(A) + P(B|!A)*P(!A)\n" +
		"This equation is useful to calculate the prior probability for an event if there is access to it through " +
		"an event with a known prior probability which it is conditionally based on.\n",
		style
	    );
	    textPane.insertIcon(new ImageIcon("images/AddConditionalProbability.png"));
	} catch (BadLocationException e) { System.out.println("Unable to construct the help page"); }
	textPane.setEditable(false);
	panel.setPreferredSize(new Dimension(390, 450));
	textPane.setPreferredSize(new Dimension(390, 450));
	panel.add(textPane);
	return panel;
    }
    
    //how to navigate through the program
    private JComponent navigation() {
	JPanel panel = new JPanel(false); //doesn't need double buffering!
	JTextPane textPane = new JTextPane();
	StyledDocument doc = (StyledDocument) textPane.getDocument();
	
	Style style = doc.addStyle("Info", null);
	try {
	    doc.insertString(doc.getLength(),
		"File allows you to\n" +
		"  - Start a new network\n" + 
		"  - Load an existing network\n" + 
		"  - Save a created network\n" + 
		"  - Quit the program\n" +
		"\n" +
		"Fallacies allow you to access the library of logical fallacies packaged with the program.\n" +
		"Click on one and follow the steps to learn about a logical fallacy.\n",
		style
	    );
	} catch (BadLocationException e) { System.out.println("Unable to construct the help page"); }
	textPane.setEditable(false);
	panel.setPreferredSize(new Dimension(390, 450));
	textPane.setPreferredSize(new Dimension(390, 450));
	panel.add(textPane);
	return panel;
    }
    
    //show how to make a network
    private JComponent networking() {
	JPanel panel = new JPanel(false); //doesn't need double buffering!
	JTextPane textPane = new JTextPane();
	StyledDocument doc = (StyledDocument) textPane.getDocument();
	
	Style style = doc.addStyle("Info", null);
	try {
	    doc.insertString(doc.getLength(),
		"Time to make your own Bayesian network!\n" +
		"\n" +
		"To make a new event, click any free space away from other events\n" +
		"Here you can choose a name and (optionally) a propability\n" +
		"\n" +
		"To connect two events conditionally (or to edit existing conditional probabilities), select one event" +
		" by clicking on it. " +
		"Then hold CTRL+SPACE and select the second event. Now you can input probabilities!\n" +
		"\n" +
		"To attempt a prior probability calculation for an event, simply click on it and select 'Attempt Calculation'.\n" +
		"\n" +
		"To attempt calculation of conditional probabilities, select an event. " +
		"Next hold CTRL+C and select the second event. If it is possible to calculate the probability between " +
		"these events, new probabilities will be generated.\n",
		style
	    );
	    textPane.insertIcon(new ImageIcon("images/AddConditionalProbability.png"));
	    doc.insertString(doc.getLength(), "\n", style);
	    textPane.insertIcon(new ImageIcon("images/AddConditionalProbability2.png"));
	    doc.insertString(doc.getLength(),
		"\n" +
		"To attempt a prior probability calculation for an event, simply click on it and select 'Attempt Calculation'.\n" +
		"\n" +
		"To attempt calculation of conditional probabilities, select an event. " +
		"Next hold CTRL+C and select the second event. If it is possible to calculate the probability between " +
		"these events, new probabilities will be generated.\n",
		style
	    );
	} catch (BadLocationException e) { System.out.println("Unable to construct the help page"); }
	textPane.setEditable(false);
	panel.setPreferredSize(new Dimension(390, 450));
	textPane.setPreferredSize(new Dimension(390, 450));
	panel.add(textPane);
	return panel;
    }
    
    //show how to make calculations in a network
    private JComponent calculate() {
	JPanel panel = new JPanel(false); //doesn't need double buffering!
	JTextPane textPane = new JTextPane();
	StyledDocument doc = (StyledDocument) textPane.getDocument();
	
	Style style = doc.addStyle("Info", null);
	try {
	    doc.insertString(doc.getLength(),
		"To attempt a prior probability calculation for an event, simply click on it and select 'Attempt Calculation'.\n",
		style
            );
	    textPane.insertIcon(new ImageIcon("images/CalcProb.png"));
	    doc.insertString(doc.getLength(),
		"\n" +
		"To attempt calculation of conditional probabilities, select an event. " +
		"Next hold CTRL+C and select the second event. If it is possible to calculate the probability between " +
		"these events, new probabilities will be generated.\n",
		style
	    );
	    textPane.insertIcon(new ImageIcon("images/CalcCondProb.png"));
	} catch (BadLocationException e) { System.out.println("Unable to construct the help page"); }
	textPane.setEditable(false);
	panel.setPreferredSize(new Dimension(390, 450));
	textPane.setPreferredSize(new Dimension(390, 450));
	panel.add(textPane);
	return panel;
    }
}