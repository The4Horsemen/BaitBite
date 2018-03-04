package org.The4Horsemen.Senior.BaitBite.model;

import java.util.ArrayList;

public class Dish {
	
	private ArrayList<Comment> Dish_Comments;
	private ArrayList<QandA> QA_List; 

	private int Quantity;
	private String Description;
	private String Name;
	private boolean prepare_now;
	private boolean prepare_later;
	private double price;
	
	public Dish(String Name, String Description, int Quantity, double price) {
		this.Dish_Comments = new ArrayList<Comment>();
		this.QA_List = new ArrayList<QandA>();
		
		this.Name = Name;
		this.Description = Description;
		this.Quantity = Quantity;
		this.price = price;
		
	}
	
	public void add_comment(Comment comment){
		this.Dish_Comments.add(comment);
	}
	
	public ArrayList<Comment> getDish_Comments() {
		return Dish_Comments;
	}
	
	public void add_picture(){}
	
	public void delete_Comment(Comment comment){
		if(!this.Dish_Comments.remove(comment)){
			System.out.println("Error: comment not found");
		}
	}
	
	public void add_QA(QandA QA){
		this.QA_List.add(QA);
	}
	
	public void delete_QA(QandA QA){
		if(!this.QA_List.remove(QA)){
			System.out.println("Error: QA not found");
		}
	}
	
	public void remove_picture(){}

	public int getQuantity() {
		return Quantity;
	}

	public void setQuantity(int quantity) {
		Quantity = quantity;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public boolean isPrepare_now() {
		return prepare_now;
	}

	public void setPrepare_now(boolean prepare_now) {
		this.prepare_now = prepare_now;
	}

	public boolean isPrepare_later() {
		return prepare_later;
	}

	public void setPrepare_later(boolean prepare_later) {
		this.prepare_later = prepare_later;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	

}
