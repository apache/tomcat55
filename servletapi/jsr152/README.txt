================================================================================
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
================================================================================

$Id$

                      Java Servlet and JSP API Classes
                      ================================

This subproject contains the compiled code for the implementation classes of
the Java Servlet and JSP APIs (packages javax.servlet, javax.servlet.http,
javax.servlet.jsp, and javax.servlet.jsp.tagext).  It includes the following
contents:


  BUILDING.txt                Instructions for building from sources
  LICENSE                     Apache Software License for this release
  README.txt                  This document
  docs/                       Documentation for this release
      api/                    Javadocs for Servlet and JSP API classes
  lib/                        Binary JAR files for this release
      servlet.jar             Binary Servlet and JSP API classes
  src/                        Sources for Servlet and JSP API classes

In general, you will need to add the "servlet.jar" file (found in the "lib"
subdirectory of this release) into the compilation class path for your
projects that depend on these APIs.

The compiled "servlet.jar" file included in this subproject is automatically
included in binary distributions of Tomcat 4.0, so you need not download this
subproject separately unless you wish to utilize the Javadocs, or peruse the
source code to see how the API classes are implemented.
