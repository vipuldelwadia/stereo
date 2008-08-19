JICYLIB1 = $(NSJ)/jicylib1
# get those source files
JICYLIB1SOURCES = $(wildcard $(JICYLIB1)/*.java) $(wildcard $(JICYLIB1)/metadata/*.java)
JICYLIB1CLASSES = $(JICYLIB1SOURCES:.java=.class)
