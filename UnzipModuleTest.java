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
			// 파일 스트림
			fis = new FileInputStream(zipFile);
			// Zip 파일 스트림
			zis = new ZipInputStream(fis);
			// entry가 없을때까지 뽑기
			while ((zipentry = zis.getNextEntry()) != null) {
				String filename = zipentry.getName();
				File file = new File(directory, filename);
				// entiry가 폴더면 폴더 생성
				dirFile.mkdirs();
				createFile(file, zis, dirFile);
				
				/*if (zipentry.isDirectory()) {
					file.mkdirs();
				} else {
					// 파일이면 파일 만들기
					createFile(file, zis);
				}*/
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} 
	}

	/**
	 * 파일 만들기 메소드
	 * 
	 * @param file
	 *            파일
	 * @param zis
	 *            Zip스트림
	 */
	private  void createFile(File file, ZipInputStream zis, File dirFile) {
		/*// 디렉토리 확인
		File parentDir = new File(file.getParent());
		// 디렉토리가 없으면 생성하자
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}*/
		
		// 파일 스트림 선언
		try (FileOutputStream fos = new FileOutputStream(dirFile.getAbsolutePath()+"\\"+file.getName())) {
			byte[] buffer = new byte[256];
			int size = 0;
			// Zip스트림으로부터 byte뽑아내기
			while ((size = zis.read(buffer)) > 0) {
				// byte로 파일 만들기
				fos.write(buffer, 0, size);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	// 클래스 사용법
	/*
	 * public static void main(String[] args){ try{ Zip.decompress("D:\\test.zip",
	 * "D:\\test1"); }catch(Throwable e){ e.printStackTrace(); } }
	 */

}
