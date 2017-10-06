package service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StrategyCall {
	
	/** Construct the class. */
	public StrategyCall(int callType){
		setCallType(callType);
	}
	
	public static int CALL_TYPE_ONE=1;
	
	public static int CALL_TYPE_TWO=2;
	
	private int callType = 0;
	
	public int getCallType(){
		return this.callType;
	}
	
	public void setCallType(int ctype){
		this.callType = ctype;
	}
	
	public void call(int version, int type){
		if(callType==CALL_TYPE_ONE){
			callTypeOne(version,type);
		}else if(callType==CALL_TYPE_TWO){
			callTypeTwo(version,type);
		}
		
	}
	
	/** It the default call. */
	public void callTypeOne(int version, int type){
		System.out.println("callTypeOne!");
		
		if(version == 1){//new version license sent by player
			if(type==0){
				System.out.println("Version 1 Type 0 result true");
			}else if(type==1){
				System.out.println("Version 1 Type 1 result true");
			}else{
				System.out.println("Version 1 Type other result true");
			}	
		}else {//other version
			if(type == 0){
				System.out.println("Version other Type 0 result true");
			}else if(type == 1){
				System.out.println("Version other Type 1 result false");
			}else{
				System.out.println("Version other Type other result false");
			}
		}
		
	}
	/** It the another call. */
	public void callTypeTwo(int version, int type){
		//System.out.println("callTypeTwo!");
		
		String jsonStr = getJsonString();
		
		ObjectMapper objMapper = new ObjectMapper();
		
		try {
			Map<String,Object> map = objMapper.readValue(jsonStr, Map.class);
			//System.out.println("map="+map.toString());
			for (Map.Entry e : map.entrySet()){
			    //System.out.println(e.getKey() + ": " + e.getValue());
			    
			    List<Map> childList = (List<Map>) e.getValue();
			    if(childList!=null && childList.size()>0){
			    	//System.out.println("childList.size()="+childList.size());
			    	outer:
			    	for(int idx=0;idx<childList.size(); idx++){
			    		Map<String,String> mapChild=childList.get(idx);
			    		String zeroRs="", oneRs="", otherRs="";
			    		boolean run=false;
			    		for (Map.Entry<String,String> child : mapChild.entrySet()){
			    			//System.out.println(child.getKey() + ": " + child.getValue());
			    			if("vtype".equalsIgnoreCase(child.getKey())){
			    				run=true;
			    				System.out.println("run!");
			    			}
			    			
			    			if(run==true && "0".equalsIgnoreCase(child.getKey())){
			    				zeroRs = String.valueOf(child.getValue());
			    			}
			    			
			    			if(run==true && "1".equalsIgnoreCase(child.getKey())){
			    				oneRs = String.valueOf(child.getValue());
			    			}
			    			
			    			if(run==true && "other".equalsIgnoreCase(child.getKey())){
			    				otherRs = String.valueOf(child.getValue());
			    				
			    				System.out.println("version="+version+",type="+type);
					    		System.out.println("zeroRs="+zeroRs);
					    		System.out.println("oneRs="+oneRs);
					    		System.out.println("otherRs="+otherRs);
			    				break outer;
			    			}
			    		}//end for	
			    		
			    		
			    		
			    		
			    	}
			    }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

		
	}
	
	/** Get json string somewhere, ex:properties, xml, database. */
	public String getJsonString(){
		String jsonStr="{" +
				"\"versionmap\": [" +
					"{"+
						"\"vtype\": \"1\","+
						"\"0\": true," +
						"\"1\": true,"+
						"\"other\": false"+
					"},"+
					"{"+
						"\"vtype\": \"2\","+
						"\"0\": true,"+
						"\"1\": false,"+
						"\"other\": false"+
					"},"+
					"{"+
						"\"vtype\": \"other\","+
						"\"0\": true,"+
						"\"1\": false,"+
						"\"other\": false"+
					"}"+
				"]"+
			"}";
		return jsonStr;
	}
	
	/**
	 * {["vtype":"1","0":true,"1":true,"other":false},{"vtype":"other","0":true,"1":false,"other":false}]}
	 {
	"versionmap": [
		{
			"vtype": "1",
			"0": true,
			"1": true,
			"other": false
		},
		{
			"vtype": "2",
			"0": true,
			"1": false,
			"other": false
		},
		{
			"vtype": "other",
			"0": true,
			"1": false,
			"other": false
		}
	]
}
	 * @param args
	 */
	public static void main(String[] args){
//		StrategyCall startegyOne = new StrategyCall(CALL_TYPE_ONE);
//		startegyOne.call(0,0);
//		startegyOne.call(0,1);
//		startegyOne.call(1,0);
//		startegyOne.call(1,1);
//		startegyOne.call(2,0);
//		startegyOne.call(2,1);
		
		StrategyCall startegyTwo = new StrategyCall(CALL_TYPE_TWO);
		startegyTwo.call(1,0);
		startegyTwo.call(1,1);
		
		startegyTwo.call(0,0);
		startegyTwo.call(0,1);
		
		startegyTwo.call(2,0);
		startegyTwo.call(2,1);
				
	}
}
