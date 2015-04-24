package com.alejandrorg.nejmfb.crawler.objects;

public class Retry {

	private String urlRetry;
	private int numRetries;
	
	public Retry(String ur) {
		this.urlRetry = ur;
		this.numRetries = 1;
	}
	
	public void incRetry() {
		this.numRetries++;
	}
	
	public int getNumRetries() {
		return this.numRetries;
	}

	public String getUrlRetry() {
		return urlRetry;
	}

	public void setUrlRetry(String urlRetry) {
		this.urlRetry = urlRetry;
	}
	
}
