package com.apkclass.study;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import com.apkclass.ui.R;
import com.apkclass.ui.XmlParser;
import com.apkclass.utils.log;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class CodeProvider {

	private static final String TAG = "CodeProvider";
	private static final int BUFFER_SIZE = 64;
	Context context;

	public CodeProvider(Context context) {
		super();
		this.context = context;
	}

	/**
	 * 通过网络取得题目集合
	 * 
	 * @param typenumber
	 *            题目类型区别编号
	 * @return 题目集合
	 */
	public ArrayList<CodeBean> getCodeListFromServer(int typenumber) {

		log.e("getCodeListFromServer");
		return null;
	}

	/**
	 * 取得本地题目集合
	 * 
	 * @param typenumber
	 *            题目类型区别编号
	 * @return 题目集合
	 */
	public ArrayList<CodeBean> getCodeListFromFile(int typenumber) {

		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			File downLoadPath = context.getExternalFilesDir(null);
			// Log.e(TAG, "downLoadPath:"+downLoadPath);
			// downLoadPath:/storage/emulated/0/Android/data/com.apkclass.ui/files
			log.e("downLoadPath:" + downLoadPath);

			// cache文件夹下storage目录为题库存放目录。
			String path_storage = downLoadPath.getPath() + "/storage";

			File file = new File(path_storage);
			if (!file.exists()) {
				file.mkdir();
				// 创建目录后从网站下载题库
                log.e("xml file not exist");
				getCodeListFromServer(typenumber);
			} else {
				String path_files = downLoadPath.getPath() + "/storage/"
						+ typenumber;
				File file2 = new File(path_files);

				if (!file2.exists()) {
					// 如果文件不存在，则需要从网站下载
					// getCodeListFromServer(typenumber);

					// 暂时先从raw中读入
					InputStream is = context.getResources().openRawResource(
							R.raw.code);
					try {
						FileOutputStream fos = new FileOutputStream(file2);
						if (fos == null) {
							log.e("fos:" + fos);
						}
						byte[] buffer = new byte[BUFFER_SIZE];
						int count = 0;
						while ((count = is.read(buffer)) > 0) {
							fos.write(buffer, 0, count);
							fos.flush();
						}
						fos.close();
						is.close();
						log.e("1");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// 解析xml得到题目集合
				// 文件已经拷贝到指定文件夹，重新读出，打开
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file2);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ArrayList<CodeBean> codeBeanList = XmlParser.readXML(fis);
				if (codeBeanList != null) {
					return codeBeanList;
				}else {
					log.e("codeBeanList is:"+codeBeanList);
				}
			}
		}
		return null;
	}
}
