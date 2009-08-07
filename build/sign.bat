
rem Example script to sign the entire release
rem pass in your password as the first argument, then this script will
rem sign all the files in the release directory

rem todo - make one for unix as well, and avoid signing the .md5 files

@echo off
FOR /R %cd%\release\v5.5.19 %%i in (*.*) do (
  echo Signing %%i
  echo %1|gpg --passphrase-fd 0 -a -b %%i 
)
