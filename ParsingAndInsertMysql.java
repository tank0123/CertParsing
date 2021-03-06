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

// 다운받은 데이터를 파싱하고 DB에 저장하는 클래스
public class ParsingAndInsertMysql {
	private String[][] ipNTime = new String[36876][];
	private int txtIndex = 0;
	private int identify = 1;
	private Connection conn = null;

	// 생성자에 의해 한번만 호출됨
	public ParsingAndInsertMysql() {
		txtParsing("C://BoBTest//CertDown");
		findCert("C://BoBTest//CertDown//ZipOut");
	}

	// 주어진 txt파일을 읽고 정규식을 이용하여 아이피를 뽑아내고, 시간정보를 뽑아내는 메소드
	private void txtParsing(String Path) {
		try {
			System.out.println("---txt Parsing & Unzip Files ---");
			//저장된 txt파일 객체 생성
			File file = new File(Path+"//IPList.txt");

			// 줄 단위로 읽을 수 있는 버퍼리더 객체 생성
			FileReader fileReader = new FileReader(file);
			BufferedReader bfr = new BufferedReader(fileReader);

			String line = new String();
			int i = 0;

			// txt파일의 끝까지 읽어서 탭 단위로 구분하여 시간정보를 뽑아내고, 정규식을 이용하여 아이피 주소를 추출해냄
			while ((line = bfr.readLine()) != null) {
				ipNTime[i] = line.split("\t");
				//TOdo 디렉터리와 파일구분하기 인자: 파일 이름과 파일 디렉터리 경로
				decompressNmakeNewDir(Path+"\\"+ipNTime[i][0], Path+"\\ZipOut");

				Pattern dirPattern = Pattern.compile("(([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))");
				Matcher dirMatcher = dirPattern.matcher(ipNTime[i][0]);
				
				// 추출된 시간정보와 아이피주소는 ipNTime이라는 문자열에 저장됨
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

	// 파싱한 txt파일정보와(IP,시간),과 파싱한 cert데이터를 가지고 DB에 삽입
	private void parsingData(String[] dirInfo, String text) {

		// 정규식을 통해 이름, 계좌정보, 은행정보, 발급국가를 파싱
		Pattern filePattern = Pattern
				.compile("^cn=([가-힣]{1,5})\\(\\)([0-9]*),ou=([a-zA-Z]*),ou=[a-zA-Z]*,o=[a-zA-Z]*,c=(([a-zA-Z]*))$");

		Matcher fileMatcher = filePattern.matcher(text);
		String buffer[] = new String[6];

		while (fileMatcher.find()) {
			buffer[0] = fileMatcher.group(1);
			buffer[1] = fileMatcher.group(2);
			buffer[2] = fileMatcher.group(3);
			buffer[3] = fileMatcher.group(4);
		}

		// 아이피주소와 시간정보는 인자값으로 받은 문자열을 통해 DB Insert문에 전달된 버퍼 배열 완성
		buffer[4] = dirInfo[1];
		buffer[5] = dirInfo[0];

		//완성된 버퍼 배열을 Insert 해주는 메서드로 전달 
		insert(buffer);
	}

	// 파싱된 아이피와 시간주소를 통합하여 DB에 삽입하는 메서드
	private void findCert(String certPath) {
		System.out.println("---insert value into DB ---");
		// 인증서 갯수의 끝까지 반복문을 실행하며, 파싱된 인증서 경로를 읽어서 DB에 삽입해주는 메서드로 전달
		connect();
		for (int i = 0; i < txtIndex; i++) {
			String pathBuf = certPath + "\\" + ipNTime[i][0] + "\\signCert.cert";
			parsingData(ipNTime[i], getFileData(pathBuf));
		}
	}

	// DB의 연결을 성립을 위한 메서드
	private void connect() {
		// SQLite connection string
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/certdb?serverTimezone=UTC&useSSL=false","root","!ta159753");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// DB에 값을 삽입하기위한 메서드
	private void insert(String strArr[]) {
		String sql = "INSERT INTO cert(id, time, name, bank, account, ip, country) VALUES(?,?,?,?,?,?,?)";
		//준비된 insert문에 value값을 각각 넣어 쿼리문 수행
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
	
	// 인증서 파일을 읽어들이기 위한 메서드
	String getFileData(String FilePath) {
		try {
			// 인자로 받은 인증서 경로를 받아 인증서 내용 파싱
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
		// 첫번째로 들어오는것 : C:\BoBTest\CertDown\18.179.168.5.zip, 두번째로 들어오는것 : C:\BoBTest\CertDown\ZipOut
		// 필요한거 : 1. 압축 풀 파일 경로 및 파일명
		// 2. 압축 풀 파일의 경로명
		// 3. 압축 풀 폴더의 경로
		// 4. 각 압축 폴더의 생성
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
			// 압축푼 파일의 디렉터리 생성
			dirFile.mkdir();
			
			fis = new FileInputStream(zipFile);
			zis = new ZipInputStream(fis);
			fos = new FileOutputStream(dirFile+"\\signCert.cert");
			while ((zipentry = zis.getNextEntry()) != null) 
			
			while ((size = zis.read(buffer)) > 0) {
				// byte로 파일 만들기
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
