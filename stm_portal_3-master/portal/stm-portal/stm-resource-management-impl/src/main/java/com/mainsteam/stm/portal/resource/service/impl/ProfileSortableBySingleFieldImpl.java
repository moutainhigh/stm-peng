package com.mainsteam.stm.portal.resource.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.portal.resource.api.IProfileSortableBySingleFieldApi;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.util.Util;
/**
 * 策略排序实现
 * @author Administrator
 *
 */
public class ProfileSortableBySingleFieldImpl implements IProfileSortableBySingleFieldApi {
	@Resource
	private CapacityService capacityService;
	
	@Resource
	private IDomainApi domainApi;
	
	@Override
	public List<ProfileInfo> sort(List<ProfileInfo> profileInfos, final String field,
			String order) {
		if (!Util.isEmpty(order)) {
			if ("ASC".equals(order.toUpperCase())) {
				Collections.sort(profileInfos, new Comparator<ProfileInfo>() {
					@Override
					public int compare(ProfileInfo pf1, ProfileInfo pf2) {
						if("strategyName".equals(field)){
							 if(pf1.getProfileName() == null && pf2.getProfileName() == null){
	                    		   return 0;
	                    	   }else if(pf1.getProfileName() == null && pf2.getProfileName() != null){
	                    		   return -1;
	                    	   }else if(pf1.getProfileName() != null && pf2.getProfileName() == null){
	                    		   return 1;
	                    	   }else{
	                    		   if(pf1.getProfileName().compareToIgnoreCase(pf2.getProfileName()) == 0){
	                    			   return 0;
	                    		   }else if(pf1.getProfileName().compareToIgnoreCase(pf2.getProfileName()) > 0){
	                    			   return 1; 
	                    		   }else{
	                    			   return -1; 
	                    		   }
	                    	   }
						}
						
						if("strategyType".equals(field)){
							String strategyTypeOne = null;
							String strategyTypeTwo = null;
							ResourceDef rdOne = capacityService.getResourceDefById(pf1.getResourceId());
							ResourceDef rdTwo = capacityService.getResourceDefById(pf2.getResourceId());
							if(rdOne != null){
								strategyTypeOne = rdOne.getName();
							}
							if(rdTwo != null){
								strategyTypeTwo = rdTwo.getName();
							}
							 if(strategyTypeOne == null && strategyTypeTwo == null){
	                    		   return 0;
	                    	   }else if(strategyTypeOne == null && strategyTypeTwo != null){
	                    		   return -1;
	                    	   }else if(strategyTypeOne != null && strategyTypeTwo == null){
	                    		   return 1;
	                    	   }else{
	                    		   if(strategyTypeOne.compareToIgnoreCase(strategyTypeTwo) == 0){
	                    			   return 0;
	                    		   }else if(strategyTypeOne.compareToIgnoreCase(strategyTypeTwo) > 0){
	                    			   return 1; 
	                    		   }else{
	                    			   return -1; 
	                    		   }
	                    	   }
						}
						
						if("domainName".equals(field)){
							String domainNameOne = null;
							String domainNameTwo = null;
							Domain domainOne =  domainApi.get(pf1.getDomainId());
							Domain domainTwo =  domainApi.get(pf2.getDomainId());
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
				Collections.sort(profileInfos, new Comparator<ProfileInfo>() {
					@Override
					public int compare(ProfileInfo pf1, ProfileInfo pf2) {
						if("strategyName".equals(field)){
							 if(pf1.getProfileName() == null && pf2.getProfileName() == null){
	                    		   return 0;
	                    	   }else if(pf1.getProfileName() == null && pf2.getProfileName() != null){
	                    		   return 1;
	                    	   }else if(pf1.getProfileName() != null && pf2.getProfileName() == null){
	                    		   return -1;
	                    	   }else{
	                    		   if(pf1.getProfileName().compareToIgnoreCase(pf2.getProfileName()) == 0){
	                    			   return 0;
	                    		   }else if(pf1.getProfileName().compareToIgnoreCase(pf2.getProfileName()) > 0){
	                    			   return -1; 
	                    		   }else{
	                    			   return 1; 
	                    		   }
	                    	   }
						}
						
						if("strategyType".equals(field)){
							String strategyTypeOne = null;
							String strategyTypeTwo = null;
							ResourceDef rdOne = capacityService.getResourceDefById(pf1.getResourceId());
							ResourceDef rdTwo = capacityService.getResourceDefById(pf2.getResourceId());
							if(rdOne != null){
								strategyTypeOne = rdOne.getName();
							}
							if(rdTwo != null){
								strategyTypeTwo = rdTwo.getName();
							}
							 if(strategyTypeOne == null && strategyTypeTwo == null){
	                    		   return 0;
	                    	   }else if(strategyTypeOne == null && strategyTypeTwo != null){
	                    		   return 1;
	                    	   }else if(strategyTypeOne != null && strategyTypeTwo == null){
	                    		   return -1;
	                    	   }else{
	                    		   if(strategyTypeOne.compareToIgnoreCase(strategyTypeTwo) == 0){
	                    			   return 0;
	                    		   }else if(strategyTypeOne.compareToIgnoreCase(strategyTypeTwo) > 0){
	                    			   return -1; 
	                    		   }else{
	                    			   return 1; 
	                    		   }
	                    	   }
						}
						
						if("domainName".equals(field)){
							String domainNameOne = null;
							String domainNameTwo = null;
							Domain domainOne =  domainApi.get(pf1.getDomainId());
							Domain domainTwo =  domainApi.get(pf2.getDomainId());
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
		
		return profileInfos;
	}

}
