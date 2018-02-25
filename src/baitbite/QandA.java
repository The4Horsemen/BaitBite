package baitbite;

public class QandA {
	
	//private Customer customer;
	private Dish dish;
	
	private String question;
	private String answer;
	
	
	public QandA(Dish dish , String question) {
		// TODO Auto-generated constructor stub
		this.dish = dish;
		this.question=question;
	}


	public String getQuestion() {
		return question;
	}


	public void setQuestion(String question) {
		this.question = question;
	}


	public String getAnswer() {
		return answer;
	}


	public void setAnswer(String answer) {
		this.answer = answer;
	}


	public Dish getDish() {
		return dish;
	}
	
	
	

}
