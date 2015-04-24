package com.alejandrorg.nejmfb.mains;

import com.alejandrorg.nejmfb.analysis.UserAnalysis;

public class MainUserAnalysis {

	public MainUserAnalysis() {
		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void init() throws Exception {
		UserAnalysis ua = new UserAnalysis();
		ua.loadByUser("statistics/statistics.tsv");
		ua.showAnalysis();
		ua.saveData("statistics/sta_users.tsv");
		
	}
	public static void main(String[] args) {
		new MainUserAnalysis();
	}

}
