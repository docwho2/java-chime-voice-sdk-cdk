#!/bin/bash

ACCOUNT_ID=`aws sts get-caller-identity --query Account --output text`

#  Bootstrap both us-east-1 and us-west-2

cdk bootstrap aws://${ACCOUNT_ID}/us-east-1 -c accountId=${ACCOUNT_ID}
cdk bootstrap aws://${ACCOUNT_ID}/us-west-2 -c accountId=${ACCOUNT_ID}