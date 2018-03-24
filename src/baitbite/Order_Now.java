package baitbite;

import chef_package.Chef;
import chef_package.Dish;
import customer_package.Customer;

public class Order_Now extends Order{

	public Order_Now(Chef chef, Dish dish, Customer customer, int quantity, String Order_comment, String Request_Status){
		super( chef, dish, customer, quantity, Order_comment, Request_Status);
	}
}
