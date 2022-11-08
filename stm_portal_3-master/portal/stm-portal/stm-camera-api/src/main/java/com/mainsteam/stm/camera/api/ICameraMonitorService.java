package com.mainsteam.stm.camera.api;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.camera.bo.CameraMonitorPageBo;
import com.mainsteam.stm.camera.bo.CameraResourcePageBo;
import com.mainsteam.stm.camera.bo.CaremaMonitorBo;
import com.mainsteam.stm.camera.bo.JDBCVo;
import com.mainsteam.stm.camera.bo.TreeVo;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;

public abstract interface ICameraMonitorService {

	public CameraMonitorPageBo getMonitored(ILoginUser user, long startRow,
			long pageSize, String instanceStatus, String ipOrShowName,
			Long domainId, String categoryId, String parentCategoryId,
			String resourceIds, String IsCustomResGroup, String sort,
			String order, Map<String, List<String>> queryMap,
			Map<String, List<String>> statusMap);

	public List<TreeVo> getAllResourceCategoryNew();

	public abstract long addReportTemplate(ReportTemplate paramReportTemplate,
			ILoginUser paramILoginUser);

	public abstract boolean updateReportTemplate(
			ReportTemplate paramReportTemplate, ILoginUser paramILoginUser);

	public List<CaremaMonitorBo> getCameraList();

	public Map<Long, CaremaMonitorBo> getMetricQueryMap();

	public List<JDBCVo> getDataBaseList(String configFile);

	public String getPlanNumber(String configFile, String elementName,
			String id, int dignoseNumber);

	public List<TreeVo> getCameraGroupList(String configFile,
			List<JDBCVo> jdbcList);

	public List<TreeVo> getCameraListByParentId(long parentId,
			List<JDBCVo> jdbcList, Map<String, List<CaremaMonitorBo>> groupMap);

	public Map<String, Double> getRationFromConfig(String configFile);

	public List<CaremaMonitorBo> getCaremaBoReportList(ILoginUser user,
			long startRow, long pageSize, String instanceStatus,
			String ipOrShowName, Long domainId, String categoryId,
			String parentCategoryId, String resourceIds, String IsCustomResGroup);

	public void loadChildCameraList(long parentId, List<TreeVo> treeList,
			List<Connection> connectList,
			Map<String, List<CaremaMonitorBo>> groupMap);

	public List<Connection> getConnection(List<JDBCVo> jdbcList);

	public CameraResourcePageBo getHaveMonitorCamera(ILoginUser user, long startRow, long pageSize,
			String instanceStatus, String iPorName, String domainId, String sort, String order);
	
	public CameraResourcePageBo getNotMonitorCamera(ILoginUser user, long startRow, long pageSize,
			String iPorName, String domainId, String sort, String order);
	
}
