%define version 5.0.25
%define base /opt/jakarta-tomcat-%{version}

Summary: Apache Jakarta Servlet/JSP server 
Name: jakarta-tomcat
Version: %{version}
Release: 1asf
Vendor: Apache Software Foundation
Group: System Environment/Daemons
Copyright: Apache License
BuildArch: noarch
URL: http://jakarta.apache.org/tomcat
BuildRoot: %{_tmppath}/%{name}-%{version}-%{release}-buildroot 
Provides: jakarta-tomcat tomcat tomcat5

# No requires - Java should be installed, but it can be installed for Sun's site as binary ( no RPM ) or 
# in some other form. Adding dependency would require the user to install some dummy java RPM  he doesn't want
# 

# This is a complete distribution - using exactly the same jar files that we used when testing tomcat.
# While this may create duplication with other RPMs distributing Xerces or MX4J - our goal is to 
# have the most stable tomcat, even if you waste few megs of  hard drive space. 

# TODO: decide over /opt or /usr/local
Prefix: /opt

# We build from the official binary distribution - it should produce identical result as on all other OSes and linux variants.
Source: http://www.apache.org/dist/jakarta/tomcat-5/v%{version}/bin/jakarta-tomcat-%{version}.tar.gz

%description
Servlet container for developing Web applications in Java. Used as basis for the Reference Implementation 
of the Servlet and JSP specificiations. This RPM mirrors the layout used by official Tomcat distributions
on all other OSes and linux variants. 

If you want a FHS-based package - be warned that tomcat may not work very well. ( TODO: add more warnings !).

If you still want FHS-based package - please use jpackage.org or an RPM that is using the same exact layout.


%prep
%setup -q -n jakarta-tomcat-%{version} -T -b 0

%build


%install
# cd jakarta-tomcat-%{version}

install -d %{buildroot}/opt/jakarta-tomcat-%{version}
cp -a * ${RPM_BUILD_ROOT}/opt/jakarta-tomcat-%{version}

%clean

%pre
grep '^tomcat:' /etc/group > /dev/null
if [ $? eq 1 ] ; then 
  %{_sbindir}/groupadd tomcat
fi

grep '^tomcat:' /etc/passwd > /dev/null

if [ $? eq 1 ] ; then 
  %{_sbindir}/useradd -c "Tomcat Server" -d /opt/jakarta-tomcat-4 -g tomcat tomcat 
fi


%post
ln -s /opt/jakarta-tomcat-%{version}/bin/catalina.sh /etc/init.d

%preun

%files
%defattr(-,tomcat,tomcat,0755)
%config(noreplace) %{base}/conf
%{base}/NOTICE
%{base}/LICENSE
%{base}/RELEASE-NOTES
%{base}/*.txt
%{base}/bin
%{base}/server
%{base}/common
%{base}/temp
%{base}/webapps


%changelog
* Mon May 17 2004 Costin Manolache <costin@apache.org> 0:4.1.30-1asf
- Initial version
  
