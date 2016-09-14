WORKDIR=$PWD

RED="\e[31m"
GREEN="\e[32m"
YELLOW="\e[33m"
NORMAL="\e[0m"

function error() {
  echo -e "$RED-------> $1 $NORMAL"
}

function info() {
  echo -e "$GREEN-------> $1 $NORMAL"
}

if [ ! -d target ]; then
  mkdir target
fi

