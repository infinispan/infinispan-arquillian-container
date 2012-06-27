How to run example tests
========================

1)  set jbossHome properties in src/test/resources/arquillian.xml to
    point to two different JDG (JBoss Data Grid) instances

2)  setup port-offset in ${JDG_HOME2}/standalone/configuration/standalone-ha.xml

...
<socket-binding-group name="standard-sockets" default-interface="public" port-offset="100">
...

3) change ManagementRealm definition in standalone-ha.xml and add testing credentials:

   <security-realm name="ManagementRealm">
      <authentication>
         <users>
            <user username="testuser">
               <password>testpassword</password>
            </user>
         </users>
      </authentication>
   </security-realm>

4)  run the tests:

    mvn clean verify
