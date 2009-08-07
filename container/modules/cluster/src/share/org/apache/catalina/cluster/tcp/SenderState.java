/*
 * Copyright 1999,2004 The Apache Software Foundation.
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

package org.apache.catalina.cluster.tcp;


/**
 * Send cluster messages with a pool of sockets (25).
 * 
 * FIXME support processing stats
 * 
 * @author Filip Hanik
 * @version 1.0
 * @since 5.5.16
 */

public class SenderState {
    
    public static final int READY = 0;
    public static final int SUSPECT = 1;
    public static final int FAILING = 2;
    /**
     * The descriptive information about this implementation.
     */
    private static final String info = "SenderState/1.0";

    // ----------------------------------------------------- Instance Variables

    private int state = READY;

    //  ----------------------------------------------------- Constructor

    
    public SenderState() {
        this(READY);
    }

    public SenderState(int state) {
        this.state = state;
    }
    
    public boolean isSuspect() {
        return state == SUSPECT;
    }

    public void setSuspect() {
        state = SUSPECT;
    }
    
    public boolean isReady() {
        return state == READY;
    }
    
    public void setReady() {
        state = READY;
    }
    
    public boolean isFailing() {
        return state == FAILING;
    }
    
    public void setFailing() {
        state = FAILING;
    }
    

    //  ----------------------------------------------------- Public Properties

}
