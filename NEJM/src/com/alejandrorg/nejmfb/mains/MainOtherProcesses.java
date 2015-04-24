package com.alejandrorg.nejmfb.mains;

import com.alejandrorg.nejmfb.other.OtherProcesses;

public class MainOtherProcesses {

	public MainOtherProcesses() throws Exception {
		init();
	}

	private void init() throws Exception {
		String sourceData = "data/crawlerOutput";
		String destinyData = "anonimizedData";
		OtherProcesses op = new OtherProcesses();
		op.anonymizeData(sourceData, destinyData);
	}

	public static void main(String[] args) {
		try {
			new MainOtherProcesses();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
