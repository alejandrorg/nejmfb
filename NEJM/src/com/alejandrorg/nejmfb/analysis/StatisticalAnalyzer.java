package com.alejandrorg.nejmfb.analysis;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.LinkedList;

import com.alejandrorg.nejmfb.analysis.objects.AnswerOptionWithNumberOfOcurrences;
import com.alejandrorg.nejmfb.analysis.objects.AnswerTrend;
import com.alejandrorg.nejmfb.analysis.objects.QuizAndAnswerAndCommentsResults;
import com.alejandrorg.nejmfb.core.Constants;
import com.alejandrorg.nejmfb.core.MyLogger;
import com.alejandrorg.nejmfb.objects.CommentIdentificationManager;
import com.alejandrorg.nejmfb.objects.DateComparator;
import com.alejandrorg.nejmfb.quizretriever.objects.CommentAndAnswer;

public class StatisticalAnalyzer {

	private boolean printData = false;
	private NumberFormat formatter = new DecimalFormat("#0.00");
	private LinkedList<QuizAndAnswerAndCommentsResults> results;
	private MyLogger log;
	private CommentIdentificationManager cim;

	private int postsWhereMajorityGetRight;
	private int postsWhereWOCWorks;
	private int totalNumberOfDismissedAnswers;
	private int totalRetrievedCommentAnswers;
	private int numberOfPostsWithMoreDismissedAnswersThanValidAnswers;
	private int numberOfPostsMajorityDontWorkAndDismissedAnswersWereGreaterThanValidAnswers;
	private int numberOfPostsWOCDontWorkAndDismissedAnswersWereGreaterThanValidAnswers;

	private int numOfTrends;
	private int numMostVotedAnswerIsCorrect;
	private int numMajorityGetRight;
	private int numTrendsCorrectAnswer;
	private int numTrendsIncorrectAnswer;
	private int largestCorrectAnswerTrendSize;
	private int largestIncorrectAnswerTrendSize;

	private LinkedList<QuizAndAnswerAndCommentsResults> finalResults;

	/**
	 * Constructor.
	 * 
	 * @param rs
	 *            Receives the results from the comment analyzer.
	 * @param toci
	 *            Receives an array with the values related to the type of
	 *            comment identification.
	 */
	public StatisticalAnalyzer(LinkedList<QuizAndAnswerAndCommentsResults> rs,
			CommentIdentificationManager cim) {
		this.results = rs;
		this.createLogger(true);
		this.cim = cim;
		this.finalResults = new LinkedList<QuizAndAnswerAndCommentsResults>();
	}

	/**
	 * Method to create the logger.
	 * 
	 * @param append
	 *            boolean to see if should append content to last file or start
	 *            from scratch.
	 */
	private void createLogger(boolean append) {
		try {
			this.log = new MyLogger(append);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error creating logger: " + e.getMessage());
		}
	}

	public void runBasicStatisticalAnalysis() {
		this.postsWhereMajorityGetRight = 0;
		this.postsWhereWOCWorks = 0;
		this.totalNumberOfDismissedAnswers = 0;
		this.totalRetrievedCommentAnswers = 0;
		this.numberOfPostsWithMoreDismissedAnswersThanValidAnswers = 0;
		this.numberOfPostsMajorityDontWorkAndDismissedAnswersWereGreaterThanValidAnswers = 0;
		this.numberOfPostsWOCDontWorkAndDismissedAnswersWereGreaterThanValidAnswers = 0;
		for (int i = 0; i < results.size(); i++) {
			QuizAndAnswerAndCommentsResults fr = results.get(i);

			int ca = fr.getNumberOfCorrectAnswers();
			int ica = fr.getNumberOfIncorrectAnswers();
			int dismissed = fr.getNumberOfAnswerComments() - (ca + ica);
			fr.setNumberOfDismissedAnswers(dismissed);
			AnswerOptionWithNumberOfOcurrences mostVotedAnswer = fr
					.getMostVotedAnswer();

			if (mostVotedAnswer.getAnswerOption().getOptionID()
					.equals(fr.getAnswer().getAnswerOption())) {
				postsWhereWOCWorks++;
				if (dismissed > (ca + ica)) {
					numberOfPostsWOCDontWorkAndDismissedAnswersWereGreaterThanValidAnswers++;
				}

				if (printData) {
					log.log("Post-Answer ID: " + fr.getPostAnswerID());
					System.out
							.println("Number of answers provided in the quiz: "
									+ fr.getNumberOfAnswerComments());
					log.log("Number of correct answers: " + ca);
					log.log("Number of incorrect answers: " + ica);
					log.log("Number of dismissed answers: " + dismissed);
					log.log("Most voted option win!");
				}
				fr.setMostVotedAnswersIsCorrect(true);
			}
			totalNumberOfDismissedAnswers += dismissed;
			totalRetrievedCommentAnswers += fr.getNumberOfAnswerComments();
			if (dismissed > (ca + ica)) {
				fr.setNumberOfDismissedAnswersIsGreaterThanValidOnes(true);
				if (printData) {
					System.out
							.println("\tHere the number of dismissed answer is greater than the valid ones!");
					log.log("");
				}
				numberOfPostsWithMoreDismissedAnswersThanValidAnswers++;
				if (ca < ica) {
					numberOfPostsMajorityDontWorkAndDismissedAnswersWereGreaterThanValidAnswers++;
				}
			}
			if (ca > ica) {
				fr.setMajorityGetRight(true);
				postsWhereMajorityGetRight++;
			}
			finalResults.add(fr);
		}
	}

	/**
	 * Method to analyze if exists trends in the pattern of answers in the
	 * comments of the quizs.
	 * 
	 * @param finalResults
	 *            Receives the final results.
	 */
	@SuppressWarnings("unchecked")
	public void runTrendAnalysis() {
		numOfTrends = 0;
		numMostVotedAnswerIsCorrect = 0;
		numMajorityGetRight = 0;
		numTrendsCorrectAnswer = 0;
		numTrendsIncorrectAnswer = 0;

		largestCorrectAnswerTrendSize = 0;
		largestIncorrectAnswerTrendSize = 0;

		Collections.sort(finalResults, new DateComparator());

		for (int i = 0; i < finalResults.size(); i++) {
			QuizAndAnswerAndCommentsResults qac = finalResults.get(i);
			boolean trendAnalysis[] = analyzeTrends(qac);
			if (qac.hasTrend()) {
				numOfTrends++;
				if (trendAnalysis[0]) {
					numMostVotedAnswerIsCorrect++;
				}
				if (trendAnalysis[1]) {
					numMajorityGetRight++;
				}
				if (trendAnalysis[2]) {
					numTrendsCorrectAnswer++;
					for (int j = 0; j < qac.getTrends().size(); j++) {
						if (qac.getTrends().get(j).getNumberOfAppearances() > largestCorrectAnswerTrendSize) {
							largestCorrectAnswerTrendSize = qac.getTrends().get(j).getNumberOfAppearances();
						}
					}
				} else {
					numTrendsIncorrectAnswer++;
					for (int j = 0; j < qac.getTrends().size(); j++) {
						if (qac.getTrends().get(j).getNumberOfAppearances() > largestIncorrectAnswerTrendSize) {
							largestIncorrectAnswerTrendSize = qac.getTrends().get(j).getNumberOfAppearances();
						}
					}
				}
			}

		}
	}

	/**
	 * Method to analyze if exists trends in the pattern of answers in the
	 * comments of a given quiz.
	 * 
	 * @param qac
	 *            Receive a post with their comments and the validations.
	 */
	@SuppressWarnings("unchecked")
	private boolean[] analyzeTrends(QuizAndAnswerAndCommentsResults qac) {
		LinkedList<CommentAndAnswer> commentsWithAnswer = qac
				.getCommentsWithAnswer();
		LinkedList<AnswerTrend> answerTrends = new LinkedList<AnswerTrend>();
		if (commentsWithAnswer.size() >= 2) {
			Collections.sort(commentsWithAnswer, new DateComparator());
			String currentAnswerOption = commentsWithAnswer.get(0)
					.getChoosenAnswerOption().getOptionID();
			int totalTrendAppearances = 1;
			for (int i = 1; i < commentsWithAnswer.size(); i++) {
				String answerOption = commentsWithAnswer.get(i)
						.getChoosenAnswerOption().getOptionID();
				if (answerOption.equalsIgnoreCase(currentAnswerOption)) {
					totalTrendAppearances++;
				} else {
					if (totalTrendAppearances > Constants.NUMBER_OF_APPEARANCES_TO_BE_TREND) {
						answerTrends.add(new AnswerTrend(currentAnswerOption,
								totalTrendAppearances));
					}
					totalTrendAppearances = 1;
					currentAnswerOption = answerOption;
				}
			}
		}
		boolean isTrend = false;
		boolean mostVotedAnswerCorrect = true;
		boolean majorityGetRight = true;
		boolean trendOfCorrectAnswer = false;
		for (int i = 0; i < answerTrends.size(); i++) {
			if ((answerTrends.get(i).getNumberOfAppearances() > Constants.NUMBER_OF_APPEARANCES_TO_BE_TREND)) {
				answerTrends.get(i).setIsTrend(true);
				if (qac.getAnswer()
						.getAnswerOption()
						.equalsIgnoreCase(answerTrends.get(i).getAnswerOption())) {
					trendOfCorrectAnswer = true;
				}
				isTrend = true;
			}
		}
		if (isTrend) {
			if (!qac.isMostVotedAnswerTheCorrect()) {
				mostVotedAnswerCorrect = false;
			}
			if (!qac.getMajorityGetRight()) {
				majorityGetRight = false;
			}
		}
		qac.setTrends(answerTrends);
		return new boolean[] { mostVotedAnswerCorrect, majorityGetRight,
				trendOfCorrectAnswer };
	}

	public NumberFormat getFormatter() {
		return formatter;
	}

	public MyLogger getLog() {
		return log;
	}

	public int getPostsWhereMajorityGetRight() {
		return postsWhereMajorityGetRight;
	}

	public int getPostsWhereWOCWorks() {
		return postsWhereWOCWorks;
	}

	public int getTotalNumberOfDismissedAnswers() {
		return totalNumberOfDismissedAnswers;
	}

	public int getTotalRetrievedCommentAnswers() {
		return totalRetrievedCommentAnswers;
	}

	public int getNumberOfPostsWithMoreDismissedAnswersThanValidAnswers() {
		return numberOfPostsWithMoreDismissedAnswersThanValidAnswers;
	}

	public int getNumberOfPostsMajorityDontWorkAndDismissedAnswersWereGreaterThanValidAnswers() {
		return numberOfPostsMajorityDontWorkAndDismissedAnswersWereGreaterThanValidAnswers;
	}

	public int getNumberOfPostsWOCDontWorkAndDismissedAnswersWereGreaterThanValidAnswers() {
		return numberOfPostsWOCDontWorkAndDismissedAnswersWereGreaterThanValidAnswers;
	}

	public int getNumOfTrends() {
		return numOfTrends;
	}

	public int getNumMostVotedAnswerIsCorrect() {
		return numMostVotedAnswerIsCorrect;
	}

	public int getNumMajorityGetRight() {
		return numMajorityGetRight;
	}

	public LinkedList<QuizAndAnswerAndCommentsResults> getFinalResults() {
		return finalResults;
	}

	public CommentIdentificationManager getTypeOfCommentIdentificationResults() {
		return cim;
	}

	public int getNumTrendsCorrectAnswer() {
		return numTrendsCorrectAnswer;
	}

	public int getNumTrendsIncorrectAnswer() {
		return numTrendsIncorrectAnswer;
	}

	public int getLargestCorrectAnswerTrendSize() {
		return largestCorrectAnswerTrendSize;
	}

	public int getLargestIncorrectAnswerTrendSize() {
		return largestIncorrectAnswerTrendSize;
	}

}
