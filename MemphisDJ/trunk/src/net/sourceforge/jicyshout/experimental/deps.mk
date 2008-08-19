EXPERIMENTAL = $(NSJ)/experimental
# get those source files
EXPERIMENTALSOURCES = $(wildcard $(EXPERIMENTAL)/*.java)
EXPERIMENTALCLASSES = $(EXPERIMENTALSOURCES:.java=.class)
