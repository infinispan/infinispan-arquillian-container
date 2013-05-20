How to run example tests
========================

The examples generally use several (2) server instances. Since they will run
on the same machine, they have to be mapped either to different network interfaces
or there has to be a port offset.

On Unix based systems, you create additional network interfaces with the following commands:

sudo ifconfig eth0:1 192.168.11.101 netmask 255.255.255.0
sudo ifconfig eth0:2 192.168.11.102 netmask 255.255.255.0

and by putting the following lines in '/etc/hosts' in order to create aliases:

192.168.11.101    test1
192.168.11.102    test2

Binding server instance to test1 and test2 virtual interfaces is done via
'${example_dir}/src/test/resources/arquillian.xml' files.

Examples in the distribution use the port-offset (i.e. arquillian.xml
points to different ports for the second server rather than to a different interface).

The port-offset can be configured in ${INFINISPAN_SERVER_HOME}/standalone/configuration/clustered.xml:

   ...
   <socket-binding-group name="standard-sockets" default-interface="public" port-offset="100">
   ...

Or by passing the following property when starting the server:

   -Djboss.socket.binding.port-offset=XX property:


Follow steps in individual example's readme files to setup the tests.
