/*
 * Copyright 1999,2004 The Apache Software Foundation.
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

package org.apache.jasper.xmlparser;

import java.io.IOException;
import java.util.jar.JarFile;

import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.compiler.ErrorDispatcher;


public class XMLEncodingDetector {

    /**
     * Autodetects the encoding of the XML document supplied by the given
     * input stream.
     *
     * Encoding autodetection is done according to the XML 1.0 specification,
     * Appendix F.1: Detection Without External Encoding Information.
     *
     * @param err The error dispatcher
     *
     * @return Two-element array, where the first element (of type
     * java.lang.String) contains the name of the autodetected encoding, and
     * the second element (of type java.lang.Boolean) specifies whether the 
     * encoding was specified by the encoding attribute of an XML declaration
     * (prolog).
     */
    public static Object[] getEncoding(String fname, JarFile jarFile,
                                       JspCompilationContext ctxt,
                                       ErrorDispatcher err)
            throws IOException, JasperException
    {
        XMLEncodingDetector detector=null;
        try {
            Class.forName("org.apache.xerces.util.SymbolTable");
            Class detectorClass=Class.forName("org.apache.jasper.xmlparser.XercesEncodingDetector");
            detector = (XMLEncodingDetector) detectorClass.newInstance();
        } catch(Exception ex ) {
            detector = new XMLEncodingDetector();
        }
        return detector.getEncodingMethod(fname, jarFile, ctxt, err);
    }

    public Object[] getEncodingMethod(String fname, JarFile jarFile,
				      JspCompilationContext ctxt,
				      ErrorDispatcher err)
	throws IOException, JasperException
    {
        Object result[] = new Object[] { "UTF8", new Boolean(false) };
        return result;
    }
}


