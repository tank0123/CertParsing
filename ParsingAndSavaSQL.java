package CertParsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// ´Ù¿î¹ŞÀº µ¥ÀÌÅÍ¸¦ ÆÄ½ÌÇÏ°í DB¿¡ ÀúÀåÇÏ´Â Å¬·¡½º
public class ParsingAndSavaSQL {
	private String[][] ipNTime = new String[36876][];
	private int txtIndex = 0;
	private int identify = 1;

	// »ı¼ºÀÚ¿¡ ÀÇÇØ ÇÑ¹ø¸¸ È£ÃâµÊ
	public ParsingAndSavaSQL() {
		txtParsing();
		findCert("C://BoBTest//CertDown//ZipOut");
	}

	// ÁÖ¾îÁø txtÆÄÀÏÀ» ÀĞ°í Á¤±Ô½ÄÀ» ÀÌ¿ëÇÏ¿© ¾ÆÀÌÇÇ¸¦ »Ì¾Æ³»°í, ½Ã°£Á¤º¸¸¦ »Ì¾Æ³»´Â ¸Ş¼Òµå
	private void txtParsing() {
		try {
			//ÀúÀåµÈ txtÆÄÀÏ °´Ã¼ »ı¼º
			File file = new File("C://BoBTest//CertDown//IPList.txt");

			// ÁÙ ´ÜÀ§·Î ÀĞÀ» ¼ö ÀÖ´Â ¹öÆÛ¸®´õ °´Ã¼ »ı¼º
			FileReader fileReader = new FileReader(file);
			BufferedReader bfr = new BufferedReader(fileReader);

			String line = new String();
			int i = 0;

			// txtÆÄÀÏÀÇ ³¡±îÁö ÀĞ¾î¼­ ÅÇ ´ÜÀ§·Î ±¸ºĞÇÏ¿© ½Ã°£Á¤º¸¸¦ »Ì¾Æ³»°í, Á¤±Ô½ÄÀ» ÀÌ¿ëÇÏ¿© ¾ÆÀÌÇÇ ÁÖ¼Ò¸¦ ÃßÃâÇØ³¿
			while ((line = bfr.readLine()) != null) {
				ipNTime[i] = line.split("\t");

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
				.compile("^cn=([°¡-ÆR]{1,4})\\(\\)([0-9]*),ou=([a-zA-Z]*),ou=[a-zA-Z]*,o=[a-zA-Z]*,c=(([a-zA-Z]*))$");

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
		// ÀÎÁõ¼­ °¹¼öÀÇ ³¡±îÁö ¹İº¹¹®À» ½ÇÇàÇÏ¸ç, ÆÄ½ÌµÈ ÀÎÁõ¼­ °æ·Î¸¦ ÀĞ¾î¼­ DB¿¡ »ğÀÔÇØÁÖ´Â ¸Ş¼­µå·Î Àü´Ş
		for (int i = 0; i < txtIndex; i++) {
			String pathBuf = certPath + "\\" + ipNTime[i][0] + "\\signCert.cert";
			parsingData(ipNTime[i], getFileData(pathBuf));
		}
	}

	// DBÀÇ ¿¬°áÀ» ¼º¸³À» À§ÇÑ ¸Ş¼­µå
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

	// DB¿¡ °ªÀ» »ğÀÔÇÏ±âÀ§ÇÑ ¸Ş¼­µå
	private void insert(String strArr[]) {
		String sql = "INSERT INTO cert(id, time, name, bank, account, ip, country) VALUES(?,?,?,?,?,?,?)";
		//ÁØºñµÈ insert¹®¿¡ value°ªÀ» °¢°¢ ³Ö¾î Äõ¸®¹® ¼öÇà
		try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

}
