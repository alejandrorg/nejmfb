package com.alejandrorg.nejmfb.mains;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.Properties;

import com.alejandrorg.nejmfb.core.Constants;
import com.alejandrorg.nejmfb.evaluation.CommentEvaluationObject;
import com.alejandrorg.nejmfb.evaluation.CommentEvaluationPrecisionResult;
import com.alejandrorg.nejmfb.evaluation.PRValues;

public class MainEvaluationCommentIdentification {

	private LinkedList<CommentEvaluationObject> commentsDismissed;
	private LinkedList<CommentEvaluationObject> commentsDetected;
	private File folderDismissed;
	private File folderDetected;
	private LinkedList<CommentEvaluationPrecisionResult> commentsDetectedPrecision;

	private int IDS_COMMENT_DETECTION[] = { 101, 102, 103, 111, 112, 113, 201,
			211, 301, 311, 401, 402, 412 };

	public MainEvaluationCommentIdentification() throws Exception {
		folderDismissed = new File(
				"commentIdentificationEvaluationResults/dismissed");
		folderDetected = new File(
				"commentIdentificationEvaluationResults/detected");
		commentsDismissed = new LinkedList<CommentEvaluationObject>();
		commentsDetected = new LinkedList<CommentEvaluationObject>();
		commentsDetectedPrecision = new LinkedList<CommentEvaluationPrecisionResult>();
		init();
	}

	private void init() throws Exception {
		loadComments(commentsDismissed, folderDismissed);
		loadComments(commentsDetected, folderDetected);
		calculateMetricsForDismissedAndDetectedIdentification();
		calculateMetricsForDetectedComments();
	}

	private void calculateMetricsForDetectedComments() {
		int meanNTP = 0, meanNFP = 0;
		for (int i = 0; i < IDS_COMMENT_DETECTION.length; i++) {
			int identMethod = IDS_COMMENT_DETECTION[i];
			int nTP = 0, nFP = 0;
			for (int j = 0; j < commentsDetected.size(); j++) {
				CommentEvaluationObject ob = commentsDetected.get(j);
				if (ob.getTypeOfIdentification() == identMethod) {
					if (ob.getResultPrecision() != PRValues.NONE) {
						if (ob.getResultPrecision() == PRValues.TP)
							nTP++;
						if (ob.getResultPrecision() == PRValues.FP)
							nFP++;
						if (ob.getResultPrecision() == PRValues.FP
								&& ob.getResultPrecision() == PRValues.TP) {
							System.out.println("Error, invalid value!: "
									+ ob.getResultPrecision());
						}
					}
				}
			}
			meanNTP += nTP;
			meanNFP += nFP;
			CommentEvaluationPrecisionResult obR = new CommentEvaluationPrecisionResult(
					nTP, nFP, identMethod);
			commentsDetectedPrecision.add(obR);

		}
		System.out.println();
		System.out.println("Analysis of the comment detection algorithms:");
		System.out.println();
		for (int i = 0; i < commentsDetectedPrecision.size(); i++) {
			CommentEvaluationPrecisionResult obR = commentsDetectedPrecision
					.get(i);
			System.out.println(obR.toString());
			System.out.println();
		}
		System.out.println();
		System.out
				.println("Global results of the comment detection algorithms:");
		System.out.println();
		System.out.println("\tTP: " + meanNTP);
		System.out.println("\tFP: " + meanNFP);
		System.out.println();
		System.out.println("\tPrecision: " + getPrecision(meanNTP, meanNFP));

	}

	private void calculateMetricsForDismissedAndDetectedIdentification() {
		LinkedList<CommentEvaluationObject> all = new LinkedList<CommentEvaluationObject>();
		all.addAll(commentsDismissed);
		all.addAll(commentsDetected);
		int nTP = 0, nTN = 0, nFN = 0, nFP = 0;
		for (int i = 0; i < all.size(); i++) {
			CommentEvaluationObject ob = all.get(i);
			if (ob.getResult() != PRValues.NONE) {
				if (ob.getResult() == PRValues.TP)
					nTP++;
				if (ob.getResult() == PRValues.TN)
					nTN++;
				if (ob.getResult() == PRValues.FP)
					nFP++;
				if (ob.getResult() == PRValues.FN)
					nFN++;
			}
		}
		double precision = getPrecision(nTP, nFP);
		double recall = getRecall(nTP, nFN);
		double specificity = getSpecificity(nTN, nFP);
		double F1 = getF1(nTP, nFP, nFN);

		System.out
				.println("Analysis of the metrics for identification of correct and dismissed comments:");
		System.out.println("\tPrecision: " + precision);
		System.out.println("\tRecall: " + recall);
		System.out.println("\tSpecificity: " + specificity);
		System.out.println("\tF1: " + F1);
		System.out.println();
		System.out.println("\tTP: " + nTP);
		System.out.println("\tTN: " + nTN);
		System.out.println("\tFP: " + nFP);
		System.out.println("\tFN: " + nFN);
	}

	private double getF1(int TP, int FP, int FN) {
		double f1 = 2 * ((double) TP)
				/ ((2 * (double) TP) + (double) FP + (double) FN);
		return f1;
	}

	private double getSpecificity(int TN, int FP) {
		double specificity = ((double) TN) / ((double) FP + (double) TN);
		return specificity;
	}

	private double getRecall(int TP, int FN) {
		double recall = ((double) TP) / ((double) TP + (double) FN);
		return recall;
	}

	private double getPrecision(int TP, int FP) {
		double precision = ((double) TP) / ((double) TP + (double) FP);
		return precision;
	}

	private void loadComments(LinkedList<CommentEvaluationObject> lst, File fd)
			throws Exception {
		for (int i = 0; i < fd.listFiles().length; i++) {
			CommentEvaluationObject ob = process(fd.listFiles()[i]);
			lst.add(ob);
		}
	}

	private CommentEvaluationObject process(File file) throws Exception {
		Properties prop = new Properties();
		prop.load(new FileInputStream(file));
		String quiz = prop.getProperty("QUIZ");
		String cimIDStr = prop.getProperty("COMMENT_IDENTIFICATION_METHOD_ID");
		String result = prop.getProperty("RESULT");
		int cimID = Integer.parseInt(cimIDStr);
		String comment = "";
		if (cimID == Constants.COMMENT_DISMISSED) {
			comment = prop.getProperty("DISCARDED_COMMENT");
			return new CommentEvaluationObject(quiz, cimID, result, comment);

		} else {
			comment = prop.getProperty("COMMENT");
			String resultPrecision = prop.getProperty("RESULT_PRECISION");
			return new CommentEvaluationObject(quiz, cimID, result, comment,
					resultPrecision);
		}

	}

	public static void main(String[] args) {
		try {
			new MainEvaluationCommentIdentification();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
