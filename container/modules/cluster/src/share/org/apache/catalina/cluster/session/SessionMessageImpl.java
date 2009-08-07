package org.apache.catalina.cluster.session;


import org.apache.catalina.cluster.Member;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class SessionMessageImpl implements SessionMessage, java.io.Serializable {
    public SessionMessageImpl() {
    }
    
    
    /*

     * Private serializable variables to keep the messages state
     */
    private int mEvtType = -1;
    private byte[] mSession;
    private String mSessionID;
    private Member mSrc;
    private String mContextName;
    private long serializationTimestamp;
    private String uniqueId;


    /**
     * Creates a session message. Depending on what event type you want this
     * message to represent, you populate the different parameters in the constructor<BR>
     * The following rules apply dependent on what event type argument you use:<BR>
     * <B>EVT_SESSION_CREATED</B><BR>
     *    The parameters: session, sessionID must be set.<BR>
     * <B>EVT_SESSION_EXPIRED</B><BR>
     *    The parameters: sessionID must be set.<BR>
     * <B>EVT_SESSION_ACCESSED</B><BR>
     *    The parameters: sessionID must be set.<BR>
     * <B>EVT_SESSION_EXPIRED_XXXX</B><BR>
     *    The parameters: sessionID must be set.<BR>
     * <B>EVT_ATTRIBUTE_ADDED</B><BR>
     *    The parameters: sessionID, attrName, attrValue must be set.<BR>
     * <B>EVT_ATTRIBUTE_REMOVED</B><BR>
     *    The parameters: sessionID, attrName must be set.<BR>
     * <B>EVT_SET_USER_PRINCIPAL</B><BR>
     *    The parameters: sessionID, principal<BR>
     * <B>EVT_REMOVE_SESSION_NOTE</B><BR>
     *    The parameters: sessionID, attrName<
     * <B>EVT_SET_SESSION_NOTE</B><BR>
     *    The parameters: sessionID, attrName, attrValue
     * @param eventtype - one of the 8 event type defined in this class
     * @param session - the serialized byte array of the session itself
     * @param sessionID - the id that identifies this session
     * @param attrName - the name of the attribute added/removed
     * @param attrValue - the value of the attribute added

     */
    private SessionMessageImpl( String contextName,
                           int eventtype,
                           byte[] session,
                           String sessionID)
    {
        mEvtType = eventtype;
        mSession = session;
        mSessionID = sessionID;
        mContextName = contextName;
        uniqueId = sessionID;
    }

    public SessionMessageImpl( String contextName,
                           int eventtype,
                           byte[] session,
                           String sessionID,
                           String uniqueID)
    {
        this(contextName,eventtype,session,sessionID);
        uniqueId = uniqueID;
    }

    /**
     * returns the event type
     * @return one of the event types EVT_XXXX
     */
    public int getEventType() { return mEvtType; }
    /**
     * @return the serialized data for the session
     */
    public byte[] getSession() { return mSession;}
    /**
     * @return the session ID for the session
     */
    public String getSessionID(){ return mSessionID; }
    /**
     * @return the name of the attribute
     */
//    public String getAttributeName() { return mAttributeName; }
    /**
     * the value of the attribute
     */
//    public Object getAttributeValue() {return mAttributeValue; }

//    public SerializablePrincipal getPrincipal() { return mPrincipal;}

    public void setTimestamp(long time) {serializationTimestamp=time;}
    public long getTimestamp() { return serializationTimestamp;}
    /**
     * @return the event type in a string representating, useful for debugging
     */
    public String getEventTypeString()
    {
        switch (mEvtType)
        {
            case EVT_SESSION_CREATED : return "SESSION-MODIFIED";
            case EVT_SESSION_EXPIRED : return "SESSION-EXPIRED";
            case EVT_SESSION_ACCESSED : return "SESSION-ACCESSED";
            case EVT_GET_ALL_SESSIONS : return "SESSION-GET-ALL";
            case EVT_SESSION_DELTA : return "SESSION-DELTA";
            case EVT_ALL_SESSION_DATA : return "ALL-SESSION-DATA";
            default : return "UNKNOWN-EVENT-TYPE";
        }
    }

    /**
     * Get the address that this message originated from.  This would be set
     * if the message was being relayed from a host other than the one
     * that originally sent it.
     */
    public Member getAddress()
    {
        return this.mSrc;
    }

    /**
     * Use this method to set the address that this message originated from.
     * This can be used when re-sending the EVT_GET_ALL_SESSIONS message to
     * another machine in the group.
     */
    public void setAddress(Member src)
    {
        this.mSrc = src;
    }

    public String getContextName() {
       return mContextName;
    }
    public String getUniqueId() {
        return uniqueId;
    }
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }


}
