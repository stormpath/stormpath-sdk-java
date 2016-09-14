#! /bin/bash

export S3_BASE="http://stormpath-sdk-java-travis-setup.s3-website-us-east-1.amazonaws.com"
export ENC_TARBALL="stormpath_travis_setup.tar.enc"
export ENC_TARBALL_URL="$S3_BASE/$ENC_TARBALL"
wget $ENC_TARBALL_URL
if [ -e $ENC_TARBALL ]; then echo "$ENC_TARBALL_URL found. Proceeding."; else echo "$ENC_TARBALL_URL not found. Exiting."; exit 1; fi
openssl aes-256-cbc -K $encrypted_0b7f5d43be1f_key -iv $encrypted_0b7f5d43be1f_iv -in $ENC_TARBALL -out stormpath_travis_setup.tar -d
tar xvf stormpath_travis_setup.tar
export STORMPATH_AUTHORS="stormpath_authors.sh"
export STORMPATH_AUTHORS_URL="$S3_BASE/$STORMPATH_AUTHORS"
wget $STORMPATH_AUTHORS_URL
if [ -e $STORMPATH_AUTHORS ]; then echo "$STORMPATH_AUTHORS_URL found. Proceeding."; else echo "$STORMPATH_AUTHORS_URL not found. Exiting."; exit 1; fi
export AUTHOR_EMAIL=$(git show --format="%ae" | head -n 1)
source $STORMPATH_AUTHORS $AUTHOR_EMAIL
test $TRAVIS_BRANCH = master && MIDDLE="/" || MIDDLE="/$AUTHOR/"
export ENC_ENV="stormpath_env.sh.enc"
export ENC_ENV_URL="$S3_BASE$MIDDLE$ENC_ENV"
wget $ENC_ENV_URL
if [ -e $ENC_ENV ]; then echo "$ENC_ENV_URL found. Proceeding."; else echo "$ENC_ENV_URL not found. Exiting."; exit 1; fi
openssl aes-256-cbc -K $ENCRYPT_KEY -iv $ENCRYPT_IV -in $ENC_ENV -out stormpath_env.sh -d
