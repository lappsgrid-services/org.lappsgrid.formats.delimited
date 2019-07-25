#!/usr/bin/env bash

cat <<- EOF 

    GOALS
	
    jar     - builds the jar file
    clean   - removes all build artifactss
    docker  - builds the Docker image
    run     - runs the jar file
    start   - starts a  container
    stop    - stops a running container
    test    - runs the LSD integration test
    push    - pushes the latest image to docker.lappsgrid.org
    tag     - tags the latest image with the current VERSION 
    all     - build the jar, images, and does a push
    update  - calls Portainer's update web hook
	
EOF
