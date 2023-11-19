# Common stuff between deploy and destroy scripts

# Stack name for the SMA general deployment
STACK_NAME=chime-sdk-cdk-provisioning


# Regions we will deploy to (the only supported US regions for Chime PSTN SDK)
declare -a regions=( us-east-1 us-west-2)
