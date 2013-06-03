Vaadin currency converter
===========================

To build this project use

    mvn install

To run this project with Maven use

    mvn jetty:run

For more help see the Vaadin documentation

    http://vaadin.com/
    
A sample vaadin application demonstrating a currency converter service based on Yahoo Finance/YQL.
The client is a simple form (YQL doesn't allow all symbols to be retrieved unfortunately) which
invokes yahoo finance service to obtain exchange rate between currency symbols.
