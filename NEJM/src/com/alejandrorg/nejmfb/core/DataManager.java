package com.alejandrorg.nejmfb.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.LinkedList;
import java.util.Properties;

import com.alejandrorg.nejmfb.objects.Comment;
import com.alejandrorg.nejmfb.objects.Post;

public class DataManager {

	private final static String NA_TEXT = "NA";
	
	/**
	 * Method to load the comments of a given post.
	 * 
	 * @param p
	 *            Receives the post.
	 * @param filename
	 *            Receives the filename (base) of the post.
	 * @return Return the number of comments loaded for this post.
	 * @throws Exception
	 *             It can throws an exception.
	 */
	public static int loadComments(Post p, String filename) throws Exception {
		int ncoms = 0;
		String commentsFile = filename + ".comments";
		File fCom = new File(commentsFile);
		if (fCom.exists()) {
			Properties prop = new Properties();
			prop.load(new FileInputStream(fCom));
			String numberComments = prop
					.getProperty(Constants.NUMBER_OF_COMMENTS);
			int nc = StaticUtils.isEmpty(numberComments) ? 0 : Integer
					.parseInt(numberComments);
			for (int j = 0; j < nc; j++) {
				String id = prop.getProperty(Constants.COMMENT
						+ Constants.UNDERSCORE + j + Constants.UNDERSCORE
						+ Constants.ID);
				String msg = prop.getProperty(Constants.COMMENT
						+ Constants.UNDERSCORE + j + Constants.UNDERSCORE
						+ Constants.MESSAGE);
				String userID = prop.getProperty(Constants.COMMENT
						+ Constants.UNDERSCORE + j + Constants.UNDERSCORE
						+ Constants.USER_ID);
				String username = prop.getProperty(Constants.COMMENT
						+ Constants.UNDERSCORE + j + Constants.UNDERSCORE
						+ Constants.USERNAME);
				String cd = prop.getProperty(Constants.COMMENT
						+ Constants.UNDERSCORE + j + Constants.UNDERSCORE
						+ Constants.CREATED_DATE);
				Comment com = new Comment(id, userID, username, msg, cd);
				com.setFile(fCom);
				p.addComment(com);
				ncoms++;
			}
		}
		return ncoms;
	}
	
	/**
	 * Method to load a post from a file.
	 * 
	 * @param file
	 *            Receives the file.
	 * @return Returns the post.
	 * @throws Exception
	 *             It can throws an exception.
	 */
	public static Post getPost(File file) throws Exception {
		Properties prop = new Properties();
		prop.load(new FileInputStream(file));
		String id = prop.getProperty(Constants.ID);
		String cd = prop.getProperty(Constants.CREATED_DATE);
		String msg = prop.getProperty(Constants.MESSAGE);
		Post p = new Post(id, cd, msg);
		p.setFile(file);
		return p;
	}
	
	/**
	 * Method to save current results.
	 * 
	 * @throws Exception
	 *             It can throws an exception.
	 */
	public static void save(LinkedList<Post> posts, String outputFolder) throws Exception {
		for (int i = 0; i < posts.size(); i++) {
			Post p = posts.get(i);
			String file = p.getId();
			Properties prop = new Properties();
			prop.setProperty(Constants.ID, p.getId());
			prop.setProperty(Constants.CREATED_DATE,
					p.getCreatedDateOriginalString());
			prop.setProperty(Constants.MESSAGE, p.getMessage());
			prop.store(
					new FileOutputStream(outputFolder
							+ Constants.SEPARATOR
							+ file
							+ Constants.POST_EXTENSION), "");
			if (p.getComments().size() > 0) {
				prop = new Properties();
				prop.setProperty(Constants.NUMBER_OF_COMMENTS,
						Integer.toString(p.getComments().size()));
				for (int j = 0; j < p.getComments().size(); j++) {
					Comment com = p.getComments().get(j);
					prop.setProperty(Constants.COMMENT + Constants.UNDERSCORE
							+ j + Constants.UNDERSCORE + Constants.ID,
							com.getId());
					prop.setProperty(Constants.COMMENT + Constants.UNDERSCORE
							+ j + Constants.UNDERSCORE + Constants.MESSAGE,
							com.getMessage());
					prop.setProperty(Constants.COMMENT + Constants.UNDERSCORE
							+ j + Constants.UNDERSCORE + Constants.USER_ID,
							com.getIdUserComment());
					prop.setProperty(Constants.COMMENT + Constants.UNDERSCORE
							+ j + Constants.UNDERSCORE + Constants.USERNAME,
							com.getUsernameComment());
					prop.setProperty(
							Constants.COMMENT + Constants.UNDERSCORE + j
									+ Constants.UNDERSCORE
									+ Constants.CREATED_DATE,
							com.getCreatedDateOriginalString());
				}
				prop.store(
						new FileOutputStream(outputFolder
								+ Constants.SEPARATOR
								+ file
								+ Constants.COMMENTS_EXTENSION), "");
			}
		}
	}

	public static LinkedList<Post> loadData(String folder) throws Exception {
		LinkedList<Post> ret = new LinkedList<Post>();
		File fold = new File(folder);
		File files[] = fold.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".post");
				/*
				 * We only load "posts".
				 */
			}
		});
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			String filename = StaticUtils.getFileNameFromFile(file
					.getAbsoluteFile().toString());
			Post p = DataManager.getPost(file);
			if (!p.getMessage().equalsIgnoreCase(NA_TEXT)) {
				DataManager.loadComments(p, filename);
				ret.add(p);
			}
		}
		return ret;
	}
}
