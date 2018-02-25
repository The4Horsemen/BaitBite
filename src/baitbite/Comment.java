package baitbite;

public class Comment {
	
	private Dish dish;
	private String commentID;
	private String comment_content;
	//private Customer comment_writer;
	private int Rate;

	public Comment() {
		// TODO Auto-generated constructor stub
	}
	
	public void Report_Comment(){
		
	}

	public String getComment_content() {
		return comment_content;
	}

	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}

	public int getRate() {
		return Rate;
	}

	public void setRate(int rate) {
		Rate = rate;
	}

	public Dish getDish() {
		return dish;
	}

	public String getCommentID() {
		return commentID;
	}
	
	
	

}
