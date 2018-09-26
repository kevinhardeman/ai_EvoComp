
COMPILE = javac -cp contest.jar player40.java Elephant.java
SUBMISSION = jar cmf MainClass.txt submission.jar player40.class Elephant.class
RUN = java -jar testrun.jar -submission=player40 -seed=1

start:
	export LD_LIBRARY_PATH=.

startkevin:
	export LD_LIBRARY_PATH=~/Projects/assignment_evocomp/

evocomp1:
	$(COMPILE) && $(SUBMISSION) && $(RUN) -evaluation=BentCigarFunction

evocomp2:
	$(COMPILE) && $(SUBMISSION) && $(RUN) -evaluation=KatsuuraEvaluation

evocomp3:
	$(COMPILE) && $(SUBMISSION) && $(RUN) -evaluation=SchaffersEvaluation
