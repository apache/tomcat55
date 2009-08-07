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

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.cluster.CatalinaCluster;
import org.apache.catalina.cluster.ClusterManager;
import org.apache.catalina.cluster.ClusterMessage;
import org.apache.catalina.cluster.ClusterSession;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.StringManager;
import org.apache.catalina.valves.Constants;
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
    extends ValveBase {
    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( ReplicationValve.class );

    // ----------------------------------------------------- Instance Variables


    /**
     * The descriptive information related to this implementation.
     */
    private static final String info =
        "org.apache.catalina.cluster.tcp.ReplicationValve/1.1";


    /**
     * The StringManager for this package.
     */
    protected static StringManager sm =
        StringManager.getManager(Constants.Package);

    /**
     * holds file endings to not call for like images and others
     */
    protected java.util.regex.Pattern[] reqFilters = new java.util.regex.Pattern[0];
    protected String filter ;

    protected long totalRequestTime=0;
    protected long totalSendTime=0;
    protected long nrOfRequests =0;
    protected long lastSendTime =0;

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

    // --------------------------------------------------------- Public Methods
    
    protected synchronized void addClusterSendTime(long requestTime, long clusterTime) {
        totalSendTime+=clusterTime;
        totalRequestTime+=requestTime;
        nrOfRequests++;
        if ( (nrOfRequests % 100) == 0 ) {
            if(log.isInfoEnabled()) {
                 log.info("Average request time="+(totalRequestTime/nrOfRequests)+" ms for "+
                          "Cluster overhead time="+(totalSendTime/nrOfRequests)+" ms for "+
                           nrOfRequests+" requests (Request="+totalRequestTime+"ms Cluster="+totalSendTime+"ms).");
            }
            lastSendTime=System.currentTimeMillis();
        }//end if
    }


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
        //long _debugstart = System.currentTimeMillis();
        if (primaryIndicator)
            createPrimaryIndicator( request) ;
        getNext().invoke(request, response);
        //System.out.println("[DEBUG] Regular invoke took="+(System.currentTimeMillis()-_debugstart)+" ms.");
        //this happens after the request
        try
        {
            long start = System.currentTimeMillis();
            HttpSession session = request.getSession(false);
            
            if (!( request.getContext().getManager() instanceof ClusterManager) ) return;
            
            ClusterManager manager = (ClusterManager)request.getContext().getManager();
            CatalinaCluster cluster = (CatalinaCluster)getContainer().getCluster();
            if ( cluster == null ) {
               	if(log.isWarnEnabled())
               	    log.warn("No cluster configured for this request.");
                return;
            }
            //first check for session invalidations
            String[] invalidIds=manager.getInvalidatedSessions();
            if ( invalidIds.length > 0 ) {
                for ( int i=0;i<invalidIds.length; i++ ) {
                    try {
                        ClusterMessage imsg = manager.requestCompleted(invalidIds[i]);
                        if (imsg != null)
                            cluster.send(imsg);
                    }catch ( Exception x ) {
                        log.error("Unable to send session invalid message over cluster.",x);
                    }
                }
            }

            String id = null;
            if ( session != null )
                id = session.getId();
            else
                return;

            if ( id == null )
                return;

            if ( (request.getContext().getManager()==null) ||
                 (!(request.getContext().getManager() instanceof ClusterManager)))
                return;



            String uri = request.getDecodedRequestURI();
            boolean filterfound = false;

            for ( int i=0; (i<reqFilters.length) && (!filterfound); i++ )
            {
                java.util.regex.Matcher matcher = reqFilters[i].matcher(uri);
                filterfound = matcher.matches();
            }//for
            if ( filterfound )
                return;

            if(log.isDebugEnabled())
                log.debug("Invoking replication request on "+uri);

            
            ClusterMessage msg = manager.requestCompleted(id);

            if ( msg == null ) return;

            cluster.send(msg);
            long stop = System.currentTimeMillis();
            addClusterSendTime(stop-totalstart,stop-start);

        }catch (Exception x)
        {
            log.error("Unable to perform replication request.",x);
        }
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
                        log.debug("Primarity of session " + id
                                + " in request attribute "
                                + primaryIndicatorName + " is " + isPrimary);
                    request.setAttribute(primaryIndicatorName, isPrimary);
                }
            } else {
                if (log.isDebugEnabled()) {
                    if (session != null) {
                        log.debug("Found session " + id
                                + " but it is not a ClusterSession.");
                    } else {
                        log.debug("Requested session " + id + " is invalid.");
                    }
                }
            }
        }
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

    /**
     * @param filter The filter to set.
     */
    public void setFilter(String filter)
    {
    	if(log.isDebugEnabled())
    	     log.debug("Loading request filters="+filter);
        this.filter = filter ;
        java.util.StringTokenizer t = new java.util.StringTokenizer(filter,";");
        this.reqFilters = new java.util.regex.Pattern[t.countTokens()];
        int i = 0;
        while ( t.hasMoreTokens() )
        {
            String s = t.nextToken();
            if(log.isDebugEnabled())
                 log.debug("Request filter="+s);
            try
            {
                reqFilters[i++] = java.util.regex.Pattern.compile(s);
            }catch ( Exception x )
            {
                log.error("Unable to compile filter "+s,x);
            }
        }
    }

    /**
     * @return Returns the filter
     */
    public String getFilter() {
       return filter ;
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
}
