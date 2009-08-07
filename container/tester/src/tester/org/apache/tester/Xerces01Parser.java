/*
 * Copyright 1999, 2000, 2001 ,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.tester;


import java.net.URL;
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.ParserFactory;


/**
 * SAX parser (based on SAXCount) that exercises the Xerces parser within the
 * environment of a web application.
 *
 * @author Amy Roh
 * @author Craig McClanahan
 * @version $Revision$ $Date$
 */

public class Xerces01Parser extends HandlerBase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The number of XML attributes we have encountered.
     */
    protected int attributes = 0;


    /**
     * The number of characters we have encountered.
     */
    protected int characters = 0;


    /**
     * The number of XML elements we have encountered.
     */
    protected int elements = 0;


    /**
     * The amount of ignorable whitespace we have encountered.
     */
    protected int whitespace = 0;



    // --------------------------------------------------------- Public Methods


    /**
     * Execute the requested parse.
     *
     * @param url The URL of the XML resource to be parsed
     *
     * @exception Exception if any processing exception occurs
     */
    public void parse(URL url) throws Exception {

        // Construct a parser for our use
        Parser parser =
            ParserFactory.makeParser("org.apache.xerces.parsers.SAXParser");
        parser.setDocumentHandler(this);
        parser.setErrorHandler(this);

        // Perform the requested parse
        long before = System.currentTimeMillis();
        parser.parse(url.toString());
        long after = System.currentTimeMillis();

        // Log the results
        StaticLogger.write("Parsing time = " + (after - before) +
                           " milliseconds");
        StaticLogger.write("Processed " + elements + " elements");
        StaticLogger.write("Processed " + attributes + " attributes");
        StaticLogger.write("Processed " + characters + " characters");
        StaticLogger.write("Processed " + whitespace + " whitespaces");

    }


    // ------------------------------------------------------ SAX Error Methods


    /**
     * Receive notification of a parser error.
     *
     * @param e The parser exception being reported
     *
     * @exception SAXException if a parsing error occurs
     */
    public void error(SAXParseException e) throws SAXException {

        StaticLogger.write("[Error] " +
                           getLocationString(e) + ": " +
                           e.getMessage());

    }


    /**
     * Receive notification of a fatal error.
     *
     * @param e The parser exception being reported
     *
     * @exception SAXException if a parsing error occurs
     */
    public void fatalError(SAXParseException e) throws SAXException {

        StaticLogger.write("[Fatal] " +
                           getLocationString(e) + ": " +
                           e.getMessage());

    }


    /**
     * Receive notification of a parser warning.
     *
     * @param e The parser exception being reported
     *
     * @exception SAXException if a parsing error occurs
     */
    public void warning(SAXParseException e) throws SAXException {

        StaticLogger.write("[Warning] " +
                           getLocationString(e) + ": " +
                           e.getMessage());

    }


    /**
     * Return the location at which this exception occurred.
     *
     * @param e The SAXParseException we are reporting on
     */
    private String getLocationString(SAXParseException e) {

        StringBuffer sb = new StringBuffer();
        String systemId = e.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1)
                systemId = systemId.substring(index + 1);
            sb.append(systemId);
        }
        sb.append(':');
        sb.append(e.getLineNumber());
        sb.append(':');
        sb.append(e.getColumnNumber());
        return (sb.toString());

    }


    // ------------------------------------------------------ SAX Event Methods


    /**
     * Character data event handler.
     *
     * @param ch Character array containing the characters
     * @param start Starting position in the array
     * @param length Number of characters to process
     *
     * @exception SAXException if a parsing error occurs
     */
    public void characters(char ch[], int start, int length)
        throws SAXException {

        characters += length;

    }


    /**
     * Ignorable whitespace event handler.
     *
     * @param ch Character array containing the characters
     * @param start Starting position in the array
     * @param length Number of characters to process
     *
     * @exception SAXException if a parsing error occurs
     */
    public void ignorableWhitespace(char ch[], int start, int length)
        throws SAXException {

        whitespace += length;

    }


    /**
     * Start of element event handler.
     *
     * @param name The element type name
     * @param attrs The specified or defaulted attributes
     *
     * @exception SAXException if a parsing error occurs
     */
    public void startElement(String name, AttributeList attrs) {

        elements++;
        if (attrs != null)
            attributes += attrs.getLength();

    }



}
