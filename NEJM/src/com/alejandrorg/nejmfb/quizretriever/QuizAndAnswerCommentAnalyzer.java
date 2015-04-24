package com.alejandrorg.nejmfb.quizretriever;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;

import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

import com.alejandrorg.nejmfb.analysis.objects.QuizAndAnswerAndCommentsResults;
import com.alejandrorg.nejmfb.core.Constants;
import com.alejandrorg.nejmfb.core.MyLogger;
import com.alejandrorg.nejmfb.core.StaticUtils;
import com.alejandrorg.nejmfb.objects.Comment;
import com.alejandrorg.nejmfb.objects.CommentIdentificationManager;
import com.alejandrorg.nejmfb.objects.DateComparator;
import com.alejandrorg.nejmfb.quizretriever.objects.Answer;
import com.alejandrorg.nejmfb.quizretriever.objects.AnswerOption;
import com.alejandrorg.nejmfb.quizretriever.objects.CommentAndAnswer;
import com.alejandrorg.nejmfb.quizretriever.objects.DiscardedCommentQuizAndAnswer;
import com.alejandrorg.nejmfb.quizretriever.objects.Quiz;
import com.alejandrorg.nejmfb.quizretriever.objects.QuizAndAnswer;

public class QuizAndAnswerCommentAnalyzer {

	private Levenshtein metric;
	private LinkedList<QuizAndAnswer> quizsAndAnswers;
	private CommentIdentificationManager cim;

	private MyLogger log;

	private LinkedList<QuizAndAnswerAndCommentsResults> results;
	private LinkedList<DiscardedCommentQuizAndAnswer> discardedComments;

	@SuppressWarnings("unchecked")
	public QuizAndAnswerCommentAnalyzer(LinkedList<QuizAndAnswer> qas) {
		this.cim = new CommentIdentificationManager();
		this.quizsAndAnswers = qas;
		Collections.sort(this.quizsAndAnswers, new DateComparator());
		this.metric = new Levenshtein();
		this.results = new LinkedList<QuizAndAnswerAndCommentsResults>();
		this.discardedComments = new LinkedList<DiscardedCommentQuizAndAnswer>();
		this.log = MyLogger.createLogger(true);
	}

	public LinkedList<QuizAndAnswerAndCommentsResults> getResults() {
		return this.results;
	}

	public LinkedList<DiscardedCommentQuizAndAnswer> getDiscardedComments() {
		return this.discardedComments;
	}

	/**
	 * Method to analyze the comments.
	 */
	public void run() {
		log.log("Analyzing comments..");
		for (int i = 0; i < quizsAndAnswers.size(); i++) {
			if ((i % 15) == 0) {
				log.log("Quiz.. "
						+ i
						+ " - "
						+ new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
								.format(new Date()));
			}
			Quiz quiz = quizsAndAnswers.get(i).getQuiz();
			Answer answer = quizsAndAnswers.get(i).getAnswer();
			QuizAndAnswerAndCommentsResults caaacr = new QuizAndAnswerAndCommentsResults(
					quiz, answer);
			for (int j = 0; j < quiz.getComments().size(); j++) {
				Comment com = quiz.getComments().get(j);
				CommentAndAnswer caa = getCommentAndAnswer(com, quiz, answer);
				if (caa != null) {
					if (!caaacr.contains(caa)) {
						caaacr.addCommentAndAnswer(caa);
						cim.addCommentIdentifiedByCode(caa.getHowFound());
					}
				} else {
					discardedComments.add(new DiscardedCommentQuizAndAnswer(
							com, quiz, answer));
				}
			}
			results.add(caaacr);
		}
	}

	public CommentIdentificationManager getNumericResultsOfCommentIdentification() {
		return this.cim;
	}

	/**
	 * Method to get, for a given comment, an object which contains the object
	 * and the answer provided in the comment (if applicable)
	 * 
	 * @param com
	 *            Receives the comment.
	 * @param quiz
	 *            Receives the Quiz
	 * @param answer
	 *            Receives the answer object (correct answer for this quiz)
	 * @return the value
	 */
	private CommentAndAnswer getCommentAndAnswer(Comment com, Quiz quiz,
			Answer answer) {
		/*
		 * Preprocessing of comment and answer text
		 */
		String commentWithoutProcessing = com.getMessage();
		String comment = StaticUtils.removeSymbols(com.getMessage()
				.toUpperCase().trim());
		comment = StaticUtils.removeStopWords(comment,
				Constants.STOP_WORDS_FOR_QUIZ_OPTIONS).trim();
		comment = StaticUtils.removeSymbols(comment);
		String answerText = StaticUtils.removeSymbols(answer.getAnswerText()
				.toUpperCase().trim());
		answerText = StaticUtils.removeStopWords(answerText,
				Constants.STOP_WORDS_FOR_QUIZ_OPTIONS).trim();
		answerText = StaticUtils.removeSymbols(answerText);

		/*
		 * If the comment only contains one letter, probably the user introduces
		 * the letter of the option
		 */
		String commentWithoutWhiteSpaces = commentWithoutProcessing.trim();
		commentWithoutWhiteSpaces = commentWithoutWhiteSpaces.replace(" ", "");
		if (com.getMessage().length() >= 2) {
			if (isAnswerOptionID(com.getMessage().charAt(0), quiz)
					&& (isTypicalSeparatorForAnswer(com.getMessage().charAt(1)))) {
				/*
				 * This option allows to find a correct answer based on the
				 * assumption that user has written something like:
				 * 
				 * A. blabla A- blabla A: blabla A; blabla
				 */
				String userOption = Character.toString(com.getMessage()
						.toUpperCase().charAt(0));
				if (userOption.equalsIgnoreCase(answer.getAnswerOption())) {
					/*
					 * If we found the CORRECT OPTION...
					 */

					return new CommentAndAnswer(
							com,
							getAnswerOptionByOptionID(com.getMessage()
									.charAt(0), quiz),
							Constants.ANSWER_CORRECT_FIND_ANSWER_OPTION_AFTER_REMOVE_SYMBOLS,
							true);
				} else {
					if (isAnswerOptionID(userOption.charAt(0), quiz)) {

						/*
						 * If we found the INCORRECT OPTION...
						 * 
						 * It should be the incorrect if previous if didn't
						 * return true (because previous if checks correct
						 * option)
						 */
						return new CommentAndAnswer(
								com,
								getAnswerOptionByOptionID(com.getMessage()
										.charAt(0), quiz),
								Constants.ANSWER_INCORRECT_FIND_ANSWER_OPTION_AFTER_REMOVE_SYMBOLS,
								false);
					}
				}
			}
			if (StaticUtils.allTheStringContainsSameLetter(comment)) {
				if (isAnswerOptionID(com.getMessage().charAt(0), quiz)) {
					return new CommentAndAnswer(
							com,
							getAnswerOptionByOptionID(com.getMessage()
									.charAt(0), quiz),
							Constants.ANSWER_CORRECT_FIND_ANSWER_OPTION_AFTER_REMOVE_SYMBOLS,
							true);
				}
			}
		}
		if (comment.length() == 1) {
			if (comment.equalsIgnoreCase(answer.getAnswerOption())
					|| comment.equalsIgnoreCase(answer.getAnswerText())) {
				/*
				 * If is the valid option.
				 */
				return new CommentAndAnswer(com,
						getCorrectAnswerOptionFromAnswer(answer, quiz),
						Constants.ANSWER_CORRECT_DIRECT_EQUALS_ANSWER_OPTION,
						true);
			} else {
				/*
				 * If not..
				 */
				AnswerOption aop = getAnswerOptionProvidedInComment(comment,
						quiz, answerText, Constants.TYPE_ANSWER_OPTION);

				/*
				 * We get the tentative answer option provided by the user.
				 */
				if (aop != null) {
					/*
					 * If is valid.. we return it as a "incorrect answer".
					 */
					return new CommentAndAnswer(
							com,
							aop,
							Constants.ANSWER_INCORRECT_DIRECT_EQUALS_ANSWER_OPTION,
							false);
				}
				/*
				 * If not, is an invalid answer.
				 */
			}
		} else {
			/*
			 * If the comment contains more characters..
			 */
			if (comment.equalsIgnoreCase(answerText)) {
				/*
				 * If it equals directly with the answer text.. valid option.
				 */
				return new CommentAndAnswer(
						com,
						getCorrectAnswerOptionFromAnswer(answer, quiz),
						Constants.ANSWER_CORRECT_COMMENT_DIRECT_EQUALS_ANSWER_TEXT,
						true);
			} else {
				/*
				 * If comment equals directly to an incorrect option..
				 */
				for (int i = 0; i < quiz.getAnswerOptions().size(); i++) {
					if (comment.equalsIgnoreCase(quiz.getAnswerOptions().get(i)
							.getOptionText())) {
						/*
						 * If it equals directly with the answer text.. valid
						 * option.
						 */
						return new CommentAndAnswer(
								com,
								quiz.getAnswerOptions().get(i),
								Constants.ANSWER_INCORRECT_COMMENT_DIRECT_EQUALS_ANSWER_TEXT,
								false);
					}
				}

				/*
				 * If not, we call
				 * containsValidAnswerButNotRestOfPossibleOptions() method.
				 * 
				 * This method analyze the comment to tell us three possible
				 * options.
				 */
				switch (containsValidAnswerButNotRestOfPossibleOptions(comment,
						answerText, quiz.getAnswerOptions())) {
				case 0:
					/*
					 * 0 = correct answer is not contained and contains an
					 * invalid answer
					 */
					return new CommentAndAnswer(
							com,
							getAnswerOptionProvidedInComment(comment, quiz,
									answerText, Constants.TYPE_ANSWER_TEXT),
							Constants.ANSWER_INCORRECT_COMMENT_VALID_ANSWER_WITHOUT_INCORRECT_OPTION,
							false);
				case 1:
					/*
					 * 1 = it contains the correct answer
					 */
					return new CommentAndAnswer(
							com,
							getCorrectAnswerOptionFromAnswer(answer, quiz),
							Constants.ANSWER_CORRECT_COMMENT_VALID_ANSWER_WITHOUT_INCORRECT_OPTION,
							true);
				case 2:
					/*
					 * 2 = impossible to determine if the answer is correct or
					 * not with this procedure
					 * 
					 * In this case, we try to determine if is a valid answer
					 * comparing the strings of the comment and the answer text
					 * by means if Levehnstein distance.
					 */

					float dist = metric.getSimilarity(
							comment,
							StaticUtils.removeSymbols(answer.getAnswerText()
									.toUpperCase().trim()));
					if (dist >= Constants.LEVENSHTEIN_ANSWER_THRESHOLD) {
						/*
						 * If the distance is > that the threshold, we conclude
						 * that the answer is valid
						 */
						return new CommentAndAnswer(com,
								getCorrectAnswerOptionFromAnswer(answer, quiz),
								Constants.ANSWER_CORRECT_COMMENT_LEVENSHTEIN,
								true);
					} else {
						/*
						 * If we didn't found a matching between the correct
						 * answer and the comment applying levenshtein, we try
						 * to find a match between the comment and the incorrect
						 * answers.
						 */
						for (int i = 0; i < quiz.getAnswerOptions().size(); i++) {
							dist = metric.getSimilarity(
									comment,
									StaticUtils.removeSymbols(quiz
											.getAnswerOptions().get(i)
											.getOptionText().toUpperCase()
											.trim()));
							if (dist >= Constants.LEVENSHTEIN_ANSWER_THRESHOLD) {
								return new CommentAndAnswer(
										com,
										quiz.getAnswerOptions().get(i),
										Constants.ANSWER_INCORRECT_COMMENT_LEVENSHTEIN,
										false);
							}
						}

						/*
						 * If we are here, we try the remaining strategies
						 */
						Object result[] = analyzeCommentRemainingStrategies(
								commentWithoutProcessing, answer,
								quiz.getAnswerOptions(), quiz);
						switch (((Integer) result[0]).intValue()) {
						case Constants.ANSWER_CORRECT_FIND_ANSWER_OPTION_AFTER_TOKENIZE_WHITE_SPACE:
							return new CommentAndAnswer(
									com,
									(AnswerOption) result[1],
									Constants.ANSWER_CORRECT_FIND_ANSWER_OPTION_AFTER_TOKENIZE_WHITE_SPACE,
									true);
						case Constants.ANSWER_CORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_DIRECT_EQUALS:
							return new CommentAndAnswer(
									com,
									(AnswerOption) result[1],
									Constants.ANSWER_CORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_DIRECT_EQUALS,
									true);
						case Constants.ANSWER_CORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_LEVENSHTEIN:
							return new CommentAndAnswer(
									com,
									(AnswerOption) result[1],
									Constants.ANSWER_CORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_LEVENSHTEIN,
									true);
						case Constants.ANSWER_INCORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_DIRECT_EQUALS:
							return new CommentAndAnswer(
									com,
									(AnswerOption) result[1],
									Constants.ANSWER_INCORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_DIRECT_EQUALS,
									false);
						case Constants.ANSWER_INCORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_LEVENSHTEIN:
							return new CommentAndAnswer(
									com,
									(AnswerOption) result[1],
									Constants.ANSWER_INCORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_LEVENSHTEIN,
									false);
						case Constants.ANSWER_INCORRECT_FIND_ANSWER_OPTION_AFTER_TOKENIZE_WHITE_SPACE:
							return new CommentAndAnswer(
									com,
									(AnswerOption) result[1],
									Constants.ANSWER_INCORRECT_FIND_ANSWER_OPTION_AFTER_TOKENIZE_WHITE_SPACE,
									false);

						}
					}
				}
			}
		}

		/*
		 * If we arrive to this point we return a null value because we are not
		 * capable to determine if the answer is valid or not. It is supposed
		 * that if it is not a valid answer means that:
		 * 
		 * 1) it is not the correct answer 2) it is not an incorrect answer
		 * 
		 * this means that probably could be spam or whatever, so it is not a
		 * "fail".
		 */
		return null;
	}

	/**
	 * This method uses the remaining possible strategies to find a correct
	 * answer in the text.
	 * 
	 * @param commentWP
	 *            Receive the comment without pre-process.
	 * @param answerText
	 *            Receives the answer text.
	 * @param answerOptions
	 *            Receives the answer options.
	 * @return Return the value (an objet[] which encapsulates in [0] the
	 *         integer value with the result identifier and in [1] the possible
	 *         answeroption object.
	 */
	private Object[] analyzeCommentRemainingStrategies(String commentWP,
			Answer answer, LinkedList<AnswerOption> answerOptions, Quiz quiz) {

		/*
		 * first option: brute force
		 */
		Object[] foundBF = analyzeCommentContainsAnswerTextByBruteForce(
				commentWP, answerOptions, answer, quiz);
		if (foundBF != null) {
			return foundBF;
		}

		/*
		 * second option: analyze the comment to see if contain an answer option
		 * after splitting the comment text using a white space.
		 */
		Object[] found = analyzeCommentContainsAnswerOptionAfterTokenWhiteSpace(
				commentWP, answerOptions, answer);
		if (found != null) {
			if (((Boolean) found[1]).booleanValue()) {
				return new Object[] {
						new Integer(
								Constants.ANSWER_CORRECT_FIND_ANSWER_OPTION_AFTER_TOKENIZE_WHITE_SPACE),
						found[0] };
			} else {
				return new Object[] {
						new Integer(
								Constants.ANSWER_INCORRECT_FIND_ANSWER_OPTION_AFTER_TOKENIZE_WHITE_SPACE),
						found[0] };
			}

		}

		return new Object[] { Constants.COMMENT_DISMISSED, null };
	}

	/**
	 * This method uses some kind of "brute force" to see if a comment contains
	 * a valid answer.
	 * 
	 * Imagine a quiz where the answer is "Orthostatic hypotension".
	 * 
	 * Imagine this comment: I consider that the correct answer is Ortostatc
	 * hpotnsion because a change of the position of the patient could provide a
	 * change in the blood pressure and.. blabla.
	 * 
	 * Traditional methods will not find it because "Ortostatc hpotnsion" is
	 * poorly writteng and will not find a direct match with the correct optio
	 * (there is not an easy way to obtain the
	 * "substring inside of a string more similar to a given one"). A
	 * levehnstein distance word by word could be do it but the problem is that
	 * they are two words and the algorithm will not work efficiently.
	 * 
	 * Then, we are going to use a method which is going to take ranges from 1
	 * to 5 words (5 words is the mean of words that a concrete answer normally
	 * have at maximum). Then, with the resulting strings we are going to apply
	 * levehnstein to see if we find a matching. If we find it is because the
	 * correct answer was inside.
	 * 
	 * For example, in this case:
	 * 
	 * I -> no I consider -> no I consider that -> no I consider that the -> I
	 * consider that the correct -> no consider -> no consider that -> no
	 * consider that the -> no consider that the correct -> no consider that the
	 * correct answer -> no that -> no that the -> no that the correct -> no
	 * that the correct answer -> no that the correct answer is -> no the -> no
	 * the correct -> no the correct answer -> no the correct answer is
	 * Ortostatic -> no correct -> no correct answer -> no correct answer is ->
	 * correct answer is Ortostatic -> no correct answer is Ortostatic hpotnsion
	 * -> no answer -> no answer is -> no answer is Ortostatic -> no answer is
	 * Ortostatic hpotnsion -> no answer is Ortostatic hpotnsion because -> no
	 * is -> no is Ortostatic -> no is Ortostatic hpotnsion -> no is Ortostatic
	 * hpotnsion because -> no is Ortostatic hpotnsion because a -> no
	 * Ortostatic -> no Ortostatic hpotnsion -> YES
	 * 
	 * 
	 * @param commentWP
	 *            Comment of the user without processing
	 * @param answerText
	 *            Answer text.
	 * @param answerOptions
	 *            Answer options
	 * @param answer
	 * @return result
	 */
	private Object[] analyzeCommentContainsAnswerTextByBruteForce(
			String commentWP, LinkedList<AnswerOption> answerOptions,
			Answer answer, Quiz quiz) {

		String comment = StaticUtils.removeSymbols(commentWP.toUpperCase()
				.trim());
		comment = StaticUtils.removeStopWords(comment,
				Constants.STOP_WORDS_FOR_QUIZ_OPTIONS).trim();
		comment = StaticUtils.removeSymbols(comment);

		LinkedList<String> strs = StaticUtils
				.getAllPossibleSubstringCombinations(comment,
						Constants.MAX_SIZE_OF_SUBSTRINGS);

		String correctAnswerText = answer.getAnswerText();
		correctAnswerText = StaticUtils.removeSymbols(correctAnswerText
				.toUpperCase().trim());
		correctAnswerText = StaticUtils.removeStopWords(correctAnswerText,
				Constants.STOP_WORDS_FOR_QUIZ_OPTIONS).trim();
		correctAnswerText = StaticUtils.removeSymbols(correctAnswerText);

		for (int i = 0; i < strs.size(); i++) {
			String str = strs.get(i);
			if (str.equalsIgnoreCase(correctAnswerText)) {
				return new Object[] {
						new Integer(
								Constants.ANSWER_CORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_DIRECT_EQUALS),
						getAnswerOptionByOptionID(answer.getAnswerOption()
								.charAt(0), quiz) };
			}
			float dist = metric.getSimilarity(correctAnswerText, str);
			if (dist >= Constants.LEVENSHTEIN_ANSWER_THRESHOLD) {
				return new Object[] {
						new Integer(
								Constants.ANSWER_CORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_LEVENSHTEIN),
						getAnswerOptionByOptionID(answer.getAnswerOption()
								.charAt(0), quiz) };
			}
			for (int j = 0; j < answerOptions.size(); j++) {
				String aopText = answerOptions.get(j).getOptionText();
				aopText = StaticUtils.removeSymbols(aopText.toUpperCase()
						.trim());
				aopText = StaticUtils.removeStopWords(aopText,
						Constants.STOP_WORDS_FOR_QUIZ_OPTIONS).trim();
				aopText = StaticUtils.removeSymbols(aopText);

				if (aopText.equalsIgnoreCase(str)) {
					return new Object[] {
							new Integer(
									Constants.ANSWER_INCORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_DIRECT_EQUALS),
							answerOptions.get(j) };
				} else {
					/*
					 * If we didn't found a matching between the correct answer
					 * and the comment applying levenshtein, we try to find a
					 * match between the comment and the incorrect answers.
					 */
					dist = metric.getSimilarity(
							str,
							StaticUtils.removeSymbols(aopText.toUpperCase().trim()));
					if (dist >= Constants.LEVENSHTEIN_ANSWER_THRESHOLD) {
						return new Object[] {
								new Integer(
										Constants.ANSWER_INCORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_LEVENSHTEIN),
								answerOptions.get(j) };
					}

				}
			}
		}
		return null;
	}

	/**
	 * Method to analyze if the comment contains an "answer option" after
	 * tokenize the comment using white space as split.
	 * 
	 * The idea is that several users put their answer in the comment after a
	 * bunch of text. We use this method to determine if the user did that and
	 * provides a correct answer. For example:
	 * 
	 * I consider that correct answer is "B" because blablbala..
	 * 
	 * After split on " " we can get this "B". All the tokens obtained are
	 * pre-processed removing external symbols so we have a clean B that is the
	 * answer.
	 * 
	 * The problem of this method is the use of preposition 'a'. If the user has
	 * used this preposision in upper case it will take it as answer, but this
	 * is the best solution based on the analysis of the comments.
	 * 
	 * @param comment
	 *            Receives the comment text (without pre-processing)
	 * @param answerText
	 *            Receives the answer text.
	 * @param answerOptions
	 *            Receives the answer options.
	 * @return Return a object with the answer option and if it is the correct
	 *         or not.
	 */
	private Object[] analyzeCommentContainsAnswerOptionAfterTokenWhiteSpace(
			String comment, LinkedList<AnswerOption> answerOptions,
			Answer answer) {
		String parts[] = StaticUtils.removeSymbols(comment).trim().split(" ");
		for (int i = 0; i < parts.length; i++) {
			for (int j = 0; j < answerOptions.size(); j++) {
				AnswerOption aop = answerOptions.get(j);
				if (aop.getOptionID().equals(parts[i].trim())) {
					if (aop.getOptionID().equalsIgnoreCase(
							answer.getAnswerOption())) {
						return new Object[] { aop, Boolean.TRUE };
					} else {
						return new Object[] { aop, Boolean.FALSE };
					}
				}
			}
		}
		return null;
	}

	/**
	 * Method to check if the character 'c' is a typical separator for answer
	 * option.
	 * 
	 * For example:
	 * 
	 * a. blabla
	 * 
	 * @param c
	 *            The char
	 * @return true/false
	 */
	private boolean isTypicalSeparatorForAnswer(char c) {
		for (int i = 0; i < Constants.TYPICAL_ANSWER_CHAR_SEPARATORS.length; i++) {
			if (c == Constants.TYPICAL_ANSWER_CHAR_SEPARATORS[i].charAt(0)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method to check if a char belongs to a answer option.
	 * 
	 * @param c
	 *            The char
	 * @param q
	 *            The quiz with the aops.
	 * @return true/false
	 */
	private boolean isAnswerOptionID(char c, Quiz q) {
		c = Character.toString(c).toUpperCase().charAt(0);
		for (int i = 0; i < q.getAnswerOptions().size(); i++) {
			AnswerOption aop = q.getAnswerOptions().get(i);
			if (aop.getOptionID().toUpperCase().charAt(0) == c) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method to get an answer option by the option id
	 * 
	 * @param c
	 *            Receives the ID
	 * @param q
	 *            Receives the quiz with the aops
	 * @return the object
	 */
	private AnswerOption getAnswerOptionByOptionID(char c, Quiz q) {
		c = Character.toString(c).toUpperCase().charAt(0);
		for (int i = 0; i < q.getAnswerOptions().size(); i++) {
			AnswerOption aop = q.getAnswerOptions().get(i);
			if (aop.getOptionID().toUpperCase().charAt(0) == c) {
				return aop;
			}
		}
		return null;
	}

	/**
	 * Method to get the correct answer from answer options given an answer
	 * option.
	 * 
	 * @param answer
	 *            Receives the answer option.
	 * @param q
	 *            Receives the quiz with all the answer options.
	 * @return The value.
	 */
	private AnswerOption getCorrectAnswerOptionFromAnswer(Answer answer, Quiz q) {
		for (int i = 0; i < q.getAnswerOptions().size(); i++) {
			AnswerOption aop = q.getAnswerOptions().get(i);
			if (answer.getAnswerOption().trim()
					.equalsIgnoreCase(aop.getOptionID().trim())) {
				return new AnswerOption(aop);
			}
		}
		return null;
	}

	/**
	 * Given a comment which contains an answer, we want to get the AnswerOption
	 * object of the answer.
	 * 
	 * @param comment
	 *            The comment.
	 * @param quiz
	 *            The quiz which contains all the Answer options.
	 * @param realAnswer
	 *            The real answer.
	 * @param type
	 *            The type of answer provided in the comment (text or option).
	 * @return Return the value.
	 */
	private AnswerOption getAnswerOptionProvidedInComment(String comment,
			Quiz quiz, String realAnswer, int type) {
		switch (type) {
		case Constants.TYPE_ANSWER_OPTION:
			for (int i = 0; i < quiz.getAnswerOptions().size(); i++) {
				if (quiz.getAnswerOptions().get(i).getOptionID()
						.equalsIgnoreCase(comment)) {
					return new AnswerOption(quiz.getAnswerOptions().get(i));
				}
			}
			break;
		case Constants.TYPE_ANSWER_TEXT:
			for (int i = 0; i < quiz.getAnswerOptions().size(); i++) {
				AnswerOption aop = quiz.getAnswerOptions().get(i);
				if (!aop.getOptionText().equalsIgnoreCase(realAnswer)) {
					/*
					 * We avoid the real answer.
					 */

					String optText = StaticUtils.removeSymbols(aop
							.getOptionText().toUpperCase().trim());
					float dist = metric.getSimilarity(comment, optText);
					if ((comment.contains(optText))
							|| (dist >= Constants.LEVENSHTEIN_ANSWER_THRESHOLD)) {
						/*
						 * If we find a not valid option.. we finished.
						 */
						return new AnswerOption(aop);
					}
				}
			}
			break;
		}
		return null;
	}

	/**
	 * Method to check if contains a valid answer (ensuring that the message
	 * didn't contain any of the other possible options).
	 * 
	 * 0 = not contained and contain an invalid answer 1 = it contains the
	 * correct answer 2 = impossible to determine if the answer is correct or
	 * not with this procedure
	 * 
	 * @param comment
	 *            Receives the comment.
	 * @param realAnswer
	 *            Receives the real answer.
	 * @param answerOptions
	 *            Receives the answer options.
	 * @return the value
	 */
	private int containsValidAnswerButNotRestOfPossibleOptions(String comment,
			String realAnswer, LinkedList<AnswerOption> answerOptions) {
		/*
		 * We check if in the message the user wrote the other possible options.
		 */
		for (int i = 0; i < answerOptions.size(); i++) {
			AnswerOption aop = answerOptions.get(i);
			if (!aop.getOptionText().equalsIgnoreCase(realAnswer)) {
				/*
				 * We avoid the real answer.
				 */
				String optText = StaticUtils.removeSymbols(aop.getOptionText()
						.toUpperCase().trim());
				float dist = metric.getSimilarity(comment, optText);

				if ((comment.contains(optText))
						|| (dist >= Constants.LEVENSHTEIN_ANSWER_THRESHOLD)) {
					/*
					 * If we find a not valid option.. we finished.
					 */
					return 0;
				}
			}
		}
		/*
		 * If we arrive here, we check if it contains a valid option.
		 */
		return (comment.contains(StaticUtils.removeSymbols(realAnswer))) ? 1
				: 2;
	}

	/**
	 * Method to save the data in a statistical format (TSV) for analysis in a
	 * separate software.
	 * 
	 * @param qaFile
	 *            Receives the file to save the quizzes.
	 * @param comFile
	 *            Receives the file to save the comments.
	 * @param cryptUserID
	 *            Receives if should crypt (put as MD5) the user id.
	 * @throws Exception
	 *             It can throws an exception.
	 */
	public void saveDataForStatisticsTwoFiles(String qaFile, String comFile,
			boolean cryptUserID) throws Exception {
		log.log("Saving data for external statistical analysis.. (in two files!)");
		LinkedList<String> quizAnswerStatisticalOutputs = new LinkedList<String>();
		quizAnswerStatisticalOutputs
				.add("QUIZ ID\tQUIZ CREATED DATE\tANSWER\r\n");
		LinkedList<String> commentsStatisticalOutputs = new LinkedList<String>();
		commentsStatisticalOutputs
				.add("COMMENT ID\tQUIZ ID\tCOMMENT CREATED DATE\tUSER COMMENT ID\tANSWER\tHOW FOUND\tCORRECT/INCORRECT ANSWER\r\n");
		for (int i = 0; i < results.size(); i++) {
			QuizAndAnswerAndCommentsResults qacr = results.get(i);
			quizAnswerStatisticalOutputs.add(qacr
					.getQuizAnswerStatisticsOutput());
			commentsStatisticalOutputs.addAll(qacr
					.getCommentsStatisticsOutput(cryptUserID));
		}

		BufferedWriter bW = new BufferedWriter(new FileWriter(qaFile));
		for (int i = 0; i < quizAnswerStatisticalOutputs.size(); i++) {
			bW.write(quizAnswerStatisticalOutputs.get(i));
			bW.newLine();
		}
		bW.close();

		bW = new BufferedWriter(new FileWriter(comFile));
		for (int i = 0; i < commentsStatisticalOutputs.size(); i++) {
			bW.write(commentsStatisticalOutputs.get(i));
			bW.newLine();
		}
		bW.close();

		log.log("done!");
	}

	/**
	 * Method to save the statistics to be stored in a single file
	 * 
	 * @param statisticsFile
	 *            File to store.
	 * @param cryptUserID
	 *            Receives if should crypt (put as MD5) the user id.
	 * @throws Exception
	 *             It can throws an exception.
	 */
	public void saveDataForStatisticsOneFile(String statisticsFile,
			boolean cryptUserID) throws Exception {
		log.log("Saving data for external statistical analysis.. (in one file!)");

		LinkedList<String> output = new LinkedList<String>();
		output.add("COMMENT ID\tQUIZ ID\tCOMMENT CREATED DATE\tUSER COMMENT ID\tANSWER\tCORRECT ANSWER\tHOW FOUND\tCORRECT/INCORRECT ANSWER\r\n");
		for (int i = 0; i < results.size(); i++) {
			QuizAndAnswerAndCommentsResults qacr = results.get(i);
			output.addAll(qacr.getCommentsStatisticsOutput(qacr.getAnswer()
					.getAnswerOption(), cryptUserID));
		}

		BufferedWriter bW = new BufferedWriter(new FileWriter(statisticsFile));
		for (int i = 0; i < output.size(); i++) {
			bW.write(output.get(i));
			bW.newLine();
		}
		bW.close();

		log.log("done!");
	}
}
