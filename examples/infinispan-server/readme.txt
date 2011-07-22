How to run example tests
========================

1)  set ispnHome properties in src/test/resources/arquillian.xml to
    point to two different standalone Infinispan instances

2)  set cacheConfig properties in src/test/resources/arquillian.xml to
    point to configuration files for Infinispan instances (if not set, 
    Infinispan will run with default settings)

3)  run the tests:

    mvn clean verify
