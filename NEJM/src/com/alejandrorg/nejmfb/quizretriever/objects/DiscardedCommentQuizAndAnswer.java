package com.alejandrorg.nejmfb.quizretriever.objects;

import com.alejandrorg.nejmfb.objects.Comment;

public class DiscardedCommentQuizAndAnswer {

	private Comment comment;
	private Quiz quiz;
	private Answer answer;
	
	public DiscardedCommentQuizAndAnswer(Comment c, Quiz q, Answer a) {
		this.comment = c;
		this.quiz = q;
		this.answer = a;
	}

	public Comment getComment() {
		return comment;
	}

	public Quiz getQuiz() {
		return quiz;
	}

	public Answer getAnswer() {
		return answer;
	}

	
}
