#!/bin/bash

DAAPHOST="majoribanks"
DAAPPORT="8080"
MESSAGE="GET /"

echo $MESSAGE | /usr/pkg/sbin/nc $DAAPHOST $DAAPPORT

