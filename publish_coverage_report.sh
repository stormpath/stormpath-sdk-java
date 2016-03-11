#! /bin/bash

echo "Cloning Heroku Coverage Report Repo"
git config --global user.email "evangelists@stormpath.com"
git config --global user.name "stormpath-sdk-java Auto Doc Build"
git clone https://git.heroku.com/afternoon-oasis-83667.git

echo "Updating Heroku app"
cd afternoon-oasis-83667

find ../build -type d -name clover -print

cp -p -r ../build/stormpath/stormpath-sdk-java/clover/target/site/clover/* .
cp index.html home.html

echo "commit and push Heroku app"
git add -A .
git commit -m "update report"
git push origin master

