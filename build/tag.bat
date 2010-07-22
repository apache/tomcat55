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

@echo off
rem  Expects one argument, the tag name (no quotes needed, e.g. TOMCAT_5_5_13)

rem  All - new layout
svn copy https://svn.apache.org/repos/asf/tomcat/tc5.5.x/trunk https://svn.apache.org/repos/asf/tomcat/tc5.5.x/tags/%1 -m "Tagging Tomcat version %1."

rem  Build
rem svn copy https://svn.apache.org/repos/asf/tomcat/build/tc5.5.x https://svn.apache.org/repos/asf/tomcat/build/tags/tc5.5.x/%1 -m "Tagging Tomcat version %1."

rem  Connectors
rem svn copy https://svn.apache.org/repos/asf/tomcat/connectors/trunk https://svn.apache.org/repos/asf/tomcat/connectors/tags/tc5.5.x/%1 -m "Tagging Tomcat version %1."

rem  Container
rem svn copy https://svn.apache.org/repos/asf/tomcat/container/tc5.5.x https://svn.apache.org/repos/asf/tomcat/container/tags/tc5.5.x/%1 -m "Tagging Tomcat version %1."

rem  Jasper
rem svn copy https://svn.apache.org/repos/asf/tomcat/jasper/tc5.5.x https://svn.apache.org/repos/asf/tomcat/jasper/tags/tc5.5.x/%1 -m "Tagging Tomcat version %1."

rem  ServletAPI
rem svn copy https://svn.apache.org/repos/asf/tomcat/servletapi/servlet2.4-jsp2.0-tc5.x https://svn.apache.org/repos/asf/tomcat/servletapi/tags/servlet2.4-jsp2.0-tc5.x/%1 -m "Tagging Tomcat version %1."

rem  Site
rem svn copy https://svn.apache.org/repos/asf/tomcat/site/trunk https://svn.apache.org/repos/asf/tomcat/site/tags/%1 -m "Tagging Tomcat version %1."