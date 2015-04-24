package com.alejandrorg.nejmfb.quizretriever;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Collections;
import java.util.LinkedList;

import com.alejandrorg.nejmfb.core.Constants;
import com.alejandrorg.nejmfb.core.DataManager;
import com.alejandrorg.nejmfb.objects.DateComparator;
import com.alejandrorg.nejmfb.objects.Post;

public class QuizAndAnswerSeparator extends QuizAndAnswerManager {

	private String errorPostsFile;
	private String folder;
	
	private final int QUIZ = 1;
	private final int ANSWER_QUIZ = 2;
	private final int OTHER_POST = 3;
	private final int ALL_POSTS = 4;
	
	private final String NA_TEXT = "NA";
	
	private LinkedList<Post> errorPosts;
	/**
	 * Constructor.
	 * @param qf Quizs file.
	 * @param af Answers file.
	 * @param ap All posts file.
	 */
	public QuizAndAnswerSeparator(String f, String qf, String af, String apf, String epf) {
		super(qf, af, apf);
		this.folder = f;
		this.errorPostsFile = epf;

	}
	
	/**
	 * Method to separate posts in "Quizs" and "Answers".
	 * 
	 * It uses the analyze() method which return if the post if a quiz, an
	 * answer or "other post".
	 * 
	 * @param folder
	 *            It receives the folder where are all the posts.
	 * @param allPostsFile
	 * @param answersFile
	 * @param quizsFile
	 * @param errorPostsFile
	 * @throws Exception
	 *             It can throws an exception.
	 */
	@SuppressWarnings("unchecked")
	public void run() throws Exception {
		log.log("Separating posts..");
		this.posts = DataManager.loadData(folder);
		this.errorPosts = new LinkedList<Post>();
		this.quizPosts = new LinkedList<Post>();
		this.answersPosts = new LinkedList<Post>();
		int ncoms = 0;
		for (int i = 0; i < posts.size(); i++) {
			Post p = posts.get(i);
			if (!p.getMessage().equalsIgnoreCase(NA_TEXT)) {
				switch (analyzeQuiz(p)) {
				/*
				 * This method return the type of post.
				 */
				case QUIZ:
					if (!this.quizPosts.contains(p)) {
						this.quizPosts.add(p);
						ncoms += p.getComments().size();
					}
					break;
				case ANSWER_QUIZ:
					if (!this.answersPosts.contains(p)) {
						this.answersPosts.add(p);
					}
					break;
				}
			} else {
				this.errorPosts.add(p);
			}
		}
		log.log("Posts loaded: " + posts.size() + " posts");
		log.log("Total comments: " + ncoms);
		log.log("Quizs: " + this.quizPosts.size());
		log.log("Answers Quizs: " + this.answersPosts.size());

		log.log("Posts with errors: " + this.errorPosts.size());
		/*
		 * Once the analysis is done, we create a list with the files which
		 * contains the quizs and the answers. This "separate" method just
		 * should be done one time. This is the reason to separate the files in
		 * other folders. Now we can execute the alignment (create quiz-answer
		 * pair) separately.
		 */
		Collections.sort(this.posts, new DateComparator());
		Collections.sort(this.quizPosts, new DateComparator());
		Collections.sort(this.answersPosts, new DateComparator());
		Collections.sort(this.errorPosts, new DateComparator());
		saveList(this.quizPosts, quizsFile, QUIZ);
		saveList(this.answersPosts, answersFile, ANSWER_QUIZ);
		saveList(this.posts, allPostsFile, ALL_POSTS);
		saveList(this.errorPosts, errorPostsFile, ALL_POSTS);
		log.log("Done!");
	}
	/**
	 * This method is used to determine whether a post is a "quiz" or an answer.
	 * 
	 * @param p
	 *            It receives the post.
	 * @return Return the type.
	 */
	private int analyzeQuiz(Post p) {
		/*
		 * A quiz is typically formed with a text describing the question and a
		 * set of possible answers in the form of "A. text", "B. text",
		 * "C. text", .. being each answer in a separate paragraph.
		 */
		boolean boolAnswers[] = new boolean[Constants.ANSWER_OPTIONS.length];
		for (int i = 0; i < boolAnswers.length; i++) {
			boolAnswers[i] = false;
		}
		String msg = p.getMessage().trim();
		if (seemsQuiz(msg)) {
			String parts[] = msg.split("\n");
			/* After trim the message, we divide it in paragraphs */
			for (int i = 0; i < parts.length; i++) {
				/*
				 * We check each paragraph to see if it contains a text
				 * (seemsAnswer method) which can guide us to think that it is
				 * an answer and not a quiz.
				 */
				if (seemsAnswer(parts[i])) {
					return ANSWER_QUIZ;
				}
				for (int j = 0; j < Constants.ANSWER_OPTIONS.length; j++) {
					/*
					 * If it not seems to be an answer, we check if it is a
					 * quiz. To check if it is a quiz, we check if each
					 * paragraph starts with a letter which represents an option
					 * of the quiz. If this is the case, we establish as true
					 * the corresponding "letter".
					 */
					if (parts[i].toUpperCase().startsWith(
							Constants.ANSWER_OPTIONS[j])) {
						boolAnswers[j] = true;
						j = Constants.ANSWER_OPTIONS.length;
					}
				}
			}
			int numberOfAnswersOptions = 0;
			for (int i = 0; i < boolAnswers.length; i++) {
				/*
				 * After that, we count the number of answers options retrieved.
				 */
				if (boolAnswers[i]) {
					numberOfAnswersOptions++;
				}
			}
			if (numberOfAnswersOptions >= Constants.MINIMUM_NUMBER_OF_ANSWER_OPTIONS_TO_CONSIDER_QUIZ) {
				/*
				 * If the number is higher than a threshold.. we consider that
				 * this post is a quiz
				 */
				return QUIZ;
			}
		}
		return OTHER_POST;
	}
	
	/**
	 * Method to save a list which contains the path to the post file.
	 * 
	 * @param posts
	 *            Receives the list with the posts.
	 * @param dir
	 *            Destiny folder.
	 * @param type
	 *            Type of post (quizs, answers or all).
	 * @throws Exception
	 *             It can throws an exception.
	 */
	private void saveList(LinkedList<Post> posts, String f, int type)
			throws Exception {
		BufferedWriter bW = new BufferedWriter(new FileWriter(f));
		for (int i = 0; i < posts.size(); i++) {
			bW.write(posts.get(i).getFile().toString());
			bW.newLine();
			if (type == QUIZ || type == ALL_POSTS) {
				if (posts.get(i).getComments().size() > 0) {
					File fComment = posts.get(i).getComments().get(0).getFile();
					bW.write(fComment.toString());
					bW.newLine();
				}
			}
		}
		bW.close();
	}
	
	/**
	 * Method to copy the files of the linkedlist<Post> to the folder.
	 * 
	 * @param posts
	 *            Receives the list with the posts.
	 * @param dir
	 *            Destiny folder.
	 * @param type
	 *            Type of copy (quizs or answers).
	 * @throws Exception
	 *             It can throws an exception.
	 */
	@SuppressWarnings("unused")
	private void copy(LinkedList<Post> posts, String dir, int type)
			throws Exception {
		File foldDest = new File(dir);
		for (int i = 0; i < posts.size(); i++) {

			File fPost = posts.get(i).getFile();
			File fDest = new File(foldDest.getAbsoluteFile().toString() + "\\"
					+ fPost.getName());
			if (!fDest.exists()) {
				Files.copy(fPost.toPath(), fDest.toPath());
			} else {
				System.err.println("Duplicate post!: " + fDest.toString());
			}
			if (type == QUIZ) {
				if (posts.get(i).getComments().size() > 0) {
					File fComment = posts.get(i).getComments().get(0).getFile();
					fDest = new File(foldDest.getAbsoluteFile().toString()
							+ "\\" + fComment.getName());
					if (!fDest.exists()) {
						Files.copy(fComment.toPath(), fDest.toPath());
					} else {
						System.err.println("Duplicate comment!: "
								+ fDest.toString());
					}
				}
			}
		}
	}
}
