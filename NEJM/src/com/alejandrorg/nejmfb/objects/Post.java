package com.alejandrorg.nejmfb.objects;

import java.util.Collections;
import java.util.LinkedList;
import com.alejandrorg.nejmfb.core.StaticUtils;
import com.fasterxml.jackson.databind.JsonNode;

public class Post extends FBObject {

	private LinkedList<Comment> comments;
	private int howFound;
	private String urlPost;
	private boolean commentsSorted;

	public Post(JsonNode i, JsonNode cd, JsonNode m) {
		super(i,cd,m);
		this.comments = new LinkedList<Comment>();
		this.howFound = -1;
		this.urlPost = StaticUtils.getURLIn(this.message);
		this.commentsSorted = false;
	}

	public Post(String id, String cd, String msg) {
		super(id,cd,msg);
		this.comments = new LinkedList<Comment>();
		this.howFound = -1;
		this.urlPost = StaticUtils.getURLIn(this.message);
		this.commentsSorted = false;
	}

	public int getHowFound() {
		return this.howFound;
	}
	
	public String getUrlPost() {
		return urlPost;
	}

	public void setUrlPost(String urlPost) {
		this.urlPost = urlPost;
	}

	public boolean equals(Object o) {
		if (o instanceof Post) {
			Post po = (Post)o;
			return po.getId().equalsIgnoreCase(this.getId());
		}
		return false;
	}
	
	public void setHowFound(int i) {
		this.howFound = i;
	}

	@SuppressWarnings("unchecked")
	public LinkedList<Comment> getComments() {
		if (!this.commentsSorted) {
			Collections.sort(this.comments, new DateComparator());
		}
		return comments;
	}

	@SuppressWarnings("unchecked")
	public void setComments(LinkedList<Comment> comments) {
		this.comments = comments;
		Collections.sort(this.comments, new DateComparator());
	}

	public void addComment(Comment comment) {
		this.comments.add(comment);
	}


//	public boolean containsCommentByUser(String userID) {
//		for (int i = 0; i < this.comments.size(); i++) {
//			if (this.comments.get(i).getIdUserComment().equalsIgnoreCase(userID)) {
//				return true;
//			}
//		}
//		return false;
//	}

}
