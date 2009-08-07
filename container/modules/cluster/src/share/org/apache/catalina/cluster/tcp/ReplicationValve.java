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

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.cluster.CatalinaCluster;
import org.apache.catalina.cluster.ClusterManager;
import org.apache.catalina.cluster.ClusterMessage;
import org.apache.catalina.cluster.ClusterSession;
import org.apache.catalina.cluster.ClusterValve;
import org.apache.catalina.cluster.session.DeltaManager;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.StringManager;
import org.apache.catalina.valves.ValveBase;

/**
 * <p>Implementation of a Valve that logs interesting contents from the
 * specified Request (before processing) and the corresponding Response
 * (after processing).  It is especially useful in debugging problems
 * related to headers and cookies.</p>
 *
 * <p>This Valve may be attached to any Container, depending on the granularity
 * of the logging you wish to perform.</p>
 *
 * <p>primaryIndicator=true, then the request attribute <i>org.apache.catalina.cluster.tcp.isPrimarySession.</i>
 * is set true, when request processing is at sessions primary node.
 * </p>
 *
 * @author Craig R. McClanahan
 * @author Filip Hanik
 * @author Peter Rossbach
 * @version $Revision$ $Date$
 */

public class ReplicationValve
    extends ValveBase implements ClusterValve {
    
    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( ReplicationValve.class );

    // ----------------------------------------------------- Instance Variables

    /**
     * The descriptive information related to this implementation.
     */
    private static final String info =
        "org.apache.catalina.cluster.tcp.ReplicationValve/1.2";


    /**
     * The StringManager for this package.
     */
    protected static StringManager sm =
        StringManager.getManager(Constants.Package);

    private CatalinaCluster cluster = null ;

    /**
     * holds file endings to not call for like images and others
     */
    protected java.util.regex.Pattern[] reqFilters = new java.util.regex.Pattern[0];
    protected String filter ;

    protected long totalRequestTime=0;
    protected long totalSendTime=0;
    protected long nrOfRequests =0;
    protected long lastSendTime =0;
    protected long nrOfFilterRequests=0;
    protected boolean primaryIndicator = false ;
    protected String primaryIndicatorName = "org.apache.catalina.cluster.tcp.isPrimarySession";
   
    // ------------------------------------------------------------- Properties

    public ReplicationValve() {
    }
    /**
     * Return descriptive information about this Valve implementation.
     */
    public String getInfo() {

        return (info);

    }
    
    /**
     * @return Returns the cluster.
     */
    public CatalinaCluster getCluster() {
        return cluster;
    }
    
    /**
     * @param cluster The cluster to set.
     */
    public void setCluster(CatalinaCluster cluster) {
        this.cluster = cluster;
    }
 
    /**
     * @return Returns the filter
     */
    public String getFilter() {
       return filter ;
    }

    /**
     * compile filter string to regular expressions
     * @see Pattern#compile(java.lang.String)
     * @param filter
     *            The filter to set.
     */
    public void setFilter(String filter) {
        if (log.isDebugEnabled())
            log.debug(sm.getString("ReplicationValve.filter.loading", filter));
        this.filter = filter;
        StringTokenizer t = new StringTokenizer(filter, ";");
        this.reqFilters = new Pattern[t.countTokens()];
        int i = 0;
        while (t.hasMoreTokens()) {
            String s = t.nextToken();
            if (log.isTraceEnabled())
                log.trace(sm.getString("ReplicationValve.filter.token", s));
            try {
                reqFilters[i++] = Pattern.compile(s);
            } catch (Exception x) {
                log.error(sm.getString("ReplicationValve.filter.token.failure",
                        s), x);
            }
        }
    }

    /**
     * @return Returns the primaryIndicator.
     */
    public boolean isPrimaryIndicator() {
        return primaryIndicator;
    }
    /**
     * @param primaryIndicator The primaryIndicator to set.
     */
    public void setPrimaryIndicator(boolean primaryIndicator) {
        this.primaryIndicator = primaryIndicator;
    }
    /**
     * @return Returns the primaryIndicatorName.
     */
    public String getPrimaryIndicatorName() {
        return primaryIndicatorName;
    }
    /**
     * @param primaryIndicatorName The primaryIndicatorName to set.
     */
    public void setPrimaryIndicatorName(String primaryIndicatorName) {
        this.primaryIndicatorName = primaryIndicatorName;
    }
      
    /**
     * @return Returns the lastSendTime.
     */
    public long getLastSendTime() {
        return lastSendTime;
    }
    
    /**
     * @return Returns the nrOfRequests.
     */
    public long getNrOfRequests() {
        return nrOfRequests;
    }
    
    /**
     * @return Returns the nrOfFilterRequests.
     */
    public long getNrOfFilterRequests() {
        return nrOfFilterRequests;
    }

    /**
     * @return Returns the totalRequestTime.
     */
    public long getTotalRequestTime() {
        return totalRequestTime;
    }
    
    /**
     * @return Returns the totalSendTime.
     */
    public long getTotalSendTime() {
        return totalSendTime;
    }

    /**
     * @return Returns the reqFilters.
     */
    protected java.util.regex.Pattern[] getReqFilters() {
        return reqFilters;
    }
    /**
     * @param reqFilters The reqFilters to set.
     */
    protected void setReqFilters(java.util.regex.Pattern[] reqFilters) {
        this.reqFilters = reqFilters;
    }
    
    // --------------------------------------------------------- Public Methods
    

    /**
     * Log the interesting request parameters, invoke the next Valve in the
     * sequence, and log the interesting response parameters.
     *
     * @param request The servlet request to be processed
     * @param response The servlet response to be created
     * @param context The valve context used to invoke the next valve
     *  in the current processing pipeline
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void invoke(Request request, Response response)
        throws IOException, ServletException
    {
        long totalstart = System.currentTimeMillis();
        //this happens before the request
        if (primaryIndicator)
            createPrimaryIndicator( request) ;
        getNext().invoke(request, response);
        //this happens after the request
        long start = System.currentTimeMillis();
        Manager manager = request.getContext().getManager();
        if (manager != null && manager instanceof ClusterManager) {
            ClusterManager clusterManager = (ClusterManager) manager;
            CatalinaCluster cluster = (CatalinaCluster) getContainer()
                    .getCluster();
            if (cluster == null) {
                if (log.isWarnEnabled())
                    log.warn(sm.getString("ReplicationValve.nocluster"));
                return;
            }
            // valve cluster can access manager - other clusterhandle replication 
            // at host level - hopefully!
            if(cluster.getManager(clusterManager.getName()) == null)
                return ;
            if(cluster.getMembers().length > 0  ) {
                try {
                    // send invalid sessions
                    // DeltaManager returns String[0]
                    if (!(clusterManager instanceof DeltaManager))
                        sendInvalidSessions(clusterManager, cluster);
                    // send replication
                    sendSessionReplicationMessage(request, clusterManager, cluster);
                } catch (Exception x) {
                    log.error(sm.getString("ReplicationValve.send.failure"), x);
                } finally {
                    long stop = System.currentTimeMillis();
                    updateStats(stop - totalstart, stop - start);
                }
            }
        }
    }
  
    /**
     * reset the active statitics 
     */
    public void resetStatistics() {
        totalRequestTime = 0 ;
        totalSendTime = 0 ;
        lastSendTime = 0 ;
        nrOfFilterRequests = 0 ;
        nrOfRequests = 0 ;
    }
    
    /**
     * Return a String rendering of this object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("ReplicationValve[");
        if (container != null)
            sb.append(container.getName());
        sb.append("]");
        return (sb.toString());

    }

    // --------------------------------------------------------- Protected Methods

    /**
     * Send Cluster Replication Request
     * @see DeltaManager#requestCompleted(String)
     * @see SimpleTcpCluster#send(ClusterMessage)
     * @param request
     * @param manager
     * @param cluster
     */
    protected void sendSessionReplicationMessage(Request request,
            ClusterManager manager, CatalinaCluster cluster) {
        Session session = request.getSessionInternal(false);
        if (session != null) {
            String uri = request.getDecodedRequestURI();
            // request without session change
            if (!isRequestWithoutSessionChange(uri)) {

                if (log.isDebugEnabled())
                    log.debug(sm.getString("ReplicationValve.invoke.uri", uri));
                String id = session.getIdInternal();
                if (id != null) {
                    ClusterMessage msg = manager.requestCompleted(id);
                    // really send replication send request
                    // FIXME send directly via ClusterManager.send
                    if (msg != null) {
                        if(manager.isSendClusterDomainOnly())
                            cluster.sendClusterDomain(msg);
                        else
                            cluster.send(msg);
                    }
                }
            } else
                nrOfFilterRequests++;
        }

    }
    
    /**
     * check for session invalidations
     * @param manager
     * @param cluster
     */
    protected void sendInvalidSessions(ClusterManager manager, CatalinaCluster cluster) {
        String[] invalidIds=manager.getInvalidatedSessions();
        if ( invalidIds.length > 0 ) {
            for ( int i=0;i<invalidIds.length; i++ ) {
                try {
                    ClusterMessage imsg = manager.requestCompleted(invalidIds[i]);
                    // FIXME send directly via ClusterManager.send
                    if (imsg != null) {
                        if(manager.isSendClusterDomainOnly())
                            cluster.sendClusterDomain(imsg);
                        else
                            cluster.send(imsg);
                    }
                } catch ( Exception x ) {
                    log.error(sm.getString("ReplicationValve.send.invalid.failure",invalidIds[i]),x);
                }
            }
        }
    }
    
    /**
     * is request without possible session change
     * @param request
     * @return
     */
    protected boolean isRequestWithoutSessionChange(String uri) {

        boolean filterfound = false;

        for (int i = 0; (i < reqFilters.length) && (!filterfound); i++) {
            java.util.regex.Matcher matcher = reqFilters[i].matcher(uri);
            filterfound = matcher.matches();
        }
        return filterfound;
    }

    /**
     * protocol cluster replications stats
     * @param requestTime
     * @param clusterTime
     */
    protected synchronized void updateStats(long requestTime, long clusterTime) {
        totalSendTime+=clusterTime;
        totalRequestTime+=requestTime;
        nrOfRequests++;
        if ( (nrOfRequests % 100) == 0 ) {
            if(log.isInfoEnabled()) {
                 log.info(sm.getString("ReplicationValve.stats",
                     new Object[]{
                         new Long(totalRequestTime/nrOfRequests),
                         new Long(totalSendTime/nrOfRequests),
                         new Long(nrOfRequests),
                         new Long(nrOfFilterRequests),
                         new Long(totalRequestTime),
                         new Long(totalSendTime)}));
             }
        }
        lastSendTime=System.currentTimeMillis();
    }


    /**
     * Mark Request that processed at primary node with attribute
     * primaryIndicatorName
     * 
     * @param request
     * @throws IOException
     */
    protected void createPrimaryIndicator(Request request) throws IOException {
        String id = request.getRequestedSessionId();
        if ((id != null) && (id.length() > 0)) {
            Manager manager = request.getContext().getManager();
            Session session = manager.findSession(id);
            if (session instanceof ClusterSession) {
                ClusterSession cses = (ClusterSession) session;
                if (cses != null) {
                    Boolean isPrimary = new Boolean(cses.isPrimarySession());
                    if (log.isDebugEnabled())
                        log.debug(sm.getString(
                                "ReplicationValve.session.indicator", request.getContext().getName(),id,
                                primaryIndicatorName, isPrimary));
                    request.setAttribute(primaryIndicatorName, isPrimary);
                }
            } else {
                if (log.isDebugEnabled()) {
                    if (session != null) {
                        log.debug(sm.getString(
                                "ReplicationValve.session.found", request.getContext().getName(),id));
                    } else {
                        log.debug(sm.getString(
                                "ReplicationValve.session.invalid", request.getContext().getName(),id));
                    }
                }
            }
        }
    }



}
