package com.alejandrorg.nejmfb.analysis.objects;

import java.util.LinkedList;

import org.apache.commons.codec.digest.DigestUtils;

import com.alejandrorg.nejmfb.objects.Comment;
import com.alejandrorg.nejmfb.quizretriever.objects.Answer;
import com.alejandrorg.nejmfb.quizretriever.objects.AnswerOption;
import com.alejandrorg.nejmfb.quizretriever.objects.CommentAndAnswer;
import com.alejandrorg.nejmfb.quizretriever.objects.Quiz;
import com.alejandrorg.nejmfb.quizretriever.objects.QuizAndAnswer;

public class QuizAndAnswerAndCommentsResults extends QuizAndAnswer {

	private LinkedList<CommentAndAnswer> commentsWithAnswer;
	private int correctAnswers;
	private int incorrectAnswers;
	private int dismissedAnswers;

	private boolean isMostVotedAnswerTheCorrect;
	private boolean isNumberOfDismissedAnswersIsGreaterThanValidOnes;
	private boolean majorityGetRight;

	private LinkedList<AnswerOptionWithNumberOfOcurrences> answerOptionsWithNumberOfOccurences;
	private AnswerOptionWithNumberOfOcurrences mostVotedAnswerOption;
	private AnswerOptionWithNumberOfOcurrences lessVotedAnswerOption;

	private LinkedList<AnswerTrend> answerTrends;

	public QuizAndAnswerAndCommentsResults(Quiz q, Answer a) {
		super(q, a);
		this.commentsWithAnswer = new LinkedList<CommentAndAnswer>();
		this.correctAnswers = -1;
		this.incorrectAnswers = -1;
		this.dismissedAnswers = -1;
		this.majorityGetRight = false;
		this.isNumberOfDismissedAnswersIsGreaterThanValidOnes = false;
		this.isMostVotedAnswerTheCorrect = false;
		this.answerTrends = new LinkedList<AnswerTrend>();
	}

	public void addCommentAndAnswer(CommentAndAnswer caa) {
		this.commentsWithAnswer.add(caa);
	}

	public LinkedList<CommentAndAnswer> getCommentsWithAnswer() {
		return commentsWithAnswer;
	}

	public String getPostAnswerID() {
		return "Q: " + this.getQuiz().getOriginalPost().getId() + " - A: "
				+ this.getAnswer().getOriginalPost().getId();
	}

	public int getNumberOfAnswerComments() {
		return this.getQuiz().getOriginalPost().getComments().size();
	}

	public int getNumberOfCorrectAnswers() {
		if (correctAnswers == -1) {
			calculateCorrectAndIncorrectAnswers();
		}
		return this.correctAnswers;
	}

	public int getNumberOfIncorrectAnswers() {
		if (incorrectAnswers == -1) {
			calculateCorrectAndIncorrectAnswers();
		}
		return this.incorrectAnswers;
	}

	private void calculateCorrectAndIncorrectAnswers() {
		correctAnswers = 0;
		incorrectAnswers = 0;
		for (int i = 0; i < commentsWithAnswer.size(); i++) {
			if (commentsWithAnswer.get(i).isCorrect()) {
				correctAnswers++;
			} else {
				incorrectAnswers++;
			}
		}
	}

	public AnswerOptionWithNumberOfOcurrences getMostVotedAnswer() {
		if (mostVotedAnswerOption == null) {
			loadMostAndLessVotedAnswerOption();
		}
		return mostVotedAnswerOption;
	}

	private void loadMostAndLessVotedAnswerOption() {
		answerOptionsWithNumberOfOccurences = new LinkedList<AnswerOptionWithNumberOfOcurrences>();
		/*
		 * We init the answer options with number of occurrences.
		 */
		for (int i = 0; i < this.getQuiz().getAnswerOptions().size(); i++) {
			answerOptionsWithNumberOfOccurences
					.add(new AnswerOptionWithNumberOfOcurrences(this.getQuiz()
							.getAnswerOptions().get(i)));
		}
		for (int i = 0; i < commentsWithAnswer.size(); i++) {
			CommentAndAnswer caa = commentsWithAnswer.get(i);
			incrementAOP(caa.getChoosenAnswerOption());
		}
		mostVotedAnswerOption = answerOptionsWithNumberOfOccurences.get(0);
		lessVotedAnswerOption = answerOptionsWithNumberOfOccurences.get(0);
		for (int i = 0; i < answerOptionsWithNumberOfOccurences.size(); i++) {
			AnswerOptionWithNumberOfOcurrences aono = answerOptionsWithNumberOfOccurences
					.get(i);
			if (aono.getNumberOfOccurrences() > mostVotedAnswerOption
					.getNumberOfOccurrences()) {
				mostVotedAnswerOption = aono;
			}
			if (aono.getNumberOfOccurrences() < lessVotedAnswerOption
					.getNumberOfOccurrences()) {
				lessVotedAnswerOption = aono;
			}
		}
	}

	/**
	 * Method to increment the occurrences of a given answer option.
	 * 
	 * @param choosenAnswerOption
	 */
	private void incrementAOP(AnswerOption choosenAnswerOption) {
		for (int i = 0; i < answerOptionsWithNumberOfOccurences.size(); i++) {
			AnswerOptionWithNumberOfOcurrences aopno = answerOptionsWithNumberOfOccurences
					.get(i);
			if (aopno.getAnswerOption().equals(choosenAnswerOption)) {
				aopno.incNumberOfOccurrences();
			}
		}
	}

	public void setNumberOfDismissedAnswers(int d) {
		this.dismissedAnswers = d;
	}

	public int getNumberOfDismissedAnswers() {
		return this.dismissedAnswers;
	}

	public void setMostVotedAnswersIsCorrect(boolean b) {
		this.isMostVotedAnswerTheCorrect = b;
	}

	public boolean isMostVotedAnswerTheCorrect() {
		return this.isMostVotedAnswerTheCorrect;
	}

	public void setNumberOfDismissedAnswersIsGreaterThanValidOnes(boolean b) {
		this.isNumberOfDismissedAnswersIsGreaterThanValidOnes = b;
	}

	public boolean isNumberOfDismissedAnswersIsGreaterThanValidOnes() {
		return isNumberOfDismissedAnswersIsGreaterThanValidOnes;
	}

	public void setMajorityGetRight(boolean b) {
		this.majorityGetRight = b;
	}

	public boolean getMajorityGetRight() {
		return this.majorityGetRight;
	}

	/**
	 * Method to check if this object already contains a analyzed comment of
	 * this user. We don't allow two comments from a same user.
	 * 
	 * @param caa
	 *            The object
	 * @return true/false
	 */
	public boolean contains(CommentAndAnswer caa) {
		for (int i = 0; i < this.commentsWithAnswer.size(); i++) {
			Comment com = commentsWithAnswer.get(i).getComment();
			if (com.getIdUserComment().equalsIgnoreCase(
					caa.getComment().getIdUserComment())) {
				return true;
			}
		}
		return false;
	}

	public void setTrends(LinkedList<AnswerTrend> at) {
		this.answerTrends = at;
	}

	public LinkedList<AnswerTrend> getTrends() {
		return this.answerTrends;
	}

	/**
	 * If in the trends identified one has been concluded to be a trend, we have
	 * trend.
	 * 
	 * @return boolean
	 */
	public boolean hasTrend() {
		for (int i = 0; i < this.answerTrends.size(); i++) {
			if (this.answerTrends.get(i).isTrend()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method to get the statistical output.
	 * 
	 * @return A String
	 */
	public String getQuizAnswerStatisticsOutput() {
		return getQuiz().getID() + "\t" + getQuiz().getCreatedDate() + "\t"
				+ getAnswer().getAnswerOption();
		// StringBuffer sb = new StringBuffer();
		// sb.append("# QUIZ ID\tQUIZ CREATED DATE\tANSWER\r\n");
		// sb.append( + "\r\n");
		// sb.append("# COMMENT ID\tCOMMENT CREATED DATE\tUSER COMMENT ID\tANSWER\tHOW FOUND\tCORRECT/INCORRECT ANSWER\r\n");
		// for (int i = 0; i < commentsWithAnswer.size(); i++) {
		// CommentAndAnswer caa = commentsWithAnswer.get(i);
		// sb.append(caa.getComment().getId() + "\t" +
		// caa.getComment().getCreatedDate().toString() + "\t" +
		// caa.getComment().getIdUserComment() + "\t" +
		// caa.getChoosenAnswerOption().getOptionID() + "\t" + caa.getHowFound()
		// + "\t" + (caa.isCorrect()?"CORRECT":"INCORRECT") + "\r\n");
		// }
		// return sb.toString();
	}

	public LinkedList<String> getCommentsStatisticsOutput(boolean cryptUserID) {
		LinkedList<String> ret = new LinkedList<String>();
		for (int i = 0; i < commentsWithAnswer.size(); i++) {
			CommentAndAnswer caa = commentsWithAnswer.get(i);
			String userID = caa.getComment().getIdUserComment();
			if (cryptUserID) {
				userID = DigestUtils.md5Hex(userID);
			}
			ret.add(caa.getComment().getId() + "\t"
					+ getQuiz().getID() + "\t"
					+ caa.getComment().getCreatedDate().toString() + "\t"
					+  userID + "\t"
					+ caa.getChoosenAnswerOption().getOptionID() + "\t"
					+ caa.getHowFound() + "\t"
					+ (caa.isCorrect() ? "CORRECT" : "INCORRECT"));
		}
		return ret;
	}

	public LinkedList<String> getCommentsStatisticsOutput(String correctAnswer, boolean cryptUserID) {
		LinkedList<String> ret = new LinkedList<String>();
		for (int i = 0; i < commentsWithAnswer.size(); i++) {
			CommentAndAnswer caa = commentsWithAnswer.get(i);
			String userID = caa.getComment().getIdUserComment();
			if (cryptUserID) {
				userID = DigestUtils.md5Hex(userID);
			}
			ret.add(caa.getComment().getId() + "\t"
					+ getQuiz().getID() + "\t"
					+ caa.getComment().getCreatedDate().toString() + "\t"
					+  userID + "\t"
					+ caa.getChoosenAnswerOption().getOptionID() + "\t"
					+ correctAnswer + "\t" +
					+ caa.getHowFound() + "\t"
					+ (caa.isCorrect() ? "CORRECT" : "INCORRECT"));
		}
		return ret;
	}
	// public boolean equals(Object o) {
	// if (o instanceof QuizAndAnswerAndCommentsResults) {
	// QuizAndAnswerAndCommentsResults ob = (QuizAndAnswerAndCommentsResults)o;
	//
	// }
	// return false;
	// }
}
