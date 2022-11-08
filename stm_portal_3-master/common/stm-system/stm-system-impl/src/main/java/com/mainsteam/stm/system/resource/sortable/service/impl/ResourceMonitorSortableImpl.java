package com.mainsteam.stm.system.resource.sortable.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.resource.sortable.api.IResourceSortableBySingleFieldApi;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.util.Util;

/**
 * 资源列表排序
 * 
 * @author Administrator
 *
 */
public class ResourceMonitorSortableImpl implements
		IResourceSortableBySingleFieldApi {
	@Resource
	private CapacityService capacityService;
	
	@Resource
	private IDomainApi domainApi;
	@Override
	public List<ResourceInstanceBo> sort(List<ResourceInstanceBo> resources,
			final String field, String order) {
		
		if (!Util.isEmpty(order)) {
			if ("ASC".equals(order.toUpperCase())) {
				Collections.sort(resources, new Comparator<ResourceInstanceBo>() {
					@Override
					public int compare(ResourceInstanceBo bo1, ResourceInstanceBo bo2) {
                       if("ipAddress".equals(field)){
                    	   if(bo1.getDiscoverIP() == null && bo2.getDiscoverIP() == null){
                    		   return 0;
                    	   }else if(bo1.getDiscoverIP() == null && bo2.getDiscoverIP() != null){
                    		   return -1;
                    	   }else if(bo1.getDiscoverIP() != null && bo2.getDiscoverIP() == null){
                    		   return 1;
                    	   }else{
                    		   if(bo1.getDiscoverIP().compareToIgnoreCase(bo2.getDiscoverIP()) == 0){
                    			   return 0;
                    		   }else if(bo1.getDiscoverIP().compareToIgnoreCase(bo2.getDiscoverIP()) > 0){
                    			   return 1; 
                    		   }else{
                    			   return -1; 
                    		   }
                    	   }
                       }
                       
                       if("sourceName".equals(field)){
                    	   if(bo1.getShowName() == null && bo2.getShowName() == null){
                    		   return 0;
                    	   }else if(bo1.getShowName() == null && bo2.getShowName() != null){
                    		   return -1;
                    	   }else if(bo1.getShowName() != null && bo2.getShowName() == null){
                    		   return 1;
                    	   }else{
                    		   if(bo1.getShowName().compareToIgnoreCase(bo2.getShowName()) == 0){
                    			   return 0;
                    		   }else if(bo1.getShowName().compareToIgnoreCase(bo2.getShowName()) > 0){
                    			   return 1; 
                    		   }else{
                    			   return -1; 
                    		   }
                    	   }
                       }
                       
                       if("monitorType".equals(field)){
                    	   String monitorTypeOne = null;
                    	   String monitorTypeTwo = null;
                    	  
                    	   ResourceDef rdfOne =   capacityService.getResourceDefById(bo1.getResourceId());
                    	   if(rdfOne != null){
                    		   monitorTypeOne = rdfOne.getName();
                    	   }
                    	   ResourceDef rdfTwo= capacityService.getResourceDefById(bo2.getResourceId());
                    	   if(rdfTwo != null){
                    		   monitorTypeTwo = rdfTwo.getName();
                    	   }
                    	   if(monitorTypeOne == null && monitorTypeTwo == null){
                    		   return 0;
                    	   }else if(monitorTypeOne == null && monitorTypeTwo != null){
                    		   return -1;
                    	   }else if(monitorTypeOne != null && monitorTypeTwo == null){
                    		   return 1;
                    	   }else{
                    		   if(monitorTypeOne.compareToIgnoreCase(monitorTypeTwo) == 0){
                    			   return 0;
                    		   }else if(monitorTypeOne.compareToIgnoreCase(monitorTypeTwo) > 0){
                    			   return 1; 
                    		   }else{
                    			   return -1; 
                    		   }
                    	   }
                       }
                       
                       if("domainName".equals(field)){
							String domainNameOne = null;
							String domainNameTwo = null;
							Domain domainOne =  domainApi.get(bo1.getDomainId());
							Domain domainTwo =  domainApi.get(bo2.getDomainId());
							if(domainOne != null){
								domainNameOne = domainOne.getName();
							}
							if(domainTwo != null){
								domainNameTwo = domainTwo.getName();
							}
							 if(domainNameOne == null && domainNameTwo == null){
	                    		   return 0;
	                    	   }else if(domainNameOne == null && domainNameTwo != null){
	                    		   return -1;
	                    	   }else if(domainNameOne != null && domainNameTwo == null){
	                    		   return 1;
	                    	   }else{
	                    		   if(domainNameOne.compareToIgnoreCase(domainNameTwo) == 0){
	                    			   return 0;
	                    		   }else if(domainNameOne.compareToIgnoreCase(domainNameTwo) > 0){
	                    			   return 1; 
	                    		   }else{
	                    			   return -1; 
	                    		   }
	                    	   }
						}
						return 0;
					}

				});
			}else{
				Collections.sort(resources, new Comparator<ResourceInstanceBo>() {
					@Override
					public int compare(ResourceInstanceBo bo1, ResourceInstanceBo bo2) {
                       if("ipAddress".equals(field)){
                    	   if(bo1.getDiscoverIP() == null && bo2.getDiscoverIP() == null){
                    		   return 0;
                    	   }else if(bo1.getDiscoverIP() == null && bo2.getDiscoverIP() != null){
                    		   return 1;
                    	   }else if(bo1.getDiscoverIP() != null && bo2.getDiscoverIP() == null){
                    		   return -1;
                    	   }else{
                    		   if(bo1.getDiscoverIP().compareToIgnoreCase(bo2.getDiscoverIP()) == 0){
                    			   return 0;
                    		   }else if(bo1.getDiscoverIP().compareToIgnoreCase(bo2.getDiscoverIP()) >  0){
                    			   return -1; 
                    		   }else{
                    			   return 1; 
                    		   }
                    	   }
                       }
                       
                       if("sourceName".equals(field)){
                    	   if(bo1.getShowName() == null && bo2.getShowName() == null){
                    		   return 0;
                    	   }else if(bo1.getShowName() == null && bo2.getShowName() != null){
                    		   return 1;
                    	   }else if(bo1.getShowName() != null && bo2.getShowName() == null){
                    		   return -1;
                    	   }else{
                    		   if(bo1.getShowName().compareToIgnoreCase(bo2.getShowName()) == 0){
                    			   return 0;
                    		   }else if(bo1.getShowName().compareToIgnoreCase(bo2.getShowName()) > 0){
                    			   return -1; 
                    		   }else{
                    			   return 1; 
                    		   }
                    	   }
                       }
                       
                       if("monitorType".equals(field)){
                    	   String monitorTypeOne = null;
                    	   String monitorTypeTwo = null;
                    	  
                    	   ResourceDef rdfOne =   capacityService.getResourceDefById(bo1.getResourceId());
                    	   if(rdfOne != null){
                    		   monitorTypeOne = rdfOne.getName();
                    	   }
                    	   ResourceDef rdfTwo= capacityService.getResourceDefById(bo2.getResourceId());
                    	   if(rdfTwo != null){
                    		   monitorTypeTwo = rdfTwo.getName();
                    	   }
                    	   if(monitorTypeOne == null && monitorTypeTwo == null){
                    		   return 0;
                    	   }else if(monitorTypeOne == null && monitorTypeTwo != null){
                    		   return 1;
                    	   }else if(monitorTypeOne != null && monitorTypeTwo == null){
                    		   return -1;
                    	   }else{
                    		   if(monitorTypeOne.compareToIgnoreCase(monitorTypeTwo) == 0){
                    			   return 0;
                    		   }else if(monitorTypeOne.compareToIgnoreCase(monitorTypeTwo) > 0){
                    			   return -1; 
                    		   }else{
                    			   return 1; 
                    		   }
                    	   }
                       }
                       
                       if("domainName".equals(field)){
							String domainNameOne = null;
							String domainNameTwo = null;
							Domain domainOne =  domainApi.get(bo1.getDomainId());
							Domain domainTwo =  domainApi.get(bo2.getDomainId());
							if(domainOne != null){
								domainNameOne = domainOne.getName();
							}
							if(domainTwo != null){
								domainNameTwo = domainTwo.getName();
							}
							 if(domainNameOne == null && domainNameTwo == null){
	                    		   return 0;
	                    	   }else if(domainNameOne == null && domainNameTwo != null){
	                    		   return 1;
	                    	   }else if(domainNameOne != null && domainNameTwo == null){
	                    		   return -1;
	                    	   }else{
	                    		   if(domainNameOne.compareToIgnoreCase(domainNameTwo) == 0){
	                    			   return 0;
	                    		   }else if(domainNameOne.compareToIgnoreCase(domainNameTwo) > 0){
	                    			   return -1; 
	                    		   }else{
	                    			   return 1; 
	                    		   }
	                    	   }
						}
                       
						return 0;
					}

				});
			}
			
		}

		return resources;
	}

}
