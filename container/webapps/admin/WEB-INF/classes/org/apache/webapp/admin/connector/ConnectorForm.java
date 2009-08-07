/*
 * Copyright 2001,2004 The Apache Software Foundation.
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


package org.apache.webapp.admin.connector;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import java.net.InetAddress;
import java.util.List;

/**
 * Form bean for the connector page.
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public final class ConnectorForm extends ActionForm {
    
    // ----------------------------------------------------- Instance Variables
    
     /**
     * The administrative action represented by this form.
     */
    private String adminAction = "Edit";

    /**
     * The object name of the Connector this bean refers to.
     */
    private String objectName = null;
    
    /**
     * The object name of the service this connector belongs to.
     */
    private String serviceName = null;
   
    /**
     * The text for the scheme.
     */
    private String scheme = null;

    /**
     * The text for the connector type. 
     * Specifies if it is a CoyoteConnector or AJP13Connector etc.
     */
    private String connectorType = null;    
    
     /**
     * The text for the node label.
     */
    private String nodeLabel = null;
    
    /**
     * The text for the accept Count.
     */
    private String acceptCountText = null;
    
    /**
     * The text for the algorithm.
     */
    private String algorithm = null;
    
    /**
     * The text for the ciphers.
     */
    private String ciphers = null;
    
    /**
     * The text for the Connection Linger.
     */
    private String connLingerText = null;
    
    /**
     * The text for the Connection Time Out.
     */
    private String connTimeOutText = null;
    
    /**
     * The text for the Connection Upload Time Out.
     */
    private String connUploadTimeOutText = null;
    
    /**
     * The text for the buffer size.
     */
    private String bufferSizeText = null;
    
    /**
     * The value of disable upload timeout.
     */
    private String disableUploadTimeout = "false";
    
    /**
     * The value of enable Lookups.
     */
    private String enableLookups = "false";
    
    /**
     * The value of compression.
     */
    private String compression = "off";
    
    /**
     * The text for the address.
     */
    private String address = null;
    
    /**
     * The text for the minProcessors.
     */
    private String minProcessorsText = null;
    
    /**
     * The text for the max Processors.
     */
    private String maxProcessorsText = null;
    
    /**
     * The text for the maxKeepAlive.
     */
    private String maxKeepAliveText = null;
    
    /**
     * The text for the maxSpare.
     */
    private String maxSpare = null;
    
    /**
     * The text for the maxThreads.
     */
    private String maxThreads = null;
    
    /**
     * The text for the minSpare.
     */
    private String minSpare = null;

    /**
     * The text for the threadPriority.
     */
    private String threadPriority = null;
    
    /**
     * The text for the URIEncoding.
     */
    private String uriEncodingText = null;
    
    /**
     * The value of useBodyEncodingForURI.
     */
    private String useBodyEncodingForURI = "false";

    /**
     * The value of allowTrace.
     */
    private String allowTrace = "false";

    /**
     * The text for the port.
     */
    private String portText = null;
    
    /**
     * The text for the redirect port.
     */
    private String redirectPortText = null;
    
    /**
     * The text for the proxyName.
     */
    private String proxyName = null;
    
    /**
     * The text for the proxy Port Number.
     */
    private String proxyPortText = null;
    
    
    /**
     * The text for the connectorName.
     */
    private String connectorName = null;
        
    /**
     * Whether client authentication is supported.
     */
    private String clientAuthentication = "false";
        
    /**
     * The keyStore Filename.
     */
    private String keyStoreFileName = null;
        
    /**
     * The keyStore Password.
     */
    private String keyStorePassword = null;
    
    /**
     * The keyStore Type.
     */
    private String keyStoreType = null;

    /**
     * The text for the Ssl Protocol.
     */
    private String sslProtocol= null;
    
    /*
     * Represent boolean (true, false) values for enableLookups etc.
     */    
    private List booleanVals = null;

    /*
     * Represent supported connector types.
     */    
    private List connectorTypeVals = null;

    /**
     * Represent supported clientAuth values.
     */
    private List clientAuthVals = null;

    /**
     * The value of secure.
     */
    private String secure = "false";
    /**
     * The value of tcpNoDelay.
     */
    private String tcpNoDelay = "true";
    
    /**
     * The value of xpoweredBy.
     */
    private String xpoweredBy = "false";
    
    // ------------------------------------------------------------- Properties
    
   /**
     * Return the administrative action represented by this form.
     */
    public String getAdminAction() {

        return this.adminAction;

    }


    /**
     * Set the administrative action represented by this form.
     */
    public void setAdminAction(String adminAction) {

        this.adminAction = adminAction;

    }

    /**
     * Return the object name of the Connector this bean refers to.
     */
    public String getObjectName() {

        return this.objectName;

    }


    /**
     * Set the object name of the Connector this bean refers to.
     */
    public void setObjectName(String objectName) {

        this.objectName = objectName;

    }
    
      /**
     * Return the object name of the service this connector belongs to.
     */
    public String getServiceName() {

        return this.serviceName;

    }


    /**
     * Set the object name of the Service this connector belongs to.
     */
    public void setServiceName(String serviceName) {

        this.serviceName = serviceName;

    }
    
    /**
     * Return the Scheme.
     */
    public String getScheme() {
        
        return this.scheme;
        
    }
    
    /**
     * Set the Scheme.
     */
    public void setScheme(String scheme) {
        
        this.scheme = scheme;
        
    }
    
    /**
     * Return the Connector type.
     */
    public String getConnectorType() {
        
        return this.connectorType;
        
    }
    
    /**
     * Set the Connector type.
     */
    public void setConnectorType(String connectorType) {
        
        this.connectorType = connectorType;
        
    }
    
    /**
     * Return the label of the node that was clicked.
     */
    public String getNodeLabel() {
        
        return this.nodeLabel;
        
    }
    
    /**
     * Set the node label.
     */
    public void setNodeLabel(String nodeLabel) {
        
        this.nodeLabel = nodeLabel;
        
    }
    
    /**
     * Return the acceptCountText.
     */
    public String getAcceptCountText() {
        
        return this.acceptCountText;
        
    }
    
    
    /**
     * Set the acceptCountText.
     */
    
    public void setAcceptCountText(String acceptCountText) {
        
        this.acceptCountText = acceptCountText;
        
    }
    
    /**
     * Return the algorithm.
     */
    public String getAlgorithm() {
        
        return this.algorithm;
        
    }
    
    
    /**
     * Set the algorithm.
     */
    
    public void setAlgorithm(String algorithm) {
        
        this.algorithm = algorithm;
        
    }
    
    /**
     * Return the ciphers.
     */
    public String getCiphers() {
        
        return this.ciphers;
        
    }
    
    /**
     * Set the ciphers.
     */
    
    public void setCiphers(String ciphers) {
        
        this.ciphers = ciphers;
        
    }
    
    /**
     * Return the connLingerText.
     */
    public String getConnLingerText() {
        
        return this.connLingerText;
        
    }
    
    /**
     * Set the connLingerText.
     */
    
    public void setConnLingerText(String connLingerText) {
        
        this.connLingerText = connLingerText;
        
    }
    
    /**
     * Return the connTimeOutText.
     */
    public String getConnTimeOutText() {
        
        return this.connTimeOutText;
        
    }
    
    /**
     * Set the connTimeOutText.
     */
    
    public void setConnTimeOutText(String connTimeOutText) {
        
        this.connTimeOutText = connTimeOutText;
        
    }
       
    /**
     * Return the connUploadTimeOutText.
     */
    public String getConnUploadTimeOutText() {
        
        return this.connUploadTimeOutText;
        
    }
    
    /**
     * Set the connUploadTimeOutText.
     */
    
    public void setConnUploadTimeOutText(String connUploadTimeOutText) {
        
        this.connUploadTimeOutText = connUploadTimeOutText;
        
    }
    /**
     * Return the bufferSizeText.
     */
    public String getBufferSizeText() {
        
        return this.bufferSizeText;
        
    }
    
    /**
     * Set the bufferSizeText.
     */
    
    public void setBufferSizeText(String bufferSizeText) {
        
        this.bufferSizeText = bufferSizeText;
        
    }
    
    /**
     * Return the address.
     */
    public String getAddress() {
        
        return this.address;
        
    }
    
    /**
     * Set the address.
     */
    
    public void setAddress(String address) {
        
        this.address = address;
        
    }
    
    
    /**
     * Return the proxy Name.
     */
    public String getProxyName() {
        
        return this.proxyName;
        
    }
    
    /**
     * Set the proxy Name.
     */
    
    public void setProxyName(String proxyName) {
        
        this.proxyName = proxyName;
        
    }
    
    /**
     * Return the proxy Port NumberText.
     */
    public String getProxyPortText() {
        
        return this.proxyPortText;
        
    }
    
    /**
     * Set the proxy Port NumberText.
     */
    
    public void setProxyPortText(String proxyPortText) {
        
        this.proxyPortText = proxyPortText;
        
    }

   /**
     * Return the true/false value of client authentication.
     */
    public String getClientAuthentication() {

        return this.clientAuthentication;

    }


    /**
     * Set whether client authentication is supported or not.
     */
    public void setClientAuthentication(String clientAuthentication) {

        this.clientAuthentication = clientAuthentication;

    }

    /**
     * Return the object name of the service this connector belongs to.
     */
    public String getKeyStoreFileName() {

        return this.keyStoreFileName;

    }


    /**
     * Set the object name of the Service this connector belongs to.
     */
    public void setKeyStoreFileName(String keyStoreFileName) {

        this.keyStoreFileName = keyStoreFileName;

    }

    /**
     * Return the object name of the service this connector belongs to.
     */
    public String getKeyStorePassword() {

        return this.keyStorePassword;

    }


    /**
     * Set the object name of the Service this connector belongs to.
     */
    public void setKeyStorePassword(String keyStorePassword) {

        this.keyStorePassword = keyStorePassword;

    }

    /**
     * Return the keystore type.
     */
    public String getKeyStoreType() {

        return this.keyStoreType;

    }


    /**
     * Set the keystore type.
     */
    public void setKeyStoreType(String keyStoreType) {

        this.keyStoreType = keyStoreType;

    }
    /**
     * Return the sslProtocol
     */
    public String getSslProtocol() {

        return this.sslProtocol;

    }


    /**
     * Set the sslProtocol.
     */
    public void setSslProtocol(String sslProtocol) {

        this.sslProtocol = sslProtocol;

    }
    
    /**
     * Return the Enable lookup Text.
     */
    
    public String getEnableLookups() {
        
        return this.enableLookups;
        
    }
    
    /**
     * Set the Enable Lookup Text.
     */
    public void setEnableLookups(String enableLookups) {
        
        this.enableLookups = enableLookups;
        
    }
    
    /**
     * Return the disableUploadTimeout.
     */
    
    public String getDisableUploadTimeout() {
        
        return this.disableUploadTimeout;
        
    }
    
    /**
     * Set the disableUploadTimeout.
     */
    public void setDisableUploadTimeout(String disableUploadTimeout) {
        
        this.disableUploadTimeout = disableUploadTimeout;
        
    }
    
    /**
     * Return the compression Text.
     */
    
    public String getCompression() {
        
        return this.compression;
        
    }
    
    /**
     * Set the Compression Text.
     */
    public void setCompression(String compression) {
        
        this.compression = compression;
        
    }
    
    /**
     * Return the booleanVals.
     */
    public List getBooleanVals() {
        
        return this.booleanVals;
        
    }
    
    /**
     * Set the debugVals.
     */
    public void setBooleanVals(List booleanVals) {
        
        this.booleanVals = booleanVals;
        
    }

    /**
     * Return the clientAuth values.
     */
    public List getClientAuthVals() {
        return clientAuthVals;
    }
    /**
     * Set the clientAuth vaues.
     */
    public void setClientAuthVals(List clientAuthVals) {
        this.clientAuthVals = clientAuthVals;
    }
    
    /**
     * Return the min Processors Text.
     */
    public String getMinProcessorsText() {
        
        return this.minProcessorsText;
        
    }
    
    /**
     * Set the minProcessors Text.
     */
    public void setMinProcessorsText(String minProcessorsText) {
        
        this.minProcessorsText = minProcessorsText;
        
    }
    
    /**
     * Return the max processors Text.
     */
    public String getMaxProcessorsText() {
        
        return this.maxProcessorsText;
        
    }
    
    /**
     * Set the Max Processors Text.
     */
    public void setMaxProcessorsText(String maxProcessorsText) {
        
        this.maxProcessorsText = maxProcessorsText;
        
    }
    
    /**
     * Return the maxKeepAliveText.
     */
    public String getMaxKeepAliveText() {
        
        return this.maxKeepAliveText;
        
    }
    
    /**
     * Set the maxKeepAliveText.
     */
    
    public void setMaxKeepAliveText(String maxKeepAliveText) {
        
        this.maxKeepAliveText = maxKeepAliveText;
        
    }
    
    /**
     * Return the maxSpare.
     */
    public String getMaxSpare() {
        
        return this.maxSpare;
        
    }
    
    /**
     * Set the maxSpare.
     */
    
    public void setMaxSpare(String maxSpare) {
        
        this.maxSpare = maxSpare;
        
    } 
    
    /**
     * Return the maxThreads.
     */
    public String getMaxThreads() {
        
        return this.maxThreads;
        
    }
    
    /**
     * Set the maxThreads.
     */
    
    public void setMaxThreads(String maxThreads) {
        
        this.maxThreads = maxThreads;
        
    } 
    
    /**
     * Return the minSpare.
     */
    public String getMinSpare() {
        
        return this.minSpare;
        
    }
    
    /**
     * Set the minSpare.
     */
    
    public void setMinSpare(String minSpare) {
        
        this.minSpare = minSpare;
        
    }

    /**
     * Return the threadPriority.
     */
    public String getThreadPriority() {

      return this.threadPriority;

    }

    /**
     * Set the threadPriority.
     */
    
    public void setThreadPriority(String threadPriority) {
      
      this.threadPriority = threadPriority;
    
    }
    
    /**
     * Return the URIEncoding text.
     */
    public String getURIEncodingText() {
        
        return this.uriEncodingText;
        
    }
    
    /**
     * Set the URIEncoding Text.
     */
    public void setURIEncodingText(String uriEncodingText) {
        
        this.uriEncodingText = uriEncodingText;
        
    }
    
    /**
     * Return the useBodyEncodingForURI Text.
     */
    public String getUseBodyEncodingForURIText() {
        
        return this.useBodyEncodingForURI;
        
    }
    
    /**
     * Set the useBodyEncodingForURI Text.
     */
    public void setUseBodyEncodingForURIText(String useBodyEncodingForURI) {
        
        this.useBodyEncodingForURI = useBodyEncodingForURI;
        
    }    
    
    /**
     * Return the allowTrace Text.
     */
    public String getAllowTraceText() {
        
        return this.allowTrace;
        
    }
    
    /**
     * Set the allowTrace Text.
     */
    public void setAllowTraceText(String allowTrace) {
        
        this.allowTrace = allowTrace;
        
    }    
    
    /**
     * Return the port text.
     */
    public String getPortText() {
        
        return this.portText;
        
    }
    
    /**
     * Set the port Text.
     */
    public void setPortText(String portText) {
        
        this.portText = portText;
        
    }
    
    
    /**
     * Return the port.
     */
    public String getRedirectPortText() {
        
        return this.redirectPortText;
        
    }
    
    /**
     * Set the Redirect Port Text.
     */
    public void setRedirectPortText(String redirectPortText) {
        
        this.redirectPortText = redirectPortText;
        
    }
    
    /**
     * Return the Service Name.
     */
    public String getConnectorName() {
        
        return this.connectorName;
        
    }
    
    /**
     * Set the Service Name.
     */
    public void setConnectorName(String connectorName) {
        
        this.connectorName = connectorName;
        
    }
    
    /**
     * Return the connectorTypeVals.
     */
    public List getConnectorTypeVals() {
        
        return this.connectorTypeVals;
        
    }
    
    /**
     * Set the connectorTypeVals.
     */
    public void setConnectorTypeVals(List connectorTypeVals) {
        
        this.connectorTypeVals = connectorTypeVals;
        
    }
    
     /**
     * Return the secure Text.
     */
    public String getSecure() {
        
        return this.secure;
        
    }
    
    /**
     * Set the secure Text.
     */
    public void setSecure(String secure) {
        
        this.secure = secure;
        
    }    
    
    /**
     * Return the tcpNoDelay Text.
     */
    public String getTcpNoDelay() {
        
        return this.tcpNoDelay;
        
    }
    
    /**
     * Set the tcpNoDelay Text.
     */
    public void setTcpNoDelay(String tcpNoDelay) {
        
        this.tcpNoDelay = tcpNoDelay;
        
    }   
    
    /**
     * Return the xpoweredBy Text.
     */
    public String getXpoweredBy() {
        
        return this.xpoweredBy;
        
    }
    
    /**
     * Set the xpoweredBy Text.
     */
    public void setXpoweredBy(String xpoweredBy) {
        
        this.xpoweredBy = xpoweredBy;
        
    }
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
    
        this.objectName = null;
        this.connectorType = null;
        this.portText = null;
        this.acceptCountText = null;
        this.connLingerText = null;
        this.connTimeOutText = null;
        this.connUploadTimeOutText = null;
        this.bufferSizeText = null;
        this.address = null;
        this.enableLookups = "false";
        this.compression = "off";
        this.minProcessorsText = null;
        this.maxProcessorsText = null;
        this.maxKeepAliveText = null;
        this.maxSpare = null;
        this.maxThreads = null;
        this.minSpare = null;
        this.threadPriority = null;
        this.uriEncodingText = null;
        this.useBodyEncodingForURI = "false";
        this.allowTrace = "false";
        this.portText = null;
        this.redirectPortText = null;
        this.proxyName = null;
        this.proxyPortText = null;
        this.keyStoreFileName = null;
        this.keyStorePassword = null;        
        this.clientAuthentication = "false";
        this.secure = "false";
        this.tcpNoDelay = "false";
        this.xpoweredBy = "false";
        
    }
    
    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionErrors</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionErrors</code> object with no
     * recorded error messages.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    
    private ActionErrors errors;
    
    public ActionErrors validate(ActionMapping mapping,
    HttpServletRequest request) {
    
        errors = new ActionErrors();
        
        String submit = request.getParameter("submit");
        // front end validation when save is clicked.
        //if (submit != null) {
  
            /* The IP address can also be null -- which means open the
             server socket on *all* IP addresses for this host */
            if ((address.length() > 0) && !address.equalsIgnoreCase(" ")) {
                try {
                    InetAddress.getByName(address);
                } catch (Exception e) {
                    errors.add("address", new ActionError("error.address.invalid"));
                }
            } else {
                address = " ";
            }
            
            /* ports */
            numberCheck("portNumber",  portText, true, 1, 65535);
            numberCheck("redirectPortText",  redirectPortText, true, -1, 65535);
            
            /* processors*/
            //numberCheck("minProcessorsText",  minProcessorsText, true, 1, 512);
            //try {
                // if min is a valid integer, then check that max >= min
                //int min = Integer.parseInt(minProcessorsText);
                //numberCheck("maxProcessorsText",  maxProcessorsText, true, min, 512);
            //} catch (Exception e) {
                // check for the complete range
                //numberCheck("maxProcessorsText",  maxProcessorsText, true, 1, 512);
            //}
            
            // proxy                  
            if ((proxyName!= null) && (proxyName.length() > 0)) {
                try {
                    InetAddress.getByName(proxyName);
                } catch (Exception e) {
                    errors.add("proxyName", new ActionError("error.proxyName.invalid"));
                }
            }   
            
            // supported only by Coyote HTTP and HTTPS connectors
            if (!("AJP".equalsIgnoreCase(connectorType))) {
                numberCheck("acceptCountText", acceptCountText, true, 0, 128);
                //numberCheck("connTimeOutText", connTimeOutText, true, -1, 60000);
                numberCheck("bufferSizeText", bufferSizeText, true, 1, 8192);
                numberCheck("proxyPortText",  proxyPortText, true, 0, 65535);  
            }
        //}
        
        return errors;
    }
    
    /*
     * Helper method to check that it is a required number and
     * is a valid integer within the given range. (min, max).
     *
     * @param  field  The field name in the form for which this error occured.
     * @param  numText  The string representation of the number.
     * @param rangeCheck  Boolean value set to true of reange check should be performed.
     *
     * @param  min  The lower limit of the range
     * @param  max  The upper limit of the range
     *
     */
    
    public void numberCheck(String field, String numText, boolean rangeCheck,
    int min, int max) {
        
        /* Check for 'is required' */
        if ((numText == null) || (numText.length() < 1)) {
            errors.add(field, new ActionError("error."+field+".required"));
        } else {
            
        /*check for 'must be a number' in the 'valid range'*/
            try {
                int num = Integer.parseInt(numText);
                // perform range check only if required
                if (rangeCheck) {
                    if ((num < min) || (num > max ))
                        errors.add( field,
                        new ActionError("error."+ field +".range"));
                }
            } catch (NumberFormatException e) {
                errors.add(field,
                new ActionError("error."+ field + ".format"));
            }
        }
    }
    
}
