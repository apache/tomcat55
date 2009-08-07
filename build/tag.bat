@echo off
rem  Expects one argument, the tag name (no quotes needed, e.g. TOMCAT_5_5_13)

rem  Build
svn copy https://svn.apache.org/repos/asf/tomcat/build/tc5.5.x https://svn.apache.org/repos/asf/tomcat/build/tags/tc5.5.x/%1 -m "Tagging Tomcat version %1."

rem  Connectors
svn copy https://svn.apache.org/repos/asf/tomcat/connectors/trunk https://svn.apache.org/repos/asf/tomcat/connectors/tags/tc5.5.x/%1 -m "Tagging Tomcat version %1."

rem  Container
svn copy https://svn.apache.org/repos/asf/tomcat/container/tc5.5.x https://svn.apache.org/repos/asf/tomcat/container/tags/tc5.5.x/%1 -m "Tagging Tomcat version %1."

rem  Jasper
svn copy https://svn.apache.org/repos/asf/tomcat/jasper/tc5.5.x https://svn.apache.org/repos/asf/tomcat/jasper/tags/tc5.5.x/%1 -m "Tagging Tomcat version %1."

rem  ServletAPI
svn copy https://svn.apache.org/repos/asf/tomcat/servletapi/servlet2.4-jsp2.0-tc5.x https://svn.apache.org/repos/asf/tomcat/servletapi/tags/servlet2.4-jsp2.0-tc5.x/%1 -m "Tagging Tomcat version %1."

rem  Site
svn copy https://svn.apache.org/repos/asf/tomcat/site/trunk https://svn.apache.org/repos/asf/tomcat/site/tags/%1 -m "Tagging Tomcat version %1."