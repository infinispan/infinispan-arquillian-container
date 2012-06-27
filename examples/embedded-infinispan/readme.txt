How to run example tests in embedded mode
=========================================

1)  run this command:

    mvn clean verify


NOTE: By default, example tests use infinispan-core classes of a version defined
in a parent pom. Users can change version of Infinispan being tested inside examples' 
pom files by changing the version of the following artifact:

    <dependency>
        <groupId>org.infinispan</groupId>
        <artifactId>infinispan-core</artifactId>
        <scope>test</scope>
        <version>XXX</version>
    </dependency>
