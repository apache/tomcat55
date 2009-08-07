Status:

Peter Roßbach
29.11.2004
	
Feature
=========================
	Backup context.xml
	Backup für server.xml
	Cluster Handling
	Store all 5.5.5 elements
	Context workDir default Handling
	WatchedResource Default Handling
	Write your own Registry
		System Property -Dcatalina.storeconfig=file:../xxx.xml
		${catalina.base}/conf bzw ${catalina.home}/conf
		Classpath org.apache.catalina.storeconfig
	JMX StoreConfig MBean
		Store context.xml and server.xmls
	Store Format is changeable with StoreAppenders
	Easy extendable 
	Connector support
		AJP
		HTTP
		HTTPS
	Context.xml and server.xml with backup				
		 
Example
=========================
	
	<Server ...>
	      <Listener className="org.apache.catalina.storeconfig.StoreConfigLifecycleListener"/>
          <Service ...
		  </Service>
    </Server>
	
	Usages with JMX Adaptor 
		ObjectName : Catalina:type=StoreConfig
		
		important operations:
			storeConfig			Store complete server
			storeServer			Store Server with ObjectName"               
			   parameter name="objectname"
                 description="Objectname from Server"
                 default="Catalina:type=Server"
			   parameter name="backup"
                 description="store Context with backup"
               parameter name="externalAllowed"
                 description="store all Context external that have a configFile"
            storeContext 		Store Context from ObjectName 
               parameter name="objectname"
                 description="ObjectName from Context"
                 example="Catalina:j2eeType=WebModule,name=//localhost/manager,J2EEApplication=none,J2EEServer=none
			   parameter name="backup"
                 description="store with Backup"
               parameter name="externalAllowed"
                 description="store all or store only internal server.xml context (configFile == null)"
			
Have fun
Peter Rossbach


	