package com.alejandrorg.nejmfb.analysis;

import com.alejandrorg.nejmfb.quizretriever.objects.CommentAndAnswer;
import com.alejandrorg.nejmfb.quizretriever.objects.Quiz;

public class CommentAndAnswerAndQuiz {

	private CommentAndAnswer commentAndAnswer;
	private Quiz quiz;
	
	public CommentAndAnswerAndQuiz(CommentAndAnswer caa, Quiz q) {
		this.commentAndAnswer = caa;
		this.quiz = q;
	}

	public CommentAndAnswer getCommentAndAnswer() {
		return commentAndAnswer;
	}

	public Quiz getQuiz() {
		return quiz;
	}

	
	
}
