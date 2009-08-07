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


package org.apache.catalina.cluster.session;

/**
 * This class is used to track the series of actions that happens when
 * a request is executed. These actions will then translate into invokations of methods 
 * on the actual session.
 * This class is NOT thread safe. One DeltaRequest per session
 * @author <a href="mailto:fhanik@apache.org">Filip Hanik</a>
 * @version 1.0
 */

import java.util.LinkedList;
import java.io.Externalizable;
import java.security.Principal;
import org.apache.catalina.realm.GenericPrincipal;


public class DeltaRequest implements Externalizable {

    public static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( DeltaRequest.class );

    public static final int TYPE_ATTRIBUTE = 0;
    public static final int TYPE_PRINCIPAL = 1;
    public static final int TYPE_ISNEW = 2;
    public static final int TYPE_MAXINTERVAL = 3;

    public static final int ACTION_SET = 0;
    public static final int ACTION_REMOVE = 1;

    public static final String NAME_PRINCIPAL = "__SET__PRINCIPAL__";
    public static final String NAME_MAXINTERVAL = "__SET__MAXINTERVAL__";
    public static final String NAME_ISNEW = "__SET__ISNEW__";

    private String sessionId;
    private LinkedList actions = new LinkedList();
    private LinkedList actionPool = new LinkedList();
    
    private boolean recordAllActions = false;

    public DeltaRequest() {
        
    }
    
    public DeltaRequest(String sessionId, boolean recordAllActions) {
        this.recordAllActions=recordAllActions;
        setSessionId(sessionId);
    }


    public void setAttribute(String name, Object value) {
        int action = (value==null)?ACTION_REMOVE:ACTION_SET;
        addAction(TYPE_ATTRIBUTE,action,name,value);
    }

    public void removeAttribute(String name) {
        int action = ACTION_REMOVE;
        addAction(TYPE_ATTRIBUTE,action,name,null);
    }

    public void setMaxInactiveInterval(int interval) {
        int action = ACTION_SET;
        addAction(TYPE_MAXINTERVAL,action,NAME_MAXINTERVAL,new Integer(interval));
    }

    public void setPrincipal(Principal p) {
        int action = (p==null)?ACTION_REMOVE:ACTION_SET;
        SerializablePrincipal sp = null;
        if ( p != null ) {
            sp = SerializablePrincipal.createPrincipal((GenericPrincipal)p);
        }
        addAction(TYPE_PRINCIPAL,action,NAME_PRINCIPAL,sp);
    }

    public void setNew(boolean n) {
        int action = ACTION_SET;
        addAction(TYPE_ISNEW,action,NAME_ISNEW,new Boolean(n));
    }

    protected synchronized void addAction(int type,
                             int action,
                             String name,
                             Object value) {
        AttributeInfo info = null;
        if ( this.actionPool.size() > 0 ) {
            try {
                info = (AttributeInfo) actionPool.removeFirst();
            }catch ( Exception x ) {
                log.error("Unable to remove element:",x);
                info = new AttributeInfo(type, action, name, value);
            }
            info.init(type,action,name,value);
        } else {
            info = new AttributeInfo(type, action, name, value);
        }
        //if we have already done something to this attribute, make sure
        //we don't send multiple actions across the wire
        if ( !recordAllActions) {
            try {
                actions.remove(info);
            } catch (java.util.NoSuchElementException x) {
                //do nothing, we wanted to remove it anyway
            }
        }
        //add the action
        actions.addLast(info);
    }
    
    public void execute(DeltaSession session) {
        execute(session,true);
    }

    public synchronized void execute(DeltaSession session, boolean notifyListeners) {
        if ( !this.sessionId.equals( session.getId() ) )
            throw new java.lang.IllegalArgumentException("Session id mismatch, not executing the delta request");
        session.access();
        for ( int i=0; i<actions.size(); i++ ) {
            AttributeInfo info = (AttributeInfo)actions.get(i);
            switch ( info.getType() ) {
                case TYPE_ATTRIBUTE: {
                    if ( info.getAction() == ACTION_SET ) {
                        session.setAttribute(info.getName(), info.getValue(),notifyListeners,false);
                    }  else
                        session.removeAttribute(info.getName(),notifyListeners,false);
                    break;
                }//case
                case TYPE_ISNEW: {
                    session.setNew(((Boolean)info.getValue()).booleanValue(),false);
                    break;
                }//case
                case TYPE_MAXINTERVAL: {
                    session.setMaxInactiveInterval(((Integer)info.getValue()).intValue(),false);
                    break;
                }//case
                case TYPE_PRINCIPAL: {
                    Principal p = null;
                    if ( info.getAction() == ACTION_SET ) {
                        SerializablePrincipal sp = (SerializablePrincipal)info.getValue();
                        p = (Principal)sp.getPrincipal(session.getManager().getContainer().getRealm());
                    }
                    session.setPrincipal(p,false);
                    break;
                }//case
                default : throw new java.lang.IllegalArgumentException("Invalid attribute info type="+info);
            }//switch
        }//for
        session.endAccess();
    }

    public synchronized void reset() {
        while ( actions.size() > 0 ) {
            try {
                AttributeInfo info = (AttributeInfo) actions.removeFirst();
                info.recycle();
                actionPool.addLast(info);
            }catch  ( Exception x ) {
                log.error("Unable to remove element",x);
            }
        }
        actions.clear();
    }
    
    public String getSessionId() {
        return sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
        if ( sessionId == null ) {
            new Exception("Session Id is null for setSessionId").fillInStackTrace().printStackTrace();
        }
    }
    public int getSize() {
        return actions.size();
    }
    
    public synchronized void clear() {
        actions.clear();
        actionPool.clear();
    }
    
    public synchronized void readExternal(java.io.ObjectInput in) throws java.io.IOException,
        java.lang.ClassNotFoundException {
        //sessionId - String
        //recordAll - boolean
        //size - int
        //AttributeInfo - in an array
        reset();
        sessionId = in.readUTF();
        recordAllActions = in.readBoolean();
        int cnt = in.readInt();
        if (actions == null)
            actions = new LinkedList();
        else
            actions.clear();
        for (int i = 0; i < cnt; i++) {
            AttributeInfo info = null;
            if (this.actionPool.size() > 0) {
                try {
                    info = (AttributeInfo) actionPool.removeFirst();
                } catch ( Exception x )  {
                    log.error("Unable to remove element",x);
                    info = new AttributeInfo(-1,-1,null,null);
                }
            }
            else {
                info = new AttributeInfo(-1,-1,null,null);
            }
            info.readExternal(in);
            actions.addLast(info);
        }//for
    }
        


    public synchronized void writeExternal(java.io.ObjectOutput out ) throws java.io.IOException {
        //sessionId - String
        //recordAll - boolean
        //size - int
        //AttributeInfo - in an array
        out.writeUTF(getSessionId());
        out.writeBoolean(recordAllActions);
        out.writeInt(getSize());
        for ( int i=0; i<getSize(); i++ ) {
            AttributeInfo info = (AttributeInfo)actions.get(i);
            info.writeExternal(out);
        }
    }
    
    private static class AttributeInfo implements java.io.Externalizable {
        private String name = null;
        private Object value = null;
        private int action;
        private int type;

        public AttributeInfo() {}

        public AttributeInfo(int type,
                             int action,
                             String name,
                             Object value) {
            super();
            init(type,action,name,value);
        }

        public void init(int type,
                         int action,
                         String name,
                         Object value) {
            this.name = name;
            this.value = value;
            this.action = action;
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public int getAction() {
            return action;
        }

        public Object getValue() {
            return value;
        }
        public int hashCode() {
            return name.hashCode();
        }

        public String getName() {
            return name;
        }
        
        public void recycle() {
            name = null;
            value = null;
            type=-1;
            action=-1;
        }

        public boolean equals(Object o) {
            if ( ! (o instanceof AttributeInfo ) ) return false;
            AttributeInfo other =  (AttributeInfo)o;
            return other.getName().equals(this.getName());
        }
        
        public synchronized void readExternal(java.io.ObjectInput in ) throws java.io.IOException,
            java.lang.ClassNotFoundException {
            //type - int
            //action - int
            //name - String
            //value - object
            type = in.readInt();
            action = in.readInt();
            name = in.readUTF();
            value = in.readObject();
        }

        public synchronized void writeExternal(java.io.ObjectOutput out) throws java.io.
            IOException {
            //type - int
            //action - int
            //name - String
            //value - object
            out.writeInt(getType());
            out.writeInt(getAction());
            out.writeUTF(getName());
            out.writeObject(getValue());
        }
        
        public String toString() {
            StringBuffer buf = new StringBuffer("AttributeInfo[type=");
            buf.append(getType()).append(", action=").append(getAction());
            buf.append(", name=").append(getName()).append(", value=").append(getValue());
            buf.append(", addr=").append(super.toString()).append("]");
            return buf.toString();
        }

    }

}
