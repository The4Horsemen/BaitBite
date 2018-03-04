package baitbite;

import java.util.ArrayList;



public class Customer extends Account{
	
	
	
	private ArrayList<Order_Now> Placed_Orders;
	private ArrayList<Special_Order> Placed_SpecialOrders_Requests;
	
	public Customer(String email, String location, String name, String phone_number){
		super(email, location, name, phone_number);
	}
	
	
	public void add_comment(Comment comment){
	}
	
	public void Ask_Question() {
		
	}
	
	public void Notify_Customer() {
		
	}
	
	public void Place_Order(Order_Now order) {
		this.Placed_Orders.add(order);
	}
	
	public void Place_SpecialOrder(Special_Order order) {
		this.Placed_SpecialOrders_Requests.add(order);
	}
	
	public void Rate_Chef() {
		
	}
	
	public void Remove_Comment() {
		
	}
	
	public void Request_Account_Access() {
		
	}
}
