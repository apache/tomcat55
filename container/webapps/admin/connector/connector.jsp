<!-- Standard Struts Entries -->

<%@ page language="java" import="java.net.URLEncoder" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/controls.tld" prefix="controls" %>

<html:html locale="true">

<%@ include file="../users/header.jsp" %>

<!-- Body -->
<body bgcolor="white" background="../images/PaperTexture.gif">

<!--Form -->

<html:errors/>

<html:form method="POST" action="/SaveConnector">

  <bean:define id="thisObjectName" type="java.lang.String"
               name="connectorForm" property="objectName"/>
  <html:hidden property="connectorName"/>
  <html:hidden property="adminAction"/>
  <html:hidden property="objectName"/>
  <html:hidden property="connectorType"/>
  <html:hidden property="serviceName"/>

  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="7171A5">
      <td width="81%">
       <div class="page-title-text" align="left">
          <logic:equal name="connectorForm" property="adminAction" value="Create">
            <bean:message key="actions.connectors.create"/>
          </logic:equal>
          <logic:equal name="connectorForm" property="adminAction" value="Edit">
           <bean:write name="connectorForm" property="nodeLabel"/>
          </logic:equal>
       </div>
      </td>
      <td align="right" nowrap>
        <div class="page-title-text">
      <controls:actions label="Connector Actions">
            <controls:action selected="true"> ----<bean:message key="actions.available.actions"/>---- </controls:action>
            <controls:action> --------------------------------- </controls:action>
            <logic:notEqual name="connectorForm" property="adminAction" value="Create">
            <logic:notEqual name="connectorForm" property="portText"
                            value='<%= Integer.toString(request.getServerPort()) %>'>
            <controls:action url='<%= "/DeleteConnector.do?select=" +
                                        URLEncoder.encode(thisObjectName,"UTF-8") %>'>
            <bean:message key="actions.connectors.delete"/>
            </controls:action>
            </logic:notEqual>
            </logic:notEqual>
       </controls:actions>
         </div>
      </td>
    </tr>
  </table>
    <%@ include file="../buttons.jsp" %>
  <br>

  <table class="back-table" border="0" cellspacing="0" cellpadding="1" width="100%">
    <tr>
      <td>
       <controls:table tableStyle="front-table" lineStyle="line-row">
            <controls:row header="true"
                labelStyle="table-header-text" dataStyle="table-header-text">
            <controls:label>General</controls:label>
            <controls:data>&nbsp;</controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="connectorType">
            <controls:label><bean:message key="connector.type"/>:</controls:label>
            <controls:data>
                 <logic:equal name="connectorForm" property="adminAction" value="Create">
                    <html:select property="connectorType" onchange="IA_jumpMenu('self',this)" styleId="connectorType">
                     <bean:define id="connectorTypeVals" name="connectorForm" property="connectorTypeVals"/>
                     <html:options collection="connectorTypeVals" property="value" labelProperty="label"/>
                    </html:select>
                </logic:equal>
                <logic:equal name="connectorForm" property="adminAction" value="Edit">
                  <bean:write name="connectorForm" property="connectorType" scope="session"/>
                </logic:equal>
            </controls:data>
        </controls:row>

    <%-- do not show scheme while creating a new connector --%>
    <logic:notEqual name="connectorForm" property="adminAction" value="Create">
        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text">
            <controls:label><bean:message key="connector.scheme"/>:</controls:label>
            <controls:data>
              <bean:write name="connectorForm" property="scheme" scope="session"/>
            </controls:data>
        </controls:row>
     </logic:notEqual>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="enableDNS">
            <controls:label><bean:message key="connector.enable.dns"/>:</controls:label>
            <controls:data>
                <html:select property="enableLookups" styleId="enableDNS">
                     <bean:define id="booleanVals" name="connectorForm" property="booleanVals"/>
                     <html:options collection="booleanVals" property="value"
                   labelProperty="label"/>
                </html:select>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="uriencoding">
            <controls:label><bean:message key="connector.uriencoding"/>:</controls:label>
            <controls:data>
               <html:text property="URIEncodingText" size="30" styleId="uriencoding"/>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="usebodyencoding">
            <controls:label><bean:message key="connector.useBodyEncodingForURI"/>:</controls:label>
            <controls:data>
                <html:select property="useBodyEncodingForURIText" styleId="usebodyencoding">
                     <bean:define id="booleanVals" name="connectorForm" property="booleanVals"/>
                     <html:options collection="booleanVals" property="value"
                   labelProperty="label"/>
                </html:select>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="allowTrace">
            <controls:label><bean:message key="connector.allowTrace"/>:</controls:label>
            <controls:data>
                <html:select property="allowTraceText" styleId="allowTrace">
                     <bean:define id="booleanVals" name="connectorForm" property="booleanVals"/>
                     <html:options collection="booleanVals" property="value"
                   labelProperty="label"/>
                </html:select>
            </controls:data>
        </controls:row>

        <%-- Input only allowed on create transaction --%>
        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="address">
            <controls:label><bean:message key="connector.address.ip"/>:</controls:label>
            <controls:data>
             <logic:equal name="connectorForm" property="adminAction" value="Create">
               <html:text property="address" size="20" styleId="address"/>
             </logic:equal>
             <logic:equal name="connectorForm" property="adminAction" value="Edit">
               &nbsp;<bean:write name="connectorForm" property="address"/>
               <html:hidden property="address"/>
             </logic:equal>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="secure">
            <controls:label><bean:message key="connector.secure"/>:</controls:label>
            <controls:data>
                <html:select property="secure" styleId="secure">
                     <bean:define id="booleanVals" name="connectorForm" property="booleanVals"/>
                     <html:options collection="booleanVals" property="value"
                   labelProperty="label"/>
                </html:select>
            </controls:data>
        </controls:row>

        <%--controls:row header="true" labelStyle="table-header-text" dataStyle="table-header-text">
            <controls:label>Processors</controls:label>
            <controls:data>&nbsp;</controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="minProcessor">
            <controls:label><bean:message key="connector.min"/>:</controls:label>
            <controls:data>
               <html:text property="minProcessorsText" size="5" styleId="minProcessor"/>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="connectorMax">
            <controls:label><bean:message key="connector.max"/>:</controls:label>
            <controls:data>
               <html:text property="maxProcessorsText" size="5" styleId="connectorMax"/>
            </controls:data>
        </controls:row--%>

<%-- The following properties are supported only for Coyote HTTP/S 1.1 Connectors --%>

     <logic:notEqual name="connectorForm" property="connectorType" scope="session"
                  value="AJP">
       <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="acceptCount">
            <controls:label><bean:message key="connector.accept.count"/>:</controls:label>
            <controls:data>
              <html:text property="acceptCountText" size="5" maxlength="5" styleId="acceptCount"/>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="compression">
            <controls:label><bean:message key="connector.compression"/>:</controls:label>
            <controls:data>
               <html:text property="compression" size="10" styleId="compression"/>
            </controls:data>
        </controls:row>

       <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="linger">
            <controls:label><bean:message key="connector.connection.linger"/><br>
                (<bean:message key="connector.milliseconds"/>) :</controls:label>
            <controls:data>
               <html:text property="connLingerText" size="10" styleId="linger"/>
            </controls:data>
        </controls:row>

       <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="timeout">
            <controls:label><bean:message key="connector.connection.timeout"/><br>
                (<bean:message key="connector.milliseconds"/>) :</controls:label>
            <controls:data>
               <html:text property="connTimeOutText" size="10" styleId="timeout"/>
            </controls:data>
        </controls:row>

       <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="upload">
            <controls:label><bean:message key="connector.connection.uploadTimeout"/><br>
                (<bean:message key="connector.milliseconds"/>) :</controls:label>
            <controls:data>
               <html:text property="connUploadTimeOutText" size="10" styleId="upload"/>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="buffersize">
            <controls:label><bean:message key="connector.default.buffer"/>:</controls:label>
            <controls:data>
               <html:text property="bufferSizeText" size="5" styleId="buffersize"/>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="disableUpload">
            <controls:label><bean:message key="connector.connection.disableUploadTimeout"/>:</controls:label>
            <controls:data>
                <html:select property="disableUploadTimeout" styleId="disableUpload">
                     <bean:define id="booleanVals" name="connectorForm" property="booleanVals"/>
                     <html:options collection="booleanVals" property="value"
                   labelProperty="label"/>
                </html:select>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="maxkeepalive">
            <controls:label><bean:message key="connector.maxkeepalive"/>:</controls:label>
            <controls:data>
              <html:text property="maxKeepAliveText" size="5" maxlength="5" styleId="maxkeepalive"/>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="maxspare">
            <controls:label><bean:message key="connector.maxspare"/>:</controls:label>
            <controls:data>
              <html:text property="maxSpare" size="5" maxlength="5" styleId="maxspare"/>
            </controls:data>
        </controls:row>
	
        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="maxthreads">
            <controls:label><bean:message key="connector.maxthreads"/>:</controls:label>
            <controls:data>
              <html:text property="maxThreads" size="5" maxlength="5" styleId="maxthreads"/>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="minspare">
            <controls:label><bean:message key="connector.minspare"/>:</controls:label>
            <controls:data>
              <html:text property="minSpare" size="5" maxlength="5" styleId="minspare"/>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="threadpriority">
            <controls:label><bean:message key="connector.threadpriority"/>:</controls:label>
            <controls:data>
              <html:text property="threadPriority" size="5" maxlength="5" styleId="threadpriority"/>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="tcpNoDelay">
            <controls:label><bean:message key="connector.tcpNoDelay"/>:</controls:label>
            <controls:data>
                <html:select property="tcpNoDelay" styleId="tcpNoDelay">
                     <bean:define id="booleanVals" name="connectorForm" property="booleanVals"/>
                     <html:options collection="booleanVals" property="value"
                   labelProperty="label"/>
                </html:select>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="xpoweredby">
            <controls:label><bean:message key="connector.xpoweredby"/>:</controls:label>
            <controls:data>
                <html:select property="xpoweredBy" styleId="xpoweredby">
                     <bean:define id="booleanVals" name="connectorForm" property="booleanVals"/>
                     <html:options collection="booleanVals" property="value"
                   labelProperty="label"/>
                </html:select>
            </controls:data>
        </controls:row>
     </logic:notEqual>

        <controls:row header="true" labelStyle="table-header-text" dataStyle="table-header-text">
            <controls:label>Ports</controls:label>
            <controls:data>&nbsp;</controls:data>
        </controls:row>

        <%-- Input only allowed on create transaction --%>
        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="portnumber">
            <controls:label><bean:message key="server.portnumber"/>:</controls:label>
            <controls:data>
             <logic:equal name="connectorForm" property="adminAction" value="Create">
               <html:text property="portText" size="5" styleId="portnumer"/>
             </logic:equal>
             <logic:equal name="connectorForm" property="adminAction" value="Edit">
               <bean:write name="connectorForm" property="portText"/>
               <html:hidden property="portText"/>
             </logic:equal>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="redirectport">
            <controls:label><bean:message key="connector.redirect.portnumber"/>:</controls:label>
            <controls:data>
               <html:text property="redirectPortText" size="5" styleId="redirectport"/>
            </controls:data>
        </controls:row>

        <controls:row header="true" labelStyle="table-header-text" dataStyle="table-header-text">
            <controls:label>Proxy</controls:label>
            <controls:data>&nbsp;</controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="proxyName">
            <controls:label><bean:message key="connector.proxy.name"/>:</controls:label>
            <controls:data>
               <html:text property="proxyName" size="30" styleId="proxyName"/>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="portNumber">
            <controls:label><bean:message key="connector.proxy.portnumber"/>:</controls:label>
            <controls:data>
                <html:text property="proxyPortText" size="5" styleId="portNumber"/>
            </controls:data>
        </controls:row>

<%-- The following properties are supported only on HTTPS Connector --%>
     <logic:equal name="connectorForm" property="scheme" scope="session"
                  value="https">
        <br>
        <controls:row header="true" labelStyle="table-header-text" dataStyle="table-header-text">
            <controls:label>Factory Properties:</controls:label>
            <controls:data>&nbsp;</controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="algorithm">
            <controls:label><bean:message key="connector.algorithm"/>:</controls:label>
            <controls:data>
               <html:text property="algorithm" size="10" styleId="algorithm"/>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="ciphers">
            <controls:label><bean:message key="connector.ciphers"/>:</controls:label>
            <controls:data>
               <html:text property="ciphers" size="10" styleId="ciphers"/>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="clientauth">
            <controls:label><bean:message key="connector.client.auth"/>:</controls:label>
            <controls:data>
                <html:select property="clientAuthentication" styleId="clientauth">
                     <bean:define id="clientAuthVals" name="connectorForm" property="clientAuthVals"/>
                     <html:options collection="clientAuthVals" property="value"
                   labelProperty="label"/>
                </html:select>
            </controls:data>
        </controls:row>

        <%-- Input allowed only on create --%>
        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="keystore">
            <controls:label><bean:message key="connector.keystore.filename"/>:</controls:label>
            <controls:data>
            <logic:equal name="connectorForm" property="adminAction" value="Create">
                <html:text property="keyStoreFileName" size="30" styleId="keystore"/>
             </logic:equal>
             <logic:equal name="connectorForm" property="adminAction" value="Edit">
               <bean:write name="connectorForm" property="keyStoreFileName"/>
             </logic:equal>
            </controls:data>
        </controls:row>

        <%-- input password allowed only while creating connector --%>
        <logic:equal name="connectorForm" property="adminAction" value="Create">
        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="password">
            <controls:label><bean:message key="connector.keystore.password"/>:</controls:label>
            <controls:data>
                <html:password property="keyStorePassword" size="30" styleId="password"/>
                <%--
                <logic:equal name="connectorForm" property="adminAction" value="Edit">
                   <bean:write name="connectorForm" property="keyStorePassword"/>
                </logic:equal>
                --%>
            </controls:data>
        </controls:row>
        </logic:equal>

        <%-- Input allowed only on create --%>
        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="keytype">
            <controls:label><bean:message key="connector.keystore.type"/>:</controls:label>
            <controls:data>
            <logic:equal name="connectorForm" property="adminAction" value="Create">
                <html:text property="keyStoreType" size="30" styleId="keytype"/>
             </logic:equal>
             <logic:equal name="connectorForm" property="adminAction" value="Edit">
               <bean:write name="connectorForm" property="keyStoreType"/>
             </logic:equal>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="sslProtocol">
            <controls:label><bean:message key="connector.sslProtocol"/>:</controls:label>
            <controls:data>
               <html:text property="sslProtocol" size="10" styleId="sslProtocol"/>
            </controls:data>
        </controls:row>

    </logic:equal>
   </controls:table>

      </td>
    </tr>
  </table>
    <%@ include file="../buttons.jsp" %>
  <br>
  </html:form>
<p>&nbsp;</p>
</body>
</html:html>
