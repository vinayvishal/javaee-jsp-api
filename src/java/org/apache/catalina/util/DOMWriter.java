

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

package org.apache.catalina.util;

import java.io.*;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * A sample DOM writer. This sample program illustrates how to
 * traverse a DOM tree in order to print a document that is parsed.
 */
public class DOMWriter {

   //
   // Data
   //

   /** Default Encoding */
   private static  String
   PRINTWRITER_ENCODING = "UTF8";

   private static String MIME2JAVA_ENCODINGS[] =
    { "Default", "UTF-8", "US-ASCII", "ISO-8859-1", "ISO-8859-2", "ISO-8859-3", "ISO-8859-4",
      "ISO-8859-5", "ISO-8859-6", "ISO-8859-7", "ISO-8859-8", "ISO-8859-9", "ISO-2022-JP",
      "SHIFT_JIS", "EUC-JP","GB2312", "BIG5", "EUC-KR", "ISO-2022-KR", "KOI8-R", "EBCDIC-CP-US",
      "EBCDIC-CP-CA", "EBCDIC-CP-NL", "EBCDIC-CP-DK", "EBCDIC-CP-NO", "EBCDIC-CP-FI", "EBCDIC-CP-SE",
      "EBCDIC-CP-IT", "EBCDIC-CP-ES", "EBCDIC-CP-GB", "EBCDIC-CP-FR", "EBCDIC-CP-AR1",
      "EBCDIC-CP-HE", "EBCDIC-CP-CH", "EBCDIC-CP-ROECE","EBCDIC-CP-YU",
      "EBCDIC-CP-IS", "EBCDIC-CP-AR2", "UTF-16"
    };


   /** Print writer. */
   protected PrintWriter out;

   /** Canonical output. */
   protected boolean canonical;


   public DOMWriter(String encoding, boolean canonical)
   throws UnsupportedEncodingException {
      out = new PrintWriter(new OutputStreamWriter(System.out, encoding));
      this.canonical = canonical;
   } // <init>(String,boolean)

   //
   // Constructors
   //

   /** Default constructor. */
   public DOMWriter(boolean canonical) throws UnsupportedEncodingException {
      this( getWriterEncoding(), canonical);
   }

    public DOMWriter(Writer writer, boolean canonical) {
        out = new PrintWriter(writer);
        this.canonical = canonical;
    }

   public static String getWriterEncoding( ) {
      return (PRINTWRITER_ENCODING);
   }// getWriterEncoding

   public static void  setWriterEncoding( String encoding ) {
      if( encoding.equalsIgnoreCase( "DEFAULT" ) )
         PRINTWRITER_ENCODING  = "UTF8";
      else if( encoding.equalsIgnoreCase( "UTF-16" ) )
         PRINTWRITER_ENCODING  = "Unicode";
      else
         PRINTWRITER_ENCODING = MIME2Java.convert( encoding );
   }// setWriterEncoding


   public static boolean isValidJavaEncoding( String encoding ) {
      for ( int i = 0; i < MIME2JAVA_ENCODINGS.length; i++ )
         if ( encoding.equals( MIME2JAVA_ENCODINGS[i] ) )
            return (true);

      return (false);
   }// isValidJavaEncoding


   /** Prints the specified node, recursively. */
   public void print(Node node) {

      // is there anything to do?
      if ( node == null ) {
         return;
      }

      int type = node.getNodeType();
      switch ( type ) {
         // print document
         case Node.DOCUMENT_NODE: {
               if ( !canonical ) {
                  String  Encoding = this.getWriterEncoding();
                  if( Encoding.equalsIgnoreCase( "DEFAULT" ) )
                     Encoding = "UTF-8";
                  else if( Encoding.equalsIgnoreCase( "Unicode" ) )
                     Encoding = "UTF-16";
                  else
                     Encoding = MIME2Java.reverse( Encoding );

                  out.println("<?xml version=\"1.0\" encoding=\""+
                           Encoding + "\"?>");
               }
               print(((Document)node).getDocumentElement());
               out.flush();
               break;
            }

            // print element with attributes
         case Node.ELEMENT_NODE: {
               out.print('<');
               out.print(node.getNodeName());
               Attr attrs[] = sortAttributes(node.getAttributes());
               for ( int i = 0; i < attrs.length; i++ ) {
                  Attr attr = attrs[i];
                  out.print(' ');
                  out.print(attr.getNodeName());
                  out.print("=\"");
                  out.print(normalize(attr.getNodeValue()));
                  out.print('"');
               }
               out.print('>');
               NodeList children = node.getChildNodes();
               if ( children != null ) {
                  int len = children.getLength();
                  for ( int i = 0; i < len; i++ ) {
                     print(children.item(i));
                  }
               }
               break;
            }

            // handle entity reference nodes
         case Node.ENTITY_REFERENCE_NODE: {
               if ( canonical ) {
                  NodeList children = node.getChildNodes();
                  if ( children != null ) {
                     int len = children.getLength();
                     for ( int i = 0; i < len; i++ ) {
                        print(children.item(i));
                     }
                  }
               } else {
                  out.print('&');
                  out.print(node.getNodeName());
                  out.print(';');
               }
               break;
            }

            // print cdata sections
         case Node.CDATA_SECTION_NODE: {
               if ( canonical ) {
                  out.print(normalize(node.getNodeValue()));
               } else {
                  out.print("<![CDATA[");
                  out.print(node.getNodeValue());
                  out.print("]]>");
               }
               break;
            }

            // print text
         case Node.TEXT_NODE: {
               out.print(normalize(node.getNodeValue()));
               break;
            }

            // print processing instruction
         case Node.PROCESSING_INSTRUCTION_NODE: {
               out.print("<?");
               out.print(node.getNodeName());
               String data = node.getNodeValue();
               if ( data != null && data.length() > 0 ) {
                  out.print(' ');
                  out.print(data);
               }
               out.print("?>");
               break;
            }
      }

      if ( type == Node.ELEMENT_NODE ) {
         out.print("</");
         out.print(node.getNodeName());
         out.print('>');
      }

      out.flush();

   } // print(Node)

   /** Returns a sorted list of attributes. */
   protected Attr[] sortAttributes(NamedNodeMap attrs) {

      int len = (attrs != null) ? attrs.getLength() : 0;
      Attr array[] = new Attr[len];
      for ( int i = 0; i < len; i++ ) {
         array[i] = (Attr)attrs.item(i);
      }
      for ( int i = 0; i < len - 1; i++ ) {
         String name  = array[i].getNodeName();
         int    index = i;
         for ( int j = i + 1; j < len; j++ ) {
            String curName = array[j].getNodeName();
            if ( curName.compareTo(name) < 0 ) {
               name  = curName;
               index = j;
            }
         }
         if ( index != i ) {
            Attr temp    = array[i];
            array[i]     = array[index];
            array[index] = temp;
         }
      }

      return (array);

   } // sortAttributes(NamedNodeMap):Attr[]


   /** Normalizes the given string. */
   protected String normalize(String s) {
      StringBuffer str = new StringBuffer();

      int len = (s != null) ? s.length() : 0;
      for ( int i = 0; i < len; i++ ) {
         char ch = s.charAt(i);
         switch ( ch ) {
            case '<': {
                  str.append("&lt;");
                  break;
               }
            case '>': {
                  str.append("&gt;");
                  break;
               }
            case '&': {
                  str.append("&amp;");
                  break;
               }
            case '"': {
                  str.append("&quot;");
                  break;
               }
            case '\r':
            case '\n': {
                  if ( canonical ) {
                     str.append("&#");
                     str.append(Integer.toString(ch));
                     str.append(';');
                     break;
                  }
                  // else, default append char
               }
            default: {
                  str.append(ch);
               }
         }
      }

      return (str.toString());

   } // normalize(String):String

   private static void printValidJavaEncoding() {
      System.err.println( "    ENCODINGS:" );
      System.err.print( "   " );
      for( int i = 0;
                     i < MIME2JAVA_ENCODINGS.length; i++) {
         System.err.print( MIME2JAVA_ENCODINGS[i] + " " );
      if( (i % 7 ) == 0 ){
         System.err.println();
         System.err.print( "   " );
         }
      }

   } // printJavaEncoding()

}
