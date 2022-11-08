/**
 * 
 */
package com.mainsteam.stm.resourcelog.query;

import java.util.Map;

/** 
 * @author 作者：ziw
 * @date 创建时间：2017年11月21日 上午10:18:36
 * @version 1.0
 */
/** 
 */
public class TrapCacheTimeredFresh<T> {
	private DataLoader<T> loader;
	private Map<String, T> cachedData;

	public static interface DataLoader<T> {
		public Map<String, T> load();
	}

	public TrapCacheTimeredFresh(DataLoader<T> loader, String name) {
		super();
		this.loader = loader;
		init(name);
	}

	private void init(String name) {
		cachedData = this.loader.load();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					synchronized (this) {
						try {
							this.wait(30000);// wait 30 seconds
						} catch (InterruptedException e) {
						}
					}
				}
			}
		}, "TrapCacheTimeredFresh-" + name).start();
	}
	
	public T get(String ip){
		return cachedData == null ? null:cachedData.get(ip);
	}
}
