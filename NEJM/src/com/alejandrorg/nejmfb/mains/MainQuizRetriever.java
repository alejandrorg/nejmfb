package com.alejandrorg.nejmfb.mains;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import com.alejandrorg.nejmfb.analysis.CommentAndAnswerAndQuiz;
import com.alejandrorg.nejmfb.analysis.StatisticalAnalyzer;
import com.alejandrorg.nejmfb.analysis.objects.QuizAndAnswerAndCommentsResults;
import com.alejandrorg.nejmfb.core.Constants;
import com.alejandrorg.nejmfb.quizretriever.QuizAndAnswerCommentAnalyzer;
import com.alejandrorg.nejmfb.quizretriever.QuizAndAnswerRetriever;
import com.alejandrorg.nejmfb.quizretriever.QuizAndAnswerSeparator;
import com.alejandrorg.nejmfb.quizretriever.objects.DiscardedCommentQuizAndAnswer;

public class MainQuizRetriever {

	public MainQuizRetriever() {
		try {
			run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private void run() throws Exception {

		String postsFolder = "data/crawlerOutput";
		String quizsFile = "separatorOutput/quizs.data";
		String answersFile = "separatorOutput/answers.data";
		String allPostsFile = "separatorOutput/allPosts.data";
		String errorPostsFile = "separatorOutput/errorPosts.data";

		String usersIDsFile = "usersIDs.lst";

		String qaStatisticsFile = "statistics/quizsAnswers.tsv";
		String commentsStatisticsFile = "statistics/comments.tsv";

		String statisticsFile = "statistics/statistics.tsv";

		QuizAndAnswerSeparator qas = new QuizAndAnswerSeparator(postsFolder,
				quizsFile, answersFile, allPostsFile, errorPostsFile);
		qas.run();

		/*
		 * Two options to run qar.
		 */
		/*
		 * First one: pass the files which contains the data.
		 * 
		 * Use this one if separation has been already executed previously and
		 * is not going to be executed again.
		 */
		QuizAndAnswerRetriever qar1 = new QuizAndAnswerRetriever(quizsFile,
				answersFile, allPostsFile);
		qar1.run();

		// qar1.saveUserCommentsID(usersIDsFile);

		/*
		 * Second one: pass the lists with the data directly.
		 * 
		 * Use this one if you want to execute always all the workflow.
		 */

		// QuizAndAnswerRetriever qar2 = new
		// QuizAndAnswerRetriever(qas.getQuizPosts(),
		// qas.getAnswersPosts(), qas.getPosts());
		// qar2.run();

		QuizAndAnswerCommentAnalyzer qa = new QuizAndAnswerCommentAnalyzer(
				qar1.getQuizsAndAnswers());
		qa.run();
		qa.saveDataForStatisticsTwoFiles(qaStatisticsFile,
				commentsStatisticsFile, true);
		qa.saveDataForStatisticsOneFile(statisticsFile, true);

		StatisticalAnalyzer sa = new StatisticalAnalyzer(qa.getResults(),
				qa.getNumericResultsOfCommentIdentification());
		sa.runBasicStatisticalAnalysis();
		sa.runTrendAnalysis();

		showBasicStatisticalResults(sa, qa);
		showStatisticsAboutTypeOfCommentIdentification(sa);
		showTrends(sa);

//		saveCommentIdentificationSample(
//				"commentIdentificationEvaluationSample", "detected",
//				"dismissed", qa);
	}

	@SuppressWarnings("unused")
	private void saveCommentIdentificationSample(String fBase,
			String detectedFolder, String dismissedFolder,
			QuizAndAnswerCommentAnalyzer qa) throws Exception {
		File folderDetected = new File(fBase + "/" + detectedFolder);
		File folderDismissed = new File(fBase + "/" + dismissedFolder);
		HashMap<Integer, Integer> map = qa.getNumericResultsOfCommentIdentification().getHashMap();
		Iterator<Integer> keySetIterator = map.keySet().iterator();

		while (keySetIterator.hasNext()) {
			Integer key = keySetIterator.next();
			LinkedList<CommentAndAnswerAndQuiz> resultsByMethod = new LinkedList<CommentAndAnswerAndQuiz>();
			for (int j = 0; j < qa.getResults().size(); j++) {
				for (int k = 0; k < qa.getResults().get(j)
						.getCommentsWithAnswer().size(); k++) {
					if (qa.getResults().get(j).getCommentsWithAnswer()
							.get(k).getHowFound() == key.intValue()) {
						CommentAndAnswerAndQuiz obToAdd = new CommentAndAnswerAndQuiz(
								qa.getResults().get(j)
										.getCommentsWithAnswer().get(k), qa
										.getResults().get(j).getQuiz());
						resultsByMethod.add(obToAdd);
					}
				}
			}
			extractRandomObjectsAndSave(resultsByMethod, key.intValue(), 10,
					folderDetected);
		}
		extractRandomDiscardedCommentsAndSave(qa.getDiscardedComments(),
				Constants.COMMENT_DISMISSED, 120, folderDismissed);
	}

	private void extractRandomDiscardedCommentsAndSave(
			LinkedList<DiscardedCommentQuizAndAnswer> discardedComments,
			int commentDismissed, int numberToExtract, File folder)
			throws Exception {
		LinkedList<DiscardedCommentQuizAndAnswer> results = new LinkedList<DiscardedCommentQuizAndAnswer>();
		int extracted = 0;
		int seed = new java.util.Random(System.currentTimeMillis())
				.nextInt(999999);
		do {
			seed++;
			int rnd = new java.util.Random(System.currentTimeMillis() + seed)
					.nextInt(discardedComments.size());
			DiscardedCommentQuizAndAnswer obToAdd = discardedComments.get(rnd);
			if (!results.contains(obToAdd)) {
				results.add(obToAdd);
				extracted++;
			}
		} while (extracted < numberToExtract);

		for (int i = 0; i < results.size(); i++) {
			DiscardedCommentQuizAndAnswer ob = results.get(i);
			String comID = ob.getComment().getId();
			String file = folder.getAbsolutePath().toString() + "/" + comID
					+ ".cmt";
			Properties prop = new Properties();
			prop.setProperty("QUIZ", ob.getQuiz().getOriginalPost()
					.getMessage());
			prop.setProperty("DISCARDED_COMMENT", ob.getComment().getMessage());
			prop.setProperty("COMMENT_IDENTIFICATION_METHOD_ID",
					Integer.toString(commentDismissed));
			prop.setProperty("COMMENT_IDENTIFICATION_METHOD_STRING", Constants
					.getHowCommentWasIdentifiedString(commentDismissed));
			prop.setProperty("RESULT", "");
			prop.store(new FileOutputStream(file), "");
		}
	}

	private void extractRandomObjectsAndSave(
			LinkedList<CommentAndAnswerAndQuiz> resultsByMethod,
			int howCommentWasIdentified, int numberToExtract, File folder)
			throws Exception {
		LinkedList<CommentAndAnswerAndQuiz> results = new LinkedList<CommentAndAnswerAndQuiz>();
		int extracted = 0;
		int seed = new java.util.Random(System.currentTimeMillis())
				.nextInt(999999);
		do {
			seed++;
			int rnd = new java.util.Random(System.currentTimeMillis() + seed)
					.nextInt(resultsByMethod.size());
			CommentAndAnswerAndQuiz obToAdd = resultsByMethod.get(rnd);
			if (!results.contains(obToAdd)) {
				results.add(obToAdd);
				extracted++;
			}
		} while (extracted < numberToExtract);

		for (int i = 0; i < results.size(); i++) {
			CommentAndAnswerAndQuiz ob = results.get(i);
			String comID = ob.getCommentAndAnswer().getComment().getId();
			String file = folder.getAbsolutePath().toString() + "/" + howCommentWasIdentified + "_" + comID
				 + ".cmt";
			Properties prop = new Properties();
			prop.setProperty("QUIZ", ob.getQuiz().getOriginalPost()
					.getMessage());
			prop.setProperty("COMMENT", ob.getCommentAndAnswer().getComment()
					.getMessage());
			prop.setProperty("DETECTED_ANSWER", ob.getCommentAndAnswer()
					.getChoosenAnswerOption().getOptionID()
					+ " - "
					+ ob.getCommentAndAnswer().getChoosenAnswerOption()
							.getOptionText());
			prop.setProperty("COMMENT_IDENTIFICATION_METHOD_ID",
					Integer.toString(ob.getCommentAndAnswer().getHowFound()));
			prop.setProperty("COMMENT_IDENTIFICATION_METHOD_STRING", Constants
					.getHowCommentWasIdentifiedString(ob.getCommentAndAnswer()
							.getHowFound()));
			prop.setProperty("RESULT", "");
			prop.setProperty("RESULT_PRECISION", "");
			prop.store(new FileOutputStream(file), "");
		}
	}

	/*
	 * Analysis of "correct/dismissed" comment identification:
	 * 
	 * TP = System detects as answer and it is TN = System discard as answer and
	 * it is not FP = System detects as answer and it is not FN = System discard
	 * as answer and it is
	 */
	private void showStatisticsAboutTypeOfCommentIdentification(
			StatisticalAnalyzer sa) {

		HashMap<Integer, Integer> map = sa
				.getTypeOfCommentIdentificationResults().getHashMap();
		sa.getLog().log("");
		sa.getLog().log(
				"--- Statistics about comment identification --- ("
						+ map.size() + " identification methods)");
		sa.getLog().log("");
		Iterator<Integer> keySetIterator = map.keySet().iterator();

		while (keySetIterator.hasNext()) {
			Integer key = keySetIterator.next();
			sa.getLog().log(
					"\t"
							+ Constants.getHowCommentWasIdentifiedString(key
									.intValue()) + ": "
							+ map.get(key).intValue());
		}
		sa.getLog().log("");
	}

	private void showTrends(StatisticalAnalyzer sa) {
		sa.getLog().log("");
		sa.getLog().log("--- Trend analysis ---");
		sa.getLog().log("");
		sa.getLog()
				.log("Number of trends found: "
						+ sa.getNumOfTrends()
						+ " of "
						+ sa.getFinalResults().size()
						+ " ("
						+ sa.getFormatter()
								.format((double) (((double) sa.getNumOfTrends() * 100) / (double) sa
										.getFinalResults().size())) + "%)");
		sa.getLog().log(
				"Number of trends where most voted answer was the correct: "
						+ sa.getNumMostVotedAnswerIsCorrect());
		sa.getLog().log(
				"Number of trends where majority of the users got right: "
						+ sa.getNumMajorityGetRight());
		sa.getLog().log(
				"Number of trends in correct answer: "
						+ sa.getNumTrendsCorrectAnswer());
		sa.getLog().log(
				"Largest correct answer trend size: "
						+ sa.getLargestCorrectAnswerTrendSize());
		sa.getLog().log(
				"Number of trends in incorrect answer: "
						+ sa.getNumTrendsIncorrectAnswer());
		sa.getLog().log(
				"Largest incorrect answer trend size: "
						+ sa.getLargestIncorrectAnswerTrendSize());

		sa.getLog().log("");
		for (int i = 0; i < sa.getFinalResults().size(); i++) {
			QuizAndAnswerAndCommentsResults ob = sa.getFinalResults().get(i);
			if (ob.hasTrend()) {
				String content = "";
				content += "QUIZ: " + ob.getQuiz().getOriginalPost().getId()
						+ "\n";
				content += "Correct answer: "
						+ ob.getAnswer().getAnswerOption() + "\n";
				for (int j = 0; j < ob.getTrends().size(); j++) {
					content += "\tAnswerTrend: "
							+ ob.getTrends().get(j).getAnswerOption() + " ["
							+ ob.getTrends().get(j).getNumberOfAppearances()
							+ "]\n";
				}
				if (!ob.isMostVotedAnswerTheCorrect()) {
					content += ".. and most voted answer was not the correct!\n";
				}
				if (!ob.getMajorityGetRight()) {
					content += ".. and majority was not right!\n";
				}
				content += "\n";
				sa.getLog().log(content);
				sa.getLog().log("");
			}
		}
		sa.getLog().log("");
	}

	private void showBasicStatisticalResults(StatisticalAnalyzer sa,
			QuizAndAnswerCommentAnalyzer qa) {
		sa.getLog().log("");
		sa.getLog().log("--- Basic statistics ---");
		sa.getLog().log("");
		sa.getLog().log(
				"Date of first analyzed quiz: "
						+ qa.getResults().get(0).getQuiz().getOriginalPost()
								.getCreatedDate().toString());
		sa.getLog().log(
				"Date of last analyzed quiz: "
						+ qa.getResults().get(qa.getResults().size() - 1)
								.getQuiz().getOriginalPost().getCreatedDate()
								.toString());
		sa.getLog()
				.log("WOC works in a total of "
						+ sa.getPostsWhereWOCWorks()
						+ " from "
						+ qa.getResults().size()
						+ " ("
						+ sa.getFormatter()
								.format((double) (((double) sa
										.getPostsWhereWOCWorks() * 100) / (double) qa
										.getResults().size()))
						+ "%). Only in "
						+ (qa.getResults().size() - sa.getPostsWhereWOCWorks())
						+ " WOC didn't work ("
						+ sa.getFormatter()
								.format((double) (100 - (((double) sa
										.getPostsWhereWOCWorks() * 100) / (double) qa
										.getResults().size()))) + "%)");
		sa.getLog()
				.log("Majority get right in a total of "
						+ sa.getPostsWhereMajorityGetRight()
						+ " from "
						+ qa.getResults().size()
						+ " ("
						+ sa.getFormatter()
								.format((double) (((double) sa
										.getPostsWhereMajorityGetRight() * 100) / (double) qa
										.getResults().size())) + "%)");
		sa.getLog()
				.log("A total number of "
						+ sa.getTotalNumberOfDismissedAnswers()
						+ " comment answers were dismissed from the total retrieved of "
						+ sa.getTotalRetrievedCommentAnswers()
						+ " ("
						+ sa.getFormatter()
								.format((double) (((double) sa
										.getTotalNumberOfDismissedAnswers() * 100) / (double) sa
										.getTotalRetrievedCommentAnswers()))
						+ "%)");
		sa.getLog()
				.log("From a total number of "
						+ qa.getResults().size()
						+ " quizs, in "
						+ sa.getNumberOfPostsWithMoreDismissedAnswersThanValidAnswers()
						+ " the dismissed answers were greater than the valid ones.");
		sa.getLog()
				.log("In a total of "
						+ (qa.getResults().size() - sa
								.getPostsWhereMajorityGetRight())
						+ " of posts where majority didn't work, in "
						+ sa.getNumberOfPostsMajorityDontWorkAndDismissedAnswersWereGreaterThanValidAnswers()
						+ " of these posts the dismissed answers were greater than the valid ones.");

		sa.getLog()
				.log("In a total of "
						+ (qa.getResults().size() - sa.getPostsWhereWOCWorks())
						+ " of posts where WOC didn't work, in "
						+ sa.getNumberOfPostsWOCDontWorkAndDismissedAnswersWereGreaterThanValidAnswers()
						+ " of these posts the dismissed answers were greater than the valid ones.");
		sa.getLog().log("");
	}

	public static void main(String[] args) {
		new MainQuizRetriever();
	}

}
