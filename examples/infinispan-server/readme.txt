How to run example tests
========================

1)  set ispnHome properties in src/test/resources/arquillian.xml to
    point to two different standalone Infinispan instances

    * jmxPort property has to be set for second and each next container, default 
      value is 1090
    * the example tests will pass only if no cacheConfig (configuration file) is 
      specified, otherwise the tests have to be reconfigured to conform to
      cache manager/cache names specified in those configuration files

2)  run the tests:

    mvn clean verify
