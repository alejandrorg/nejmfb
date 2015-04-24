package com.alejandrorg.nejmfb.other;

import java.util.LinkedList;

import org.apache.commons.codec.digest.DigestUtils;

import com.alejandrorg.nejmfb.core.DataManager;
import com.alejandrorg.nejmfb.objects.Comment;
import com.alejandrorg.nejmfb.objects.Post;

public class OtherProcesses {

	public void anonymizeData(String sourceData, String destinyData) throws Exception {
		int nC = 0;
		System.out.println("Anonymizing data..");
		LinkedList<Post> posts = DataManager.loadData(sourceData);
		for (int i = 0; i < posts.size(); i++) {
			nC += posts.get(i).getComments().size();
			cryptUserNameAndID(posts.get(i).getComments());
		}
		DataManager.save(posts, destinyData);
		System.out.println("done!");
		System.out.println("Number of comments crypted: " + nC);
	}

	private void cryptUserNameAndID(LinkedList<Comment> comments) {
		for (int i = 0; i < comments.size(); i++) {
			String userName = crypt(comments.get(i).getUsernameComment());
			String userID = crypt(comments.get(i).getIdUserComment());
			comments.get(i).setUsernameComment(userName);
			comments.get(i).setIdUserComment(userID);
		}
	}

	private String crypt(String str) {
		String md5 = DigestUtils.md5Hex(str);
		return md5;
	}


}
