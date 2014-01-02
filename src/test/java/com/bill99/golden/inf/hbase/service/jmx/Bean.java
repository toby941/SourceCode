package com.bill99.golden.inf.hbase.service.jmx;

/**
 * @author jun.bao
 * @since 2013年12月19日
 */
public class Bean {

	private String name;
	private RegionServers[] regionServers;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RegionServers[] getRegionServers() {
		return regionServers;
	}

	public void setRegionServers(RegionServers[] regionServers) {
		this.regionServers = regionServers;
	}

}
