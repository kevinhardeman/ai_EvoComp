COMPILE = javac -cp contest.jar player40.java Elephant.java
SUBMISSION = jar cmf MainClass.txt submission.jar player40.class Elephant.class
FLAGS = 
RUN = java $(FLAGS) -jar testrun.jar -submission=player40 -seed=1

start:
	export LD_LIBRARY_PATH=.

startkevin:
	export LD_LIBRARY_PATH=~/Projects/assignment_evocomp/

submit:
	$(COMPILE) && $(SUBMISSION)

1:
	$(RUN) -evaluation=BentCigarFunction

2:
	$(RUN) -evaluation=KatsuuraEvaluation

3:
	$(RUN) -evaluation=SchaffersEvaluation
