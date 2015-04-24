package com.alejandrorg.nejmfb.evaluation;

import com.alejandrorg.nejmfb.core.StaticUtils;

public class CommentEvaluationObject {

	private String quiz;
	private String comment;
	private String result;
	private String resultPrecision;
	private int typeOfIdentification;

	public CommentEvaluationObject(String q, int cimID, String r, String c) {
		this.quiz = q;
		this.typeOfIdentification = cimID;
		this.comment = c;
		this.result = r;
	}

	public CommentEvaluationObject(String q, int cimID, String r, String c,
			String rp) {
		this.quiz = q;
		this.typeOfIdentification = cimID;
		this.comment = c;
		this.result = r;
		this.resultPrecision = rp;
	}

	public String getQuiz() {
		return quiz;
	}

	public String getComment() {
		return comment;
	}

	public PRValues getResult() {
		if (result.equalsIgnoreCase("TP"))
			return PRValues.TP;
		if (result.equalsIgnoreCase("TN"))
			return PRValues.TN;
		if (result.equalsIgnoreCase("FP"))
			return PRValues.FP;
		if (result.equalsIgnoreCase("FN"))
			return PRValues.FN;
		return PRValues.NONE;
	}

	public PRValues getResultPrecision() {
		if (!StaticUtils.isEmpty(resultPrecision)) {
			if (resultPrecision.equalsIgnoreCase("TP"))
				return PRValues.TP;
			if (resultPrecision.equalsIgnoreCase("TN"))
				return PRValues.TN;
			if (resultPrecision.equalsIgnoreCase("FP"))
				return PRValues.FP;
			if (resultPrecision.equalsIgnoreCase("FN"))
				return PRValues.FN;
		}
		return PRValues.NONE;
	}

	public int getTypeOfIdentification() {
		return typeOfIdentification;
	}

}
