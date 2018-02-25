package baitbite;

public class Driver {

	public static void main(String[] args) {
	Dish d1 = new Dish("Rice", "white rice", 4, 12.00);
	Comment C1 = new Comment(d1,"1","Good Dish",10);
	d1.add_comment(C1);
	
	QandA Q1 = new QandA(d1,"What is the size ?");
	Q1.setAnswer("it is large");
	
	
	
	
	
	}

}
