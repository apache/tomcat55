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

import org.apache.catalina.cluster.ClusterMessage;

/**
 * Session id change cluster message
 * 
 * @author Peter Rossbach
 * 
 * @version $Revision$ $Date$
 */
public class SessionIDMessage implements ClusterMessage {

	private org.apache.catalina.cluster.Member address;

	private int messageNumber;

	private long timestamp;

	private String orignalSessionID;

	private String backupSessionID;

	private String host ;
	private String contextPath;
    private int resend = ClusterMessage.FLAG_DEFAULT ;
    private int compress = ClusterMessage.FLAG_DEFAULT ;

	public org.apache.catalina.cluster.Member getAddress() {
		return address;
	}

	public void setAddress(org.apache.catalina.cluster.Member address) {
		this.address = address;
	}

	public String getUniqueId() {
		StringBuffer result = new StringBuffer(getOrignalSessionID());
		result.append("#-#");
		result.append(getHost());
                result.append("#-#");
                result.append(getContextPath());
		result.append("#-#");
		result.append(getMessageNumber());
		result.append("#-#");
		result.append(System.currentTimeMillis());
		return result.toString();
	}

	/**
         * @return Returns the host.
         */
        public String getHost() {
             return host;
        }
    
        /**
         * @param host The host to set.
         */
         public void setHost(String host) {
             this.host = host;
        }
    
	/**
	 * @return Returns the contextPath.
	 */
	public String getContextPath() {
		return contextPath;
	}
	/**
	 * @param contextPath The contextPath to set.
	 */
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	/**
	 * @return Returns the messageNumber.
	 */
	public int getMessageNumber() {
		return messageNumber;
	}

	/**
	 * @param messageNumber
	 *            The messageNumber to set.
	 */
	public void setMessageNumber(int messageNumber) {
		this.messageNumber = messageNumber;
	}

	/**
	 * @return Returns the timestamp.
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            The timestamp to set.
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return Returns the backupSessionID.
	 */
	public String getBackupSessionID() {
		return backupSessionID;
	}

	/**
	 * @param backupSessionID
	 *            The backupSessionID to set.
	 */
	public void setBackupSessionID(String backupSessionID) {
		this.backupSessionID = backupSessionID;
	}

	/**
	 * @return Returns the orignalSessionID.
	 */
	public String getOrignalSessionID() {
		return orignalSessionID;
	}

	/**
	 * @param orignalSessionID
	 *            The orignalSessionID to set.
	 */
	public void setOrignalSessionID(String orignalSessionID) {
		this.orignalSessionID = orignalSessionID;
	}

    /**
     * @return Returns the compress.
     * @since 5.5.10 
     */
    public int getCompress() {
        return compress;
    }
    /**
     * @param compress The compress to set.
     * @since 5.5.10
     */
    public void setCompress(int compress) {
        this.compress = compress;
    }
    /**
     * @return Returns the resend.
     * @since 5.5.10
     */
    public int getResend() {
        return resend;
    }
    /**
     * @param resend The resend to set.
     * @since 5.5.10
     */
    public void setResend(int resend) {
        this.resend = resend;
    }


}

