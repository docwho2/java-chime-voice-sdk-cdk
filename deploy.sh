#!/bin/bash

ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)


cdk deploy -c accountId=${ACCOUNT_ID} -c stackName=${CDK_STACK_NAME} -c regionEast=${regions[0]} -c regionWest=${regions[1]} --concurrency=3 --all --require-approval=never