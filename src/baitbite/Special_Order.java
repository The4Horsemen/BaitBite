package baitbite;

import java.util.Date;

import chef_package.Chef;
import chef_package.Dish;
import customer_package.Customer;

public class Special_Order extends Order{
	
	private  Date Receive_Date;
	
	public Special_Order(Chef chef, Dish dish, Customer customer, int quantity, String Order_comment, String Request_Status, Date Receive_Date) {
		super(chef, dish, customer, quantity, Order_comment, Request_Status);
		this.Receive_Date = Receive_Date; 
		// TODO Auto-generated constructor stub
	}

	
	
}
