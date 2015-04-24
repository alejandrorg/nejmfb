package com.alejandrorg.nejmfb.quizretriever.objects;

import java.util.LinkedList;

import com.alejandrorg.nejmfb.core.Constants;
import com.alejandrorg.nejmfb.objects.Comment;
import com.alejandrorg.nejmfb.objects.Post;

public class Quiz {

	private LinkedList<AnswerOption> answerOptions;
	private LinkedList<String> wording;
	private LinkedList<String> urls;

	private Post originalPost;

	public Quiz(Post quizPost) {
		this.originalPost = quizPost;
		this.answerOptions = new LinkedList<AnswerOption>();
		this.wording = new LinkedList<String>();
		this.urls = new LinkedList<String>();
	}

	public String getID() {
		return this.originalPost.getId();
	}
	public void addAnswerOption(AnswerOption aop) {
		this.answerOptions.add(aop);
	}

	public Post getOriginalPost() {
		return originalPost;
	}

	public void setOriginalPost(Post originalPost) {
		this.originalPost = originalPost;
	}

	public LinkedList<AnswerOption> getAnswerOptions() {
		return answerOptions;
	}

	public void addWording(String w) {
		this.wording.add(w);
	}

	public String getWording() {
		String ret = "";
		for (int i = 0; i < wording.size(); i++) {
			ret += wording.get(i) + "\n";
		}
		return ret;
	}

	public LinkedList<String> getWordingList() {
		return wording;
	}

	public void addURL(String u) {
		this.urls.add(u);
	}

	public LinkedList<String> getURLList() {
		return this.urls;
	}

	public String getHowFound() {
		switch (this.originalPost.getHowFound()) {
		case Constants.HOW_FOUND_DIRECT:
			return "Found: Direct";
		case Constants.HOW_FOUND_BY_SEARCH_QUIZ_WITHOUT_ANSWER_BY_URL:
			return "Found: Searching for an answer (quiz without answer) - By URL";
		case Constants.HOW_FOUND_BY_SEARCH_ANSWER_WITHOUT_QUIZ_BY_URL:
			return "Found: Searching for a quiz (answer without quiz) - By URL";
		case Constants.HOW_FOUND_BY_SEARCH_ANSWER_WITHOUT_QUIZ_BY_MESSAGE_LCSS:
			return "Found: Searching for a quiz (answer without quiz) - By Longest common substring comparison";
		case Constants.HOW_FOUND_BY_SEARCH_ANSWER_WITHOUT_QUIZ_BY_MESSAGE_RATIO_SMALLEST_POST_LCSS:
			return "Found: Searching for a quiz (answer without quiz) - By ratio smallest post - lcss";
		case Constants.HOW_FOUND_BY_SEARCH_ANSWER_WITHOUT_QUIZ_BY_MESSAGE_POSTS_SIMILAR:
			return "Found: Searching for a quiz (answer without quiz) - By number of words post similarity";
		}
		return "Found: NA";
	}
	public String toString() {
		String ret = "";
		ret += "Original post:\n";
		ret += this.originalPost.toString() + "\n";
		ret += "-----------------------\n";
		ret += "Wording:\n";
		for (int i = 0; i < wording.size(); i++) {
			ret += "\t" + wording.get(i) + "\n";
		}
		ret += "URLs:\n";
		for (int i = 0; i < urls.size(); i++) {
			ret += "\t" + urls.get(i) + "\n";
		}
		ret += "Answer options:\n";
		for (int i = 0; i < answerOptions.size(); i++) {
			ret += "\t" + answerOptions.get(i).toString() + "\n";
		}
		ret += getHowFound() + "\n";
		return ret;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Quiz) {
			Quiz po = (Quiz)o;
			return po.getOriginalPost().getId().equalsIgnoreCase(this.originalPost.getId());
		}
		return false;
	}

	public void setAnswerOptions(LinkedList<AnswerOption> aops) {
		this.answerOptions = aops;
	}

	public String getAnswerOptionsString() {
		String ret = "";
		for (int i = 0; i < this.answerOptions.size(); i++) {
			ret += "\t" + this.answerOptions.get(i).getOptionID() + " - " + this.answerOptions.get(i).getOptionText() + "\n";
		}
		return ret;
	}

	public LinkedList<Comment> getComments() {
		return this.originalPost.getComments();
	}

	public String getCreatedDate() {
		return this.originalPost.getCreatedDate().toString();
	}
	
	public String getStatisticalOutput() {
		String ret = "";
		
		return ret;
	}
}
