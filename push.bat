@echo off
git pull origin master
git commit -am "batch file running"
git push origin master
rem This normally happens when you git commit and try to git push changes before git pulling on that branch x.

rem The normal flow would be as below,

rem STEP 1: git stash your local changes on that branch.

rem STEP 2: git pull origin branch_name -v to pull and merge to locally commited changes on that branch, give the merge some message, and fix conflicts if any.

rem STEP 3: git stash pop the stashed changes.

rem STEP 4: git push origin branch_name -v the merged changes.

rem Replace branch_name with master (for master branch).
