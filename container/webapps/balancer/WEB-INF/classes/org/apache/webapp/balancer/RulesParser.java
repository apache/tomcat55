/*
 * Copyright 2000,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.webapp.balancer;

import org.apache.tomcat.util.digester.Digester;

import java.io.InputStream;


/**
 * The rules parser uses Digester
 * to parse the rules definition
 * file and return a RuleChain object.
 *
 * @author Yoav Shapira
 */
public class RulesParser {
    /**
     * The resulting rule chain.
     */
    private RuleChain result;

    /**
     * Constructor.
     *
     * @param input To read the configuration
     */
    public RulesParser(InputStream input) {
        try {
            Digester digester = createDigester();
            result = (RuleChain) digester.parse(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the parsed rule chain.
     *
     * @return The resulting RuleChain
     */
    public RuleChain getResult() {
        return result;
    }

    /**
     * Creates the digester instance.
     *
     * @return Digester
     */
    protected Digester createDigester() {
        Digester digester = new Digester();
        digester.setUseContextClassLoader(true);

        String rules = "rules";
        String rule = "/rule";

        // Construct rule chain
        digester.addObjectCreate(rules, RuleChain.class);

        // Construct rule
        digester.addObjectCreate(rules + rule, null, "className");

        // Set rule properties
        digester.addSetProperties(rules + rule);

        // Add rule to chain
        digester.addSetNext(rules + rule, "addRule", "org.apache.webapp.balancer.Rule");

        return digester;
    }
}


// End of class: RulesParser.java
