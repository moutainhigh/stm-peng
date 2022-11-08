package com.mainsteam.stm.topo.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.topo.api.IMacApi;
import com.mainsteam.stm.topo.api.IResourceInstanceExApi;
import com.mainsteam.stm.topo.api.ISettingApi;
import com.mainsteam.stm.topo.api.ITopoGraphApi;
import com.mainsteam.stm.topo.api.ThirdService;
import com.mainsteam.stm.topo.api.TopoAlarmExApi;
import com.mainsteam.stm.topo.bo.MacBaseBo;
import com.mainsteam.stm.topo.bo.MacHistoryBo;
import com.mainsteam.stm.topo.bo.MacLatestBo;
import com.mainsteam.stm.topo.bo.MacRuntimeBo;
import com.mainsteam.stm.topo.dao.IMacBaseDao;
import com.mainsteam.stm.topo.dao.IMacHistoryDao;
import com.mainsteam.stm.topo.dao.IMacLatestDao;
import com.mainsteam.stm.topo.dao.IMacRuntimeDao;
import com.mainsteam.stm.topo.dao.INodeDao;
import com.mainsteam.stm.topo.dao.ISettingDao;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * IP-MAC-PORT业务实现
 *
 * @author zwx
 */
public class MacImpl implements IMacApi {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String defaultConstant = "- -";    //默认字符串常量
    //系统用户
    @Autowired
    public IUserApi userApi;
    @Autowired
    public ResourceInstanceService resourceService;
    private IMacRuntimeDao macRuntimeDao;
    private IMacBaseDao macBaseDao;
    private IMacLatestDao latestMacDao;
    private IMacHistoryDao macHistoryDao;
    @Autowired
    private ISettingDao settingDao;
    @Autowired
    private INodeDao nodeDao;
    @Autowired
    private ThirdService thirdSvc;
    @Autowired
    private TopoAlarmExApi alarmExApi;
    //远程调用服务
    @Autowired
    private OCRPCClient client;
    @Autowired
    private ISettingApi settingApi;
    @Autowired
    private IResourceInstanceExApi resourceExApi;
    @Autowired
    private ITopoGraphApi graphApi;
//	@Value("${dbtype}")
//	private String dbtype;

    @Override
    public List<String> getUpdeviceIpsByHostIp(String hostIp) {
        List<String> ips = new ArrayList<String>();
        //根据Ip查询
        List<MacRuntimeBo> bos = macRuntimeDao.getMacByHostIp(hostIp);
        //解析出上联设备ips
        this.parseIps(ips, bos);
        return ips;
    }


    @Override
    public List<String> getUpdeviceIpsByHostMac(String hostMac) {
        List<String> ips = new ArrayList<String>();
        //根据mac查询
        List<MacRuntimeBo> bos = macRuntimeDao.getMacByHostMac(hostMac);
        //解析出上联设备ips
        this.parseIps(ips, bos);
        return ips;
    }

    /**
     * 解析上联设备ips
     *
     * @param ips
     * @param ipsSet
     * @param bos
     */
    private void parseIps(List<String> ips, List<MacRuntimeBo> bos) {
        Set<String> ipsSet = new HashSet<String>();
        if (null != bos && bos.size() != 0) {
            for (MacRuntimeBo bo : bos) {
                String upIps = bo.getUpDeviceIp();        //上联设备ips
                if (StringUtils.isNotBlank(upIps)) {
                    String[] tmp = upIps.split(",");
                    ipsSet.addAll(Arrays.asList(tmp));    //去重复
                }
            }
        }
        if (!ipsSet.isEmpty()) ips.addAll(ipsSet);
    }

    @Override
    public List<JSONObject> getBatchUpDeviceMacInfos(String[] ipInterfaces) {
        logger.error("批量查询下联设备参数：\n" + JSONObject.toJSONString(ipInterfaces));
        List<JSONObject> list = new ArrayList<JSONObject>();
        for (String ipInterface : ipInterfaces) {
            JSONObject ipIfac = JSONObject.parseObject(ipInterface.replace("@", ","));
            List<MacRuntimeBo> listTmp = this.getUpDeviceMacInfos(Long.valueOf(ipIfac.getString("instanceId")), ipIfac.getString("interface"));
            ipIfac.put("datas", listTmp);
            list.add(ipIfac);
        }
        logger.error(new StringBuffer("批量查询下联设备结果=\n").append(JSONObject.toJSON(list)).toString());
        return list;
    }

    @Override
    public List<MacRuntimeBo> getUpDeviceMacInfos(Long instanceId, String upDeviceInterface) {
        //1.查询资源实例获取设备ip集合
        ResourceInstance resource = null;
        try {
            resource = resourceService.getResourceInstance(instanceId);
        } catch (InstancelibException e) {
            logger.error("查询资源实例异常", e);
        }
        List<MacRuntimeBo> runtimes = null, runtimesTmp = new ArrayList<MacRuntimeBo>();
        if (null != resource) {
//			String instanceIp = null != resource?resource.getDiscoverIP():"";	//发现时候填写的管理ip
            String instanceIp = resourceExApi.getPropVal(resource, MetricIdConsts.METRIC_IP);    //设备采集的ip（可多个）
            logger.error(new StringBuilder("资源实例instanceId=").append(instanceId).append(",getModulePropBykey(ip)的ip=").append(instanceIp).toString());
            if (!defaultConstant.equals(instanceIp) && StringUtils.isNotBlank(instanceIp)) {
                String ips[] = instanceIp.split(",");
                for (String ip : ips) {
                    //1.查询所有实时表数据
                    runtimes = macRuntimeDao.getMacInfos(ip, upDeviceInterface);
                    if (null != runtimes) break;
                }
            }
        }

        if (runtimes != null) {    //去重复
            Set<MacRuntimeBo> set = new HashSet<MacRuntimeBo>();
            set.addAll(runtimes);
            runtimesTmp.addAll(set);
        }
        return runtimesTmp;
    }

    @Override
    public List<MacRuntimeBo> getUpDeviceMacInfos(String upDeviceIp, String upDeviceInterface) {
        //1.查询所有实时表数据
        List<MacRuntimeBo> runtimes = macRuntimeDao.getMacInfos(upDeviceIp, upDeviceInterface);
        return runtimes;
    }

    @Override
    public String getAlarmSetting(String key) {
        String setting = settingApi.getCfg(key);
        if (StringUtils.isNotBlank(setting) && !"{}".equals(setting)) {
            JSONObject tmp = JSONObject.parseObject(setting);
            JSONArray smsSenders = tmp.getJSONArray("smsSenders");
            JSONArray emailSenders = tmp.getJSONArray("emailSenders");
            JSONArray alertSenders = tmp.getJSONArray("alertSenders");

            //处理短信用户
            for (int i = smsSenders.size() - 1; i >= 0; i--) {
                JSONObject sms = (JSONObject) JSONObject.toJSON(smsSenders.get(i));
                User user = userApi.get(sms.getLongValue("id"));
                if (null == user) {
                    smsSenders.remove(i);
                }
            }
            tmp.put("smsSenders", smsSenders);

            //处理邮件用户
            for (int i = emailSenders.size() - 1; i >= 0; i--) {
                JSONObject email = (JSONObject) JSONObject.toJSON(emailSenders.get(i));
                User user = userApi.get(email.getLongValue("id"));
                if (null == user) {
                    emailSenders.remove(i);
                }
            }
            tmp.put("emailSenders", emailSenders);

            //处理Alert用户
            for (int i = alertSenders.size() - 1; i >= 0; i--) {
                JSONObject alert = (JSONObject) JSONObject.toJSON(alertSenders.get(i));
                User user = userApi.get(alert.getLongValue("id"));
                if (null == user) {
                    alertSenders.remove(i);
                }
            }
            tmp.put("alertSenders", alertSenders);

            setting = tmp.toJSONString();
        }
        return setting;
    }

    /**
     * 导入mac基准表excel数据
     * 1. 解析excel
     * 2. 保存baseMac数据
     *
     * @param file
     * @throws IOException
     */
    @Override
    public int importMacExcel(MultipartFile file) throws IOException {
        int flag = 0;
        //1. 解析excel
        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());

        HSSFRow row = null;
        MacBaseBo macBaseBo = null;

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {    //解析sheet工作表
            HSSFSheet sheet = workbook.getSheetAt(i);
            row = sheet.getRow(0);
            if ("MAC".equals(getCellValue(row.getCell(1))) && "IP地址".equals(getCellValue(row.getCell(2)))) {
                for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {    //解析row
                    row = sheet.getRow(j);

                    macBaseBo = new MacBaseBo();
                    macBaseBo.setHostName(getCellValue(row.getCell(0)));
                    macBaseBo.setMac(getCellValue(row.getCell(1)));
                    macBaseBo.setIp(getCellValue(row.getCell(2)));
                    macBaseBo.setUpDeviceName(getCellValue(row.getCell(3)));
                    macBaseBo.setUpDeviceIp(getCellValue(row.getCell(4)));
                    macBaseBo.setUpRemarks(getCellValue(row.getCell(5)));
                    macBaseBo.setUpDeviceInterface(getCellValue(row.getCell(6)));

                    // 2. 保存baseMac数据
                    macBaseDao.insertOrUpdate(macBaseBo);
                }
            } else {
                flag = 1;    //不是标准的模板
            }
        }
        return flag;
    }

    /**
     * 判断从Excel文件中解析出来数据的格式
     *
     * @param cell
     * @return String
     */
    private String getCellValue(HSSFCell cell) {
        String value = null;
        if (null == cell) return value;

        //简单的查检列类型
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING://字符串
                value = cell.getRichStringCellValue().getString();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC://数字
                long dd = (long) cell.getNumericCellValue();
                value = dd + "";
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                value = "";
                break;
            case HSSFCell.CELL_TYPE_FORMULA:
                value = String.valueOf(cell.getCellFormula());
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN://boolean型值
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case HSSFCell.CELL_TYPE_ERROR:
                value = String.valueOf(cell.getErrorCellValue());
                break;
            default:
                break;
        }
        return value;
    }

    /**
     * 导出指定基准mac数据
     * 1. 根据ids查询基准数据
     * 2. 导出数据excel
     *
     * @param ids
     * @param exportType (selected:导出选择数据，all：导出所有数据)
     * @throws IOException
     */
    @Override
    public Workbook exportBaseMacs(Long[] ids, String exportType) {
        // 1. 根据ids查询基准数据
        List<MacBaseBo> baseBos = null;
        if (null != ids && "selected".equals(exportType)) {    //导出选择数据
            baseBos = macBaseDao.getMacBaseBosByIds(ids);
        } else if ("all".equals(exportType)) {    //导出所有数据
            baseBos = macBaseDao.getAllMacBaseBos();
        } else if ("template".equals(exportType)) {
            baseBos = new ArrayList<MacBaseBo>();
        }

        // 2. 导出数据excel
        Workbook wb = null;
        if (null != baseBos) {
            wb = exportExcel(baseBos);
        }

        return wb;
    }

    /**
     * 导出excel
     *
     * @param macBaseBos
     * @throws IOException
     */
    private Workbook exportExcel(List<MacBaseBo> macBaseBos) {
        //创建Workbook对象（代表Excel文件）
        Workbook wb = new HSSFWorkbook();

        //创建Sheet
        Sheet sheet = wb.createSheet("基准表");

        //创建表头
        createHeader(wb);

        //创建数据表格
        createDataTable(sheet, macBaseBos);

        return wb;
    }

    /**
     * 说明：创建数据表格
     *
     * @param sheet1
     */
    private void createDataTable(Sheet sheet, List<MacBaseBo> macBaseBos) {

        Row row = null;
        for (int i = 0; i < macBaseBos.size(); i++) {
            row = sheet.createRow(i + 1);
            row.setHeightInPoints(25);

            MacBaseBo bo0 = macBaseBos.get(i);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(bo0.getHostName());

            MacBaseBo bo = macBaseBos.get(i);
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(bo.getMac());

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(bo.getIp());

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(bo.getUpDeviceName());

            Cell cell4 = row.createCell(4);
            if (StringUtils.isNotBlank(bo.getUpDeviceIp())) {
                String[] ips = bo.getUpDeviceIp().split(";");
                cell4.setCellValue(ips[0]);
                String[] ipsTmp = new String[ips.length - 1];
                for (int j = 1; j < ips.length; j++) {
                    ipsTmp[j - 1] = ips[j];
                }
                createSelectBox(sheet, ipsTmp, i + 1);    //生成下拉框
            }

            Cell cell5 = row.createCell(5);
            cell5.setCellValue(bo.getUpRemarks());

            Cell cell6 = row.createCell(6);
            cell6.setCellValue(bo.getUpDeviceInterface());
        }
    }

    /**
     * 创建excel表头
     */
    private void createHeader(Workbook wb) {
        Sheet sheet = wb.getSheetAt(0);
        Row row = sheet.createRow(0);
        row.setHeightInPoints(30);

        HSSFCellStyle style = createCellStyle(wb);    //创建表头样式

        Cell cell0 = row.createCell(0);
        sheet.setColumnWidth(0, 55 * 80);    //固定列宽度
        cell0.setCellStyle(style);
        cell0.setCellValue("主机名称");


        Cell cell1 = row.createCell(1);
        sheet.setColumnWidth(1, 60 * 80);    //固定列宽度
        cell1.setCellStyle(style);
        cell1.setCellValue("MAC");


        Cell cell2 = row.createCell(2);
        sheet.setColumnWidth(2, 60 * 80);
        cell2.setCellStyle(style);
        cell2.setCellValue("IP地址");
        //批注
        HSSFPatriarch patr2 = (HSSFPatriarch) sheet.createDrawingPatriarch();
        HSSFComment comment2 = patr2.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 3, 400, (short) 5, 402));    // 定义注释的大小和位置，详见文档
        comment2.setString(new HSSFRichTextString("多IP用逗号隔开,如：192.168.0.1,192.168.0.2"));    // 设置注释内容
        cell2.setCellComment(comment2);

        Cell cell3 = row.createCell(3);
        sheet.setColumnWidth(3, 60 * 80);
        cell3.setCellValue("上联设备名称");
        cell3.setCellStyle(style);

        Cell cell4 = row.createCell(4);
        sheet.setColumnWidth(4, 70 * 80);
        cell4.setCellValue("上联设备IP");
        cell4.setCellStyle(style);
        //批注
        HSSFPatriarch patr4 = (HSSFPatriarch) sheet.createDrawingPatriarch();
        HSSFComment comment4 = patr4.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 3, 400, (short) 5, 402));    //5列402行-3列400行的矩形大小
        comment4.setString(new HSSFRichTextString("多IP用逗号隔开,如：192.168.0.1,192.168.0.2"));    // 设置注释内容
        cell4.setCellComment(comment4);

        Cell cell5 = row.createCell(5);
        sheet.setColumnWidth(5, 80 * 80);
        cell5.setCellValue("上联备注");
        cell5.setCellStyle(style);

        Cell cell6 = row.createCell(6);
        sheet.setColumnWidth(6, 50 * 80);
        cell6.setCellStyle(style);
        cell6.setCellValue("上联设备接口");
    }

    /**
     * 创建单元格样式
     *
     * @param Workbook
     * @return HSSFCellStyle
     */
    private HSSFCellStyle createCellStyle(Workbook wb) {
        HSSFCellStyle style = (HSSFCellStyle) wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);                //居中对齐
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);    //垂直居中对齐

        style.setFillForegroundColor(HSSFColor.GREEN.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        HSSFFont font = (HSSFFont) wb.createFont();
        font.setFontName("宋体");
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);    //粗体
        style.setFont(font);

        return style;
    }

    /**
     * 创建下拉框
     *
     * @param sheet
     * @param list
     * @param maxRow
     */
    private void createSelectBox(Sheet sheet, String[] list, int rowIndex) {
        if (list.length > 0) {
            //生成下拉框的列范围
            CellRangeAddressList regions = new CellRangeAddressList(rowIndex, rowIndex, 3, 3);
            //生成下拉框内容
            DVConstraint constraint = DVConstraint.createExplicitListConstraint(list);
            //绑定下拉框和作用区域
            HSSFDataValidation data = new HSSFDataValidation(regions, constraint);
            //对sheet页生效
            sheet.addValidationData(data);
        }
    }

    /**
     * 刷新新增MAC的ip-mac-port数据
     *
     * @param msg 1.查询所有实时表数据
     *            2.与基准表比较，不存在则加入【新增MAC表】
     */
    @Override
    @Transactional
    public void refreshLatestIpPortMac() {
        //1.查询所有实时表数据
        List<MacRuntimeBo> runtimes = macRuntimeDao.getAllMacRuntimeBos();

        //2.与基准表比较，不存在则加入【新增MAC表】
        for (MacRuntimeBo runtimeBo : runtimes) {
            MacBaseBo base = macBaseDao.getMacBaseByMac(runtimeBo.getMac());
            if (base == null) {
                MacLatestBo latest = new MacLatestBo();
                latest.setIp(runtimeBo.getIp());
                latest.setMac(runtimeBo.getMac());
                latest.setUpdateTime(runtimeBo.getUpdateTime());
                latest.setUpDeviceInterface(runtimeBo.getUpDeviceInterface());
                latest.setUpDeviceIp(runtimeBo.getUpDeviceIp());
                latest.setUpDeviceName(runtimeBo.getUpDeviceName());

                latestMacDao.insert(latest);
            }
        }
    }

    @Override
    public int deleteMacHistoryByIds(Long[] ids) {
        return macHistoryDao.deleteHistoryIds(ids);
    }

    @Override
    public int deleteMacHistoryByMacs(String[] macs) {
        return macHistoryDao.deleteHistoryByMac(macs);
    }

    @Override
    public int saveBaseMac(MacBaseBo macBaseBo) {
        return macBaseDao.addOrUpdate(macBaseBo);
//		return macBaseDao.insertOrUpdate(macBaseBo);
    }

    @Override
    public void deleteBaseMac(Long[] ids) {
        if (null == ids) {//清空所有
            macBaseDao.deleteAll();
            macHistoryDao.truncateAll();
        } else {    //删除选中项
            List<MacBaseBo> baseBos = macBaseDao.getMacBaseBosByIds(ids);
            String macs[] = new String[baseBos.size()];
            for (int i = 0; i < baseBos.size(); i++) {
                macs[i] = baseBos.get(i).getMac();
            }
            if (macs.length > 0) {
                macHistoryDao.deleteHistoryByMac(macs);
                macBaseDao.deleteByIds(ids);
            }
        }
    }

    /**
     * 将全部新增mac数据加入[基准表]mac
     * 1. 查询待插入mac数据列表
     * 2. 循环列表封装插入数据
     * 3. 循环列表插入基准表(存在则更新，否则插入，mac唯一)
     * 4. 如果通过新增MAC加入基准表，则删除新增MAC表数据
     */
    @Override
    public int addAllBaseMac() {
        List<MacBaseBo> baseBos = null;
        //1. 查询待插入mac数据列表
        List<MacLatestBo> latestBos = latestMacDao.getAllMacLatestBos();

        //2. 循环列表封装插入数据
        baseBos = macLatestToMacBase(latestBos);

        //3. 循环列表插入基准表(存在则更新，否则插入，mac唯一)
        int rows = 0;
        for (MacBaseBo base : baseBos) {
            rows += macBaseDao.insertOrUpdate(base);
        }

        //4. 如果通过新增MAC加入基准表，则删除新增MAC表数据
        delMacLatest(latestBos);

        return rows;
    }

    @Override
    public boolean checkMacExist(Long[] ids, String addType) {
        logger.info("checkMacExist begin:paramters>>ids=" + JSONObject.toJSONString(ids) + " >>addType=" + addType);
        boolean exits = false;
        //1. 查询基准表的mac
        List<MacBaseBo> baseBos = this.getAddMac(ids, addType);
        logger.info("getAddMac()=" + JSONObject.toJSONString(baseBos));

        List<String> macs = new ArrayList<String>();
        for (MacBaseBo base : baseBos) {
            macs.add(base.getMac());
        }
        logger.info("macs=" + JSONObject.toJSONString(macs));
        //2. 判断是否存在
        if (macs.size() > 0) {
            List<MacBaseBo> baseTemps = macBaseDao.getMacBaseBosByMacs(macs);
            if (null != baseTemps && baseTemps.size() > 0) {
                exits = true;
            }
        }

        return exits;
    }

    /**
     * 获取要加入基准表的mac
     *
     * @param ids
     * @param addType
     * @return
     */
    private List<MacBaseBo> getAddMac(Long[] ids, String addType) {
        List<MacBaseBo> baseBos = null;
        List<MacLatestBo> latestBos = null;
        //1. 根据ids查询待插入mac数据列表
        if ("runtime".equals(addType)) {
            List<MacRuntimeBo> runtimeBos = this.getMacRuntimeBosByIds(ids);
            //2. 循环列表封装插入数据
            baseBos = macRuntimeToMacBase(runtimeBos);
        } else if ("history".equals(addType)) {
            List<MacHistoryBo> historyBos = this.getMacHistoryBosByIds(ids);
            baseBos = macHistoryToMacBase(historyBos);
        } else if ("latest".equals(addType)) {
            latestBos = this.getMacLatestBosByIds(ids);
            baseBos = macLatestToMacBase(latestBos);
        }

        return baseBos;
    }

    /**
     * 将mac数据加入[基准表]mac
     * 1. 根据ids查询待插入mac数据列表
     * 2. 循环列表封装插入数据
     * 3. 循环列表插入基准表(存在则更新，否则插入，mac唯一)
     * 4. 如果通过新增MAC加入基准表，则删除新增MAC表数据
     *
     * @param ids
     * @param addType(runtime:实时表,history:历史表,latest:新增mac表)
     */
    @Override
    public int addBaseMac(Long[] ids, String addType) {
        List<MacBaseBo> baseBos = null;
        List<MacLatestBo> latestBos = null;
        //1. 根据ids查询待插入mac数据列表
        if ("runtime".equals(addType)) {
            List<MacRuntimeBo> runtimeBos = this.getMacRuntimeBosByIds(ids);
            //2. 循环列表封装插入数据
            baseBos = macRuntimeToMacBase(runtimeBos);
        } else if ("history".equals(addType)) {
            List<MacHistoryBo> historyBos = this.getMacHistoryBosByIds(ids);
            baseBos = macHistoryToMacBase(historyBos);
        } else if ("latest".equals(addType)) {
            latestBos = this.getMacLatestBosByIds(ids);
            baseBos = macLatestToMacBase(latestBos);
        }

        //3. 循环列表插入基准表(存在则更新，否则插入，mac唯一)
        int rows = 0;
        for (MacBaseBo base : baseBos) {
//			rows += macBaseDao.insertOrUpdate(base);
            rows = macBaseDao.addOrUpdate(base);
        }

        //4. 如果通过新增MAC加入基准表，则删除新增MAC表数据
        if ("latest".equals(addType)) {
            delMacLatest(latestBos);
        }

        return rows;
    }

    /**
     * 根据ids列表删除[新增MAC表]数据
     *
     * @param addType
     * @param latestBos
     */
    private void delMacLatest(List<MacLatestBo> latestBos) {
        List<Long> latestMacIds = new ArrayList<Long>();
        for (MacLatestBo latest : latestBos) {
            latestMacIds.add(latest.getId());
        }

        if (latestMacIds.size() > 0) {
            latestMacDao.deleteMacLatestByIds(latestMacIds);
        }
    }

    /**
     * 根据ids查询[新增mac表]数据
     *
     * @param ids
     * @return
     */
    private List<MacLatestBo> getMacLatestBosByIds(Long[] ids) {
        return latestMacDao.getMacLatestBosByIds(ids);
    }

    /**
     * 根据ids查询[历史变更表]数据
     *
     * @param ids
     * @return
     */
    private List<MacHistoryBo> getMacHistoryBosByIds(Long[] ids) {
        return macHistoryDao.getMacHistoryBosByIds(ids);
    }

    /**
     * 根据ids查询[实时表]数据
     *
     * @param ids
     * @return
     */
    private List<MacRuntimeBo> getMacRuntimeBosByIds(Long[] ids) {
        return macRuntimeDao.getMacRuntimeBosByIds(ids);
    }

    /**
     * 批量封装[新增mac表]数据转换为[基准表]待插入数据
     *
     * @param macLatestBos
     * @return
     */
    private List<MacBaseBo> macLatestToMacBase(List<MacLatestBo> macLatestBos) {
        List<MacBaseBo> macBaseBos = new ArrayList<MacBaseBo>();
        for (MacLatestBo bo : macLatestBos) {
            macBaseBos.add(macLatestToBase(bo));
        }
        return macBaseBos;
    }

    /**
     * 单个新增mac表数据转换为基准表数据
     *
     * @param runtimeBo
     * @return
     */
    private MacBaseBo macLatestToBase(MacLatestBo latestBo) {
        MacBaseBo baseBo = new MacBaseBo();
        baseBo.setIp(latestBo.getIp());
        baseBo.setMac(latestBo.getMac());
        baseBo.setUpDeviceInterface(latestBo.getUpDeviceInterface());
        baseBo.setUpDeviceIp(latestBo.getUpDeviceIp());
        baseBo.setUpDeviceName(latestBo.getUpDeviceName());
        baseBo.setHostName(latestBo.getHostName());
        return baseBo;
    }

    /**
     * 批量封装[历史变更表]数据转换为[基准表]待插入数据
     *
     * @param macHistoryBos
     * @return
     */
    private List<MacBaseBo> macHistoryToMacBase(List<MacHistoryBo> macHistoryBos) {
        List<MacBaseBo> macBaseBos = new ArrayList<MacBaseBo>();
        for (MacHistoryBo bo : macHistoryBos) {
            macBaseBos.add(macHistoryToBase(bo));
        }
        return macBaseBos;
    }

    /**
     * 单个历史变更表数据转换为基准表数据
     *
     * @param runtimeBo
     * @return
     */
    private MacBaseBo macHistoryToBase(MacHistoryBo historyBo) {
        MacBaseBo baseBo = new MacBaseBo();
        baseBo.setIp(historyBo.getIp());
        baseBo.setMac(historyBo.getMac());
        baseBo.setUpDeviceInterface(historyBo.getUpDeviceInterface());
        baseBo.setUpDeviceIp(historyBo.getUpDeviceIp());
        baseBo.setUpDeviceName(historyBo.getUpDeviceName());
        baseBo.setHostName(historyBo.getHostName());
        return baseBo;
    }

    /**
     * 批量封装[实时表]数据转换为[基准表]待插入数据
     *
     * @param macRuntimeBos
     * @return
     */
    private List<MacBaseBo> macRuntimeToMacBase(List<MacRuntimeBo> macRuntimeBos) {
        List<MacBaseBo> macBaseBos = new ArrayList<MacBaseBo>();
        for (MacRuntimeBo runtimeBo : macRuntimeBos) {
            macBaseBos.add(macRuntimeToBase(runtimeBo));
        }
        return macBaseBos;
    }

    /**
     * 单个实时表数据转换为基准表数据
     *
     * @param runtimeBo
     * @return
     */
    private MacBaseBo macRuntimeToBase(MacRuntimeBo runtimeBo) {
        MacBaseBo baseBo = new MacBaseBo();
        baseBo.setIp(runtimeBo.getIp());
        baseBo.setMac(runtimeBo.getMac());
        baseBo.setUpDeviceInterface(runtimeBo.getUpDeviceInterface());
        baseBo.setUpDeviceIp(runtimeBo.getUpDeviceIp());
        baseBo.setUpDeviceName(runtimeBo.getUpDeviceName());
        baseBo.setHostName(runtimeBo.getHostName());
        return baseBo;
    }

    @Override
    public void selectMacSubHistoryByPage(Page<MacHistoryBo, MacHistoryBo> page) {
        macHistoryDao.selectSubHistoryByPage(page);
        List<MacHistoryBo> bos = page.getDatas();
        JSONArray nodesArray = this.getShowTopoDevs();

        for (MacHistoryBo bo : bos) {
            //查询拓扑node，匹配ip
            for (Object nodeObj : nodesArray) {
                JSONObject node = (JSONObject) JSONObject.toJSON(nodeObj);
                if (StringUtils.isNotBlank(bo.getIp())) {
                    String ips[] = bo.getIp().split(",");
                    for (String ip : ips) {
                        if (ip.equals(node.getString("ip"))) {
                            bo.setPositionFlag(1);
                            break;
                        }
                    }
                } else {
                    bo.setIp(defaultConstant);
                }
            }
        }
    }

    @Override
    public void selectMacHistoryByPage(Page<MacHistoryBo, MacHistoryBo> page) {
        macHistoryDao.selectByPage(page);
        List<MacHistoryBo> bos = page.getDatas();
        JSONArray nodesArray = this.getShowTopoDevs();

        for (MacHistoryBo bo : bos) {
            //查询拓扑node，匹配ip
            for (Object nodeObj : nodesArray) {
                JSONObject node = (JSONObject) JSONObject.toJSON(nodeObj);
                if (StringUtils.isNotBlank(bo.getIp())) {
                    String ips[] = bo.getIp().split(",");
                    for (String ip : ips) {
                        if (ip.equals(node.getString("ip"))) {
                            bo.setPositionFlag(1);
                            break;
                        }
                    }
                } else {
                    bo.setIp(defaultConstant);
                }
            }
        }
    }

    @Override
    public void selectLatestMacByPage(Page<MacLatestBo, MacLatestBo> page) {
        latestMacDao.selectByPage(page);
        List<MacLatestBo> bos = page.getDatas();
        JSONArray nodesArray = this.getShowTopoDevs();

        for (MacLatestBo bo : bos) {
            //查询拓扑node，匹配ip
            for (Object nodeObj : nodesArray) {
                JSONObject node = (JSONObject) JSONObject.toJSON(nodeObj);
                if (StringUtils.isNotBlank(bo.getIp())) {
                    String ips[] = bo.getIp().split(",");
                    for (String ip : ips) {
                        if (ip.equals(node.getString("ip"))) {
                            bo.setPositionFlag(1);
                            break;
                        }
                    }
                } else {
                    bo.setIp(defaultConstant);
                }
            }
        }
    }

    @Override
    public void selectMacBaseByPage(Page<MacBaseBo, MacBaseBo> page) {
        macBaseDao.selectByPage(page);
        List<MacBaseBo> bases = page.getDatas();
        JSONArray nodesArray = this.getShowTopoDevs();

        for (MacBaseBo base : bases) {
            //查询拓扑node，匹配ip
            for (Object nodeObj : nodesArray) {
                JSONObject node = (JSONObject) JSONObject.toJSON(nodeObj);
                if (StringUtils.isNotBlank(base.getIp())) {
                    String ips[] = base.getIp().split(",");
                    for (String ip : ips) {
                        if (ip.equals(node.getString("ip"))) {
                            base.setPositionFlag(1);
                            break;
                        }
                    }
                } else {
                    base.setIp(defaultConstant);
                }
            }
        }
    }

    @Override
    public void selectMacRuntimeByPage(Page<MacRuntimeBo, MacRuntimeBo> page) {
        macRuntimeDao.selectByPage(page);
        List<MacRuntimeBo> runtimes = page.getDatas();
        JSONArray nodesArray = this.getShowTopoDevs();

        for (MacRuntimeBo runtime : runtimes) {
            //查询拓扑node，匹配ip
            for (Object nodeObj : nodesArray) {
                JSONObject node = (JSONObject) JSONObject.toJSON(nodeObj);
                if (StringUtils.isNotBlank(runtime.getIp())) {
                    String ips[] = runtime.getIp().split(",");
                    for (String ip : ips) {
                        if (ip.equals(node.getString("ip"))) {
                            runtime.setPositionFlag(1);
                            break;
                        }
                    }
                } else {
                    runtime.setIp(defaultConstant);
                }
            }
        }
    }

    //获取拓扑图上显示的设备
    private JSONArray getShowTopoDevs() {
        //获取拓扑图上显示的设备
        String nodeStr = graphApi.getSubTopo(null);
        JSONArray nodesArray = JSONObject.parseObject(nodeStr).getJSONArray("nodes");
        return nodesArray;
    }

    public IMacRuntimeDao getMacRuntimeDao() {
        return macRuntimeDao;
    }

    public void setMacRuntimeDao(IMacRuntimeDao macRuntimeDao) {
        this.macRuntimeDao = macRuntimeDao;
    }

    public IMacBaseDao getMacBaseDao() {
        return macBaseDao;
    }

    public void setMacBaseDao(IMacBaseDao macBaseDao) {
        this.macBaseDao = macBaseDao;
    }

    public IMacLatestDao getLatestMacDao() {
        return latestMacDao;
    }

    public void setLatestMacDao(IMacLatestDao latestMacDao) {
        this.latestMacDao = latestMacDao;
    }

    public IMacHistoryDao getMacHistoryDao() {
        return macHistoryDao;
    }

    public void setMacHistoryDao(IMacHistoryDao macHistoryDao) {
        this.macHistoryDao = macHistoryDao;
    }

    @Override
    public boolean updateUpRemarks(Long id, String upRemarks) {
        List<MacBaseBo> macBaseBosByIds = macBaseDao.getMacBaseBosByIds(new Long[]{id});
        if (macBaseBosByIds == null || macBaseBosByIds.size() == 0) {
            return false;
        }
        MacBaseBo macBaseBo = macBaseBosByIds.get(0);
        macBaseBo.setUpRemarks(upRemarks);
        int update = macBaseDao.update(macBaseBo);
        if (update > 0) {
            return true;
        }
        return false;
    }
}
