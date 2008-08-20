#!/bin/bash

if [ -x /bin/mixerctl ]; then
	mixerctl -w outputs.mono=$1
	mixerctl -w inputs.dac=$1
elif [ -x /usr/bin/amixer ]; then
	vol=$(($1*100/255))
	amixer -q set PCM $vol
fi