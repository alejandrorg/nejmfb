package com.alejandrorg.nejmfb.evaluation;

import com.alejandrorg.nejmfb.core.Constants;

public class CommentEvaluationPrecisionResult {

	private int TP;
	private int FP;
	
	private double precision;

	
	private int typeCommentEvaluation;
	
	public CommentEvaluationPrecisionResult(int nTP, int nFP,  int t) {
		this.TP = nTP;
		this.FP = nFP;
		this.precision = ((double)TP) / ((double)TP + (double)FP);
		this.typeCommentEvaluation = t;
	}

	public int getTP() {
		return TP;
	}

	public int getFP() {
		return FP;
	}

	public double getPrecision() {
		return this.precision;
	}

	public int getTypeCommentEvaluation() {
		return typeCommentEvaluation;
	}
	
	public String toString() {
		String ret = "Evaluation results:\n";
		ret += "Type of comment identification method: [" + typeCommentEvaluation + "]: " + Constants.getHowCommentWasIdentifiedString(typeCommentEvaluation) + "\n";
		ret += "\tTP: " + getTP() + "\n";
		ret += "\tFP: " + getFP() + "\n";
		ret += "\n\n";
		ret += "\tPrecision: " + getPrecision() + "\n";
		return ret;
	}
}
