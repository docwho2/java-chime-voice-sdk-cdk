#!/bin/bash

ACCOUNT_ID=`aws sts get-caller-identity --query Account --output text`

cdk synth -c accountId=${ACCOUNT_ID} east
