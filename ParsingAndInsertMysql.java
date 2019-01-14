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

// �ٿ���� �����͸� �Ľ��ϰ� DB�� �����ϴ� Ŭ����
public class ParsingAndInsertMysql {
	private String[][] ipNTime = new String[36876][];
	private int txtIndex = 0;
	private int identify = 1;
	private Connection conn = null;

	// �����ڿ� ���� �ѹ��� ȣ���
	public ParsingAndInsertMysql() {
		txtParsing();
		findCert("C://BoBTest//CertDown//ZipOut");
	}

	// �־��� txt������ �а� ���Խ��� �̿��Ͽ� �����Ǹ� �̾Ƴ���, �ð������� �̾Ƴ��� �޼ҵ�
	private void txtParsing() {
		try {
			//����� txt���� ��ü ����
			File file = new File("C://BoBTest//CertDown//IPList.txt");

			// �� ������ ���� �� �ִ� ���۸��� ��ü ����
			FileReader fileReader = new FileReader(file);
			BufferedReader bfr = new BufferedReader(fileReader);

			String line = new String();
			int i = 0;

			// txt������ ������ �о �� ������ �����Ͽ� �ð������� �̾Ƴ���, ���Խ��� �̿��Ͽ� ������ �ּҸ� �����س�
			while ((line = bfr.readLine()) != null) {
				ipNTime[i] = line.split("\t");

				Pattern dirPattern = Pattern.compile("(([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))");
				Matcher dirMatcher = dirPattern.matcher(ipNTime[i][0]);
				
				// ����� �ð������� �������ּҴ� ipNTime�̶�� ���ڿ��� �����
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

	// �Ľ��� txt����������(IP,�ð�),�� �Ľ��� cert�����͸� ������ DB�� ����
	private void parsingData(String[] dirInfo, String text) {

		// ���Խ��� ���� �̸�, ��������, ��������, �߱ޱ����� �Ľ�
		Pattern filePattern = Pattern
				.compile("^cn=([��-�R]{1,5})\\(\\)([0-9]*),ou=([a-zA-Z]*),ou=[a-zA-Z]*,o=[a-zA-Z]*,c=(([a-zA-Z]*))$");

		Matcher fileMatcher = filePattern.matcher(text);
		String buffer[] = new String[6];

		while (fileMatcher.find()) {
			buffer[0] = fileMatcher.group(1);
			buffer[1] = fileMatcher.group(2);
			buffer[2] = fileMatcher.group(3);
			buffer[3] = fileMatcher.group(4);
		}

		// �������ּҿ� �ð������� ���ڰ����� ���� ���ڿ��� ���� DB Insert���� ���޵� ���� �迭 �ϼ�
		buffer[4] = dirInfo[1];
		buffer[5] = dirInfo[0];

		//�ϼ��� ���� �迭�� Insert ���ִ� �޼���� ����
		insert(buffer);
	}

	// �Ľ̵� �����ǿ� �ð��ּҸ� �����Ͽ� DB�� �����ϴ� �޼���
	private void findCert(String certPath) {
		// ������ ������ ������ �ݺ����� �����ϸ�, �Ľ̵� ������ ��θ� �о DB�� �������ִ� �޼���� ����
		connect();
		for (int i = 0; i < txtIndex; i++) {
			String pathBuf = certPath + "\\" + ipNTime[i][0] + "\\signCert.cert";
			parsingData(ipNTime[i], getFileData(pathBuf));
		}
	}

	// DB�� ������ ������ ���� �޼���
	private void connect() {
		// SQLite connection string
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/certdb?serverTimezone=UTC&useSSL=false","root","!ta159753");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// DB�� ���� �����ϱ����� �޼���
	private void insert(String strArr[]) {
		String sql = "INSERT INTO cert(id, time, name, bank, account, ip, country) VALUES(?,?,?,?,?,?,?)";
		//�غ�� insert���� value���� ���� �־� ������ ����
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
	
	// ������ ������ �о���̱� ���� �޼���
	String getFileData(String FilePath) {
		try {
			// ���ڷ� ���� ������ ��θ� �޾� ������ ���� �Ľ�
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
