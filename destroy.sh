#!/bin/bash

# Stack names and regions
source config.sh

ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)

# Exit immediately if a command exits with a non-zero status.
set -e
cdk destroy -c accountId=${ACCOUNT_ID} --all --force

set +e

# delete things in each region
for region in "${regions[@]}"; do

echo
echo "Deleting Log Groups starting with /aws/lambda/${STACK_NAME} in region ${region}"
declare -a LGS=($(aws logs describe-log-groups --region ${region} --log-group-name-prefix /aws/lambda/${STACK_NAME} --query logGroups[].logGroupName --output text))
for logGroup in "${LGS[@]}"; do
echo "  Delete Log group [${logGroup}]"
aws logs delete-log-group --region ${region} --log-group-name "${logGroup}" > /dev/null
done

done
