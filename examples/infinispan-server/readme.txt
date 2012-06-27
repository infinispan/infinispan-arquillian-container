How to run example tests
========================

1)  set ispnHome properties in src/test/resources/arquillian.xml to
    point to two different standalone Infinispan server instances

    * jmxPort property has to be set for second and each next container as well as
      a port for server endpoint (on of memcached/hotrod/websocket)
    * the example tests will pass only if a default configuration file is
      used, otherwise the tests have to be reconfigured so that
      cache/cacheContainer names match those in user-specific configuration files

2)  run the tests:

    mvn clean verify
