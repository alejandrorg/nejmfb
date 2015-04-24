package com.alejandrorg.nejmfb.quizretriever.objects;


public class QuizAndAnswer {

	private Quiz quiz;
	private Answer answer;

	public QuizAndAnswer(Quiz q, Answer a) {
		this.quiz = q;
		this.answer = a;
	}

	public void setQuiz(Quiz quiz) {
		this.quiz = quiz;
	}

	public void setAnswer(Answer answer) {
		this.answer = answer;
	}

	public Answer getAnswer() {
		return answer;
	}

	public Quiz getQuiz() {
		return quiz;
	}
}
