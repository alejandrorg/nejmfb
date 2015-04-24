package com.alejandrorg.nejmfb.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

public class UserAnalysis {

	private final int COMMENT_ID_POSITION = 0;
	private final int QUIZ_ID_POSITION = 1;
	private final int CREATED_DATE_POSITION = 2;
	private final int USER_ID_POSITION = 3;
	private final int ANSWER_POSITION = 4;
	private final int CORRECT_ANSWER_POSITION = 5;
	private final int HOW_FOUND_POSITION = 6;

	// private final int CORRECT_INCORRECT_POSITION = 7;
	private Hashtable<String, UserData> source = new Hashtable<String, UserData>();
	private HashMap<String, UserData> dataByUsers;

	public UserAnalysis() {
		dataByUsers = new HashMap<String, UserData>(source);
	}
	public void saveData(String f) throws Exception {
		BufferedWriter bW = new BufferedWriter(new FileWriter(f));
		Iterator<String> itusers = dataByUsers.keySet().iterator();
		while (itusers.hasNext()) {
			String idUser = itusers.next();
			UserData ud = dataByUsers.get(idUser);
			bW.write(idUser + "\t" + ud.getNumberAnswersCorrect() + "\t" + ud.getNumberAnswersIncorrect());
			bW.newLine();
		}
		bW.close();
	}
	public void showAnalysis() {
		System.out.println("Number of users that participate: "
				+ dataByUsers.size());
		Iterator<String> itusers = dataByUsers.keySet().iterator();
		UserData userMostAnswersCorrect = null, userMostAnswersIncorrect = null;
		UserData userBestRatioAnswersCorrect = null, userWorstRatioAnswersCorrect = null;
		UserData userWithMoreAnswers = null, userWithLessAnswers = null;
		
		while (itusers.hasNext()) {
			String idUser = itusers.next();
			UserData ud = dataByUsers.get(idUser);
			
			if (userWithMoreAnswers == null) {
				userWithMoreAnswers = ud;
			}
			else {
				if (ud.getQuizAndCommentData().size() > userWithMoreAnswers.getQuizAndCommentData().size()) {
					userWithMoreAnswers = ud;
				}
			}
			
			if (userWithLessAnswers == null) {
				userWithLessAnswers = ud;
			}
			else {
				if (ud.getQuizAndCommentData().size() < userWithLessAnswers.getQuizAndCommentData().size()) {
					userWithLessAnswers = ud;
				}
			}
			
			if (userMostAnswersCorrect == null) {
				userMostAnswersCorrect = ud;
			}
			else {
				if (ud.getNumberAnswersCorrect() > userMostAnswersCorrect.getNumberAnswersCorrect()) {
					userMostAnswersCorrect = ud;
				}
			}
			
			if (userMostAnswersIncorrect == null) {
				userMostAnswersIncorrect = ud;
			}
			else {
				if (ud.getNumberAnswersIncorrect() > userMostAnswersIncorrect.getNumberAnswersIncorrect()) {
					userMostAnswersIncorrect = ud;
				}
			}
			
			if (userBestRatioAnswersCorrect == null) {
				userBestRatioAnswersCorrect = ud;
			}
			else {
				if (ud.getNumberAnswersCorrectRatio() > userBestRatioAnswersCorrect.getNumberAnswersCorrectRatio()) {
					userBestRatioAnswersCorrect = ud;
				}
			}
			
			if (userWorstRatioAnswersCorrect == null) {
				userWorstRatioAnswersCorrect = ud;
			}
			else {
				if (ud.getNumberAnswersCorrectRatio() < userWorstRatioAnswersCorrect.getNumberAnswersCorrectRatio()) {
					userWorstRatioAnswersCorrect = ud;
				}
			}
		}
		System.out.println("User with more answers: " + userWithMoreAnswers.getQuizAndCommentData().size());
		System.out.println("User with less answers: " + userWithLessAnswers.getQuizAndCommentData().size());
		System.out.println();
		System.out.println();
		System.out.println("User with more correct answers: " + userMostAnswersCorrect.getNumberAnswersCorrect());
		System.out.println("\tNumber of answers provided: " + userMostAnswersCorrect.getQuizAndCommentData().size());
		System.out.println("\tProportion (%): " + userMostAnswersCorrect.getNumberAnswersCorrectRatio() + "%");
		System.out.println("User with more incorrect answers: " + userMostAnswersIncorrect.getNumberAnswersIncorrect());
		System.out.println("\tNumber of answers provided: " + userMostAnswersIncorrect.getQuizAndCommentData().size());
		System.out.println("\tProportion (%): " + userMostAnswersIncorrect.getNumberAnswersIncorrectRatio() + "%");
		System.out.println();
		System.out.println();
		System.out.println("User with better ratio (Correct): " + userBestRatioAnswersCorrect.getNumberAnswersCorrectRatio() + " [Correct: " + userBestRatioAnswersCorrect.getNumberAnswersCorrect() + "/Answered:" + userBestRatioAnswersCorrect.getQuizAndCommentData().size() + "]");
		System.out.println("User with worst ratio (Correct): " + userWorstRatioAnswersCorrect.getNumberAnswersCorrectRatio()+ " [Correct: " + userWorstRatioAnswersCorrect.getNumberAnswersCorrect() + "/Answered:" + userWorstRatioAnswersCorrect.getQuizAndCommentData().size() + "]");
	}

	public void loadByUser(String f) throws Exception {
		System.out.println("Loading registries..");
		int numReg = 0;
		BufferedReader bL = new BufferedReader(new FileReader(f));
		while (bL.ready()) {
			String r = bL.readLine();
			if (numReg > 0) {
				String parts[] = r.split("\t");
				String commentID = parts[COMMENT_ID_POSITION];
				String quizID = parts[QUIZ_ID_POSITION];
				String createdDate = parts[CREATED_DATE_POSITION];
				String userID = parts[USER_ID_POSITION];
				String answer = parts[ANSWER_POSITION];
				String correctAnswer = parts[CORRECT_ANSWER_POSITION];
				String howFound = parts[HOW_FOUND_POSITION];
				UserData ud = dataByUsers.get(userID);
				if (ud == null) {
					ud = new UserData(userID);
					ud.addQuizAndCommentData(new QuizAndCommentData(commentID,
							quizID, createdDate, answer.charAt(0),
							correctAnswer.charAt(0), Integer.parseInt(howFound)));
					this.dataByUsers.put(userID, ud);
				}
				ud.addQuizAndCommentData(new QuizAndCommentData(commentID,
						quizID, createdDate, answer.charAt(0), correctAnswer
								.charAt(0), Integer.parseInt(howFound)));
			}
			numReg++;
		}
		bL.close();
		System.out.println("Finished. " + numReg + " registries loaded.");
	}

	class UserData {
		private String id;
		private LinkedList<QuizAndCommentData> quizAndCommentData;
		private int numberAnswersCorrect = 0;
		private int numberAnswersIncorrect = 0;

		public int getNumberAnswersCorrect() {
			return numberAnswersCorrect;
		}

		public int getNumberAnswersIncorrect() {
			return numberAnswersIncorrect;
		}

		public UserData(String id) {
			this.id = id;
			this.quizAndCommentData = new LinkedList<QuizAndCommentData>();
		}

		public float getNumberAnswersIncorrectRatio() {
			return (float)numberAnswersIncorrect/quizAndCommentData.size();
		}
		
		public float getNumberAnswersCorrectRatio() {
			return (float)((float)numberAnswersCorrect/(float)quizAndCommentData.size());
		}
		
		public void addQuizAndCommentData(QuizAndCommentData a) {
			this.quizAndCommentData.add(a);
			if (a.getAnswer() == a.getCorrectAnswer()) {
				numberAnswersCorrect++;
			} else {
				numberAnswersIncorrect++;
			}
		}

		public String getId() {
			return id;
		}

		public LinkedList<QuizAndCommentData> getQuizAndCommentData() {
			return quizAndCommentData;
		}

	}

	class QuizAndCommentData {
		private String commentID;
		private String quizID;
		private String createdDate;
		private char answer;
		private char correctAnswer;
		private int howFound;

		public QuizAndCommentData(String comID, String qID, String date,
				char answer, char correctAnswer, int hf) {
			this.commentID = comID;
			this.quizID = qID;
			this.createdDate = date;
			this.answer = answer;
			this.correctAnswer = correctAnswer;
			this.howFound = hf;
		}

		public String getCommentID() {
			return commentID;
		}

		public String getQuizID() {
			return quizID;
		}

		public String getCreatedDate() {
			return createdDate;
		}

		public char getAnswer() {
			return answer;
		}

		public char getCorrectAnswer() {
			return correctAnswer;
		}

		public int getHowFound() {
			return howFound;
		}

	}
}
