public class DiffieHellman 
{
	public static int generateRandomPrime()
	{
		int out = (int) (Math.random()*100);
		while (!isPrime(out)) {
		    out = (int) (Math.random()*100);
		}
		return out;
	}
	
	private static boolean isPrime(int n) {
	    for(int i=2;2*i<n;i++) {
	        if(n%i==0)
	            return false;
	    }
	    return true;
	}
	
	public static int generateRandomInt()
	{
		return 1 + (int)(Math.random() * ((100 - 1) + 1));
	}
	
	
}
