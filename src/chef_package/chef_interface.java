package chef_package;

import baitbite.database;

public class chef_interface {
	
	private static chef_interface singleton = new chef_interface( );
	
	public chef_cache chefCache = chef_cache.getInstance();
	

	   /* A private Constructor prevents any other
	    * class from instantiating.
	    */
	 private chef_interface() { }

	   /* Static 'instance' method */
	 public static chef_interface getInstance( ) {
	      return singleton;
	 }
	 
	 
	 public void create_account(Chef chef){
		 database.checkChef(chef.getPhone_Number()); // check if the chef is already in the database
		 
		 // insert the chef in the database
		 
		 chefCache.addToRecent_Accessed_Chefs(chef);
		 
	 }
	 
	 public void edit_dish(Chef chef, Dish dish){
		 database.checkDish(chef.getPhone_Number(), dish.getName());
		 
		// insert the dish to the chef in the database
		 
		 chef.Add_Dish(dish);
		 chefCache.addToRecent_Accessed_Chefs(chef);
		 chefCache.addToRecent_Accessed_Dishes(dish);
		 
	 }
	 
	 public void edit_quantity(){
		 
	 }
	 
	 public void delete_dish(Chef chef,Dish dish){
		 
	 }
	 
	 public void make_dish_available(Chef chef, Dish dish){
		 
	 }
	 
	 public void create_account(String email, String location, String name, String phone_number, String chef_Stattus){
		 
	 }
	 
	 public void edit_dish(String Name, String Description, int Quantity, double price, boolean availability){
		 
	 }
	 
	 /*public void edit_quantity(){
		 
	 }*/
	 
	 public void delete_dish(String Name, String Description, int Quantity, double price, boolean availability){
		 
	 }
	 
	 public void make_dish_available(String Name, String Description, int Quantity, double price, boolean availability){
		 
	 }
	 
	 
	 
	
	
}
