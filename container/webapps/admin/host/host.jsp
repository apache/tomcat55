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

<html:form method="POST" action="/SaveHost">

  <bean:define id="hostName" name="hostForm" property="hostName"/>
  <bean:define id="thisObjectName" type="java.lang.String"
               name="hostForm" property="objectName"/>
  <html:hidden property="adminAction"/>
  <html:hidden property="objectName"/>
  <html:hidden property="serviceName"/>

  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr class="page-title-row">
      <td align="left" nowrap>
        <div class="page-title-text" align="left">
          <logic:equal name="hostForm" property="adminAction" value="Create">
            <bean:message key="actions.hosts.create"/>
          </logic:equal>
          <logic:equal name="hostForm" property="adminAction" value="Edit">
            <bean:write name="hostForm" property="nodeLabel"/>
          </logic:equal>
        </div>
      </td>
      <td align="right" nowrap>
        <div class="page-title-text">
        <controls:actions label="Host Actions">
            <controls:action selected="true"> -----<bean:message key="actions.available.actions"/>----- </controls:action>
            <controls:action disabled="true"> ------------------------------------- </controls:action>
            <logic:notEqual name="hostForm" property="adminAction" value="Create">
            <controls:action url='<%= "/AddAlias.do?hostName=" +
                                  URLEncoder.encode(thisObjectName,"UTF-8") %>'>
                <bean:message key="actions.alias.create"/>
            </controls:action>
            <controls:action url='<%= "/DeleteAlias.do?hostName=" +
                                  URLEncoder.encode(thisObjectName,"UTF-8") %>'>
                <bean:message key="actions.alias.delete"/>
            </controls:action>
            <controls:action disabled="true"> ------------------------------------- </controls:action>
            <controls:action url='<%= "/AddContext.do?parent=" +
                                  URLEncoder.encode(thisObjectName,"UTF-8") %>'>
                <bean:message key="actions.contexts.create"/>
            </controls:action>
            <controls:action url='<%= "/DeleteContext.do?parent=" +
                                  URLEncoder.encode(thisObjectName,"UTF-8") %>'>
                <bean:message key="actions.contexts.deletes"/>
            </controls:action>
            <controls:action disabled="true"> ------------------------------------- </controls:action>
            <!--FIXME add/remove defaultcontext-->
<%--
            <!--controls:action url='<%= "/AddDefaultContext.do?parent=" +
                                  URLEncoder.encode(thisObjectName,"UTF-8") %>'>
                <bean:message key="actions.defaultcontexts.create"/>
            </controls:action-->
            <!--controls:action url='<%= "/DeleteDefaultContext.do?parent=" +
                                  URLEncoder.encode(thisObjectName,"UTF-8") %>'>
                <bean:message key="actions.defaultcontexts.deletes"/>
            </controls:action-->                        
--%>
            <logic:notEqual name="hostName" value='<%= (String)request.getAttribute("adminAppHost") %>'>
            <controls:action disabled="true">
                -------------------------------------
            </controls:action>
            <controls:action url='<%= "/AddRealm.do?parent=" +
                                  URLEncoder.encode(thisObjectName,"UTF-8") %>'>
                <bean:message key="actions.realms.create"/>
            </controls:action>
            <controls:action url='<%= "/DeleteRealm.do?parent=" +
                                  URLEncoder.encode(thisObjectName,"UTF-8") %>'>
                <bean:message key="actions.realms.deletes"/>
            </controls:action>
            </logic:notEqual>
            <controls:action disabled="true">
                -------------------------------------
            </controls:action>
            <controls:action url='<%= "/AddValve.do?parent=" +
                                  URLEncoder.encode(thisObjectName,"UTF-8") %>'>
                <bean:message key="actions.valves.create"/>
            </controls:action>
            <controls:action url='<%= "/DeleteValve.do?parent=" +
                                  URLEncoder.encode(thisObjectName,"UTF-8") %>'>
                <bean:message key="actions.valves.deletes"/>
            </controls:action>
            <logic:notEqual name="hostName" value='<%= request.getServerName() %>'>
            <controls:action disabled="true">
                -------------------------------------
            </controls:action>
            <controls:action url='<%= "/DeleteHost.do?select=" +
                                        URLEncoder.encode(thisObjectName,"UTF-8") %>'>
                <bean:message key="actions.hosts.delete"/>
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

 <%-- Host Properties --%>
 <table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr> <td> <div class="table-title-text">
        <bean:message key="host.properties"/>
    </div> </td> </tr>
  </table>

  <table class="back-table" border="0" cellspacing="0" cellpadding="1" width="100%">
    <tr>
      <td>
        <controls:table tableStyle="front-table" lineStyle="line-row">
            <controls:row header="true"
                labelStyle="table-header-text" dataStyle="table-header-text">
            <controls:label>
                <bean:message key="service.property"/>
            </controls:label>
            <controls:data>
                <bean:message key="service.value"/>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="name">
            <controls:label>
                <bean:message key="host.name"/>:
            </controls:label>
            <controls:data>
            <%-- input only allowed on create transaction --%>
             <logic:equal name="hostForm" property="adminAction" value="Create">
              <html:text property="hostName" size="50" maxlength="50" styleId="name"/>
             </logic:equal>
             <logic:equal name="hostForm" property="adminAction" value="Edit">
              <bean:write name="hostForm" property="hostName"/>
              <html:hidden property="hostName"/>
             </logic:equal>
            </controls:data>
        </controls:row>


        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="base">
            <controls:label><bean:message key="host.base"/>:</controls:label>
            <controls:data>
             <logic:equal name="hostForm" property="adminAction" value="Create">
              <html:text property="appBase" size="24" styleId="base"/>
             </logic:equal>
             <logic:equal name="hostForm" property="adminAction" value="Edit">
              <bean:write name="hostForm" property="appBase"/>
              <html:hidden property="appBase"/>
             </logic:equal>
            </controls:data>
        </controls:row>
        
       <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="autodeploy">
            <controls:label><bean:message key="host.autoDeploy"/>:</controls:label>
            <controls:data>
               <html:select property="autoDeploy" styleId="autodeploy">
                     <bean:define id="booleanVals" name="hostForm" property="booleanVals"/>
                     <html:options collection="booleanVals" property="value"
                   labelProperty="label"/>
                </html:select>
            </controls:data>
        </controls:row>
        
       <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="deployOnStartup">
            <controls:label><bean:message key="host.deployOnStartup"/>:</controls:label>
            <controls:data>
               <html:select property="deployOnStartup" styleId="deployOnStartup">
                     <bean:define id="booleanVals" name="hostForm" property="booleanVals"/>
                     <html:options collection="booleanVals" property="value"
                   labelProperty="label"/>
                </html:select>
            </controls:data>
        </controls:row>

       <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="deployxml">
            <controls:label><bean:message key="host.deployXML"/>:</controls:label>
            <controls:data>
               <html:select property="deployXML" styleId="deployxml">
                     <bean:define id="booleanVals" name="hostForm" property="booleanVals"/>
                     <html:options collection="booleanVals" property="value"
                   labelProperty="label"/>
                </html:select>
            </controls:data>
        </controls:row>
        
       <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="wars">
            <controls:label><bean:message key="host.wars"/>:</controls:label>
            <controls:data>
               <html:select property="unpackWARs" styleId="wars">
                     <bean:define id="booleanVals" name="hostForm" property="booleanVals"/>
                     <html:options collection="booleanVals" property="value"
                   labelProperty="label"/>
                </html:select>
            </controls:data>
        </controls:row>

       <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="xmlnamespace">
            <controls:label><bean:message key="host.xmlNamespaceAware"/>:</controls:label>
            <controls:data>
               <html:select property="xmlNamespaceAware" styleId="xmlnamespace">
                     <bean:define id="booleanVals" name="hostForm" property="booleanVals"/>
                     <html:options collection="booleanVals" property="value"
                   labelProperty="label"/>
                </html:select>
            </controls:data>
        </controls:row>

       <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="xmlvalidation">
            <controls:label><bean:message key="host.xmlValidation"/>:</controls:label>
            <controls:data>
               <html:select property="xmlValidation" styleId="xmlvalidation">
                     <bean:define id="booleanVals" name="hostForm" property="booleanVals"/>
                     <html:options collection="booleanVals" property="value"
                   labelProperty="label"/>
                </html:select>
            </controls:data>
        </controls:row>
      </controls:table>

      </td>
    </tr>
  </table>

<br>
<br>

<%-- Aliases List --%>
 <logic:notEqual name="hostForm" property="adminAction" value="Create">
 <table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr> <td>
        <div class="table-title-text">
            <bean:message key="host.aliases"/>
        </div>
    </td> </tr>
  </table>

  <table class="back-table" border="0" cellspacing="0" cellpadding="1" width="100%">
    <tr> <td>
        <table class="front-table" border="1" cellspacing="0" cellpadding="0" width="100%">
          <tr class="header-row">
            <td width="27%">
              <div class="table-header-text" align="left"><bean:message key="host.alias.name"/> </div>
            </td> </tr>

            <logic:iterate id="aliasVal" name="hostForm" property="aliasVals">
            <tr> <td width="27%" valign="top" colspan=2>
                <div class="table-normal-text"> <%= aliasVal %> </div>
            </td> </tr>
            </logic:iterate>
         </table>

    </td> </tr>
  </table>
 </logic:notEqual>

  <%@ include file="../buttons.jsp" %>

</html:form>
</body>

</html:html>
