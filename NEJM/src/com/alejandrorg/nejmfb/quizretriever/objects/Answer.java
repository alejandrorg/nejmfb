package com.alejandrorg.nejmfb.quizretriever.objects;

import com.alejandrorg.nejmfb.objects.Post;

public class Answer {

	private Post originalPost;

	private String answerOption;
	private String answerText;

	public Answer(Post p) {
		this.originalPost = p;
		this.answerOption = null;
		this.answerText = null;
	}

	public Post getOriginalPost() {
		return originalPost;
	}

	public void setOriginalPost(Post originalPost) {
		this.originalPost = originalPost;
	}

	public void setAnswerOption(String s) {
		this.answerOption = s;
	}

	public void setAnswerText(String s) {
		this.answerText = s;
	}

	public String getAnswerOption() {
		return answerOption;
	}

	public String getAnswerText() {
		return answerText;
	}

	
	
}
