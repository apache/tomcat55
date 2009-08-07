#!/usr/local/bin/perl

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


print <<EOM;
Content-type: text/html

<html>
<head><title>CGI Test</title></head>
<body>
<h1>CGI Test</h1>
<pre>
EOM

print "argc is " . ($#ARGV + 1) . "\n\n";

print "argv is\n";

for($i = 0; $i<($#ARGV+1); $i++) {
	print $ARGV[$i] . "\n";
}

print "\n";
	
foreach $key (sort keys %ENV) {

	print("$key: $ENV{$key}\n");

}

print <<EOM;
</pre>
</body>
</html>
EOM

close OUT;
