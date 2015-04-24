package com.alejandrorg.nejmfb.quizretriever;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Properties;

import com.alejandrorg.nejmfb.analysis.objects.PostComparison;
import com.alejandrorg.nejmfb.core.ConfigManager;
import com.alejandrorg.nejmfb.core.Constants;
import com.alejandrorg.nejmfb.core.DataManager;
import com.alejandrorg.nejmfb.core.StaticUtils;
import com.alejandrorg.nejmfb.objects.Comment;
import com.alejandrorg.nejmfb.objects.DateComparator;
import com.alejandrorg.nejmfb.objects.Post;
import com.alejandrorg.nejmfb.quizretriever.objects.Answer;
import com.alejandrorg.nejmfb.quizretriever.objects.AnswerOption;
import com.alejandrorg.nejmfb.quizretriever.objects.Quiz;
import com.alejandrorg.nejmfb.quizretriever.objects.QuizAndAnswer;
import com.alejandrorg.nejmfb.quizretriever.objects.QuizAndAnswerAsPost;

public class QuizAndAnswerRetriever extends QuizAndAnswerManager {

	private LinkedList<Post> quizWithoutAnswers;
	private LinkedList<Post> answersWithoutQuiz;
	private LinkedList<String> manualQuizs_QuizsOptions;
	private LinkedList<String> manualAnswers;
	
	/*
	 * This is used to save the "intermediate" quizs and answers before extract
	 * the answer options.
	 */
	private LinkedList<QuizAndAnswerAsPost> quizsAndAnswersAsPost;

	/*
	 * This is the final one.
	 */
	private LinkedList<QuizAndAnswer> quizsAndAnswers;
	/**
	 * Constructor.
	 * 
	 * @param qf
	 *            Quizs file.
	 * @param af
	 *            Answers file.
	 * @param ap
	 *            All posts file.
	 */
	public QuizAndAnswerRetriever(String qf, String af, String apf) {
		super(qf, af, apf);
	}

	/**
	 * Constructor
	 * 
	 * @param qs
	 *            Receives the quizs
	 * @param as
	 *            Receives the answers
	 * @param aps
	 *            Receives all the posts
	 */
	public QuizAndAnswerRetriever(LinkedList<Post> qs, LinkedList<Post> as,
			LinkedList<Post> aps) {
		super(qs, as, aps);
	}

	/**
	 * Method to find quizs and answers and match between them
	 * 
	 * @param quizsFile
	 *            Quizs file.
	 * @param answersFile
	 *            Answers file.
	 * @param allPosts
	 *            All posts file.
	 * @throws Exception
	 *             It can throws an exception.
	 */
	@SuppressWarnings("unchecked")
	public void run() throws Exception {
		loadPosts();
		this.quizsAndAnswersAsPost = new LinkedList<QuizAndAnswerAsPost>();
		for (int i = 0; i < this.quizPosts.size(); i++) {
			/*
			 * We go quiz by quiz
			 */
			Post quiz = this.quizPosts.get(i);
			/*
			 * We try to find an answer in the "answers" list.
			 */
			Post answer = findAnswer(quiz);

			if (answer != null) {
				/*
				 * If we find it, we create a pair.
				 */
				quiz.setHowFound(Constants.HOW_FOUND_DIRECT);
				answer.setHowFound(Constants.HOW_FOUND_DIRECT);
				QuizAndAnswerAsPost qafp = new QuizAndAnswerAsPost(quiz,
						answer);
				if (!this.quizsAndAnswersAsPost.contains(qafp))
					this.quizsAndAnswersAsPost.add(qafp);
			}
		}

		/*
		 * After the process, we load: - quizs without answers - answers without
		 * quizs
		 */
		quizWithoutAnswers = getQuizsWithoutAnswers();
		answersWithoutQuiz = getAnswersWithoutQuiz();
		/*
		 * Show results.
		 */
		log.log("1st. round!\n");
		log.log("\tQuizs obtained: " + quizPosts.size());
		log.log("\tAnswers: " + answersPosts.size());
		log.log("");
		log.log("\tPairs quiz-answers found: " + quizsAndAnswersAsPost.size());
		log.log("\tQuizs without answer: " + quizWithoutAnswers.size());
		log.log("\tAnswers without quiz: " + answersWithoutQuiz.size());

		log.log("2nd. round!\n");
		/*
		 * We try to find answers for those quizs which still don't have an
		 * answer searching the answer in all the posts.
		 */
		LinkedList<QuizAndAnswerAsPost> qwa = findAnswerForUnansweredQuizsInAllPosts();
		System.out
				.println("\tAnswers found in all posts for unanswered quizs: "
						+ qwa.size());
		/*
		 * We do the same but inverse. We try to find quizs for all those answer
		 * that we have without an associated quiz in all the posts.
		 */
		LinkedList<QuizAndAnswerAsPost> awq = findQuizsForAnswersWithoutQuizsInAllPosts();
		System.out
				.println("\tQuizs found in all posts for answers without quizs: "
						+ awq.size());

		log.log("\tQuizs without answer: " + quizWithoutAnswers.size());
		log.log("\tAnswers without quiz: " + answersWithoutQuiz.size());

		for (int i = 0; i < awq.size(); i++) {
			if (!quizsAndAnswersAsPost.contains(awq.get(i))) {
				quizsAndAnswersAsPost.add(awq.get(i));
			}
		}
		for (int i = 0; i < qwa.size(); i++) {
			if (!quizsAndAnswersAsPost.contains(qwa.get(i))) {
				quizsAndAnswersAsPost.add(qwa.get(i));
			}
		}

		log.log("\tPairs quiz-answers found: " + quizsAndAnswersAsPost.size());

		Collections.sort(this.quizsAndAnswersAsPost, new DateComparator());
		Collections.sort(this.posts, new DateComparator());
		log.log("First post retrieved: "
				+ this.posts.get(0).getCreatedDate().toString());
		log.log("Last post retrieved: "
				+ this.posts.get(this.posts.size() - 1).getCreatedDate()
						.toString());
		
		this.separateQuizsAndAnswersInObjects();
	}

	/**
	 * Method to separate quizs and answers (Post objects both) in "Quiz" and
	 * "Answer" objects.
	 */
	@SuppressWarnings("unchecked")
	public void separateQuizsAndAnswersInObjects() throws Exception {
		/*
		 * Some quizs are not heterogeneus and have been created manually.
		 */
		this.manualQuizs_QuizsOptions = loadManualQuizs();
		this.manualAnswers = loadManualAnswers();
		this.quizsAndAnswers = new LinkedList<QuizAndAnswer>();
		for (int i = 0; i < this.quizsAndAnswersAsPost.size(); i++) {
			/*
			 * For each pair, we take the objects.
			 */
			Post quizPost = this.quizsAndAnswersAsPost.get(i).getQuizPost();
			Post answerPost = this.quizsAndAnswersAsPost.get(i).getAnswerPost();
			/*
			 * And we "convert it" to Quiz and Answer pair objects.
			 */
			Quiz quiz = getQuiz(quizPost);
			Answer answer = getAnswer(answerPost, quiz);
			quizsAndAnswers.add(new QuizAndAnswer(quiz, answer));
		}
		Collections.sort(this.quizsAndAnswers, new DateComparator());
	}
	
	/**
	 * Method to get an "Answer" object from a Post object which seems to
	 * contains an answer.
	 * 
	 * @param answerPost
	 *            Receives the Post object.
	 * @param quiz
	 * @return Return the Answer object.
	 */
	private Answer getAnswer(Post answerPost, Quiz quiz) throws Exception {
		Answer a = new Answer(answerPost);
		String result[] = retrieveAnswerOptionAndText(answerPost, quiz);
		if (result != null) {
			a.setAnswerOption(result[0]);
			a.setAnswerText(result[1]);
		} else {
			log.logError("Error getting answer:");
			log.log("Quiz:");
			log.log(quiz.getOriginalPost().getMessage());
			log.log("Answer:");
			log.log(answerPost.getMessage());
			log.log(answerPost.getId());
		}
		return a;
	}
	
	/**
	 * Method to, given an answer message, retrieve the answer option (A,B,C,D)
	 * and the answer text.
	 * 
	 * @param answerPost
	 *            Receives the answer post.
	 * @param quiz
	 *            Receives the quiz post (with the options).
	 * @return Return the two values in an array.
	 */
	private String[] retrieveAnswerOptionAndText(Post answerPost, Quiz quiz)
			throws Exception {
		if (this.manualAnswers.contains(answerPost.getId())) {
			return getManualAnswerForPost(answerPost.getId());
		}
		String msgAnswer = answerPost.getMessage().trim();

		for (int i = 0; i < Constants.CORRECT_ANSWER_STRINGS.length; i++) {
			/*
			 * We go over the possible strings which contains the answer.
			 */
			if (msgAnswer.contains(Constants.CORRECT_ANSWER_STRINGS[i])) {
				/*
				 * We get the concrete substring (The answer is whatever)
				 */
				String answerString = getAnswerString(msgAnswer,
						Constants.CORRECT_ANSWER_STRINGS[i]).trim();
				if (answerString != null) {
					/*
					 * If we got it. We get the answer
					 */
					String answer = answerString.replace(
							Constants.CORRECT_ANSWER_STRINGS[i], "").trim();
					if (!StaticUtils.isEmpty(answer)) {
						// log.log(answer.length());
						if (answer.length() == 1) {
							/*
							 * We have the letter
							 */
							String answerText = getAssociatedTextToAnswerOption(
									answer, quiz);
							if (answerText != null) {
								return new String[] { answer, answerText };
							}
						} else {
							/*
							 * We have the content.
							 */
							String answerOption = getAssociatedOptionToAnswerText(
									answer, quiz);
							if (answerOption != null) {
								return new String[] { answerOption, answer };
							} else {
								return null;
							}
						}
					} else {
						return null;
					}
				}
				i = Constants.CORRECT_ANSWER_STRINGS.length + 1;
			}
		}
		return null;
	}
	
	/**
	 * Method to get manually the answer for a given post.
	 * 
	 * @param id
	 * @return
	 */
	private String[] getManualAnswerForPost(String id) throws Exception {
		Properties prop = new Properties();
		prop.load(new FileInputStream(ConfigManager
				.getConfig(Constants.MANUAL_ANSWERS_FILE)));
		String answerStr = prop.getProperty(id + Constants.UNDERSCORE
				+ Constants.ANSWER);
		String answer[] = answerStr.split("@");
		return answer;
	}
	/**
	 * Method to, given the answer message, and the substring which identifies
	 * the correct answer obtain the full substring which identifies the
	 * message. Example:
	 * 
	 * blalbalablalalba. The correct answer is digital imaging. blablabla
	 * 
	 * The method isolate "The correct answer is digital imaging"
	 * 
	 * @param msgAnswer
	 *            The answer message.
	 * @param string
	 *            The substring which identifies the answer.
	 * @return The value
	 */
	private String getAnswerString(String msgAnswer, String answerIdentificator) {

		String subStringFromAnswerIdentificator = msgAnswer.substring(
				msgAnswer.indexOf(answerIdentificator, 0), msgAnswer.length());
		/*
		 * Now in subStringFromAnswerIdentificator we have
		 * "The answer is the pelvis. Paget’s disease of bone..."
		 * 
		 * The problem is that this string sometimes ends with ',' or even with
		 * ')':
		 * 
		 * The answer is D, unilateral hippocampal sclerosis The answer is b) 5%
		 * in the 18 months after i
		 * 
		 * We need to get the minimum substring from the beginning of
		 * "The answer is.." to the first occurrence of '.', ',' or ')'.
		 */

		String possibleString = null;
		for (int i = 0; i < Constants.POSSIBLE_ANSWER_STRING_SEPARATORS.length; i++) {
			String current = null;
			try {
				current = subStringFromAnswerIdentificator
						.substring(
								0,
								subStringFromAnswerIdentificator
										.indexOf(Constants.POSSIBLE_ANSWER_STRING_SEPARATORS[i]));
			} catch (Exception e) {
			}
			if (current != null) {
				if (possibleString == null) {
					/*
					 * If is the first time.. we assign it.
					 */
					possibleString = current;
				} else {
					if (current.length() < possibleString.length()) {
						possibleString = current;
					}
				}
			}
		}
		if (possibleString == null) {
			log.logError("\tString with answer not found!: " + msgAnswer);
		}
		return possibleString;
	}

	
	/**
	 * Method to get the associated text answer to a given answer option.
	 * 
	 * @param answerOption
	 *            Receives the letter.
	 * @param quiz
	 *            Receives the quiz.
	 * @return the value
	 */
	private String getAssociatedTextToAnswerOption(String answerOption,
			Quiz quiz) {
		for (int i = 0; i < quiz.getAnswerOptions().size(); i++) {
			if (quiz.getAnswerOptions().get(i).getOptionID()
					.equalsIgnoreCase(answerOption)) {
				return quiz.getAnswerOptions().get(i).getOptionText();
			}
		}
		return null;
	}
	
	/**
	 * Method to get the associated option answer to a given answer text.
	 * 
	 * @param answerText
	 *            Receives the text.
	 * @param quiz
	 *            Receives the quiz.
	 * @return the value
	 */
	private String getAssociatedOptionToAnswerText(String answerText, Quiz quiz) {
		answerText = StaticUtils.removeSymbols(answerText).trim();
		for (int i = 0; i < quiz.getAnswerOptions().size(); i++) {
			String optText = StaticUtils.removeSymbols(
					quiz.getAnswerOptions().get(i).getOptionText()).trim();
			if (optText.equalsIgnoreCase(answerText)) {
				return quiz.getAnswerOptions().get(i).getOptionID();
			}
		}
		return null;
	}
	/**
	 * Method to get a "Quiz" object from a Post object which seems to contains
	 * a quiz.
	 * 
	 * @param quizPost
	 *            Receives the Post object.
	 * @return Return the Answer object.
	 */
	private Quiz getQuiz(Post quizPost) throws Exception {
		Quiz q = new Quiz(quizPost);
		String parts[] = quizPost.getMessage().trim().split("\n");
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			if (isAnswerOption(part)) {
				AnswerOption aop = new AnswerOption(part);
				q.addAnswerOption(aop);
			} else {
				String possibleURL = StaticUtils.getURLIn(part);
				if (possibleURL != null) {
					q.addURL(possibleURL);
				} else {
					q.addWording(part);
				}
			}
		}
		if (this.manualQuizs_QuizsOptions.contains(quizPost.getId())) {
			/*
			 * If the options should be obtained manually, we get it and we
			 * assign it
			 */
			LinkedList<AnswerOption> aops = getAnswerOptionsFromManualQuiz(quizPost
					.getId());
			q.setAnswerOptions(aops);
		}
		return q;
	}
	
	/**
	 * Method to get answer options of a given quiz manually.
	 * 
	 * @param id
	 *            It receives the ID of the post.
	 * @return Return the list of options.
	 * @throws Exception
	 *             Can throws an exception.
	 */
	private LinkedList<AnswerOption> getAnswerOptionsFromManualQuiz(String id)
			throws Exception {
		LinkedList<AnswerOption> ret = new LinkedList<AnswerOption>();
		Properties prop = new Properties();
		prop.load(new FileInputStream(ConfigManager
				.getConfig(Constants.MANUAL_QUIZS_OPTIONS_FILE)));
		String optionsStr = prop.getProperty(id + Constants.UNDERSCORE
				+ Constants.OPTIONS);
		String options[] = optionsStr.split("@");
		for (int i = 0; i < options.length; i++) {
			ret.add(new AnswerOption(options[i]));
		}
		return ret;
	}
	
	/**
	 * Method to check if a String seems to be an answer option.
	 * 
	 * @param part
	 *            Receives the string
	 * @return Return a boolean
	 */
	private boolean isAnswerOption(String part) {
		for (int i = 0; i < Constants.ANSWER_OPTIONS.length; i++) {
			if (part.toUpperCase().startsWith(Constants.ANSWER_OPTIONS[i]))
				return true;
		}
		return false;
	}
	
	/**
	 * Method to load the answers (from answer post) which couldn't be processed
	 * automatically.
	 * 
	 * @return Return a list of the answer post ID.
	 * @throws Exception
	 *             It can throws an exception.
	 */
	private LinkedList<String> loadManualAnswers() throws Exception {
		LinkedList<String> ret = new LinkedList<String>();
		Properties prop = new Properties();
		prop.load(new FileInputStream(ConfigManager
				.getConfig(Constants.MANUAL_ANSWERS_FILE)));
		String totalAnswersStr = prop.getProperty(Constants.MANUAL_ANSWERS);
		String answers[] = totalAnswersStr.split("@");
		for (int i = 0; i < answers.length; i++) {
			ret.add(answers[i]);
		}
		return ret;
	}

	/**
	 * Method to load the quizs which the answer options coudln't be processed
	 * automatically.
	 * 
	 * @return Return a list of the quizs ID.
	 * @throws Exception
	 *             It can throws an exception.
	 */
	private LinkedList<String> loadManualQuizs() throws Exception {
		LinkedList<String> ret = new LinkedList<String>();
		Properties prop = new Properties();
		prop.load(new FileInputStream(ConfigManager
				.getConfig(Constants.MANUAL_QUIZS_OPTIONS_FILE)));
		String totalQuizsStr = prop.getProperty(Constants.MANUAL_QUIZS);
		String quizs[] = totalQuizsStr.split("@");
		for (int i = 0; i < quizs.length; i++) {
			ret.add(quizs[i]);
		}
		return ret;
	}
	
	/**
	 * Method to load the posts.
	 * 
	 */
	private void loadPosts() throws Exception {
		if (this.posts == null || this.quizPosts == null
				|| this.answersPosts == null) {
			if ((!StaticUtils.isEmpty(allPostsFile))
					&& (!StaticUtils.isEmpty(quizsFile))
					&& (!StaticUtils.isEmpty(answersFile))) {
				this.posts = loadPosts(allPostsFile);
				this.quizPosts = loadPosts(quizsFile);
				this.answersPosts = loadPosts(answersFile);
			}
			else {
				log.logError("Error: some of the files of the posts were not passed. Can't continue..");
				System.exit(-1);
			}
		}
		/*
		 * If posts != null && quizsPosts != null && answerPosts != null
		 * 
		 * this means that we receive the data in the other constructor..
		 */
	}

	/**
	 * Method to load all the answers without a quiz.
	 */
	private LinkedList<Post> getAnswersWithoutQuiz() {
		LinkedList<Post> ret = new LinkedList<Post>();
		for (int i = 0; i < this.answersPosts.size(); i++) {
			Post p = answersPosts.get(i);
			boolean found = false;
			for (int j = 0; j < quizsAndAnswersAsPost.size(); j++) {
				Post q = quizsAndAnswersAsPost.get(j).getAnswerPost();
				if (p.getId().equalsIgnoreCase(q.getId())) {
					found = true;
					j = quizsAndAnswersAsPost.size() + 1;
				}
			}
			if (!found) {
				ret.add(p);
			}
		}
		return ret;
	}

	/**
	 * Method to load all the quizs without an answer.
	 */
	private LinkedList<Post> getQuizsWithoutAnswers() {
		LinkedList<Post> ret = new LinkedList<Post>();
		for (int i = 0; i < quizPosts.size(); i++) {
			Post p = quizPosts.get(i);
			boolean found = false;
			for (int j = 0; j < quizsAndAnswersAsPost.size(); j++) {
				Post q = quizsAndAnswersAsPost.get(j).getQuizPost();
				if (p.getId().equalsIgnoreCase(q.getId())) {
					found = true;
					j = quizsAndAnswersAsPost.size() + 1;
				}
			}
			if (!found) {
				ret.add(p);
			}
		}
		return ret;
	}

	/**
	 * Method to load posts from a list file.
	 * 
	 * @param f
	 *            Receives the file with the list of files which contains the
	 *            posts.
	 * @return Return the list of posts.
	 * @throws Exception
	 *             It can throws an exception.
	 */
	private LinkedList<Post> loadPosts(String f) throws Exception {
		BufferedReader bL = new BufferedReader(new FileReader(f));
		LinkedList<Post> ret = new LinkedList<Post>();
		while (bL.ready()) {
			String rdf = bL.readLine();
			File file = new File(rdf);
			String extension = StaticUtils.getExtensionFromFile(file
					.getAbsoluteFile().toString());
			String filename = StaticUtils.getFileNameFromFile(file
					.getAbsoluteFile().toString());
			if (extension.equalsIgnoreCase("post")) {
				Post p = DataManager.getPost(file);
				DataManager.loadComments(p, filename);
				ret.add(p);
			}
		}
		bL.close();
		return ret;
	}

	/**
	 * Method to find an answer given a quiz..
	 * 
	 * @param quiz
	 *            Receives the quiz
	 * @return Returns the answer (if found).
	 */
	private Post findAnswer(Post quiz) {
		String urlQuiz = StaticUtils.getURLIn(quiz.getMessage());
		for (int i = 0; i < this.answersPosts.size(); i++) {
			Post answer = this.answersPosts.get(i);
			if (!quiz.getId().equalsIgnoreCase(answer.getId())) {
				String urlAnswer = StaticUtils.getURLIn(answer.getMessage());
				if (!StaticUtils.isEmpty(urlQuiz)
						&& !StaticUtils.isEmpty(urlAnswer)) {
					if (urlQuiz.equalsIgnoreCase(urlAnswer)) {
						return answer;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Method to find in "all the posts" a quiz for those answers without an
	 * associated quiz.
	 * 
	 * @return Return the total number of quizs found.
	 */
	private LinkedList<QuizAndAnswerAsPost> findQuizsForAnswersWithoutQuizsInAllPosts() {
		LinkedList<QuizAndAnswerAsPost> qwa = new LinkedList<QuizAndAnswerAsPost>();

		for (int i = 0; i < answersWithoutQuiz.size(); i++) {
			boolean found = false;
			/*
			 * We took the "answer without quiz"
			 */
			Post answerWithoutQuiz = answersWithoutQuiz.get(i);
			LinkedList<PostComparison> postComp = new LinkedList<PostComparison>();
			for (int j = 0; j < posts.size(); j++) {
				/*
				 * We check every possible post.
				 */
				Post p = posts.get(j);
				if (!answerWithoutQuiz.getId().equalsIgnoreCase(p.getId())) {
					/*
					 * If they are not the same ..
					 */
					if (seemsQuiz(p.getMessage())) {
						/*
						 * If the post seems to be a quiz (discarding other
						 * things: clinical cases, etc).
						 */
						if (arePairQuizAnswerBecauseURL(
								p,
								answerWithoutQuiz,
								Constants.HOW_FOUND_BY_SEARCH_ANSWER_WITHOUT_QUIZ_BY_URL)) {

							/*
							 * We have two approaches to find a quiz for a given
							 * answer:
							 * 
							 * 1) url match (the most accurate) 2) string
							 * similarity between posts
							 */
							qwa.add(new QuizAndAnswerAsPost(p,
									answerWithoutQuiz));
							this.answersWithoutQuiz.remove(answerWithoutQuiz);
							j = posts.size();
							found = true;
						} else {
							postComp.add(new PostComparison(p,
									answerWithoutQuiz));
						}
					}
				}
			}
			if (!found) {
				if (postComp.size() > 0) {
					PostComparison max = postComp.get(0);
					for (int k = 0; k < postComp.size(); k++) {
						if (StaticUtils.getNumberOfWords(postComp.get(k)
								.getLcss()) > StaticUtils.getNumberOfWords(max
								.getLcss())) {
							max = postComp.get(k);
						}
					}
					if (StaticUtils.getNumberOfWords(max.getLcss()) > Constants.MIN_WORDS_LONGEST_COMMON_SUBSTRING) {

						Post p1 = max.getPost1();
						Post p2 = max.getPost2();
						if (p1.getUrlPost() == null || p2.getUrlPost() == null) {
							p1.setHowFound(Constants.HOW_FOUND_BY_SEARCH_ANSWER_WITHOUT_QUIZ_BY_MESSAGE_LCSS);
							p2.setHowFound(Constants.HOW_FOUND_BY_SEARCH_ANSWER_WITHOUT_QUIZ_BY_MESSAGE_LCSS);
							qwa.add(new QuizAndAnswerAsPost(p1, p2));
							this.answersWithoutQuiz.remove(answerWithoutQuiz);
						}
					}
				}
			}
		}
		return qwa;
	}

	/**
	 * Method to find answer for those quizs without answer (using all posts)
	 * 
	 * @return Return the matchs found.
	 */
	private LinkedList<QuizAndAnswerAsPost> findAnswerForUnansweredQuizsInAllPosts() {
		LinkedList<QuizAndAnswerAsPost> qwa = new LinkedList<QuizAndAnswerAsPost>();
		for (int i = 0; i < quizWithoutAnswers.size(); i++) {
			boolean found = false;
			/**
			 * We take the quiz without answer
			 */
			Post quizUnanswered = quizWithoutAnswers.get(i);
			LinkedList<PostComparison> postComp = new LinkedList<PostComparison>();
			for (int j = 0; j < posts.size(); j++) {
				/*
				 * We take each possible post
				 */
				Post p = posts.get(j);
				if (!quizUnanswered.getId().equalsIgnoreCase(p.getId())) {
					/*
					 * If they are not the same ..
					 */
					if (seemsAnswer(p.getMessage())) {
						/*
						 * If seems to be an answer the post
						 */
						if (arePairQuizAnswerBecauseURL(
								p,
								quizUnanswered,
								Constants.HOW_FOUND_BY_SEARCH_QUIZ_WITHOUT_ANSWER_BY_URL)) {
							/*
							 * We have two approaches to find a quiz for a given
							 * answer:
							 * 
							 * 1) url match (the most accurate) 2) string
							 * similarity between posts
							 */
							qwa.add(new QuizAndAnswerAsPost(quizUnanswered, p));
							this.quizWithoutAnswers.remove(quizUnanswered);
							j = posts.size();
							found = true;
						} else {
							postComp.add(new PostComparison(quizUnanswered, p));
						}
					}
				}
			}
			if (!found) {
				if (postComp.size() > 0) {
					PostComparison max = postComp.get(0);
					for (int k = 0; k < postComp.size(); k++) {
						if (StaticUtils.getNumberOfWords(postComp.get(k)
								.getLcss()) > StaticUtils.getNumberOfWords(max
								.getLcss())) {
							max = postComp.get(k);
						}
					}
					if (StaticUtils.getNumberOfWords(max.getLcss()) > Constants.MIN_WORDS_LONGEST_COMMON_SUBSTRING) {
						Post p1 = max.getPost1();
						Post p2 = max.getPost2();
						if (p1.getUrlPost() == null || p2.getUrlPost() == null) {
							/*
							 * Only in the case that one of the two urls are
							 * null can be considered. If both urls are valid,
							 * then, this match was dismissed before in the url
							 * matching. The only "reason" to create a match
							 * based on LCSS is that it fulfill the LCSS
							 * requirements and one (or both) urls are not
							 * present and for this reason the matching was not
							 * done before.
							 */
							p1.setHowFound(Constants.HOW_FOUND_BY_SEARCH_ANSWER_WITHOUT_QUIZ_BY_MESSAGE_LCSS);
							p2.setHowFound(Constants.HOW_FOUND_BY_SEARCH_ANSWER_WITHOUT_QUIZ_BY_MESSAGE_LCSS);
							qwa.add(new QuizAndAnswerAsPost(p1, p2));
							this.quizWithoutAnswers.remove(quizUnanswered);
						}
					}
				}
			}
		}
		return qwa;
	}

	/**
	 * Method to check if two posts are quiz-answer pair based on the fact that
	 * they have the same URL.
	 * 
	 * @param p
	 *            Post 1
	 * @param q
	 *            Post w
	 * @param howFoundType
	 *            Additional parameter to establish how was found
	 * @return Return true or false.
	 */
	private boolean arePairQuizAnswerBecauseURL(Post p, Post q, int howFoundType) {
		String urlP = StaticUtils.getURLIn(p.getMessage());
		String urlQ = StaticUtils.getURLIn(q.getMessage());
		if (!StaticUtils.isEmpty(urlP) && !StaticUtils.isEmpty(urlQ)) {
			p.setHowFound(howFoundType);
			q.setHowFound(howFoundType);
			return urlP.equalsIgnoreCase(urlQ);
		}
		return false;
	}

	public LinkedList<QuizAndAnswer> getQuizsAndAnswers() {
		return this.quizsAndAnswers;
	}
	
	public void saveUserCommentsID(String file) throws Exception {
		log.log("Saving list of comments users ID..");
		LinkedList<String> usersIDs = new LinkedList<String>();
		for (int i = 0; i < quizsAndAnswers.size(); i++) {
			Post p = quizsAndAnswers.get(i).getQuiz().getOriginalPost();
			LinkedList<Comment> coms = p.getComments();
			for (int j = 0; j < coms.size(); j++) {
				Comment com = coms.get(j);
				if (!usersIDs.contains(com.getIdUserComment())) {
					usersIDs.add(com.getIdUserComment());
				}
			}
		}
		BufferedWriter bW = new BufferedWriter(new FileWriter(file));
		for (int i = 0; i < usersIDs.size(); i++) {
			bW.write(usersIDs.get(i));
			bW.newLine();
		}
		bW.close();
		log.log("done!");
	}


}
