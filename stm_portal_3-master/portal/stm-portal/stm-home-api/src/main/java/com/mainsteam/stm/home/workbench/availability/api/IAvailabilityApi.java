package com.mainsteam.stm.home.workbench.availability.api;

import java.util.List;

import com.mainsteam.stm.home.workbench.availability.bo.AvailabilityBo;

public interface IAvailabilityApi {
	List<AvailabilityBo> getResourceDetailInfo(Long[] instanceIds);
}
