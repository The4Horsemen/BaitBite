package customer_package;

public class customer_interface {

	private static customer_interface singleton = new customer_interface( );
	
	

	   /* A private Constructor prevents any other
	    * class from instantiating.
	    */
	 private customer_interface() { }

	   /* Static 'instance' method */
	 public static customer_interface getInstance( ) {
	      return singleton;
	 }
}

