#!/bin/bash

mixerctl=$(which mixerctl)
amixer=$(which amixer)
if [ "x" != "$mixerctl" ]; then
	$mixerctl -w outputs.mono=$1
	$mixerctl -w inputs.dac=255
elif [ "x" != "$amixer" ]; then
	vol=$(($1*100/255))
	$amixer -q set PCM $vol
fi