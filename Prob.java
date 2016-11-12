public class Prob {
    Event A;
    Event B;
    float conditionalProbability; //premise of this class is probability is B|A
    
    public Prob(Event b, Event a, float prob) { // Prob(B, A, FLOAT)
	A = a;
	B = b;
	conditionalProbability = prob;
    }
    
    public Event getEvent() { return B; }
    public Event getConditional() { return A; }
    public float getProb() { return conditionalProbability; }
}