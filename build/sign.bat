@echo off
rem Licensed to the Apache Software Foundation (ASF) under one or more
rem contributor license agreements.  See the NOTICE file distributed with
rem this work for additional information regarding copyright ownership.
rem The ASF licenses this file to You under the Apache License, Version 2.0
rem (the "License"); you may not use this file except in compliance with
rem the License.  You may obtain a copy of the License at
rem
rem     http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.


rem Example script to sign the entire release
rem pass in your password as the first argument, then this script will
rem sign all the files in the release directory

@echo off
set VERSION=v5.5.35
FOR /R %cd%\release\%VERSION% %%i in (*.tar.gz) do (
  echo Signing %%i
  echo %1|gpg --passphrase-fd 0 -a -b %%i 
)

FOR /R %cd%\release\%VERSION% %%i in (*.exe) do (
  echo Signing %%i
  echo %1|gpg --passphrase-fd 0 -a -b %%i 
)

FOR /R %cd%\release\%VERSION% %%i in (*.zip) do (
  echo Signing %%i
  echo %1|gpg --passphrase-fd 0 -a -b %%i 
)
