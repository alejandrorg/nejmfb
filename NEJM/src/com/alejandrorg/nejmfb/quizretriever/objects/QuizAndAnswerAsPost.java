package com.alejandrorg.nejmfb.quizretriever.objects;

import com.alejandrorg.nejmfb.objects.Post;

public class QuizAndAnswerAsPost {

	private Post quizPost;
	private Post answerPost;

	public QuizAndAnswerAsPost(Post q, Post a) {
		this.quizPost = q;
		this.answerPost = a;
	}

	public Post getQuizPost() {
		return quizPost;
	}

	public void setQuiz(Post quiz) {
		this.quizPost = quiz;
	}

	public Post getAnswerPost() {
		return answerPost;
	}

	public void setAnswer(Post answer) {
		this.answerPost = answer;
	}

	public boolean equals(Object o) {
		if (o instanceof QuizAndAnswerAsPost) {
			QuizAndAnswerAsPost ob = (QuizAndAnswerAsPost) o;
			return ob.getQuizPost().getId().equalsIgnoreCase(this.getQuizPost().getId()) || ob.getAnswerPost().getId().equalsIgnoreCase(this.getAnswerPost().getId());
		}
		return false;
	}

}
