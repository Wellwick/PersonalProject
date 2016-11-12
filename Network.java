public class Network {
    //this class contains a network of Events linked by Probs
    Event[] events;
    Prob[] probabilities;
    
    public Network() {
		//generate 10 events
		events = new Event[10];
		byte letter = (byte)'A';
		events[0] = new Event((char)letter+"", 0.7f);
		letter++;
		events[1] = new Event((char)letter+"", 0.3f);
		letter++;
		events[2] = new Event((char)letter+"", 0.25f);
		letter++;
		for (int i=3; i<events.length; i++) {
		    events[i] = new Event((char)letter+"");
		    letter++;
		}
		System.out.println("The last event is labelled as " + events[9].getName());
		probabilities = new Prob[12];
		probabilities[0] = new Prob(events[3], events[0], 0.4f);
		probabilities[1] = new Prob(events[4], events[1], 0.9f);
		probabilities[2] = new Prob(events[4], events[2], 0.5f);
		probabilities[3] = new Prob(events[4], events[2].not(), 0.7f);
		probabilities[4] = new Prob(events[5], events[4], 0.9f);
		probabilities[5] = new Prob(events[6], events[4], 0.7f);
		probabilities[6] = new Prob(events[7], events[5], 0.1f);
		probabilities[7] = new Prob(events[8], events[5], 0.4f);
		probabilities[8] = new Prob(events[8], events[6], 0.3f);
		probabilities[9] = new Prob(events[8], events[7], 0.8f);
		probabilities[10] = new Prob(events[9], events[7], 0.75f);
		probabilities[11] = new Prob(events[9], events[8], 0.5f);
    }
    
    public static void main(String[] args) {
		//let's create a network for demonstration reasons
		Network net = new Network();
		net.showConnections();
		net.findProbability(3);
		net.findProbability(4);
		
    }
    
    public void showConnections() {
		for (int i=0; i<probabilities.length; i++) {
		    System.out.println("P(" + probabilities[i].getEvent().getName() + "|" + 
			probabilities[i].getConditional().getName() + ") = " + probabilities[i].getProb());
		}
    }
    
    //calculate P(B|A)
    public void calculateProbability(Event A, Event B) {
		//need to look for A having prior probability
		
		
		//find B as a resultant in conditional probability listing
    }
    
    //calculate P(A)
    private float calculateProbability(Event A) {
		//can only make use of prior probabilities
		//check if A has a prior probability
		if (A.hasPrior()) {
		    return A.getProb();
		} else {
		    //this means we need to look through the conditional probability table
		    for (int i=0; i<probabilities.length; i++) {
				if (probabilities[i].getEvent().equals(A)) {
				    //must calculate this
				    System.out.println("Searching for P(" + probabilities[i].getEvent().getName() + "|!" + 
						probabilities[i].getConditional().getName() + ")");
				    Event counterA = probabilities[i].getConditional().not();
				    for (int j=i+1; j<probabilities.length; j++) {
				    	if (probabilities[j].getConditional().equals(counterA)
				    			&& probabilities[i].getEvent().equals(A)) {
							return (probabilities[i].getProb() 
								* calculateProbability(probabilities[i].getConditional()))
								+  (probabilities[j].getProb() 
								* calculateProbability(probabilities[j].getConditional()));
				    	}
				    }
				}
		    }
		    
		}
		return -1;
    }
    
    public void findProbability(int eventToCheck) {
    	System.out.println("Calculating probability for " + events[eventToCheck].getName());
    	float prob = calculateProbability(events[eventToCheck]);
    	if (prob == -1) {
    		System.out.println("Probability incalculable");
    	} else {
			System.out.println("The probability of " + events[eventToCheck].getName() 
				+ " is " + prob);
		}
    }


}