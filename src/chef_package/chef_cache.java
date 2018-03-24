package chef_package;

import baitbite.LRUCache;

 class chef_cache {

	private static chef_cache singleton = new chef_cache( );
	
	

	   /* A private Constructor prevents any other
	    * class from instantiating.
	    */
	 private chef_cache() { }

	   /* Static 'instance' method */
	 public static chef_cache getInstance( ) {
	      return singleton;
	 }
	 
	 private LRUCache Recent_Accessed_Chefs = new LRUCache(100);
	 
	 private LRUCache Recent_Accessed_Dishes = new LRUCache(100);
	 
	 private LRUCache Top_100_Chefs = new LRUCache(100);
	 
	 private LRUCache Top_100_Dishes = new LRUCache(100);
	 
	 
	 public Chef getChef(String phoneNumber){
		 Chef top_chef = (Chef) Top_100_Chefs.get(phoneNumber);
		 Chef recent_chef = (Chef) Recent_Accessed_Chefs.get(phoneNumber);
		 
		 if(top_chef == null && recent_chef == null){
			 /*check if the item in the database 
			  get the item from the database and
			  put it in the cache */
		 }else if (top_chef != null){
			 return top_chef;
		 }else if(recent_chef != null){
			 return recent_chef;
		 }
		 
		 return null;
	 }
	 
	 public Chef getChef_DB(String phoneNumber){
		 /*establish database connection and get the chef from the data base*/
		 /*if chef not found in the database*/
		 return null;
	 }
	 
	 public Chef getDish(String chefPhoneNumber, String dishName){
		 Chef top_dish = (Chef) Top_100_Dishes.get(chefPhoneNumber + dishName);
		 Chef recent_dish = (Chef) Recent_Accessed_Dishes.get(chefPhoneNumber + dishName);
		 
		 if(top_dish == null && recent_dish == null){
			 /*check if the item in the database 
			  get the item from the database and
			  put it in the cache */
		 }else if (top_dish != null){
			 return top_dish;
		 }else if(recent_dish != null){
			 return recent_dish;
		 }
		 
		 return null;
	 }
	 
	 public Chef getDish_DB(String chefPhoneNumber, String dishName){
		 /*establish database connection and get the dish from the data base*/
		 /*if dish not found in the database*/
		 return null;
	 }
	 
	 public void addToRecent_Accessed_Chefs(Chef chef){
		 
	 }
	 
	 public void addToTop_100_Chefs(Chef chef){
		 
	 }
	 
	 public void addToRecent_Accessed_Dishes(Dish dish){
		 
	 }

	 public void addToTop_100_Dishes(Dish dish){
		 
	 }
	 
	 
	
}
