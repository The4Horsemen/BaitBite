package org.The4Horsemen.Senior.BaitBite.model;
import java.util.ArrayList;

public class Order {
	
	private Chef chef;
	private Customer customer;
	private ArrayList<Dish> Dish;
	
	private String Delivery_Method;
	private String Order_Comment;
	private int Quantity ;
	private String Request_Status;
	private String Waiting_Time;
	private String Order_Number;
	
	
	
	public String getDelivery_Method() {
		return Delivery_Method;
	}
	public void setDelivery_Method(String delivery_Method) {
		Delivery_Method = delivery_Method;
	}
	public String getOrder_Comment() {
		return Order_Comment;
	}
	public void setOrder_Comment(String order_Comment) {
		Order_Comment = order_Comment;
	}
	public int getQuantity() {
		return Quantity;
	}
	public void setQuantity(int quantity) {
		Quantity = quantity;
	}
	public String getRequest_Status() {
		return Request_Status;
	}
	public void setRequest_Status(String request_Status) {
		Request_Status = request_Status;
	}
	public String getWaiting_Time() {
		return Waiting_Time;
	}
	public void setWaiting_Time(String waiting_Time) {
		Waiting_Time = waiting_Time;
	}
	public String getOrder_Number() {
		return Order_Number;
	} 
	
	

}
