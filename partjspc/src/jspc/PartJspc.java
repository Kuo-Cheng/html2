package jspc;

import org.apache.jasper.JspC;

/**
 * Libraries:
 * ant.jar
 * catalina.jar
 * el-api.jar
 * jasper-el.jar
 * jasper.jar
 * jsp-api.jar
 * servlet-api.jsr
 * tomcat-api.jar
 * tomcat-juli.jar
 * tomcat-util.jar
 * 
 * java -cp jasper.jar; v
 * servlet-api.jar; v
 * catalina.jar; v
 * F:\server\tomcat.6\bin\tomcat-juli.jar; v
 * ant.jar;
 * jsp-api.jar; v
 * jasper-el.jar; v
 * el-api.jar; 
 * jstl.jar;
 * standard.jar;
 * jasper-el.jar; v
 * jasper-jdt.jar 
 * org.apache.jasper.JspC 
 * -uriroot ./temp -d temp temp.jsp
 * 
 * @author Alex
 *
 */
public class PartJspc {
	
	public String jspcPart() {  
        String error="";  
        try {  
            JspC jspc = new JspC();  
            /*String[] arg0 = {"-uriroot", "E:/jspc", "-d", "d:/t", 
                    "temp/temp.jsp" }; 
            jspc.setArgs(arg0);*/  
            jspc.setUriroot("D://Hudson//workspace//wcmtgz//code//eSignage//ContentServer");//web application root path  
            jspc.setOutputDir("D:/t");//.java document and .class document output path  
            
            //Parses comma-separated list of JSP files to be processed. If the argument is null, nothing is done.
            //Each file is interpreted relative to uriroot, unless it is absolute, in which case it must start with uriroot.
            //jspFiles - Comma-separated list of JSP files to be processed
            String jspFiles = getJspFiles();
            jspc.setJspFiles(jspFiles);//the jsp files will be compiled  
            
            jspc.setCompile(true);//compile false or not true just generate .java document.  
            jspc.execute();  
        } catch (Exception e) {  
            error=e.toString();  
        }  
        return error;  
    }  
	
	/**
	 * Generate the jsp file list String, separated by the comma.
	 * 
	 * @return
	 */
	private static String getJspFiles(){
		return "servermanager/test.jsp";
	}
	
    public static void main(String args[]){  
    	System.out.println("Part Jspc begins!");
    	PartJspc jspc=new PartJspc();  
        System.out.println(jspc.jspcPart());  
    }  
}
