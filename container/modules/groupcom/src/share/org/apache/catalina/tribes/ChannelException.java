/*
 * Copyright 1999,2004-2005 The Apache Software Foundation.
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
package org.apache.catalina.tribes;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Channel Exception 
 * @author Filip Hanik
 * @version $Revision: 304032 $, $Date: 2005-07-27 10:11:55 -0500 (Wed, 27 Jul 2005) $
 */

public class ChannelException
    extends Exception {
    private ArrayList faultyMembers=null;
    public ChannelException() {
        super();
    }

    public ChannelException(String message) {
        super(message);
    }

    public ChannelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChannelException(Throwable cause) {
        super(cause);
    }
    
    public String getMessage() {
        StringBuffer buf = new StringBuffer(super.getMessage());
        if (faultyMembers==null || faultyMembers.size() == 0 ) {
            buf.append("; No faulty members identified.");
        } else {
            buf.append("; Faulty members:");
            for ( int i=0; i<faultyMembers.size(); i++ ) {
                Member mbr = (Member)faultyMembers.get(i);
                buf.append(mbr.getName());
                buf.append("; ");
            }
        }
        return buf.toString();
    }
    
    public void addFaultyMember(Member[] mbrs) {
        for (int i=0; mbrs!=null && i<mbrs.length; i++ ) {
            addFaultyMember(mbrs[i]);
        }
    }

    public void addFaultyMember(Member mbr) {
        if ( this.faultyMembers==null ) this.faultyMembers = new ArrayList();
        faultyMembers.add(mbr);
    }

    public void setFaultyMembers(ArrayList faultyMembers) {
        this.faultyMembers = faultyMembers;
    }

    public void setFaultyMembers(Member[] faultyMembers) {
        if ( this.faultyMembers==null ) this.faultyMembers = new ArrayList();
        this.faultyMembers.addAll(Arrays.asList(faultyMembers));
    }

    public Member[] getFaultyMembers() {
        if ( this.faultyMembers==null ) return new Member[0];
        return (Member[])faultyMembers.toArray(new Member[faultyMembers.size()]);
    }

}
