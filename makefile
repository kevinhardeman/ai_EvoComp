COMPILE = javac -cp contest.jar player40.java Elephant.java
RUN = java -jar testrun.jar -submission=player40 -seed=1

start:
	export LD_LIBRARY_PATH=~/AI_UvA/ai_EvoComp/Assignment

evocomp1:
	$(COMPILE) && $(RUN) -evaluation=BentCigarFunction

evocomp2:
	$(COMPILE) && $(RUN) -evaluation=KatsuuraEvaluation

evocomp3:
	$(COMPILE) && $(RUN) -evaluation=SchaffersEvaluation