package ca.cmput301w14t09.view;

import ca.cmput301w14t09.model.*;
import ca.cmput301w14t09.model.Comment.Comment;


public class EditCommentActivity {

	/**
	 * @uml.property  name="comment"
	 * @uml.associationEnd  aggregation="composite" inverse="editCommentActivity:model.Comment"
	 */
	private Comment comment;

	/**
	 * Getter of the property <tt>comment</tt>
	 * @return  Returns the comment.
	 * @uml.property  name="comment"
	 */
	public Comment getComment() {
		return comment;
	}

	/**
	 * Setter of the property <tt>comment</tt>
	 * @param comment  The comment to set.
	 * @uml.property  name="comment"
	 */
	public void setComment(Comment comment) {
		this.comment = comment;
	}
}
