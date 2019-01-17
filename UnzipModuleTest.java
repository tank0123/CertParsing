package CertParsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
/*
Source From : http://nowonbun.tistory.com/321
*/

public class UnzipModuleTest {
	
	public UnzipModuleTest() {
		// TODO Auto-generated constructor stub
		decompress("C:\\BoBTest\\Proxy\\2.6.25.21.zip", "C:\\BoBTest\\Proxy");
	}

	private void decompress(String zipFileName, String directory)  {
		File zipFile = new File(zipFileName);
		File dirFile = new File(zipFileName.substring(0, zipFileName.length()-4));
		FileInputStream fis = null;
		ZipInputStream zis = null;
		ZipEntry zipentry = null;
		try {
			// ���� ��Ʈ��
			fis = new FileInputStream(zipFile);
			// Zip ���� ��Ʈ��
			zis = new ZipInputStream(fis);
			// entry�� ���������� �̱�
			while ((zipentry = zis.getNextEntry()) != null) {
				String filename = zipentry.getName();
				File file = new File(directory, filename);
				// entiry�� ������ ���� ����
				dirFile.mkdirs();
				createFile(file, zis, dirFile);
				
				/*if (zipentry.isDirectory()) {
					file.mkdirs();
				} else {
					// �����̸� ���� �����
					createFile(file, zis);
				}*/
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} 
	}

	/**
	 * ���� ����� �޼ҵ�
	 * 
	 * @param file
	 *            ����
	 * @param zis
	 *            Zip��Ʈ��
	 */
	private  void createFile(File file, ZipInputStream zis, File dirFile) {
		/*// ���丮 Ȯ��
		File parentDir = new File(file.getParent());
		// ���丮�� ������ ��������
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}*/
		
		// ���� ��Ʈ�� ����
		try (FileOutputStream fos = new FileOutputStream(dirFile.getAbsolutePath()+"\\"+file.getName())) {
			byte[] buffer = new byte[256];
			int size = 0;
			// Zip��Ʈ�����κ��� byte�̾Ƴ���
			while ((size = zis.read(buffer)) > 0) {
				// byte�� ���� �����
				fos.write(buffer, 0, size);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	// Ŭ���� ����
	/*
	 * public static void main(String[] args){ try{ Zip.decompress("D:\\test.zip",
	 * "D:\\test1"); }catch(Throwable e){ e.printStackTrace(); } }
	 */

}
