package com.alejandrorg.nejmfb.core;

public class Constants {

	public final static int MINIMUM_NUMBER_OF_ANSWER_OPTIONS_TO_CONSIDER_QUIZ = 2;

	public final static int MAX_SIZE_OF_SUBSTRINGS = 5;

	public final static int NUMBER_OF_APPEARANCES_TO_BE_TREND = 5;

	public final static int TYPE_ANSWER_OPTION = 1;
	public final static int TYPE_ANSWER_TEXT = 2;

	public final static String OPTIONS = "OPTIONS";
	public final static String ANSWER = "ANSWER";

	public final static String ID = "ID";
	public final static String CREATED_DATE = "CREATED_DATE";
	public final static String MESSAGE = "MESSAGE";
	public final static String POST_EXTENSION = ".post";
	public final static String LIKES_EXTENSION = ".likes";
	public final static String COMMENTS_EXTENSION = ".comments";
	public final static String SEPARATOR = "/";
	public final static String UNDERSCORE = "_";
	public final static String OUTPUT_FOLDER = "OUTPUT_FOLDER";

	public final static String NUMBER_OF_COMMENTS = "NUMBER_OF_COMMENTS";
	public final static String COMMENT = "COMMENT";

	public final static String USER_ID = "USER_ID";
	public final static String USERNAME = "USERNAME";

	public final static String TOTAL_LIKES = "TOTAL_LIKES";

	public final static String TOKEN = "TOKEN";
	public final static String PAGE_ID = "PAGE_ID";

	public final static String LAST_URL = "LAST_URL";
	public final static String TOTAL_TIME = "TOTAL_TIME";

	public final static String MAXIMUM_NUMBER_OF_RETRIES = "MAXIMUM_NUMBER_OF_RETRIES";
	public final static String LOG_FILE = "logfile.log";

	public final static String MANUAL_QUIZS_OPTIONS_FILE = "MANUAL_QUIZS_OPTIONS_FILE";
	public final static String MANUAL_ANSWERS_FILE = "MANUAL_ANSWERS_FILE";

	public final static String MANUAL_QUIZS = "MANUAL_QUIZS";
	public final static String MANUAL_ANSWERS = "MANUAL_ANSWERS";

	public final static int HOW_FOUND_DIRECT = 1;
	public final static int HOW_FOUND_BY_SEARCH_QUIZ_WITHOUT_ANSWER_BY_URL = 2;
	public final static int HOW_FOUND_BY_SEARCH_ANSWER_WITHOUT_QUIZ_BY_URL = 3;
	public final static int HOW_FOUND_BY_SEARCH_ANSWER_WITHOUT_QUIZ_BY_MESSAGE_LCSS = 4;
	public final static int HOW_FOUND_BY_SEARCH_ANSWER_WITHOUT_QUIZ_BY_MESSAGE_RATIO_SMALLEST_POST_LCSS = 5;
	public final static int HOW_FOUND_BY_SEARCH_ANSWER_WITHOUT_QUIZ_BY_MESSAGE_POSTS_SIMILAR = 6;

	public final static int MIN_WORDS_LONGEST_COMMON_SUBSTRING = 5;
	public final static int RATIO_SMALLEST_POST_LENGTH_LCSS = 3;
	public final static double RATIO_MATCHES_BETWEEN_BOTH_POSTS = 2.5;

	public final static double LEVENSHTEIN_ANSWER_THRESHOLD = 0.6;

	/*
	 * Comment identification constants
	 */

	/*
	 * Direct matching
	 */

	public final static int ANSWER_CORRECT_DIRECT_EQUALS_ANSWER_OPTION = 101;
	public final static int ANSWER_CORRECT_COMMENT_DIRECT_EQUALS_ANSWER_TEXT = 102;
	public final static int ANSWER_CORRECT_FIND_ANSWER_OPTION_AFTER_REMOVE_SYMBOLS = 103;

	public final static int ANSWER_INCORRECT_DIRECT_EQUALS_ANSWER_OPTION = 111;
	public final static int ANSWER_INCORRECT_COMMENT_DIRECT_EQUALS_ANSWER_TEXT = 112;
	public static final int ANSWER_INCORRECT_FIND_ANSWER_OPTION_AFTER_REMOVE_SYMBOLS = 113;

	/*
	 * Correct answer found in text
	 */

	public final static int ANSWER_CORRECT_COMMENT_VALID_ANSWER_WITHOUT_INCORRECT_OPTION = 201;
	public final static int ANSWER_INCORRECT_COMMENT_VALID_ANSWER_WITHOUT_INCORRECT_OPTION = 211;

	/*
	 * Tokenization of white spaces
	 */

	public final static int ANSWER_CORRECT_FIND_ANSWER_OPTION_AFTER_TOKENIZE_WHITE_SPACE = 301;
	public final static int ANSWER_INCORRECT_FIND_ANSWER_OPTION_AFTER_TOKENIZE_WHITE_SPACE = 311;

	/*
	 * Levenshtein
	 */

	public final static int ANSWER_CORRECT_COMMENT_LEVENSHTEIN = 401;
	public final static int ANSWER_CORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_LEVENSHTEIN = 402;

	public static final int ANSWER_INCORRECT_COMMENT_LEVENSHTEIN = 411;
	public static final int ANSWER_INCORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_LEVENSHTEIN = 412;

	/*
	 * Equals by brute force
	 */

	public final static int ANSWER_CORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_DIRECT_EQUALS = 501;
	public final static int ANSWER_INCORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_DIRECT_EQUALS = 511;

	/*
	 * Dismissed
	 */
	public final static int COMMENT_DISMISSED = 999;

	public final static String POSSIBLE_STRINGS_ANSWER[] = { "The answer is",
			"The correct answer is", "The answer to", "Yesterday's quiz",
			"Answer to yesterday" };

	public final static String ANSWER_OPTIONS[] = { "A.", "B.", "C.", "D.",
			"E.", "A)", "B)", "C)", "D)", "E)" };

	public final static String OTHER_POST_STRINGS[] = { "Case Challenge",
			"Now@NEJM", "Case Record" };

	public final static String REMOVE_CHARS[] = { "ª", "!", "·", "$", "&", "/",
			"(", ")", "=", "?", "¿", "^", "*", "¨", "Ç", ";", ":", "_", ",",
			".", "-", "´", "ç", "`", "+", "¡", "'", "\\", "|", "@", "#", "~",
			"€", "¬", "<", ">", "’", "[", "]" };

	public final static String TYPICAL_ANSWER_CHAR_SEPARATORS[] = { ")", ";",
			":", ",", ".", "-", " " };

	public final static String STOP_WORDS[] = { "FOR", "THE", "IS", "AND",
			"WITH", "OF", "A", "TO", "IN", "ANSWER", "QUIZ", "YESTERDAY",
			"YESTERDAYS", "WE", "ASKED" };

	public final static String STOP_WORDS_FOR_QUIZ_OPTIONS[] = { "FOR", "THE",
			"IS", "AND", "WITH", "OF", "TO", "IN", "ANSWER", "QUIZ",
			"YESTERDAY", "YESTERDAYS", "WE", "ASKED" };
	public final static String CORRECT_ANSWER_STRINGS[] = { "The  answer is",
			"The correct is", "The nswer is", "The correct answer is",
			"The answer is", "The answer:" };

	public final static char POSSIBLE_ANSWER_STRING_SEPARATORS[] = { '.', ',',
			')', ':', ';' };

	public static String getHowCommentWasIdentifiedString(int hf) {
		switch (hf) {
		/*
		 * Direct matching
		 */
		case ANSWER_CORRECT_DIRECT_EQUALS_ANSWER_OPTION:
			return "Correct answer found: Comment equals answer option";
		case ANSWER_CORRECT_COMMENT_DIRECT_EQUALS_ANSWER_TEXT:
			return "Correct answer found: Comment equals answer text";
		case ANSWER_CORRECT_FIND_ANSWER_OPTION_AFTER_REMOVE_SYMBOLS:
			return "Correct answer found: Answer option found after remove symbols";
		case ANSWER_INCORRECT_DIRECT_EQUALS_ANSWER_OPTION:
			return "Incorrect answer found: Direct equals";
		case ANSWER_INCORRECT_COMMENT_DIRECT_EQUALS_ANSWER_TEXT:
			return "Incorrect answer found: Comment equals answer text";
		case ANSWER_INCORRECT_FIND_ANSWER_OPTION_AFTER_REMOVE_SYMBOLS:
			return "Incorrect answer found: Answer option found after remove symbols";
			/*
			 * Correct answer found in text
			 */
		case ANSWER_CORRECT_COMMENT_VALID_ANSWER_WITHOUT_INCORRECT_OPTION:
			return "Correct answer found: answer found in text discarding an incorrect option";
		case ANSWER_INCORRECT_COMMENT_VALID_ANSWER_WITHOUT_INCORRECT_OPTION:
			return "Incorrect answer found: answer found in text discarding a correct option";
			/*
			 * Tokenization of white spaces
			 */
		case ANSWER_CORRECT_FIND_ANSWER_OPTION_AFTER_TOKENIZE_WHITE_SPACE:
			return "Correct answer found: Answer option found after tokenize with white spaces";
		case ANSWER_INCORRECT_FIND_ANSWER_OPTION_AFTER_TOKENIZE_WHITE_SPACE:
			return "Incorrect answer found: answer option found after tokenize with white spaces";
			/*
			 * Levenshtein
			 */
		case ANSWER_CORRECT_COMMENT_LEVENSHTEIN:
			return "Correct answer found: by levenshtein distance";
		case ANSWER_CORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_LEVENSHTEIN:
			return "Correct answer found: Brute force reveals levenshtein distance";
		case ANSWER_INCORRECT_COMMENT_LEVENSHTEIN:
			return "Incorrect answer found: Levenshtein detects an incorrect answer";
		case ANSWER_INCORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_LEVENSHTEIN:
			return "Incorrect answer found: Brute force reveals levenshtein distance";
			/*
			 * Equals by brute force
			 */
		case ANSWER_CORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_DIRECT_EQUALS:
			return "Correct answer found: Brute force reveals direct equals";
		case ANSWER_INCORRECT_FIND_ANSWER_TEXT_BY_BRUTE_FORCE_DIRECT_EQUALS:
			return "Incorrect answer found: Brute force reveals direct equals";
			/*
			 * Dismissed
			 */
		case COMMENT_DISMISSED:
			return "Comment discarded";
		}
		return "Found: NA";
	}
}
