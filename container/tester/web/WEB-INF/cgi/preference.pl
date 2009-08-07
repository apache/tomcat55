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


$div = 40 / 5 + 3;
$parens = 40 / (5 + 3);
$subadd = 9 - 3 + 5;
$parens_subadd = 9 - (3 + 5);

print "Content-type: text/html\n\n";
print "div is $div, parens is $parens, subadd is $subadd and parens_subadd is $parens_subadd";