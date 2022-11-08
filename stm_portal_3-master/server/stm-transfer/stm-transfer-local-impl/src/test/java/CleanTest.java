import com.mainsteam.stm.transfer.local.maintain.LocalFileCleaner;

/** 
 * @author 作者：ziw
 * @date 创建时间：2017年6月19日 上午10:26:13
 * @version 1.0
 */
public class CleanTest {

	public CleanTest() {
	}
	
	public static void main(String[] args) {
		String dataDir = args[0];
		LocalFileCleaner localFileCleaner = new LocalFileCleaner(dataDir, 60*1000L);
		localFileCleaner.scanAndRemoveFile();
	}

}
