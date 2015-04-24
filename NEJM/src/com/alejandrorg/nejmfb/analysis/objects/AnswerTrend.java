package com.alejandrorg.nejmfb.analysis.objects;

public class AnswerTrend {

	private String answerOption;
	private int numberOfAppearances;
	private boolean isTrend;
	
	public AnswerTrend(String answerOption, int tta) {
		this.answerOption = answerOption;
		this.numberOfAppearances = tta;
		this.isTrend = false;
	}

	public String getAnswerOption() {
		return answerOption;
	}

	public void setAnswerOption(String answerOption) {
		this.answerOption = answerOption;
	}

	public int getNumberOfAppearances() {
		return numberOfAppearances;
	}

	public void setNumberOfAppearances(int numberOfAppearances) {
		this.numberOfAppearances = numberOfAppearances;
	}

	public void setIsTrend(boolean b) {
		this.isTrend = b;
	}
	
	public boolean isTrend() {
		return this.isTrend;
	}
}
