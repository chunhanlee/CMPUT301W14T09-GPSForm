package ca.cmput301w14t09.model.comment;

import java.util.Date;
import java.util.LinkedList;

public class Thread {
	private LinkedList<LinkNode> comments;
	private Date lastUpdated;
	private String name;
	private int length;

	public Thread() {
		comments = new LinkedList<LinkNode>();
		name = "";
	}
	
	public void addToThread(Comment comment) {
		// Set comment thread.
		this.lastUpdated = comment.getPostDate();
		comment.setThread(this);
		comment.setPostDate(new Date());
		
		LinkNode newNode = new LinkNode(comment);
		comments.addLast(newNode);
	}
	
	public void sort() {
		
	}
	
	
	public LinkedList<LinkNode> getComments() { return comments; }
	public String getName() { return name; }
	public int getLength() { return length; }
}
