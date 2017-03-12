import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.GridLayout;

public class HelpPane extends JPanel {
    //class to show help
    public HelpPane()  {
	super(new GridLayout(1,1));
	JTabbedPane tabbedPane = new JTabbedPane();
	
	JComponent bayes = bayesRule();
	tabbedPane.addTab("Bayesian Logic", bayes);
	add(tabbedPane);
	
    }
    
    private JComponent bayesRule() {
	JPanel panel = new JPanel(false);
	JTextField words = new JTextField(
	"Bayes rule is \n" +
	"P(A|B) = P(A∩B) / P(B)\n"
	);
	panel.add(words);
	return panel;
    }
}