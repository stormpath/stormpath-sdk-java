#! /bin/bash

#mkdir -p "$HOME/usr/local/aws/bin"
#curl -s "https://s3.amazonaws.com/aws-cli/awscli-bundle.zip" -o "awscli-bundle.zip"
#unzip awscli-bundle.zip
#./awscli-bundle/install -i "$HOME/usr/local/aws" -b "$HOME/usr/local/bin/aws"
pip install --user awscli
export PATH=$PATH:$HOME/.local/bin