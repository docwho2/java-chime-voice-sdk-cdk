#!/bin/bash

# Stack names and regions
source config.sh

ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)

if [ "$AWS_EXECUTION_ENV" = "CloudShell" ]; then
    echo "CloudShell Detected, installing Java and CDK"
    # If you come back later, Java could be removed
    sudo yum -y install java-17-amazon-corretto
    # Ensure we are on latest CDK
    sudo npm install -g aws-cdk

    echo "Adding maven to path which should have been installed for CDK"
    export PATH="/home/cloudshell-user/apache-maven-3.9.5/bin:${PATH}"
fi


# Exit immediately if a command exits with a non-zero status.
set -e
cdk destroy -c accountId=${ACCOUNT_ID} -c stackName=${STACK_NAME} --all --force

set +e

# delete things in each region
for region in "${regions[@]}"; do

echo
echo "Deleting Log Groups starting with /aws/lambda/${STACK_NAME} in region ${region}"
declare -a LGS=($(aws logs describe-log-groups --region ${region} --log-group-name-prefix /aws/lambda/${STACK_NAME:0:24} --query logGroups[].logGroupName --output text))
for logGroup in "${LGS[@]}"; do
echo "  Delete Log group [${logGroup}]"
aws logs delete-log-group --region ${region} --log-group-name "${logGroup}" > /dev/null
done

done
