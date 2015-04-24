package com.alejandrorg.nejmfb.analysis.objects;

import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;

import com.alejandrorg.nejmfb.core.Constants;
import com.alejandrorg.nejmfb.core.StaticUtils;
import com.alejandrorg.nejmfb.objects.Post;

public class PostComparison {

	private Post post1;
	private Post post2;

	private String post1MessageProcessed;
	private String post2MessageProcessed;
	
	private String lcss;
	
	public PostComparison(Post p1, Post p2) {
		post1 = p1;
		post2 = p2;
		runComparison();
	}

	private void runComparison() {
		LinkedList<String> p1Words = new LinkedList<String>();
		p1Words.addAll(getWords(post1.getMessage().trim().toUpperCase()));
		LinkedList<String> p2Words = new LinkedList<String>();
		p2Words.addAll(getWords(post2.getMessage().trim()
				.toUpperCase()));

		post1MessageProcessed = StaticUtils.getStringFromList(p1Words);
		post2MessageProcessed = StaticUtils.getStringFromList(p2Words);
		lcss = StaticUtils.longestCommonSubstring(post1MessageProcessed, post2MessageProcessed);
	}
	
	
	
	public Post getPost1() {
		return post1;
	}

	public void setPost1(Post post1) {
		this.post1 = post1;
	}

	public Post getPost2() {
		return post2;
	}

	public void setPost2(Post post2) {
		this.post2 = post2;
	}

	public String getPost1MessageProcessed() {
		return post1MessageProcessed;
	}

	public void setPost1MessageProcessed(String post1MessageProcessed) {
		this.post1MessageProcessed = post1MessageProcessed;
	}

	public String getPost2MessageProcessed() {
		return post2MessageProcessed;
	}

	public void setPost2MessageProcessed(String post2MessageProcessed) {
		this.post2MessageProcessed = post2MessageProcessed;
	}

	public String getLcss() {
		return lcss;
	}

	public void setLcss(String lcss) {
		this.lcss = lcss;
	}

	/**
	 * Method to obtain all the words from a string.
	 * 
	 * @param message
	 *            Receives the string,
	 * @return Return a list.
	 */
	private LinkedList<String> getWords(String message) {
		String url = StaticUtils.getURLIn(message);
		if (url != null) {
			message = StringUtils.remove(message, url);
		}
		
		message = StaticUtils.removeSymbols(message);
		message = StringUtils.remove(message, Character.toString('"'));
		message = StaticUtils.removeStopWords(message, Constants.STOP_WORDS);
		message = StaticUtils.removeOneCharacterWords(message);
		LinkedList<String> ret = new LinkedList<String>();
		String parts[] = message.split(" ");
		for (int i = 0; i < parts.length; i++) {
			ret.add(parts[i]);
		}
		return ret;
	}
}
