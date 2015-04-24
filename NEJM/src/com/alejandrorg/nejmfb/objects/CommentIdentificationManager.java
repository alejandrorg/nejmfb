package com.alejandrorg.nejmfb.objects;

import java.util.HashMap;

public class CommentIdentificationManager {

	private HashMap<Integer, Integer> civ;

	public CommentIdentificationManager() {
		civ = new HashMap<Integer, Integer>();
	}

	public void addCommentIdentifiedByCode(int howFound) {
		if (civ.containsKey(new Integer(howFound))) {
			int value = civ.get(howFound).intValue() + 1;
			civ.put(new Integer(howFound), new Integer(value));
		}
		else {
			civ.put(new Integer(howFound), new Integer(1));
		}

	}

	public HashMap<Integer, Integer> getHashMap() {
		return civ;
	}


}
