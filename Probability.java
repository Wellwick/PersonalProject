public class Probability {
    static float A;
    static float BGiveA;
    
    public static void main(String[] args) {
	write("Trying to create a basic diagram");
	write("Prior probability of A is 0.4");
	A = 0.4f;
	
	write("Conditional probability of B|A is 0.6");
	BGiveA = 0.6f;
	
	//bayesian calculation says that P(B|A) = P(BxA) / P(A)
	//rearranging this formula produces
	write("Therefore the probability of (BxA) is " + (BGiveA * A));
	
    }

    private static void write(String words) { System.out.println(words); }
}