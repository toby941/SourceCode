package com.tuikai;

import java.util.HashMap;
import java.util.Map;

public class MapSlab extends Slab {

	static int mapSize=100000*20;
	private static Map<Integer,Object> cacheMap=new HashMap<Integer,Object>(size);
	
	public static void main(String[] args) throws InterruptedException {
		 GCMonitoring.init();
			logGCInfo(0);
			for(int i=0;i<mapSize;i++){
				cacheMap.put(i, new Long(i));
				if(i% 100000==0){
					logGCInfo(i/100000);
					if(i/100000>100){
						break; 
					}
				}
			}
			for(int i=0;i<mapSize;i++){
				if(i%2==0){
					cacheMap.remove(i);
				}
				if(i% 100000==0){
					logGCInfo(i/100000);
				}
			}
			System.gc();
			Thread.sleep(1000L*5);
			System.gc();
			logGCInfo(0);
			for(int i=0;i<mapSize;i++){
				if(i%2==0){
					cacheMap.put(i, new BigLong()); 
					//cacheMap.put(i, new Long(i));
				}
				if(i% 100000==0){
					logGCInfo(i/100000);
				}
			}
	}
}
