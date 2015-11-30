# nejmfb
Project for extracting medical quizzes (and comment identification) from NEJM Facebook page.

The project has been developed using Java as programming language and Eclipse as IDE. You can import the whole
project directly from eclipse.

It uses the following libraries:

- jackson (JSON manipulation): https://github.com/FasterXML/jackson
- simmetrics (Text similarity algorithms): http://sourceforge.net/projects/simmetrics/
- some apache commons (codec, lang3): https://commons.apache.org/

To run the project, there are several Main classes:

- com.alejandrorg.nejmfb.crawler.MainCrawler: This is the main class which runs the crawler against the Facebook page
of NEJM and retrieve all the posts.

There are two execution flags:
	- fromscratch: It runs the process from the scratch.
	- continueifcan: It tries to continue the execution from the last registered moment.
	
This class is in charge of executing the crawler and for hence only retrieves the data and store it in "data" folder.

- com.alejandrorg.nejmfb.mains.MainQuizRetriever: This is the main class to process the data crawled and separate
the posts in "quizzes" and "answer posts". It executes the separator (QuizAndAnswerSeparator),
the retriever (QuizAndAnswerRetriever) and finally the analyzer (QuizAndAnswerCommentAnalyzer) which will analyze
the comments to check the correct and incorrect answers, etc.

This process also execute the StatisticalAnalyzer class, which obtains the main results provided in the paper
and analyze the existing trends within the data.

- com.alejandrorg.nejmfb.mains.MainUserAnalysis: This class performs an analysis of the data based on the users.
- com.alejandrorg.nejmfb.mains.MainEvaluationCommentIdentification: This class performs the evaluation showed in the
paper calculating the values of precision, recall, etc.. for the different strategies.
- com.alejandrorg.nejmfb.mains.MainOtherProcesses: It executes other processes not very relevant for the project. E.g:
it anonimizes the data to be published online.