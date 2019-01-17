package CertParsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

// ´Ù¿î¹ŞÀº µ¥ÀÌÅÍ¸¦ ÆÄ½ÌÇÏ°í DB¿¡ ÀúÀåÇÏ´Â Å¬·¡½º
public class ParsingAndInsertMysql {
	private String[][] ipNTime = new String[36876][];
	private int txtIndex = 0;
	private int identify = 1;
	private Connection conn = null;

	// »ı¼ºÀÚ¿¡ ÀÇÇØ ÇÑ¹ø¸¸ È£ÃâµÊ
	public ParsingAndInsertMysql() {
		txtParsing("C://BoBTest//CertDown");
		findCert("C://BoBTest//CertDown//ZipOut");
	}

	// ÁÖ¾îÁø txtÆÄÀÏÀ» ÀĞ°í Á¤±Ô½ÄÀ» ÀÌ¿ëÇÏ¿© ¾ÆÀÌÇÇ¸¦ »Ì¾Æ³»°í, ½Ã°£Á¤º¸¸¦ »Ì¾Æ³»´Â ¸Ş¼Òµå
	private void txtParsing(String Path) {
		try {
			System.out.println("---txt Parsing & Unzip Files ---");
			//ÀúÀåµÈ txtÆÄÀÏ °´Ã¼ »ı¼º
			File file = new File(Path+"//IPList.txt");

			// ÁÙ ´ÜÀ§·Î ÀĞÀ» ¼ö ÀÖ´Â ¹öÆÛ¸®´õ °´Ã¼ »ı¼º
			FileReader fileReader = new FileReader(file);
			BufferedReader bfr = new BufferedReader(fileReader);

			String line = new String();
			int i = 0;

			// txtÆÄÀÏÀÇ ³¡±îÁö ÀĞ¾î¼­ ÅÇ ´ÜÀ§·Î ±¸ºĞÇÏ¿© ½Ã°£Á¤º¸¸¦ »Ì¾Æ³»°í, Á¤±Ô½ÄÀ» ÀÌ¿ëÇÏ¿© ¾ÆÀÌÇÇ ÁÖ¼Ò¸¦ ÃßÃâÇØ³¿
			while ((line = bfr.readLine()) != null) {
				ipNTime[i] = line.split("\t");
				//TOdo µğ·ºÅÍ¸®¿Í ÆÄÀÏ±¸ºĞÇÏ±â ÀÎÀÚ: ÆÄÀÏ ÀÌ¸§°ú ÆÄÀÏ µğ·ºÅÍ¸® °æ·Î
				decompressNmakeNewDir(Path+"\\"+ipNTime[i][0], Path+"\\ZipOut");

				Pattern dirPattern = Pattern.compile("(([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))");
				Matcher dirMatcher = dirPattern.matcher(ipNTime[i][0]);
				
				// ÃßÃâµÈ ½Ã°£Á¤º¸¿Í ¾ÆÀÌÇÇÁÖ¼Ò´Â ipNTimeÀÌ¶ó´Â ¹®ÀÚ¿­¿¡ ÀúÀåµÊ
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

	// ÆÄ½ÌÇÑ txtÆÄÀÏÁ¤º¸¿Í(IP,½Ã°£),°ú ÆÄ½ÌÇÑ certµ¥ÀÌÅÍ¸¦ °¡Áö°í DB¿¡ »ğÀÔ
	private void parsingData(String[] dirInfo, String text) {

		// Á¤±Ô½ÄÀ» ÅëÇØ ÀÌ¸§, °èÁÂÁ¤º¸, ÀºÇàÁ¤º¸, ¹ß±Ş±¹°¡¸¦ ÆÄ½Ì
		Pattern filePattern = Pattern
				.compile("^cn=([°¡-ÆR]{1,5})\\(\\)([0-9]*),ou=([a-zA-Z]*),ou=[a-zA-Z]*,o=[a-zA-Z]*,c=(([a-zA-Z]*))$");

		Matcher fileMatcher = filePattern.matcher(text);
		String buffer[] = new String[6];

		while (fileMatcher.find()) {
			buffer[0] = fileMatcher.group(1);
			buffer[1] = fileMatcher.group(2);
			buffer[2] = fileMatcher.group(3);
			buffer[3] = fileMatcher.group(4);
		}

		// ¾ÆÀÌÇÇÁÖ¼Ò¿Í ½Ã°£Á¤º¸´Â ÀÎÀÚ°ªÀ¸·Î ¹ŞÀº ¹®ÀÚ¿­À» ÅëÇØ DB Insert¹®¿¡ Àü´ŞµÈ ¹öÆÛ ¹è¿­ ¿Ï¼º
		buffer[4] = dirInfo[1];
		buffer[5] = dirInfo[0];

		//¿Ï¼ºµÈ ¹öÆÛ ¹è¿­À» Insert ÇØÁÖ´Â ¸Ş¼­µå·Î Àü´Ş 
		insert(buffer);
	}

	// ÆÄ½ÌµÈ ¾ÆÀÌÇÇ¿Í ½Ã°£ÁÖ¼Ò¸¦ ÅëÇÕÇÏ¿© DB¿¡ »ğÀÔÇÏ´Â ¸Ş¼­µå
	private void findCert(String certPath) {
		System.out.println("---insert value into DB ---");
		// ÀÎÁõ¼­ °¹¼öÀÇ ³¡±îÁö ¹İº¹¹®À» ½ÇÇàÇÏ¸ç, ÆÄ½ÌµÈ ÀÎÁõ¼­ °æ·Î¸¦ ÀĞ¾î¼­ DB¿¡ »ğÀÔÇØÁÖ´Â ¸Ş¼­µå·Î Àü´Ş
		connect();
		for (int i = 0; i < txtIndex; i++) {
			String pathBuf = certPath + "\\" + ipNTime[i][0] + "\\signCert.cert";
			parsingData(ipNTime[i], getFileData(pathBuf));
		}
	}

	// DBÀÇ ¿¬°áÀ» ¼º¸³À» À§ÇÑ ¸Ş¼­µå
	private void connect() {
		// SQLite connection string
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/certdb?serverTimezone=UTC&useSSL=false","root","!ta159753");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// DB¿¡ °ªÀ» »ğÀÔÇÏ±âÀ§ÇÑ ¸Ş¼­µå
	private void insert(String strArr[]) {
		String sql = "INSERT INTO cert(id, time, name, bank, account, ip, country) VALUES(?,?,?,?,?,?,?)";
		//ÁØºñµÈ insert¹®¿¡ value°ªÀ» °¢°¢ ³Ö¾î Äõ¸®¹® ¼öÇà
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
	
	// ÀÎÁõ¼­ ÆÄÀÏÀ» ÀĞ¾îµéÀÌ±â À§ÇÑ ¸Ş¼­µå
	String getFileData(String FilePath) {
		try {
			// ÀÎÀÚ·Î ¹ŞÀº ÀÎÁõ¼­ °æ·Î¸¦ ¹Ş¾Æ ÀÎÁõ¼­ ³»¿ë ÆÄ½Ì
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
	
	private void decompressNmakeNewDir(String zipFileName, String outPutPath) {
		// Ã¹¹øÂ°·Î µé¾î¿À´Â°Í : C:\BoBTest\CertDown\18.179.168.5.zip, µÎ¹øÂ°·Î µé¾î¿À´Â°Í : C:\BoBTest\CertDown\ZipOut
		// ÇÊ¿äÇÑ°Å : 1. ¾ĞÃà Ç® ÆÄÀÏ °æ·Î ¹× ÆÄÀÏ¸í
		// 2. ¾ĞÃà Ç® ÆÄÀÏÀÇ °æ·Î¸í
		// 3. ¾ĞÃà Ç® Æú´õÀÇ °æ·Î
		// 4. °¢ ¾ĞÃà Æú´õÀÇ »ı¼º
		File zipFile = new File(zipFileName);
		String nameBuf = zipFile.getName();
		// C:\\BoBTest\\CertDown\\ZipOut\\IPDir\\cert
		File dirFile = new File(outPutPath+"\\"+nameBuf.substring(0, nameBuf.length()-4));
		FileInputStream fis = null;
		FileOutputStream fos = null;
		ZipInputStream zis = null;
		ZipEntry zipentry = null;
		int size = 0;
		byte[] buffer = new byte[256];
		
		try {
			// ¾ĞÃàÇ¬ ÆÄÀÏÀÇ µğ·ºÅÍ¸® »ı¼º
			dirFile.mkdir();
			
			fis = new FileInputStream(zipFile);
			zis = new ZipInputStream(fis);
			fos = new FileOutputStream(dirFile+"\\signCert.cert");
			while ((zipentry = zis.getNextEntry()) != null) 
			
			while ((size = zis.read(buffer)) > 0) {
				// byte·Î ÆÄÀÏ ¸¸µé±â
				fos.write(buffer, 0, size);
			}
			
			fis.close();
			zis.close();
			fos.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	

}
