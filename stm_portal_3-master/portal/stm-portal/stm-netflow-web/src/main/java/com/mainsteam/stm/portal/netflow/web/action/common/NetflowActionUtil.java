/**
 * 
 */
package com.mainsteam.stm.portal.netflow.web.action.common;

import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.web.vo.GenericNetflowVo;

/**
 * <li>文件名称: NetflowActionUtil.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年9月4日
 * @author lil
 */
public class NetflowActionUtil {

	public static NetflowParamBo toBo(GenericNetflowVo vo) {
		if (null == vo)
			return null;
		NetflowParamBo bo = new NetflowParamBo();
		if(null != vo.getTimePerid()) {
			bo.setTimePerid(vo.getTimePerid().trim());
		}
		if(null != vo.getEndTime()) {
			bo.setEndTime(vo.getEndTime());
		}
		if(null != vo.getOrder()) {
			bo.setOrder(vo.getOrder());
		}
		bo.setRowCount(vo.getRowCount());
		if(null != vo.getSort()) {
			bo.setSort(vo.getSort());
		}
		bo.setStartRow(vo.getStartRow());
		if(null != vo.getStartTime()) {
			bo.setStartTime(vo.getStartTime());
		}
		bo.setDeviceIp(formatIP(vo.getDeviceIp()));
		bo.setNeedPagination(vo.isNeedPagination());
		bo.setQuerySize(vo.getQuerySize());
		if(null != vo.getIfId()) {
			bo.setIfId(vo.getIfId());
		}
		if(null != vo.getIfGroupId()) {
			bo.setIfGroupId(vo.getIfGroupId());
		}
		if(null != vo.getIpGroupId()) {
			bo.setIpGroupId(vo.getIpGroupId());
		}
		if(null != vo.getName()) {
			bo.setName(vo.getName());
		}
		if(null != vo.getIfName()) {
			bo.setIfName(vo.getIfName());
		}
		if(null != vo.getIpAddr()) {
			bo.setIpAddr(vo.getIpAddr());
		}
 		return bo;
	}

	private static long[] formatIP(String value) {
		if (value != null && !"".equals(value) && !"null".equals(value)) {
			value = value.replaceAll("\\[|\\]", "");
			String[] ipArray = value.split(",");
			long[] ips = new long[ipArray.length];
			for (int i = 0; i < ipArray.length; i++) {
				ips[i] = Long.parseLong(ipArray[i]);
			}
			return ips;
		}
		return null;
	}

	public static NetflowParamBo getBo(GenericNetflowVo vo) {
		if (null == vo)
			return null;
		NetflowParamBo bo = new NetflowParamBo();
		if(null != vo.getTimePerid()) {
			bo.setTimePerid(vo.getTimePerid().trim());
		}
		if(null != vo.getEndTime()) {
			bo.setEndTime(vo.getEndTime());
		}
		if(null != vo.getOrder()) {
			bo.setOrder(vo.getOrder());
		}
		bo.setRowCount(vo.getRowCount());
		if(null != vo.getSort()) {
			bo.setSort(vo.getSort());
		}
		bo.setStartRow(vo.getStartRow());
		if(null != vo.getStartTime()) {
			bo.setStartTime(vo.getStartTime());
		}
		bo.setDeviceIp(formatIP(vo.getDeviceIp()));
		bo.setNeedPagination(vo.isNeedPagination());
		if(null != vo.getIfId()) {
			bo.setIfId(vo.getIfId());
		}
		if(null != vo.getTimeInterval()) {
			bo.setTimeInterval(vo.getTimeInterval());
		}
		if(null != vo.getWholeFlows()) {
			bo.setWholeFlows(vo.getWholeFlows());
		}
		if(null != vo.getWholePackets()) {
			bo.setWholePackets(vo.getWholePackets());
		}
		if(null != vo.getWholeConnects()) {
			bo.setWholeConnects(vo.getWholeConnects());
		}
		if(null != vo.getStime()) {
			bo.setStime(vo.getStime());
		}
		if(null != vo.getEtime()) {
			bo.setEtime(vo.getEtime());
		}
		bo.setQuerySize(vo.getQuerySize());
		if (null != vo.getTableSuffix()) {
			bo.setTableSuffix(vo.getTableSuffix());
		}
		if(null != vo.getIfGroupId()) {
			bo.setIfGroupId(vo.getIfGroupId());
		}
		if(null != vo.getIpGroupId()) {
			bo.setIpGroupId(vo.getIpGroupId());
		}
		return bo;
	}

}
