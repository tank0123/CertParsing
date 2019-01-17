package CertParsing;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

// ������ URL�� ���� ������ �ٿ�޴� Ŭ����
public class GetFileFromURL {
	final static int size = 1024;

	// �����ڸ� ���� �ѹ��� �����
	public GetFileFromURL() {
		readIPAndDownload();
	}

	// ����� TXT������ �Ľ��Ͽ� ������ �ٿ�޴� �޼���
	private void readIPAndDownload() {
		try {
			System.out.println("-------Download Start------");
			// ����������� ���� ���� ��ü ����
			File file = new File("C://BoBTest//CertDown//IPList.txt");

			// Buffered Reader�� ���� ���پ� �Է�
			FileReader fileReader = new FileReader(file);
			BufferedReader bfr = new BufferedReader(fileReader);

			String line = new String();
			String tabSplit[];

			// ��� ���� ������ ���� ���� �б�
			while ((line = bfr.readLine()) != null) {
				// ���� �������� ���ڿ��� �ڸ�
				tabSplit = line.split("\t");
				// �ڸ� ���ڿ��� ù��° ���� �������ּҿ� URL�ּҸ� �����Ͽ� �ٿ� ��ũ �ϼ�
				String Buf = "http://fl0ckfl0ck.work/cert/" + tabSplit[0];
				// �ϼ��� �ٿ� ��ũ�� �����̸�, ���� �ٿ� ��η� �Է¹��� ���� ���� ���ڷ� �־� ���� �ٿ� �޼��� ȣ��
				fileUrlReadAndDownload(Buf, tabSplit[0], "C://BoBTest//CertDown");
			}
			System.out.println("It's Done!!");
			bfr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ������ URL�ּҿ� �����̸�, �ٿ�ε� ��θ� �޾� ������ �ٿ�޴� �޼���
	private void fileUrlReadAndDownload(String fileAddress, String localFileName, String downloadDir) {

		OutputStream outStream = null;
		URLConnection uCon = null;

		InputStream is = null;
		try {

			URL Url;
			byte[] buf;
			int byteRead;
			int byteWritten = 0;
			// �Է¹��� URL�ּҷ� URL��ü ����
			Url = new URL(fileAddress);
			// ������ �ٿ�޴� ���� ��Ʈ�� ��ü ����
			outStream = new BufferedOutputStream(new FileOutputStream(downloadDir + "\\" + localFileName));

			// URL�� ���� ����
			uCon = Url.openConnection();
			// ������ ���� ���� �ٿ�ε� ����
			is = uCon.getInputStream();
			buf = new byte[size];
			while ((byteRead = is.read(buf)) != -1) {
				outStream.write(buf, 0, byteRead);
				byteWritten += byteRead;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
