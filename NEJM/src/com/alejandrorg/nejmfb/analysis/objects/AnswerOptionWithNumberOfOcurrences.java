package com.alejandrorg.nejmfb.analysis.objects;

import com.alejandrorg.nejmfb.quizretriever.objects.AnswerOption;


public class AnswerOptionWithNumberOfOcurrences extends AnswerOption {

	private int numberOfOccurrences;
	private AnswerOption answerOption;
	public AnswerOptionWithNumberOfOcurrences(AnswerOption aop) {
		super(aop);
		this.answerOption = aop;
		this.numberOfOccurrences = 0;
	}

	public int getNumberOfOccurrences() {
		return this.numberOfOccurrences;
	}

	public void incNumberOfOccurrences() {
		this.numberOfOccurrences++;
	}

	public AnswerOption getAnswerOption() {
		return answerOption;
	}

}
