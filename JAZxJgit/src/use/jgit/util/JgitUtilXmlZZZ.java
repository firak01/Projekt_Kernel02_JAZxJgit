package use.jgit.util;

import java.util.Set;

public class JgitUtilXmlZZZ {
	public static String escapeXml(String text) {

	    if (text == null) {
	        return "";
	    }

	    return text.replace("&", "&amp;")
	               .replace("<", "&lt;")
	               .replace(">", "&gt;")
	               .replace("\"", "&quot;")
	               .replace("'", "&apos;");
	}
	
	
	
	public static void appendSet(StringBuilder sb, String tagName, Set<String> values) {

	    sb.append("  <").append(tagName).append(">");

	    if (values != null && !values.isEmpty()) {
	        sb.append("\n");

	        for (String value : values) {
	            sb.append("    <file>")
	              .append(escapeXml(value))
	              .append("</file>\n");
	        }

	        sb.append("  ");
	    }

	    sb.append("</").append(tagName).append(">\n");
	}
}
