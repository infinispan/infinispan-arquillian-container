#Infinispan Arquillian Container

Infinispan itself supports two modes of access – ‘Embedded’ and ‘Remote’. Embedded 
allows direct programmatic access within the same JVM and remote allows user 
applications to be hosted in a separate JVM. Remote mode typically means running a 
standalone Infinispan server or one embedded in a JBoss Application Server 7 and accessing
it via remote client calls. 

Infinispan Arquillian Container provides API for testing of both embedded and remote 
mode.

Features supporting testing of the remote mode include:

+ providing server module endpoint information like hostname/port (HotRod,
  MemCached, also REST for JBoss AS 7)

+ providing Cache Manager information:

    - cache manager status
    - cluster members
    - running cache count
    - physical addresses
    - and more...

+ providing Cache information:

    - cache status
    - average read time
    - average write time
    - number of entries
    - hits
    - misses
    - removes
    - stores
    - and more...

Features supporting testing of the embedded mode include:

+ injecting of a DatagridManager which enables users to:

    - easily create a cluster of cache managers of any size (including 1)
    - retrieve a cache, either default or named one, from any of those cache managers
    - get a transaction manager and other objects belonging to any cache in the cluster 
      (objects like transaction, advanced cache, lock manager, replication listener, etc.)

Usage of container not fully managed by Arquillian:

Some containers, like e.g. EAP 5 container, are not fully managed by Arquillian and need to be stared/stopped manually.
In such case dependency injection provided by this container cannot be used and you have to create appropriate resource 
yourselves in the code. If the used Arquillian container is not compatible with one for JBoss AS 7, you also need to
configure the container to skip adding it to  Infinispan-Arquillian context by setting `<property name="skipIspnContext"/>`
in Arquillian configuration file.

##Building

   mvn clean install
