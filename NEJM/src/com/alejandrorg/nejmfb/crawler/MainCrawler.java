package com.alejandrorg.nejmfb.crawler;

public class MainCrawler {

	private final String PARAMETERS[] = { "-fromscratch", "-continueifcan" };

	public MainCrawler(String[] args) {
		init(args);
	}

	private void init(String[] args) {
		try {
			FBCrawler nejm = new FBCrawler();
			if (args.length == 1) {
				switch (getOption(args[0])) {
				case 0:
					nejm.runFromScratch();
					break;
				case 1:
					nejm.runContinueIfCan();
					break;
				default:
					System.err.println("No option or invalid option. Please execute:");
					System.err.println("\t-fromscratch (start from scratch)");
					System.err.println("\t-continueifcan (it continues in the last page)");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int getOption(String opt) {
		for (int i = 0; i < PARAMETERS.length; i++) {
			if (PARAMETERS[i].equalsIgnoreCase(opt)) {
				return i;
			}
		}
		return -1;
	}

	public static void main(String[] args) {
		new MainCrawler(args);
	}

}
