WORKDIR=$PWD

RED="\e[31m"
GREEN="\e[32m"
YELLOW="\e[33m"
NORMAL="\e[0m"

show_spinner()
{
  local -r pid="${1}"
  local -r delay='1m'
  local spinstr='\|/-'
  local temp
  while ps a | awk '{print $1}' | grep -q "${pid}"; do
    echo "Still running... ¯\_(ツ)_/¯"
    sleep "${delay}"
  done
}

function error() {
  echo -e "$RED-------> $1 $NORMAL"
}

function info() {
  echo -e "$GREEN-------> $1 $NORMAL"
}

if [ ! -d target ]; then
  mkdir target
fi

