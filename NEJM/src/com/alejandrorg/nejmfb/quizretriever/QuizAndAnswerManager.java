package com.alejandrorg.nejmfb.quizretriever;

import java.util.LinkedList;
import com.alejandrorg.nejmfb.core.Constants;
import com.alejandrorg.nejmfb.core.MyLogger;
import com.alejandrorg.nejmfb.objects.Post;

public class QuizAndAnswerManager {

	protected String quizsFile;
	protected String answersFile;
	protected String allPostsFile;

	protected LinkedList<Post> answersPosts;
	protected LinkedList<Post> posts;
	protected LinkedList<Post> quizPosts;
	
	protected MyLogger log;

	public QuizAndAnswerManager(String qf, String af, String apf) {
		this.quizsFile = qf;
		this.answersFile = af;
		this.allPostsFile = apf;
		this.log = MyLogger.createLogger(true);
	}
	
	public QuizAndAnswerManager(LinkedList<Post> qs, LinkedList<Post> as,
			LinkedList<Post> aps) {
		this.quizPosts = qs;
		this.answersPosts = as;
		this.posts = aps;
		this.log = MyLogger.createLogger(true);
	}
	
	/**
	 * Method to check if the post seems to be a quiz. In fact it dismiss posts
	 * that seems to be other thing like case challenge or image challenge.
	 * 
	 * @param msg
	 *            Receives the post message.
	 * @return Return true or false.
	 */
	protected boolean seemsQuiz(String msg) {
		for (int i = 0; i < Constants.OTHER_POST_STRINGS.length; i++) {
			if (msg.toUpperCase().trim()
					.contains(Constants.OTHER_POST_STRINGS[i].toUpperCase())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Method to check if the string passed as parameter seems to contain the
	 * typical text of an answer.
	 * 
	 * @param str
	 *            Receives the string.
	 * @param msg
	 * @return Return if it seems to be an answer or not.
	 */
	protected boolean seemsAnswer(String str) {
		for (int i = 0; i < Constants.POSSIBLE_STRINGS_ANSWER.length; i++) {
			if ((str.trim().toUpperCase()
					.contains(Constants.POSSIBLE_STRINGS_ANSWER[i].trim()
							.toUpperCase()))) {
				return true;
			}
		}
		return false;
	}

	public LinkedList<Post> getAnswersPosts() {
		return answersPosts;
	}

	public LinkedList<Post> getPosts() {
		return posts;
	}

	public LinkedList<Post> getQuizPosts() {
		return quizPosts;
	}
	
	
}
