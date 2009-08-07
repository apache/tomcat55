package org.apache.webapp.balancer.rules;

import javax.servlet.http.HttpServletRequest;


/**
 * This rule redirects requests if they
 * are for a specific character encoding.
 *
 * @author Yoav Shapira
 */
public class CharacterEncodingRule extends BaseRule {
    /**
     * The character encoding.
     */
    private String encoding;

    /**
     * Sets the character encoding.
     *
     * @param theEncoding The encoding value
     */
    public void setEncoding(String theEncoding) {
        if (theEncoding == null) {
            throw new IllegalArgumentException("The encoding cannot be null.");
        } else {
            encoding = theEncoding;
        }
    }

    /**
     * Returns the desired encoding.
     *
     * @return String
     */
    protected String getEncoding() {
        return encoding;
    }

    /**
     * @see org.apache.webapp.balancer.Rule#matches(HttpServletRequest request)
     */
    public boolean matches(HttpServletRequest request) {
        String actualEncoding = request.getCharacterEncoding();

        return (getEncoding().compareTo(actualEncoding) == 0);
    }

    /**
     * Returns a String representation of this object.
     *
     * @return String
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[");
        buffer.append(getClass().getName());
        buffer.append(": ");

        buffer.append("Target encoding: ");
        buffer.append(getEncoding());
        buffer.append(" / ");

        buffer.append("Redirect URL: ");
        buffer.append(getRedirectUrl());

        buffer.append("]");

        return buffer.toString();
    }
}


// End of file: CharacterEncodingRule.java
