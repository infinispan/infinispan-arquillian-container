How to run example tests in embedded mode
=========================================

1)  run this command:

    mvn clean verify
    

NOTE: By default, examples are testing infinispan-core classes of version defined 
in a parent pom. Users can change version of Infinispan being tested inside examples' 
pom files by changing version of the following artifact:

    <dependency>
        <groupId>org.infinispan</groupId>
        <artifactId>infinispan-core</artifactId>
        <scope>test</scope>
        <version>${version.infinispan_core}</version>
    </dependency>

The testng example contains certain test that fail at the moment (those taking a
Method parameter. This is happening because of a bug in Arquillian which has been recently 
fixed and will appear in the next release (version 1.0.0.CR6 or Final). 
