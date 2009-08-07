@echo off
if "%OS%" == "Windows_NT" setlocal
rem ---------------------------------------------------------------------------
rem NT Service Install/Uninstall script
rem
rem Options
rem install                Install the service using Tomcat5 as service name.
rem                        Service is installed using default settings.
rem remove                 Remove the service from the System.
rem
rem name        (optional) If the second argument is present it is considered
rem                        to be new service name                                           
rem
rem $Id$
rem ---------------------------------------------------------------------------

rem Guess CATALINA_HOME if not defined
set CURRENT_DIR=%cd%
if not "%CATALINA_HOME%" == "" goto gotHome
set CATALINA_HOME=%cd%
if exist "%CATALINA_HOME%\bin\tomcat5.exe" goto okHome
rem CD to the upper dir
cd ..
set CATALINA_HOME=%cd%
:gotHome
if exist "%CATALINA_HOME%\bin\tomcat5.exe" goto okHome
echo The tomcat.exe was not found...
echo The CATALINA_HOME environment variable is not defined correctly.
echo This environment variable is needed to run this program
goto end
rem Make sure prerequisite environment variables are set
if not "%JAVA_HOME%" == "" goto okHome
echo The JAVA_HOME environment variable is not defined
echo This environment variable is needed to run this program
goto end 
:okHome
if not "%CATALINA_BASE%" == "" goto gotBase
set CATALINA_BASE=%CATALINA_HOME%
:gotBase
 
set EXECUTABLE=%CATALINA_HOME%\bin\tomcat5.exe

rem Set default Service name
set SERVICE_NAME=Tomcat5
set PR_DISPLAYNAME=Apache Tomcat

if "%1" == "" goto displayUsage
if "%2" == "" goto setServiceName
set SERVICE_NAME=%2
set PR_DISPLAYNAME=Apache Tomcat %2
:setServiceName
if %1 == install goto doInstall
if %1 == remove goto doRemove
echo Unknown parameter "%1"
:displayUsage
echo 
echo Usage: service.bat install/remove [service_name]
goto end

:doRemove
rem Remove the service
"%EXECUTABLE%" //DS//%SERVICE_NAME%
echo The service '%SERVICE_NAME%' has been removed
goto end

:doInstall
rem Install the service
echo Installing the service '%SERVICE_NAME%' ...
echo Using CATALINA_HOME:    %CATALINA_HOME%
echo Using JAVA_HOME:        %JAVA_HOME%

rem Use the environment variables as an exaple
rem Each command line option is prefixed with PR_

set PR_DESCRIPTION=Apache Tomcat Server - http://jakarta.apache.org/tomcat
set PR_INSTALL=%EXECUTABLE%
set PR_LOGPATH=%CATALINA_HOME%\logs
set PR_CLASSPATH=%JAVA_HOME%\lib\tools.jar;%CATALINA_HOME%\bin\bootstrap.jar
rem Set the server jvm frrom JAVA_HOME
set PR_JVM=%JAVA_HOME%\jre\bin\server\jvm.dll
rem You can use the 'set PR_JVM=auto' for default JVM
"%EXECUTABLE%" //IS//%SERVICE_NAME% --StartClass org.apache.catalina.startup.Bootstrap --StopClass org.apache.catalina.startup.Bootstrap --StartParams start --StopParams stop
rem Clear the environment variables. They are not needed any more.
set PR_DISPLAYNAME=
set PR_DESCRIPTION=
set PR_INSTALL=
set PR_LOGPATH=
set PR_CLASSPATH=
set PR_JVM=
rem Set extra parameters
"%EXECUTABLE%" //US//%SERVICE_NAME% --JvmOptions "-Dcatalina.base=%CATALINA_BASE%;-Dcatalina.home=%CATALINA_HOME%;-Djava.endorsed.dirs=%CATALINA_HOME%\common\endorsed" --StartMode jvm --StopMode jvm
rem More extra parameters
set PR_STDOUTPUT=%CATALINA_HOME%\logs\stdout.log
set PR_STDERROR=%CATALINA_HOME%\logs\stderr.log
"%EXECUTABLE%" //US//%SERVICE_NAME% ++JvmOptions "-Djava.io.tmpdir=%CATALINA_BASE%\temp" --JvmMs 128 --JvmMx 256
echo The service '%SERVICE_NAME%' has been installed.

:end
cd %CURRENT_DIR%
