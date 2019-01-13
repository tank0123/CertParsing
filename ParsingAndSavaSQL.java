package CertParsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsingAndSavaSQL {
	private String [][] ipNTime = new String[36876][];
	private int txtIndex = 0;
	private int identify = 1;

	public ParsingAndSavaSQL() {
		// TODO Auto-generated constructor stub
		 //connect();
		// insert();
		// parsingData();
		txtParsing();
		findCert("C://BoBTest//CertDown//ZipOut");
	}
	
	//ÁÖ¾îÁø txtÆÄÀÏÀ» ÀÐ°í Á¤±Ô½ÄÀ» ÀÌ¿ëÇÏ¿© ¾ÆÀÌÇÇ¸¦ »Ì¾Æ³»°í, ½Ã°£Á¤º¸¸¦ »Ì¾Æ³»´Â ¸Þ¼Òµå
	private void txtParsing() {
		try {
			File file = new File("C://BoBTest//CertDown//IPList.txt");
			
			FileReader fileReader = new FileReader(file);
			BufferedReader bfr = new BufferedReader(fileReader);
			
			String line = new String();
			int i = 0;
			
			while ( (line = bfr.readLine()) != null) {
				ipNTime[i] = line.split("\t");
				
				Pattern dirPattern = Pattern.compile(
						"(([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))");
				Matcher dirMatcher = dirPattern.matcher(ipNTime[i][0]);

				while (dirMatcher.find()) {
					ipNTime[i][0] = dirMatcher.group(2);
				}
				
				i++;
			}
			txtIndex = i;
			bfr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//ÆÄ½ÌÇÑ txtÆÄÀÏÁ¤º¸¿Í(IP,½Ã°£),°ú ÆÄ½ÌÇÑ certµ¥ÀÌÅÍ¸¦ °¡Áö°í DB¿¡ »ðÀÔ
	private void parsingData(String[] dirInfo, String text) {
		
			Pattern filePattern = Pattern
					.compile("^cn=([°¡-ÆR]{1,4})\\(\\)([0-9]*),ou=([a-zA-Z]*),ou=[a-zA-Z]*,o=[a-zA-Z]*,c=(([a-zA-Z]*))$");

			Matcher fileMatcher = filePattern.matcher(text);
			String buffer[] = new String[6];

			while (fileMatcher.find()) {	
				buffer[0] = fileMatcher.group(1);
				buffer[1] = fileMatcher.group(2);
				buffer[2] = fileMatcher.group(3);
				buffer[3] = fileMatcher.group(4);
			}
		
			buffer[4] = dirInfo[1];
			buffer[5] = dirInfo[0];
			/*Pattern dirPattern = Pattern.compile(
					"(([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))");
			Matcher dirMatcher = dirPattern.matcher(dirInfo[0]);

			while (dirMatcher.find()) {
				buffer[5] = dirMatcher.group(2);
			}*/
			
			//System.out.println("1"+buffer[0]+" "+buffer[1]+" "+buffer[2]+" "+buffer[3]+" "+buffer[4]+" "+buffer[5]);
			
			//DB»ðÀÔ ÇÔ¼ö ºÎ¸§
			insert(buffer);
		

	}
	
	//txtÆÄÀÏ ÆÄ½ÌÇØ¼­ µð·ºÅÍ¸® À§Ä¡ ÁöÁ¤ÇØ ÁØ´ÙÀ½ ÀÐ°í ³Ö´Â°Í ´Ù½Ã¸¸µé±â
	private void findCert(String certPath) {
		for (int i = 0; i < txtIndex; i++) {
			String pathBuf = certPath+"\\"+ipNTime[i][0]+"\\signCert.cert";
			parsingData(ipNTime[i], getFileData(pathBuf));
		}
	}
	

	private void isDirFile(String source) {
		File dir = new File(source);
		File[] fileList = dir.listFiles();
		
		for (int i = 0; i < fileList.length; i++) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				// TODO: handle exception
			}
			System.out.println(fileList[i]);
		}
		
		for (int i = 0; i < fileList.length; i++) {
			
			subDirList(fileList[i]);
		}
		
	}
	

	private void subDirList(File dir) {
		
		if (dir.isFile()) {

			// ÆÄÀÏÀÌ ÀÖ´Ù¸é ÆÄÀÏ ÀÌ¸§ Ãâ·Â

			//System.out.println("\t ÆÄÀÏ ÀÌ¸§ = " + file.getPath());
			//System.out.println("\t µð·ºÅÍ¸® ÀÌ¸§ = " + source);
			
			//String buffer = source.substring(source.lastIndexOf("\\")+1 , source.length());
			
			parsingData(ipNTime[txtIndex++], getFileData(dir.getPath()));

		} else if (dir.isDirectory()) {

			//System.out.println("µð·ºÅä¸® ÀÌ¸§ = " + file.getName());
			//parsingData(file.getName(), getFileData(source));
			// ¼­ºêµð·ºÅä¸®°¡ Á¸ÀçÇÏ¸é Àç±ÍÀû ¹æ¹ýÀ¸·Î ´Ù½Ã Å½»ö

			subDirList(dir);
		}

	}
	private Connection connect() {
		// SQLite connection string
		String url = "jdbc:sqlite:C:\\BoBTest\\DB\\certDB.db";
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	private void insert(String strArr[]) {
		String sql = "INSERT INTO cert(id, time, name, bank, account, ip, country) VALUES(?,?,?,?,?,?,?)";

		try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			//System.out.println(strArr[4]+" "+strArr[0]+" "+strArr[2]+" "+strArr[1]+" "+strArr[5]+" "+strArr[3]);
			
			/*try {
				Thread.sleep(1000);
			} catch (Exception e) {
				// TODO: handle exception
			}*/
			
			
			pstmt.setString(1, String.valueOf(identify++));
			pstmt.setString(2, strArr[4]);
			pstmt.setString(3, strArr[0]);
			pstmt.setString(4, strArr[2]);
			pstmt.setString(5, strArr[1]);
			pstmt.setString(6, strArr[5]);
			pstmt.setString(7, strArr[3]);

			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	String getFileData(String FilePath) {
		try {
			File file = new File(FilePath);

			FileReader fileReader = new FileReader(file);
			BufferedReader bfr = new BufferedReader(fileReader);

			return bfr.readLine();
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

}
