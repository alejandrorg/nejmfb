package com.alejandrorg.nejmfb.quizretriever.objects;

import com.alejandrorg.nejmfb.core.Constants;

public class AnswerOption {

	private String optionText;
	private String optionID;

	private boolean isValidAnswerOption;
	
	public AnswerOption(String o) {
		o = o.toUpperCase();
		for (int i = 0; i < Constants.ANSWER_OPTIONS.length; i++) {
			if (o.startsWith(Constants.ANSWER_OPTIONS[i])) {
				optionID = Character.toString(Constants.ANSWER_OPTIONS[i].charAt(0));
				optionText = o.substring(2, o.length()).trim();
			}
		}
		this.isValidAnswerOption = true;
	}

	public AnswerOption(AnswerOption answerOption) {
		this.optionText = answerOption.getOptionText();
		this.optionID = answerOption.getOptionID();
		this.isValidAnswerOption = answerOption.isValidAnswerOption();
	}

	public String getOptionText() {
		return optionText;
	}

	public void setOption(String optionText) {
		this.optionText = optionText;
	}

	public String getOptionID() {
		return optionID;
	}

	public void setOptionID(String optionID) {
		this.optionID = optionID;
	}

	public String toString() {
		return optionID + "(" + optionText + ")";
	}

	public boolean isValidAnswerOption() {
		return isValidAnswerOption;
	}

	public void setValidAnswerOption(boolean isValidAnswerOption) {
		this.isValidAnswerOption = isValidAnswerOption;
	}

	public boolean equals(Object o) {
		if (o instanceof AnswerOption) {
			AnswerOption aop = (AnswerOption)o;
			return aop.getOptionID().equalsIgnoreCase(this.getOptionID());
		}
		return false;
	}
}
