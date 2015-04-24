package com.alejandrorg.nejmfb.objects;

import java.util.Comparator;
import java.util.Date;

import com.alejandrorg.nejmfb.analysis.objects.QuizAndAnswerAndCommentsResults;
import com.alejandrorg.nejmfb.quizretriever.objects.CommentAndAnswer;
import com.alejandrorg.nejmfb.quizretriever.objects.QuizAndAnswer;
import com.alejandrorg.nejmfb.quizretriever.objects.QuizAndAnswerAsPost;

@SuppressWarnings("rawtypes")
public class DateComparator implements Comparator {

	private Date dp0;
	private Date dp1;

	public int compare(Object arg0, Object arg1) {
		this.dp0 = null;
		this.dp1 = null;

		if (arg0 instanceof Post && arg1 instanceof Post) {
			Post p0 = (Post) arg0;
			Post p1 = (Post) arg1;
			dp0 = p0.getCreatedDate();
			dp1 = p1.getCreatedDate();
		}
		if (arg0 instanceof QuizAndAnswerAsPost
				&& arg1 instanceof QuizAndAnswerAsPost) {
			QuizAndAnswerAsPost qafp0 = (QuizAndAnswerAsPost) arg0;
			QuizAndAnswerAsPost qafp1 = (QuizAndAnswerAsPost) arg1;
			dp0 = qafp0.getQuizPost().getCreatedDate();
			dp1 = qafp1.getQuizPost().getCreatedDate();

		}
		if (arg0 instanceof QuizAndAnswer && arg1 instanceof QuizAndAnswer) {
			QuizAndAnswer qa0 = (QuizAndAnswer) arg0;
			QuizAndAnswer qa1 = (QuizAndAnswer) arg1;
			dp0 = qa0.getQuiz().getOriginalPost().getCreatedDate();
			dp1 = qa1.getQuiz().getOriginalPost().getCreatedDate();
		}

		if (arg0 instanceof Comment && arg1 instanceof Comment) {
			Comment c0 = (Comment) arg0;
			Comment c1 = (Comment) arg1;
			dp0 = c0.getCreatedDate();
			dp1 = c1.getCreatedDate();
		}
		if (arg0 instanceof QuizAndAnswerAndCommentsResults
				&& arg1 instanceof QuizAndAnswerAndCommentsResults) {
			QuizAndAnswerAndCommentsResults qac0 = (QuizAndAnswerAndCommentsResults) arg0;
			QuizAndAnswerAndCommentsResults qac1 = (QuizAndAnswerAndCommentsResults) arg1;
			dp0 = qac0.getQuiz().getOriginalPost().getCreatedDate();
			dp1 = qac1.getQuiz().getOriginalPost().getCreatedDate();

		}
		if (arg0 instanceof CommentAndAnswer
				&& arg1 instanceof CommentAndAnswer) {
			CommentAndAnswer ca0 = (CommentAndAnswer) arg0;
			CommentAndAnswer ca1 = (CommentAndAnswer) arg1;
			dp0 = ca0.getComment().getCreatedDate();
			dp1 = ca1.getComment().getCreatedDate();

		}
		if (dp0 != null && dp1 != null) {
			return dp0.compareTo(dp1);
		}

		return 0;
	}

}
