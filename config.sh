# Common stuff between deploy and destroy scripts

# Stack name
STACK_NAME=chime-cdk-provision


# Regions we will deploy to (the only supported US regions for Chime PSTN SDK)
declare -a regions=( us-east-1 us-west-2)


# Special Vars you can enable that trigger creating more resources in the stacks
# You can use "export VOICE_CONNECTOR=TRUE" for example in shell before running deploy instead of changing here

# Will setup a Voice Connector so you can place SIP calls into SMA
# VOICE_CONNECTOR=TRUE

# Will add this IP address to Voice Connector termination allow list so you can place SIP calls into the SMA (You can always update in Console later as well)
# VOICE_CONNECTOR_ALLOW_IP=162.216.219.185

# If you have an existing phone number in Chime (in unassigned state) create SIP rule pointing number to SMA
# CHIME_PHONE_NUMBER

# If you have an Asterisk for example, create VC, and allow termination and origination to this IP (Implies VOICE_CONNECTOR=true)
# PBX_HOSTNAME=54.0.0.1


# Provision a phone number as part of the stack in the given area code (experimental and not recommended)
# It can take 15 mins to get a number in success state, recommend getting number first, then set CHIME_PHONE_NUMBER
# CHIME_AREA_CODE=612

