package baitbite;

import java.awt.Image;
import java.util.ArrayList;

public class Chef extends Customer{
	
	private ArrayList<Catagory> Categories;
	private ArrayList<Dish> Food_Menu;
	private ArrayList<Order_Now> Orders_for_Now;
	private ArrayList<Special_Order> Pending_Special_Order;
	private ArrayList<Special_Order> Special_Order;

	private String Account_Status;
	private String Chef_Status;
	private int Rating;
	private Image Health_Certificate;
	private Image Logo;
	
	
	public void Accept_Special_Order_Item(){
	}
	
	public void Add_Order_Now(Order order){
	}
	
	public void Add_Pending_Special_Order_Item() {
	}
	
	public void Create_Dish() {
	}
	
	public void Generate_Validation_Request() {
		
	}
	
	public void Notify_Chef() {
		
	}
	
	public void Reject_Special_Order() {
		
	}
	
	public void Remove_Dish() {
		
	}
	
	public void Remove_Order() {
		
	}
	
	public void Turn_Availability_Off() {
		
	}
	
	public void Turn_Availability_On() {
		
	}
	
	public void Update_Order_Status() {
		
	}
	
	
	
	
	
	
	
	
	public ArrayList<Catagory> getCategories() {
		return Categories;
	}
	public void setCategories(ArrayList<Catagory> categories) {
		Categories = categories;
	}
	public ArrayList<Dish> getFood_Menu() {
		return Food_Menu;
	}
	public void setFood_Menu(ArrayList<Dish> food_Menu) {
		Food_Menu = food_Menu;
	}
	public ArrayList<Order_Now> getOrders_for_Now() {
		return Orders_for_Now;
	}
	public void setOrders_for_Now(ArrayList<Order_Now> orders_for_Now) {
		Orders_for_Now = orders_for_Now;
	}
	public ArrayList<Special_Requests> getPending_Special_Requests() {
		return Pending_Special_Requests;
	}
	public void setPending_Special_Requests(ArrayList<Special_Requests> pending_Special_Requests) {
		Pending_Special_Requests = pending_Special_Requests;
	}
	public ArrayList<Special_Order> getSpecial_Order() {
		return Special_Order;
	}
	public void setSpecial_Order(ArrayList<Special_Order> special_Order) {
		Special_Order = special_Order;
	}
	public String getAccount_Status() {
		return Account_Status;
	}
	public void setAccount_Status(String account_Status) {
		Account_Status = account_Status;
	}
	public String getChef_Status() {
		return Chef_Status;
	}
	public void setChef_Status(String chef_Status) {
		Chef_Status = chef_Status;
	}
	public int getRating() {
		return Rating;
	}
	public void setRating(int rating) {
		Rating = rating;
	}
	public Image getHealth_Certificate() {
		return Health_Certificate;
	}
	public void setHealth_Certificate(Image health_Certificate) {
		Health_Certificate = health_Certificate;
	}
	public Image getLogo() {
		return Logo;
	}
	public void setLogo(Image logo) {
		Logo = logo;
	}
	
}
