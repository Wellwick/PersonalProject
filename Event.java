//need a way to demonstrate that an event has a probability of not occuring
public class Event {
    float probability;
    String name;
    boolean hasPrior = false;
    Event negation; //an event which is the counter 1 - probability
    int x, y;
    
    //constructor for event with no prior probability
    public Event(String n, int px, int py) { 
        name = n;
        //make assumption that each event forces the creation of it's negation
        negation = new Event("!"+n, this, px, py); // !<Event> produces the negation
        x = px;
        y = py;
    }
    
    //constructor for negation
    public Event(String n, Event neg, int px, int py) {
        name = n;
        negation = neg;
        x = px;
        y = py;
    }

    //constructor for event with prior probability
    public Event(String n, float prob, int px, int py) {
        //if the probability is not between 0 and 1, then it is not a probability
        if (prob < 0 || prob > 1) {
	    System.out.println(n + " can not have a probability outside of [0-1], removed prior probability");
	    name = n;
	    negation = new Event("!"+n, this, px, py);
	    x = px;
	    y = py;
        } else {
	    name = n;
	    probability = prob;
	    hasPrior = true;
	    negation = new Event("!"+n, 1.0f-prob, this, px, py); //needs a different
	    x = px;
	    y = py;
        }
    }

    //constructor for negation event
    public Event(String n, float prob, Event neg, int px, int py) {
        name = n;
        probability = prob;
        hasPrior = true;
        negation = neg;
        x = px;
        y = py;
    }
    
    //setting the probability for 
    public void setProb(float prob, boolean needsNegation) {
	hasPrior = true;
	probability = prob;
	if (needsNegation)
	    negation.setProb(1.0f-prob, false);
    }
    
    public String getName() { return name; }
    public boolean hasPrior() { return hasPrior; }
    public boolean equals(Event A) { return A.getName().equals(name); }
    public float getProb() { return probability; }
    public Event not() { return negation; }
    public int getX() { return x; }
    public int getY() { return y; }
}