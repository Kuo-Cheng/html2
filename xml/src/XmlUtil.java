package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
* Maintain the xml file.
* Receive json and update the xml.
*/
public class XmlUtil {
	/**
	 * The default file path. 
	 */
	private static String xmlXmlPath="C:\\tomcat\\webapps\\ROOT\\WEB-INF\\conf\\";
	
	/**
	 * The related path.
	 */
	private static String xmlXmlRelatedPath="WEB-INF\\conf\\";
	
	private static String xmlXmlName= "xml-params.xml";
	/** The threat string array. */
	private static String[] THREAT_STR_ARR = { "|", "&", "$", "%", "@", "'", "\"", "\n", "\r", "<", ">", "(", ")", "+", ",", ".", "script", "document", "eval" };
	
	 /**
	  * To prevent the sql injection threat, check these key char and string.
	  * 
	  * @param str
	  * @return true The string contains the threat string or char.
	  */
	public static boolean containsThreatChar(String str){
		return containsThreatChar(str, THREAT_STR_ARR);
	}
	
	/**
	 * Check the string by the setting file.
	 * 
	 * @param targetStr The checked string.
	 * @param request Get the real path from request.
	 * @return
	 */
	public static boolean checkXmlCode(String targetStr, HttpServletRequest request){
		boolean defRtn=false;
		if(request!=null){
			String realPath = request.getServletContext().getRealPath("/");
			String fullPath = realPath + xmlXmlRelatedPath + xmlXmlName;
			List<String> listKey = readXmlKey(fullPath);
			if(listKey!=null && listKey.size()>0){
				Object[] strArr = listKey.toArray();
				
				defRtn = containsThreatChar(targetStr,strArr);
			}
			
		}
		return defRtn;
	}
	
	public static boolean containsThreatChar(String str, Object[] threatObjArr){
		String[] threatStrArr = Arrays.copyOf(threatObjArr, threatObjArr.length, String[].class);
		return containsThreatChar(str, threatStrArr);
	}
	
	/**
	  * To prevent the sql injection threat, check these key char and string.
	  * 
	  * @param str
	  * @param threatStrArr String array - the threat string array.
	  * @return true The string contains the threat string or char.
	  */
	public static boolean containsThreatChar(String str, String[] threatStrArr){
		
		boolean containThreatChar = false;
		
		if(threatStrArr!=null && str!=null){
			for(int idx=0; idx < threatStrArr.length; idx++){
				if(StringUtils.containsIgnoreCase(str, threatStrArr[idx])==true){
					containThreatChar = true;
					break;
				}
			}
		}

		return containThreatChar;
	}
	
	
	
	public static List<String> readXmlKey(){
		return readXmlKey(xmlXmlPath + xmlXmlName);
	}
	
	/**
	 * Just get the code with true and the 'xmlCheck' is 'true'.
	 * @param filePathName
	 * @return
	 */
	public static List<String> readXmlKey(String filePathName){
		List<String> keyStrList = new ArrayList<String>();
		//long lasting = System.currentTimeMillis(); 
		try {
			File f = new File(filePathName); 
			SAXReader reader = new SAXReader(); 
			Document doc = reader.read(f); 
			Element root ; 
			Element xmlCheckElem;
			Element paramElem;
			Element checkElem;
			Element valueElem;
			
			String xmlCheckStr="";
			String checkStr="";
			String valueStr="";
			
			if(doc!=null){
				
				root = doc.getRootElement();
				
				if(root!=null){
					xmlCheckElem = root.element("xmlCheck");
					if(xmlCheckElem!=null && xmlCheckElem.getStringValue()!=null){
						xmlCheckStr = xmlCheckElem.getStringValue();
						
						//The xmlCheck is open.(true)
						if( xmlCheckStr!=null && xmlCheckStr.trim().compareToIgnoreCase("true")==0 ){
							for (Iterator itor = root.elementIterator("param"); itor.hasNext(); ) {
								paramElem = (Element) itor.next();
								
								if(paramElem!=null){
									checkElem = paramElem.element("check");
									
									if(checkElem!=null){
										checkStr = checkElem.getStringValue();
										
										//Just get the code with true value
										if( checkStr!=null && checkStr.trim().compareToIgnoreCase("true")==0 ){
											valueElem = paramElem.element("value");
											if(valueElem!=null){
												valueStr = valueElem.getStringValue();
												if(valueStr!=null && valueStr.trim().length()>0){
													keyStrList.add(valueStr);
												}
											}
										}
									}
									
								}//end if(paramElem
							}//end for
						}//end if( xmlCheckStr
					}
				}
			}
		} catch (Exception e) { 
			e.printStackTrace(); 
		} 
		return keyStrList;
	}
	
	/**
	 * Get the xml xml path from request and save to session.
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, Map<String, String>> readFullXmlKey(HttpServletRequest request){
		Map<String, Map<String, String>> rtnMap = null;
		if(request!=null){
			String realPath = request.getServletContext().getRealPath("/");
			String fullPath = realPath + xmlXmlRelatedPath + xmlXmlName;
			request.getSession().setAttribute("xmlXmlPath", fullPath);
			rtnMap = readFullXmlKey(fullPath);
		}
		
		return rtnMap;
	}

	
	public static Map<String, Map<String, String>> readFullXmlKey(){
		return readFullXmlKey(xmlXmlPath + xmlXmlName);
	}
	
	/**
	 * Get full data on the Xml check xml.
	 * 
	 * @param filePathName
	 * @return
	 */
	public static Map<String, Map<String, String>> readFullXmlKey(String filePathName){
		Map<String, Map<String, String>> returnMap = new ConcurrentHashMap<String,Map<String,String>>();
		
		//Check the key words or not.
		Map<String,String> checkStatusMap = new ConcurrentHashMap<String,String>();
		//Map<String,String> codeStatusMap = new ConcurrentHashMap<String,String>();
		Map<String,String> codeStatusMap = new LinkedHashMap<String,String>();
		//Map<String,String> codeMap = new ConcurrentHashMap<String,String>();
		Map<String,String> codeMap = new LinkedHashMap<String,String>();
		
		Map<String,String> descMap = new LinkedHashMap<String,String>();
		
		//long lasting = System.currentTimeMillis(); 
		try {
			File f = new File(filePathName); 
			SAXReader reader = new SAXReader(); 
			Document doc = reader.read(f); 
			Element root = doc.getRootElement(); 
			Element xmlCheckElem; 
			Element paramElem;
			Element seqElem;
			//Check status
			Element checkElem;
			//Code value
			Element valueElem;
			//Description
			Element descElem;
			
			String xmlCheckStatus = "";
			
			String seqStr = "";
			String checkStr = "";
			String valueStr = "";
			
			//Get xml check status: true/false
			if(root!=null){
				xmlCheckElem = root.element("xmlCheck");
				if(xmlCheckElem!=null){
					xmlCheckStatus = xmlCheckElem.getStringValue();
				}else{
					xmlCheckStatus = "false";
				}
				checkStatusMap.put("xmlCheck", xmlCheckStatus); 
			}
			returnMap.put("xmlCheck", checkStatusMap);
			
			//Iterate the 'param' tag.
			for (Iterator itor = root.elementIterator("param"); itor.hasNext(); ) {
				paramElem = (Element) itor.next(); 
				if(paramElem!=null){
					//Get sequence element (number)
					seqElem = paramElem.element("seq");
					//Get check status element (String true/false)
					checkElem = paramElem.element("check");
					
					descElem = paramElem.element("desc");
					if(checkElem!=null && seqElem!=null ){
						seqStr = seqElem.getStringValue();
						checkStr = checkElem.getStringValue();
						
						valueElem = paramElem.element("value");
						if(valueElem!=null){
							valueStr = valueElem.getStringValue();
						}
					}
					
					codeStatusMap.put(seqStr, checkStr);
					codeMap.put(seqStr, valueStr);
					descMap.put(seqStr, (descElem==null || descElem.getStringValue()==null)?"":descElem.getStringValue());
				}
			} 
			
			returnMap.put("codeStatus", codeStatusMap);
			returnMap.put("code", codeMap);
			returnMap.put("desc", descMap);
		} catch (Exception e) { 
			e.printStackTrace(); 
		} 
		return returnMap;
	}
	
//	public static synchronized boolean readAndUpdateXml(){
//		readAndUpdateXml
//	}
	
	/**
	 * Get full data on the Xml check xml.
	 * If it is the default checking Xml code, just update the flag(true/false) and description.
	 * JSONArray seq:number,check:true/false,value: code value.
	 * 
	 * 
	 * @param filePathName
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static synchronized boolean readAndUpdateXml(String filePathName, JSONArray jsonArr, boolean XmlCheck){
		Map<String, Map<String, String>> returnMap = new ConcurrentHashMap<String,Map<String,String>>();
		
		//Check the key words or not.
		Map<String,String> checkStatusMap = new ConcurrentHashMap<String,String>();
		//Map<String,String> codeStatusMap = new ConcurrentHashMap<String,String>();
		Map<String,String> codeStatusMap = new LinkedHashMap<String,String>();
		//Map<String,String> codeMap = new ConcurrentHashMap<String,String>();
		Map<String,String> codeMap = new LinkedHashMap<String,String>();
		
		File file = null;
		Document doc = null;
		//long lasting = System.currentTimeMillis(); 
		try {
			file = new File(filePathName); 
			SAXReader reader = new SAXReader(); 
			doc = reader.read(file); 
			Element root = doc.getRootElement(); 
			Element xmlCheckElem; 
			Element paramElem;
			Element seqElem;
			Element checkElem;
			Element valueElem;
			Object elemTmp;
			JSONObject jsonObjTmp;
			String checkTmp;
			
			String xmlCheckStatus = "";
			
			String seqStr = "";
			String checkStr = "";
			String valueStr = "";
			
			//Get xml check status: true/false
			if(root!=null){
				xmlCheckElem = root.element("xmlCheck");
				if(xmlCheckElem!=null && xmlCheck == true){
					//xmlCheckStatus = xmlCheckElem.getStringValue();
					xmlCheckElem.setText("true");
					xmlCheckStatus = "true";
				}else{
					xmlCheckElem.setText("false");
					xmlCheckStatus = "false";
				}
				checkStatusMap.put("xmlCheck", xmlCheckStatus); 
			}
			returnMap.put("xmlCheck", checkStatusMap);
			
			int elemIdx = 0;
			//Iterate the 'param' tag.
			for (Iterator itor = root.elementIterator("param"); itor.hasNext(); ) {
				elemIdx++;
				paramElem = (Element) itor.next(); 
				if(paramElem!=null){
					//Get sequence element (number)
					seqElem = paramElem.element("seq");
					//Get check status element (String true/false)
					checkElem = paramElem.element("check");
					
					if(checkElem!=null && seqElem!=null ){
						seqStr = seqElem.getStringValue();
						checkStr = checkElem.getStringValue();
						
						valueElem = paramElem.element("value");
						if(valueElem!=null){
							elemTmp = jsonArr.get(elemIdx);
							if(elemTmp!=null){
								jsonObjTmp = JSONObject.fromObject(elemTmp.toString());
								checkTmp = jsonObjTmp.get("check")==null?"":jsonObjTmp.get("check").toString();
								if(checkTmp!=null && checkTmp.equalsIgnoreCase("true")){
									//valueElem.setAttributeValue("check", "true");
									checkElem.setText("true");
								}else{
									//valueElem.setAttributeValue("check", "false");
									checkElem.setText("false");
								}
							}
							
							valueStr = valueElem.getStringValue();
						}
					}
					codeStatusMap.put(seqStr, checkStr);
					codeMap.put(seqStr, valueStr);
				}
				
			} 
			
			returnMap.put("codeStatus", codeStatusMap);
			returnMap.put("code", codeMap);
			
			saveToFile(file,doc);
		} catch (Exception e) { 
			e.printStackTrace(); 
		} 
		//return returnMap;
		return true;
	}
	
	/**
	 * Save the XML document to <code>storedFile</code> using UTF-8 encoding.
	 * Synchronized saving function to protect the update.
	 * 
	 * @param storedFile The stored file path. 　　
	 */
	public synchronized static void saveToFile (File storedFile, Document doc) {
		//Check the file
		if(storedFile.exists()) 
			storedFile.delete(); 
 
		FileOutputStream fos = null; 
		OutputStreamWriter osw = null; 
		XMLWriter writer = null; 
		try { 
			storedFile.createNewFile(); 
			
			OutputFormat format = OutputFormat.createPrettyPrint();   
			format.setEncoding("utf-8"); 
			fos = new FileOutputStream(storedFile); 
			osw = new OutputStreamWriter(fos, Charset.forName("utf-8")); 
			writer = new XMLWriter(osw, format); 
			writer.write(doc); 
		} catch (IOException e) { 
			e.printStackTrace(); 
		} finally {
			try{
				if(writer != null) writer.close(); 
				if(osw != null) osw.close(); 
				if(fos != null) fos.close(); 
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean readXmlCheckStatus(){
		boolean xmlCheck = false;
		
		return xmlCheck;
	}
	
	
	
	//@TODO type check function
	
	public static void main(String[] args){
		String one="12345";
		//String two="12345";
		
//		for(int idx=0; idx<THREAT_STR_ARR.length; idx++){
//			System.out.println("Check String '"+ THREAT_STR_ARR[idx] + "'=" + xmlUtil.containsThreatChar(one+THREAT_STR_ARR[idx]));
//		}
		
		//System.out.println("Check one=" + xmlUtil.containsThreatChar(one));
		
		
	}

}
