WORKDIR=$PWD

RED="\e[31m"
GREEN="\e[32m"
NORMAL="\e[0m"

function error() {
  echo -e "$RED-------> $1 $NORMAL"
}

mkdir target

