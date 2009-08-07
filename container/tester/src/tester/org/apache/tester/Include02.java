/*
 * Copyright 1999, 2000 ,2004 The Apache Software Foundation.
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


import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Positive test for handling exceptions thrown by an included servlet.
 * Request parameter <strong>exception</strong> is used to indicate the type
 * of exception that should be thrown, which must be one of
 * <code>IOException</code>, <code>ServletException</code>, or
 * <code>ServletException</code>.  According to the spec, any exceptions of
 * these types should be propogated back to the caller unchanged.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Include02 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        boolean ok = true;
        response.setContentType("text/plain");
	PrintWriter writer = response.getWriter();
        RequestDispatcher rd =
            getServletContext().getRequestDispatcher("/Include02a");
        if (rd == null) {
            writer.println("Include02 FAILED - No RequestDispatcher returned");
	    ok = false;
        }
	String type = request.getParameter("exception");
	if (ok) {
	    if (type == null) {
	        writer.println("Include02 FAILED - No exception type specified");
		ok = false;
	    } else if (!type.equals("IOException") &&
		       !type.equals("ServletException") &&
		       !type.equals("NullPointerException")) {
	        writer.println("Include02 FAILED - Invalid exception type " +
			       type + " requested");
		ok = false;
	    }
	}

	IOException ioException = null;
	ServletException servletException = null;
	Throwable throwable = null;
	try {
            if (ok)
                rd.include(request, response);
	} catch (IOException e) {
	    ioException = e;
	} catch (ServletException e) {
	    servletException = e;
	} catch (Throwable e) {
	    throwable = e;
	}

	if (ok) {
            if (type.equals("IOException")) {
                if (ioException == null) {
		    writer.println("Include02 FAILED - No IOException thrown");
		    ok = false;
		} else {
		    String message = ioException.getMessage();
		    if (!"Include02 IOException".equals(message)) {
		        writer.println("Include02 FAILED - IOException was " +
				       message);
			ok = false;
		    }
		}
	    } else if (type.equals("ServletException")) {
                if (servletException == null) {
		    writer.println("Include02 FAILED - No ServletException thrown");
		    ok = false;
		} else {
		    String message = servletException.getMessage();
		    if (!"Include02 ServletException".equals(message)) {
		        writer.println("Include02 FAILED - ServletException was " +
				       message);
			ok = false;
		    }
		}
	    } else if (type.equals("NullPointerException")) {
                if (throwable == null) {
		    writer.println("Include02 FAILED - No NullPointerException thrown");
		    ok = false;
		} else if (!(throwable instanceof NullPointerException)) {
		    writer.println("Include02 FAILED - Thrown Exception was " +
				   throwable.getClass().getName());
		    ok = false;
		} else {
		    String message = throwable.getMessage();
		    if (!"Include02 NullPointerException".equals(message)) {
		        writer.println("Include02 FAILED - NullPointerException was " +
				       message);
			ok = false;
		    }
		}
	    }
	}

	if (ok)
	    writer.println("Include02 PASSED");
        StaticLogger.reset();

    }

}
