package com.tuikai;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

public class GuavaSlab extends Slab {

	static int mapSize=100000*20;
	static Cache<Integer, Object> guavaCache=CacheBuilder.newBuilder().initialCapacity(mapSize).maximumSize(size).build(new CacheLoader<Integer, Object>(){

		@Override
		public Object load(Integer i) throws Exception {
			return new Long(i);
		}});
	
	
	public static void main(String[] args) throws InterruptedException {
		 GCMonitoring.init();
			logGCInfo(0);
			for(int i=0;i<mapSize;i++){
				guavaCache.put(i, new Long(i));
				if(i% 100000==0){
					logGCInfo(i/100000);
					if(i/100000>100){
						break; 
					}
				}
			}
			for(int i=0;i<mapSize;i++){
				if(i%2==0){
					guavaCache.invalidate(i);
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
					guavaCache.put(i, new BigLong()); 
					//cacheMap.put(i, new Long(i));
				}
				if(i% 100000==0){
					logGCInfo(i/100000);
				}
			}
	}
}
