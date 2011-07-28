#Infinispan Arquillian Container

Provides API for retrieving information about Infinispan running 
either as a standalone Infinispan server or embedded in a JBoss Application Server 7.

Features include:

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

Support for in-VM mode of Infinispan will come soon (e.g. ability to inject pre-configured 
Infinispan Cache into a test class).

##Building

   mvn clean install
