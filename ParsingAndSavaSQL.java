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
	
	//주어진 txt파일을 읽고 정규식을 이용하여 아이피를 뽑아내고, 시간정보를 뽑아내는 메소드
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

	//파싱한 txt파일정보와(IP,시간),과 파싱한 cert데이터를 가지고 DB에 삽입
	private void parsingData(String[] dirInfo, String text) {
		
			Pattern filePattern = Pattern
					.compile("^cn=([가-힣]{1,4})\\(\\)([0-9]*),ou=([a-zA-Z]*),ou=[a-zA-Z]*,o=[a-zA-Z]*,c=(([a-zA-Z]*))$");

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
			
			//DB삽입 함수 부름
			insert(buffer);
		

	}
	
	//txt파일 파싱해서 디렉터리 위치 지정해 준다음 읽고 넣는것 다시만들기
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

			// 파일이 있다면 파일 이름 출력

			//System.out.println("\t 파일 이름 = " + file.getPath());
			//System.out.println("\t 디렉터리 이름 = " + source);
			
			//String buffer = source.substring(source.lastIndexOf("\\")+1 , source.length());
			
			parsingData(ipNTime[txtIndex++], getFileData(dir.getPath()));

		} else if (dir.isDirectory()) {

			//System.out.println("디렉토리 이름 = " + file.getName());
			//parsingData(file.getName(), getFileData(source));
			// 서브디렉토리가 존재하면 재귀적 방법으로 다시 탐색

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
