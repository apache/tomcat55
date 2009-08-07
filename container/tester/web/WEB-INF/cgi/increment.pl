#!/usr/bin/perl

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


require "subparseform.lib";
&Parse_Form;
print "Content-type: text/html\n\n";

$counter = $formdata{'counter'};

++$counter;
print "Your number incremented by 1 is $counter";
$watch = ++$counter;
print "<BR>Your number, incremented again by 1, is now $counter. If we store that operation, its value is also $watch.";

$counter = $formdata{'counter'};
print "<HR>Let's start over, with your original number $counter";
$counter++;
print "<BR>Again, your number incremented by 1 is $counter";
$watch = $counter++;
print "<BR>Now we store the value of your number in a second variable, which is now equal to $watch, and then we increment your number again. It's now $counter.";