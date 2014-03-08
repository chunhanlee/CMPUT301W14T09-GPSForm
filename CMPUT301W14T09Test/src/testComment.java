import ca.cmput301w14t09.model.comment.Comment;
import junit.framework.TestCase;


public class testComment extends TestCase {

	public testComment(String name) {
		super(name);
	}
	
	public void testCommentExists(){
		Comment comments = new Comment();
		
		String content = "test comment!";
		
		comments.setCommentText(content);
		
		String commentString = comments.getCommentText();
		/**
            Comment Succesful returns true if comment was succesfully made
         **/
        assertTrue(commentSuccesful());
        
		assertTrue(commentString.equals(content));
	}

}
