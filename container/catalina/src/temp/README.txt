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

This temp directory is used by the JVM for temporary file storage.
The JVM is configured to use this as its java.io.tmpdir in the
catalina.sh and catalina.bat scripts.  Tomcat is configured to use
this temporary directory rather than its default for security reasons.
The temp directory must exist for Tomcat to work correctly.
