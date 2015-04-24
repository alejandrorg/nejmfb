package com.alejandrorg.nejmfb.quizretriever.objects;

import com.alejandrorg.nejmfb.objects.Comment;

public class CommentAndAnswer {

	private Comment comment;
	private boolean isCorrect;
	private AnswerOption choosenAnswerOption;

	private int howFound;

	public CommentAndAnswer(Comment c, AnswerOption answerOption, int hf, boolean correct) {
		this.comment = c;
		this.howFound = hf;
		this.isCorrect = correct;
		this.choosenAnswerOption = answerOption;
	}


	public AnswerOption getChoosenAnswerOption() {
		return this.choosenAnswerOption;
	}
	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	public int getHowFound() {
		return this.howFound;
	}

}
