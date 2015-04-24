package com.alejandrorg.nejmfb.crawler;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;

import com.alejandrorg.nejmfb.core.ConfigManager;
import com.alejandrorg.nejmfb.core.Constants;
import com.alejandrorg.nejmfb.core.DataManager;
import com.alejandrorg.nejmfb.core.MyLogger;
import com.alejandrorg.nejmfb.core.StaticUtils;
import com.alejandrorg.nejmfb.crawler.objects.Retry;
import com.alejandrorg.nejmfb.objects.Comment;
import com.alejandrorg.nejmfb.objects.Post;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FBCrawler {

	private String TOKEN;
	private String PAGE_ID;
	private int MAXIMUM_NUMBER_OF_RETRIES;

	private URL mainURL;
	private LinkedList<Post> posts;
	private boolean retry = false;
	private Retry lastRetry;
	private MyLogger log;

	private long timeStart, timeEnd;

	public FBCrawler() throws Exception {
		this.loadConfig();
		this.posts = new LinkedList<Post>();
	}

	/**
	 * Method to create the logger.
	 * @param append boolean to see if should append content to last file or start from scratch.
	 */
	private void createLogger(boolean append) {
		try {
			this.log = new MyLogger(append);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error creating logger: " + e.getMessage());
		}
	}

	/**
	 * Method to load the configuration.
	 * 
	 * @throws Exception
	 *             It can throws an exception.
	 */
	private void loadConfig() throws Exception {
		TOKEN = ConfigManager.getConfig(Constants.TOKEN);
		PAGE_ID = ConfigManager.getConfig(Constants.PAGE_ID);
		MAXIMUM_NUMBER_OF_RETRIES = Integer.parseInt(ConfigManager
				.getConfig(Constants.MAXIMUM_NUMBER_OF_RETRIES));
	}

	/**
	 * Method to load what is the start URL to process.
	 * 
	 * @throws Exception
	 *             It can throws an exception.
	 */
	private void loadStartURL() throws Exception {
		String lastURL = ConfigManager.getConfig(Constants.LAST_URL);
		if (StaticUtils.isEmpty(lastURL)) {
			this.mainURL = new URL("https://graph.facebook.com/v2.2/" + PAGE_ID
					+ Constants.SEPARATOR + "posts?" + "access_token=" + TOKEN);
			log.log("From scratch confirmed! Starting the process with base URL.");
		} else {
			this.mainURL = new URL(lastURL);
			log.log("Continuation confirmed! Starting the process with last URL: "
					+ lastURL);
		}
	}

	/**
	 * Method to run from the scratch.
	 * 
	 * @throws Exception
	 *             It can throws an exception.
	 */
	public void runFromScratch() throws Exception {
		this.removeFile(Constants.LOG_FILE);
		ConfigManager.setConfig(Constants.TOTAL_TIME, Integer.toString(0));
		this.createLogger(false);
		log.log("Running from scratch..");
		/*
		 * Removing the LAST_URL we ensure that is going to start from scratch
		 */
		ConfigManager.removeKey(Constants.LAST_URL);
		StaticUtils.removeFilesFromFolder(ConfigManager
				.getConfig(Constants.OUTPUT_FOLDER));
		this.run();
	}

	/**
	 * Method to remove a file.
	 * 
	 * @param f
	 *            The file.
	 */
	private void removeFile(String f) {
		File ftd = new File(f);
		ftd.delete();
	}

	/**
	 * Method to run, trying to continue (if possible).
	 * 
	 * @throws Exception
	 *             It can throws an exception.
	 */
	public void runContinueIfCan() throws Exception {
		this.createLogger(true);
		log.log("Running from last stop (if possible)..");
		this.run();
	}

	/**
	 * Main run method.
	 * 
	 * @throws Exception
	 *             It can throws an exception.
	 */
	public void run() throws Exception {
		this.loadStartURL();
		timeStart = System.currentTimeMillis();
		processData(mainURL.toString(), mainURL.openStream());
		timeEnd = System.currentTimeMillis();
		long totalTime = timeEnd - timeStart;
		long currentTime = Long.parseLong(ConfigManager
				.getConfig(Constants.TOTAL_TIME));
		currentTime += totalTime;
		ConfigManager.setConfig(Constants.TOTAL_TIME,
				Long.toString(currentTime));
		log.log("Retrieval time: "
				+ StaticUtils.formatTime((currentTime / 1000)));
		timeStart = System.currentTimeMillis();
		DataManager.save(posts, ConfigManager.getConfig(Constants.OUTPUT_FOLDER));
		timeEnd = System.currentTimeMillis();
		log.log("Saving time: "
				+ StaticUtils.formatTime(((timeEnd - timeStart) / 1000)));
	}



	/**
	 * Method to process the data.
	 * @param url Receives the URL to process (string).
	 * @param is The stream with the URL.
	 * @throws Exception It can throws an exception.
	 */
	protected void processData(String url, InputStream is) throws Exception {
		if (retry) {
			log.log("Retrying .. waiting 5 minutes..");
			Thread.sleep(300000);
			log.log("Waiting time finished! Let's go!");
			this.retry = false;
		} else {
			log.log("Normal processing..");
		}
		ConfigManager.setConfig(Constants.LAST_URL, url);
		log.log("Processing page: " + url);
		ObjectMapper om = new ObjectMapper();
		try {
			JsonNode root = om.readValue(is, JsonNode.class);
			Iterator<JsonNode> itRoot = root.get("data").iterator();
			while (itRoot.hasNext()) {
				JsonNode node = itRoot.next();
				if (node != null) {
					JsonNode id = node.get("id");
					JsonNode message = node.get("message");
					JsonNode createdTime = node.get("created_time");
					Post post = new Post(id, createdTime, message);
					Thread.sleep(1000);
					URL commentsPost = getCommentsPost(id.asText());
					retrieveComments(post, commentsPost.openStream());
					posts.add(post);
				}
			}
			JsonNode paging = root.get("paging");
			if (paging != null) {
				JsonNode nextPage = paging.get("next");
				if (nextPage != null) {
					System.out.println("Next page..");
					String urlNext = nextPage.asText();
					is.close();
					processData(urlNext, new URL(urlNext).openStream());
				}
			}
			is.close();
		} catch (Exception e) {
			log.logError("Error reading data.");
			e.printStackTrace(System.err);
			this.retry = true;
			String urlRetry = ConfigManager.getConfig(Constants.LAST_URL);
			is.close();
			if (this.lastRetry == null) {
				this.lastRetry = new Retry(urlRetry);
				log.logError("Retrying .. " + this.lastRetry.getNumRetries()
						+ " of " + MAXIMUM_NUMBER_OF_RETRIES);
				processData(urlRetry, new URL(urlRetry).openStream());
			} else {
				if (this.lastRetry.getNumRetries() >= MAXIMUM_NUMBER_OF_RETRIES) {
					log.logError("Maximum number of retries reached. Saving and exiting..");
					timeEnd = System.currentTimeMillis();
					long totalTime = timeEnd - timeStart;
					long currentTime = Long.parseLong(ConfigManager
							.getConfig(Constants.TOTAL_TIME));
					currentTime += totalTime;
					ConfigManager.setConfig(Constants.TOTAL_TIME,
							Long.toString(currentTime));
					DataManager.save(posts, ConfigManager.getConfig(Constants.OUTPUT_FOLDER));
					System.exit(-1);
				} else {
					this.lastRetry.incRetry();
					log.logError("Retrying .. "
							+ this.lastRetry.getNumRetries() + " of "
							+ MAXIMUM_NUMBER_OF_RETRIES);
					processData(urlRetry, new URL(urlRetry).openStream());
				}
			}
		}
	}

	/**
	 * Method to call GRAPH API to get the number of likes of a post.
	 * @param idPost Receives the ID of the post.
	 * @return Return the number of likes.
	 * @throws Exception It can throws an exception.
	 */
	@SuppressWarnings("unused")
	private int getTotalLikes(String idPost) throws Exception {
		URL urlLikes = new URL("https://graph.facebook.com/" + idPost
				+ Constants.SEPARATOR + "likes?summary=true&access_token="
				+ TOKEN);
		ObjectMapper om = new ObjectMapper();
		JsonNode root = om.readValue(urlLikes.openStream(), JsonNode.class);
		JsonNode summaryNode = root.get("summary");
		JsonNode totalCount = summaryNode.get("total_count");
		return totalCount.asInt();
	}

	/**
	 * Method to retrieve the comments of a Post object.
	 * @param post Receives the Post object.
	 * @param is Receives the stream.
	 * @throws Exception  It can throws an exception.
	 */
	private void retrieveComments(Post post, InputStream is) throws Exception {
		ObjectMapper om = new ObjectMapper();
		JsonNode root = om.readValue(is, JsonNode.class);
		Iterator<JsonNode> itComments = root.get("data").iterator();
		while (itComments.hasNext()) {
			JsonNode nodeComment = itComments.next();
			if (nodeComment != null) {
				JsonNode idComment = nodeComment.get("id");
				JsonNode fromComment = nodeComment.get("from");
				JsonNode nameUserComment = fromComment.get("name");
				JsonNode idUserComment = fromComment.get("id");
				JsonNode messageComment = nodeComment.get("message");
				JsonNode createdTimeComment = nodeComment.get("created_time");
				post.addComment(new Comment(idComment.asText(), idUserComment
						.asText(), nameUserComment.asText(), messageComment
						.asText(), createdTimeComment.asText()));
			}
		}
		JsonNode paging = root.get("paging");
		if (paging != null) {
			JsonNode nextPage = paging.get("next");
			if (nextPage != null) {
				Thread.sleep(1000);
				is.close();
				String urlNext = nextPage.asText();
				retrieveComments(post, new URL(urlNext).openStream());
			}
		}
		is.close();
	}
	
	/**
	 * Method to get the URL object of the comments of a given post.
	 * @param idPost Receives the id of the post.
	 * @return Returns the value.
	 * @throws Exception It can throws an exception.
	 */
	private URL getCommentsPost(String idPost) throws Exception {
		return new URL("https://graph.facebook.com/" + idPost
				+ Constants.SEPARATOR + "comments?access_token=" + TOKEN);
	}

}
