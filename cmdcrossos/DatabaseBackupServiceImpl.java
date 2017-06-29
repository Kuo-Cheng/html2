package service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.Date;

//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import utils.DateUtils;
import utils.FtpUtils;
import service.IDatabaseBackupService;
import service.exception.ServiceException;

@Service
public class DatabaseBackupServiceImpl implements IDatabaseBackupService {
	protected static final Log log = LogFactory.getLog(DatabaseBackupServiceImpl.class);
	 /** MySQL install path */  
	@Value("${mysqlBinPath}")
    private String mysqlBinPath;  
    
	/** The backup sql file name. */
	@Value("${backupSqlFile}")
	private String bakSqlFile;
	//private String bakSqlFile="./WCMLicense_bak.sql";
	/** The backup sql file path. */
	@Value("${backupSqlPath}")
	private String bakSqlPath;
	
	
	@Value("${srcHost}")
    private String srcHost;
    @Value("${srcDbname}")
    private String srcDbname;
    @Value("${srcUsername}")
    private String srcUsername;
    @Value("${srcPassword}")
    private String srcPassword;
    
    @Value("${dstHost}")
    private String dstHost;
    @Value("${dstDbname}")
    private String dstDbname;
    @Value("${dstUsername}")
    private String dstUsername;
    @Value("${dstPassword}")
    private String dstPassword;
    
    @Value("${ftp.url}")
	private String ftpUrl;
	@Value("${ftp.port}")
	private int ftpPort;
	@Value("${ftp.username}")
	private String username;
	@Value("${ftp.password}")
	private String password;
	@Value("${ftp.bak.path}")
	private String ftpPath;
		
//  /** Work on Windows 10 and fail on Windows 7. The generated file is zero size. */
//	@Override
//	public void backup() throws ServiceException {
//		String command = "cmd /c mysqldump --lock-all-tables --flush-logs -h " + srcHost + " -u" + srcUsername + " -p" + srcPassword + " --databases "+srcDbname;  
//		log.info("backup command="+command);
//		System.out.println("backup command="+command);
//		PrintWriter p = null;  
//        BufferedReader reader = null;  
//        try {  
//            p = new PrintWriter(new OutputStreamWriter(new FileOutputStream(bakSqlFile), "utf8"));  
//            Process process = Runtime.getRuntime().exec(command);  
//            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(), "utf8");  
//            reader = new BufferedReader(inputStreamReader);  
//            String line = null;  
//            while ((line = reader.readLine()) != null) {
//            	System.out.println("line="+line);
//                p.println(line);  
//            }  
//            p.flush();  
//        } catch (Exception e) {  
//        	e.printStackTrace();
//            throw new ServiceException(e); 
//        } finally {  
//            try {  
//                if (reader != null) {  
//                    reader.close();  
//                }  
//                if (p != null) {  
//                    p.close();  
//                }  
//            } catch (IOException e) {  
//                e.printStackTrace();  
//            }  
//        }  
//	}

	/**
	 * To be compatible to Windows 7 and 10 (the original function could just work on win 10), 
	 * Using the ProcessBuilder as the main class to initialize the process and generate the file.
	 * The ProcessBuilder receives the command as the separated string (by space).
	 * The backup file couldn't be generated without calling waitFor().
	 * After this function, the uploadFtp() will be called, and upload this file to remote, one day one file.
	 * The default path of generated file is located on C:\\
	 * This path is defined on the properties backup.properties. 
	 *  
	 */
	@Override
	public void backup() throws ServiceException {
        Process proc = null;
        
        try {
        	ProcessBuilder procBuilder = new ProcessBuilder("cmd", "/c", mysqlBinPath + "mysqldump","--lock-all-tables","--flush-logs","-h",srcHost,"-u"+srcUsername, "-p"+srcPassword, "--databases",srcDbname);

        	procBuilder.directory(new File(bakSqlPath));
			File sqlBackupFile = new File(bakSqlPath + System.getProperty("file.separator") + bakSqlFile);
			sqlBackupFile.delete();
			sqlBackupFile.createNewFile();	
			
			procBuilder.redirectErrorStream(true);
			procBuilder.redirectOutput(Redirect.to(sqlBackupFile));
			proc = procBuilder.start();
			proc.waitFor();
        } catch (Exception e) {  
        	e.printStackTrace();
            throw new ServiceException(e); 
        } finally {  
            try {
                if (proc != null) {  
                    proc.destroy(); 
                } 
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
	}

	
	@Override
	public void restore() throws ServiceException {
		String command = "cmd /c mysql "+ dstDbname+ " -h "+ dstHost +" -u " + dstUsername  
                 + " -p" + dstPassword +" <"+bakSqlFile;  
		log.info("restore command="+command);
		System.out.println("Backup command="+command);
		try {
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			 throw new ServiceException(e); 
		}
	}

	@Override
	public boolean uploadFtp() throws ServiceException {
		try{
			FileInputStream fis=new FileInputStream(bakSqlPath+System.getProperty("file.separator")+bakSqlFile);
			FTPClient ftp=FtpUtils.getFtpClient(ftpUrl, ftpPort, username, password);
			boolean flag=FtpUtils.uploadFile(ftp, ftpPath, "WCMLicense_Bak_"+DateUtils.format(new Date(), DateUtils.YMD_DASH)+".sql", fis);
			log.info("uploadFtp isOK?="+flag);
			return flag;
		}catch(Exception e){
			throw new ServiceException(e);
		}
		
		
	}

}
