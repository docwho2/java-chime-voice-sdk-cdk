#!/bin/bash

ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)

if [ "$AWS_EXECUTION_ENV" = "CloudShell" ]; then
    echo "CloudShell Detected, installing Java and Maven dependency"
    # Install needed tools
    sudo yum -y install java-17-amazon-corretto
    # Ensure we are on latest CDK
    sudo npm install -g aws-cdk
    # Install 3.9.5 Maven Version which we know works (yum package is too outdated)
    pushd ".."
    wget https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz
    tar -xvf apache-maven-3.9.5-bin.tar.gz
    rm -f apache-maven-3.9.5-bin.tar.gz
    export PATH="/home/cloudshell-user/apache-maven-3.9.5/bin:${PATH}"
    popd
fi

cdk deploy -c accountId=${ACCOUNT_ID} -c stackName=${CDK_STACK_NAME} -c regionEast=${regions[0]} -c regionWest=${regions[1]} --concurrency=4 --all --require-approval=never
