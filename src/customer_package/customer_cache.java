package customer_package;


public class customer_cache {
	
	private static customer_cache singleton = new customer_cache( );
	
	

	   /* A private Constructor prevents any other
	    * class from instantiating.
	    */
	 private customer_cache() { }

	   /* Static 'instance' method */
	 public static customer_cache getInstance( ) {
	      return singleton;
	 }
	 

	 
	 
}


 
