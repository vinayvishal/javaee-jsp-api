

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

package org.apache.jasper.compiler;

import org.xml.sax.Attributes;
import org.apache.jasper.JasperException;

class Dumper {

    static class DumpVisitor extends Node.Visitor {
	private int indent = 0;

	private String getAttributes(Attributes attrs) {
	    if (attrs == null)
		return "";

	    StringBuffer buf = new StringBuffer();
	    for (int i=0; i < attrs.getLength(); i++) {
		buf.append(" " + attrs.getQName(i) + "=\""
			   + attrs.getValue(i) + "\"");
	    }
	    return buf.toString();
	}

	private void printString(String str) {
	    printIndent();
	    System.out.print(str);
	}

	private void printString(String prefix, char[] chars, String suffix) {
	    String str = null;
	    if (chars != null) {
		str = new String(chars);
	    }
	    printString(prefix, str, suffix);
	}
	     
	private void printString(String prefix, String str, String suffix) {
	    printIndent();
	    if (str != null) {
		System.out.print(prefix + str + suffix);
	    } else {
		System.out.print(prefix + suffix);
	    }
	}

	private void printAttributes(String prefix, Attributes attrs,
				     String suffix) {
	    printString(prefix, getAttributes(attrs), suffix);
	}

	private void dumpBody(Node n) throws JasperException {
	    Node.Nodes page = n.getBody();
	    if (page != null) {
//		indent++;
		page.visit(this);
//		indent--;
	    }
        }

        public void visit(Node.PageDirective n) throws JasperException {
	    printAttributes("<%@ page", n.getAttributes(), "%>");
        }

        public void visit(Node.TaglibDirective n) throws JasperException {
	    printAttributes("<%@ taglib", n.getAttributes(), "%>");
        }

        public void visit(Node.IncludeDirective n) throws JasperException {
	    printAttributes("<%@ include", n.getAttributes(), "%>");
	    dumpBody(n);
        }

        public void visit(Node.Comment n) throws JasperException {
	    printString("<%--", n.getText(), "--%>");
        }

        public void visit(Node.Declaration n) throws JasperException {
	    printString("<%!", n.getText(), "%>");
        }

        public void visit(Node.Expression n) throws JasperException {
	    printString("<%=", n.getText(), "%>");
        }

        public void visit(Node.Scriptlet n) throws JasperException {
	    printString("<%", n.getText(), "%>");
        }

        public void visit(Node.IncludeAction n) throws JasperException {
	    printAttributes("<jsp:include", n.getAttributes(), ">");
	    dumpBody(n);
            printString("</jsp:include>");
        }

        public void visit(Node.ForwardAction n) throws JasperException {
	    printAttributes("<jsp:forward", n.getAttributes(), ">");
	    dumpBody(n);
	    printString("</jsp:forward>");
        }

        public void visit(Node.GetProperty n) throws JasperException {
	    printAttributes("<jsp:getProperty", n.getAttributes(), "/>");
        }

        public void visit(Node.SetProperty n) throws JasperException {
	    printAttributes("<jsp:setProperty", n.getAttributes(), ">");
            dumpBody(n);
            printString("</jsp:setProperty>");
        }

        public void visit(Node.UseBean n) throws JasperException {
	    printAttributes("<jsp:useBean", n.getAttributes(), ">");
	    dumpBody(n);
	    printString("</jsp:useBean>");
        }
	
        public void visit(Node.PlugIn n) throws JasperException {
	    printAttributes("<jsp:plugin", n.getAttributes(), ">");
	    dumpBody(n);
	    printString("</jsp:plugin>");
	}
        
        public void visit(Node.ParamsAction n) throws JasperException {
	    printAttributes("<jsp:params", n.getAttributes(), ">");
	    dumpBody(n);
	    printString("</jsp:params>");
        }
        
        public void visit(Node.ParamAction n) throws JasperException {
	    printAttributes("<jsp:param", n.getAttributes(), ">");
	    dumpBody(n);
	    printString("</jsp:param>");
        }
        
        public void visit(Node.NamedAttribute n) throws JasperException {
	    printAttributes("<jsp:attribute", n.getAttributes(), ">");
	    dumpBody(n);
	    printString("</jsp:attribute>");
        }

        public void visit(Node.JspBody n) throws JasperException {
	    printAttributes("<jsp:body", n.getAttributes(), ">");
	    dumpBody(n);
	    printString("</jsp:body>");
        }
        
        public void visit(Node.ELExpression n) throws JasperException {
	    printString( "${" + new String( n.getText() ) + "}" );
        }

        public void visit(Node.CustomTag n) throws JasperException {
	    printAttributes("<" + n.getQName(), n.getAttributes(), ">");
	    dumpBody(n);
	    printString("</" + n.getQName() + ">");
        }

	public void visit(Node.UninterpretedTag n) throws JasperException {
	    String tag = n.getQName();
	    printAttributes("<"+tag, n.getAttributes(), ">");
	    dumpBody(n);
	    printString("</" + tag + ">");
        }

	public void visit(Node.TemplateText n) throws JasperException {
	    printString(new String(n.getText()));
	}

	private void printIndent() {
	    for (int i=0; i < indent; i++) {
		System.out.print("  ");
	    }
	}
    }

    public static void dump(Node n) {
	try {
	    n.accept(new DumpVisitor());	
	} catch (JasperException e) {
	    e.printStackTrace();
	}
    }

    public static void dump(Node.Nodes page) {
	try {
	    page.visit(new DumpVisitor());
	} catch (JasperException e) {
	    e.printStackTrace();
	}
    }
}

