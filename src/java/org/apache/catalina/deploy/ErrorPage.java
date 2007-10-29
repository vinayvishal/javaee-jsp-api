

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * glassfish/bootstrap/legal/CDDLv1.0.txt or
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * glassfish/bootstrap/legal/CDDLv1.0.txt.  If applicable,
 * add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your
 * own identifying information: Portions Copyright [yyyy]
 * [name of copyright owner]
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 *
 * Portions Copyright Apache Software Foundation.
 */


package org.apache.catalina.deploy;


import org.apache.catalina.util.RequestUtil;
import java.io.Serializable;


/**
 * Representation of an error page element for a web application,
 * as represented in a <code>&lt;error-page&gt;</code> element in the
 * deployment descriptor.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2005/10/11 19:36:25 $
 */

public class ErrorPage implements Serializable {


    // ----------------------------------------------------- Instance Variables


    /**
     * The error (status) code for which this error page is active.
     */
    private int errorCode = 0;


    /**
     * The exception type for which this error page is active.
     */
    private String exceptionType = null;


    /**
     * The context-relative location to handle this error or exception.
     */
    private String location = null;


    // START SJSAS 6324911
    /**
     * The reason string to be displayed with the error (status) code
     */
    private String reason = null;
    // END SJSAS 6324911


    // ------------------------------------------------------------- Properties


    /**
     * Return the error code.
     */
    public int getErrorCode() {

        return (this.errorCode);

    }


    /**
     * Set the error code.
     *
     * @param errorCode The new error code
     */
    public void setErrorCode(int errorCode) {

        this.errorCode = errorCode;

    }


    /**
     * Set the error code (hack for default XmlMapper data type).
     *
     * @param errorCode The new error code
     */
    public void setErrorCode(String errorCode) {

        try {
            this.errorCode = Integer.parseInt(errorCode);
        } catch (Throwable t) {
            this.errorCode = 0;
        }

    }


    /**
     * Return the exception type.
     */
    public String getExceptionType() {

        return (this.exceptionType);

    }


    /**
     * Set the exception type.
     *
     * @param exceptionType The new exception type
     */
    public void setExceptionType(String exceptionType) {

        this.exceptionType = exceptionType;

    }


    /**
     * Return the location.
     */
    public String getLocation() {

        return (this.location);

    }


    /**
     * Set the location.
     *
     * @param location The new location
     */
    public void setLocation(String location) {

        //        if ((location == null) || !location.startsWith("/"))
        //            throw new IllegalArgumentException
        //                ("Error Page Location must start with a '/'");
        this.location = RequestUtil.URLDecode(location);

    }


    // START SJSAS 6324911
    /**
     * Gets the reason string that is associated with the error (status) code 
     * for which this error page is active.
     *
     * @return The reason string of this error page
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the reason string to be associated with the error (status) code 
     * for which this error page is active.
     *
     * @param reason The reason string
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
    // END SJSAS 6324911


    // --------------------------------------------------------- Public Methods


    /**
     * Render a String representation of this object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("ErrorPage[");
        if (exceptionType == null) {
            sb.append("errorCode=");
            sb.append(errorCode);
        } else {
            sb.append("exceptionType=");
            sb.append(exceptionType);
        }
        sb.append(", location=");
        sb.append(location);
        sb.append("]");
        return (sb.toString());

    }


}
