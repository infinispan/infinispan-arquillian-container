How to run example tests
========================

1)  set jbossHome properties in src/test/resources/arquillian.xml to
    point to two different EDG (Enterprise Data Grid) instances

2)  setup interfaces in ${EDG_HOME}/standalone/configuration/standalone.xml

*  first EDG instance:

    <interfaces>
        <interface name="management">
            <inet-address value="test1"/>
        </interface>
        <interface name="public">
            <inet-address value="test1"/>
        </interface>
        <interface name="clustering">
            <inet-address value="test1"/>
        </interface>
    </interfaces>

* second EDG instance:

    <interfaces>
        <interface name="management">
            <inet-address value="test2"/>
        </interface>
        <interface name="public">
            <inet-address value="test2"/>
        </interface>
        <interface name="clustering">
            <inet-address value="test2"/>
        </interface>
    </interfaces>

3)  run the tests:

    mvn clean verify
