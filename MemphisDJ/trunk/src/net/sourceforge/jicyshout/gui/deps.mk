GUI = $(NSJ)/gui
# get those source files
GUISOURCES = $(wildcard $(GUI)/*.java)
GUICLASSES = $(GUISOURCES:.java=.class)
