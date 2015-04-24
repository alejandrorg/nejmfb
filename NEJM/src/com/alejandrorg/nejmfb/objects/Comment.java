package com.alejandrorg.nejmfb.objects;

public class Comment extends FBObject {

	private String idUserComment;
	private String usernameComment;

	
	public Comment(String i, String iuc, String uc, String c, String cd) {
		super(i, cd, c);
		this.idUserComment = iuc;
		this.usernameComment = uc;
	}

	public String getIdUserComment() {
		return idUserComment;
	}

	public void setIdUserComment(String idUserComment) {
		this.idUserComment = idUserComment;
	}

	public String getUsernameComment() {
		return usernameComment;
	}

	public void setUsernameComment(String usernameComment) {
		this.usernameComment = usernameComment;
	}

	public String toString() {
		return this.usernameComment + ": " + this.message;
	}
}
