#!/bin/bash
clear
if [ "$EUID" -ne 0 ]
	then echo "Shutting down port 8080" 
		httpport=$(netstat -tnlp | grep 8080 | grep -Po 'LISTEN \K[^/]*')
		kill -9 $httpport
	else
		echo "Executing"
fi
mvn spring-boot:run