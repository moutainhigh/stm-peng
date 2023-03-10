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
 * IP-MAC-PORT????????????
 *
 * @author zwx
 */
public class MacImpl implements IMacApi {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String defaultConstant = "- -";    //?????????????????????
    //????????????
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
    //??????????????????
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
        //??????Ip??????
        List<MacRuntimeBo> bos = macRuntimeDao.getMacByHostIp(hostIp);
        //?????????????????????ips
        this.parseIps(ips, bos);
        return ips;
    }


    @Override
    public List<String> getUpdeviceIpsByHostMac(String hostMac) {
        List<String> ips = new ArrayList<String>();
        //??????mac??????
        List<MacRuntimeBo> bos = macRuntimeDao.getMacByHostMac(hostMac);
        //?????????????????????ips
        this.parseIps(ips, bos);
        return ips;
    }

    /**
     * ??????????????????ips
     *
     * @param ips
     * @param ipsSet
     * @param bos
     */
    private void parseIps(List<String> ips, List<MacRuntimeBo> bos) {
        Set<String> ipsSet = new HashSet<String>();
        if (null != bos && bos.size() != 0) {
            for (MacRuntimeBo bo : bos) {
                String upIps = bo.getUpDeviceIp();        //????????????ips
                if (StringUtils.isNotBlank(upIps)) {
                    String[] tmp = upIps.split(",");
                    ipsSet.addAll(Arrays.asList(tmp));    //?????????
                }
            }
        }
        if (!ipsSet.isEmpty()) ips.addAll(ipsSet);
    }

    @Override
    public List<JSONObject> getBatchUpDeviceMacInfos(String[] ipInterfaces) {
        logger.error("?????????????????????????????????\n" + JSONObject.toJSONString(ipInterfaces));
        List<JSONObject> list = new ArrayList<JSONObject>();
        for (String ipInterface : ipInterfaces) {
            JSONObject ipIfac = JSONObject.parseObject(ipInterface.replace("@", ","));
            List<MacRuntimeBo> listTmp = this.getUpDeviceMacInfos(Long.valueOf(ipIfac.getString("instanceId")), ipIfac.getString("interface"));
            ipIfac.put("datas", listTmp);
            list.add(ipIfac);
        }
        logger.error(new StringBuffer("??????????????????????????????=\n").append(JSONObject.toJSON(list)).toString());
        return list;
    }

    @Override
    public List<MacRuntimeBo> getUpDeviceMacInfos(Long instanceId, String upDeviceInterface) {
        //1.??????????????????????????????ip??????
        ResourceInstance resource = null;
        try {
            resource = resourceService.getResourceInstance(instanceId);
        } catch (InstancelibException e) {
            logger.error("????????????????????????", e);
        }
        List<MacRuntimeBo> runtimes = null, runtimesTmp = new ArrayList<MacRuntimeBo>();
        if (null != resource) {
//			String instanceIp = null != resource?resource.getDiscoverIP():"";	//???????????????????????????ip
            String instanceIp = resourceExApi.getPropVal(resource, MetricIdConsts.METRIC_IP);    //???????????????ip???????????????
            logger.error(new StringBuilder("????????????instanceId=").append(instanceId).append(",getModulePropBykey(ip)???ip=").append(instanceIp).toString());
            if (!defaultConstant.equals(instanceIp) && StringUtils.isNotBlank(instanceIp)) {
                String ips[] = instanceIp.split(",");
                for (String ip : ips) {
                    //1.???????????????????????????
                    runtimes = macRuntimeDao.getMacInfos(ip, upDeviceInterface);
                    if (null != runtimes) break;
                }
            }
        }

        if (runtimes != null) {    //?????????
            Set<MacRuntimeBo> set = new HashSet<MacRuntimeBo>();
            set.addAll(runtimes);
            runtimesTmp.addAll(set);
        }
        return runtimesTmp;
    }

    @Override
    public List<MacRuntimeBo> getUpDeviceMacInfos(String upDeviceIp, String upDeviceInterface) {
        //1.???????????????????????????
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

            //??????????????????
            for (int i = smsSenders.size() - 1; i >= 0; i--) {
                JSONObject sms = (JSONObject) JSONObject.toJSON(smsSenders.get(i));
                User user = userApi.get(sms.getLongValue("id"));
                if (null == user) {
                    smsSenders.remove(i);
                }
            }
            tmp.put("smsSenders", smsSenders);

            //??????????????????
            for (int i = emailSenders.size() - 1; i >= 0; i--) {
                JSONObject email = (JSONObject) JSONObject.toJSON(emailSenders.get(i));
                User user = userApi.get(email.getLongValue("id"));
                if (null == user) {
                    emailSenders.remove(i);
                }
            }
            tmp.put("emailSenders", emailSenders);

            //??????Alert??????
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
     * ??????mac?????????excel??????
     * 1. ??????excel
     * 2. ??????baseMac??????
     *
     * @param file
     * @throws IOException
     */
    @Override
    public int importMacExcel(MultipartFile file) throws IOException {
        int flag = 0;
        //1. ??????excel
        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());

        HSSFRow row = null;
        MacBaseBo macBaseBo = null;

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {    //??????sheet?????????
            HSSFSheet sheet = workbook.getSheetAt(i);
            row = sheet.getRow(0);
            if ("MAC".equals(getCellValue(row.getCell(1))) && "IP??????".equals(getCellValue(row.getCell(2)))) {
                for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {    //??????row
                    row = sheet.getRow(j);

                    macBaseBo = new MacBaseBo();
                    macBaseBo.setHostName(getCellValue(row.getCell(0)));
                    macBaseBo.setMac(getCellValue(row.getCell(1)));
                    macBaseBo.setIp(getCellValue(row.getCell(2)));
                    macBaseBo.setUpDeviceName(getCellValue(row.getCell(3)));
                    macBaseBo.setUpDeviceIp(getCellValue(row.getCell(4)));
                    macBaseBo.setUpRemarks(getCellValue(row.getCell(5)));
                    macBaseBo.setUpDeviceInterface(getCellValue(row.getCell(6)));

                    // 2. ??????baseMac??????
                    macBaseDao.insertOrUpdate(macBaseBo);
                }
            } else {
                flag = 1;    //?????????????????????
            }
        }
        return flag;
    }

    /**
     * ?????????Excel????????????????????????????????????
     *
     * @param cell
     * @return String
     */
    private String getCellValue(HSSFCell cell) {
        String value = null;
        if (null == cell) return value;

        //????????????????????????
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING://?????????
                value = cell.getRichStringCellValue().getString();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC://??????
                long dd = (long) cell.getNumericCellValue();
                value = dd + "";
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                value = "";
                break;
            case HSSFCell.CELL_TYPE_FORMULA:
                value = String.valueOf(cell.getCellFormula());
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN://boolean??????
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
     * ??????????????????mac??????
     * 1. ??????ids??????????????????
     * 2. ????????????excel
     *
     * @param ids
     * @param exportType (selected:?????????????????????all?????????????????????)
     * @throws IOException
     */
    @Override
    public Workbook exportBaseMacs(Long[] ids, String exportType) {
        // 1. ??????ids??????????????????
        List<MacBaseBo> baseBos = null;
        if (null != ids && "selected".equals(exportType)) {    //??????????????????
            baseBos = macBaseDao.getMacBaseBosByIds(ids);
        } else if ("all".equals(exportType)) {    //??????????????????
            baseBos = macBaseDao.getAllMacBaseBos();
        } else if ("template".equals(exportType)) {
            baseBos = new ArrayList<MacBaseBo>();
        }

        // 2. ????????????excel
        Workbook wb = null;
        if (null != baseBos) {
            wb = exportExcel(baseBos);
        }

        return wb;
    }

    /**
     * ??????excel
     *
     * @param macBaseBos
     * @throws IOException
     */
    private Workbook exportExcel(List<MacBaseBo> macBaseBos) {
        //??????Workbook???????????????Excel?????????
        Workbook wb = new HSSFWorkbook();

        //??????Sheet
        Sheet sheet = wb.createSheet("?????????");

        //????????????
        createHeader(wb);

        //??????????????????
        createDataTable(sheet, macBaseBos);

        return wb;
    }

    /**
     * ???????????????????????????
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
                createSelectBox(sheet, ipsTmp, i + 1);    //???????????????
            }

            Cell cell5 = row.createCell(5);
            cell5.setCellValue(bo.getUpRemarks());

            Cell cell6 = row.createCell(6);
            cell6.setCellValue(bo.getUpDeviceInterface());
        }
    }

    /**
     * ??????excel??????
     */
    private void createHeader(Workbook wb) {
        Sheet sheet = wb.getSheetAt(0);
        Row row = sheet.createRow(0);
        row.setHeightInPoints(30);

        HSSFCellStyle style = createCellStyle(wb);    //??????????????????

        Cell cell0 = row.createCell(0);
        sheet.setColumnWidth(0, 55 * 80);    //???????????????
        cell0.setCellStyle(style);
        cell0.setCellValue("????????????");


        Cell cell1 = row.createCell(1);
        sheet.setColumnWidth(1, 60 * 80);    //???????????????
        cell1.setCellStyle(style);
        cell1.setCellValue("MAC");


        Cell cell2 = row.createCell(2);
        sheet.setColumnWidth(2, 60 * 80);
        cell2.setCellStyle(style);
        cell2.setCellValue("IP??????");
        //??????
        HSSFPatriarch patr2 = (HSSFPatriarch) sheet.createDrawingPatriarch();
        HSSFComment comment2 = patr2.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 3, 400, (short) 5, 402));    // ?????????????????????????????????????????????
        comment2.setString(new HSSFRichTextString("???IP???????????????,??????192.168.0.1,192.168.0.2"));    // ??????????????????
        cell2.setCellComment(comment2);

        Cell cell3 = row.createCell(3);
        sheet.setColumnWidth(3, 60 * 80);
        cell3.setCellValue("??????????????????");
        cell3.setCellStyle(style);

        Cell cell4 = row.createCell(4);
        sheet.setColumnWidth(4, 70 * 80);
        cell4.setCellValue("????????????IP");
        cell4.setCellStyle(style);
        //??????
        HSSFPatriarch patr4 = (HSSFPatriarch) sheet.createDrawingPatriarch();
        HSSFComment comment4 = patr4.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 3, 400, (short) 5, 402));    //5???402???-3???400??????????????????
        comment4.setString(new HSSFRichTextString("???IP???????????????,??????192.168.0.1,192.168.0.2"));    // ??????????????????
        cell4.setCellComment(comment4);

        Cell cell5 = row.createCell(5);
        sheet.setColumnWidth(5, 80 * 80);
        cell5.setCellValue("????????????");
        cell5.setCellStyle(style);

        Cell cell6 = row.createCell(6);
        sheet.setColumnWidth(6, 50 * 80);
        cell6.setCellStyle(style);
        cell6.setCellValue("??????????????????");
    }

    /**
     * ?????????????????????
     *
     * @param Workbook
     * @return HSSFCellStyle
     */
    private HSSFCellStyle createCellStyle(Workbook wb) {
        HSSFCellStyle style = (HSSFCellStyle) wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);                //????????????
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);    //??????????????????

        style.setFillForegroundColor(HSSFColor.GREEN.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        HSSFFont font = (HSSFFont) wb.createFont();
        font.setFontName("??????");
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);    //??????
        style.setFont(font);

        return style;
    }

    /**
     * ???????????????
     *
     * @param sheet
     * @param list
     * @param maxRow
     */
    private void createSelectBox(Sheet sheet, String[] list, int rowIndex) {
        if (list.length > 0) {
            //???????????????????????????
            CellRangeAddressList regions = new CellRangeAddressList(rowIndex, rowIndex, 3, 3);
            //?????????????????????
            DVConstraint constraint = DVConstraint.createExplicitListConstraint(list);
            //??????????????????????????????
            HSSFDataValidation data = new HSSFDataValidation(regions, constraint);
            //???sheet?????????
            sheet.addValidationData(data);
        }
    }

    /**
     * ????????????MAC???ip-mac-port??????
     *
     * @param msg 1.???????????????????????????
     *            2.????????????????????????????????????????????????MAC??????
     */
    @Override
    @Transactional
    public void refreshLatestIpPortMac() {
        //1.???????????????????????????
        List<MacRuntimeBo> runtimes = macRuntimeDao.getAllMacRuntimeBos();

        //2.????????????????????????????????????????????????MAC??????
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
        if (null == ids) {//????????????
            macBaseDao.deleteAll();
            macHistoryDao.truncateAll();
        } else {    //???????????????
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
     * ???????????????mac????????????[?????????]mac
     * 1. ???????????????mac????????????
     * 2. ??????????????????????????????
     * 3. ???????????????????????????(?????????????????????????????????mac??????)
     * 4. ??????????????????MAC?????????????????????????????????MAC?????????
     */
    @Override
    public int addAllBaseMac() {
        List<MacBaseBo> baseBos = null;
        //1. ???????????????mac????????????
        List<MacLatestBo> latestBos = latestMacDao.getAllMacLatestBos();

        //2. ??????????????????????????????
        baseBos = macLatestToMacBase(latestBos);

        //3. ???????????????????????????(?????????????????????????????????mac??????)
        int rows = 0;
        for (MacBaseBo base : baseBos) {
            rows += macBaseDao.insertOrUpdate(base);
        }

        //4. ??????????????????MAC?????????????????????????????????MAC?????????
        delMacLatest(latestBos);

        return rows;
    }

    @Override
    public boolean checkMacExist(Long[] ids, String addType) {
        logger.info("checkMacExist begin:paramters>>ids=" + JSONObject.toJSONString(ids) + " >>addType=" + addType);
        boolean exits = false;
        //1. ??????????????????mac
        List<MacBaseBo> baseBos = this.getAddMac(ids, addType);
        logger.info("getAddMac()=" + JSONObject.toJSONString(baseBos));

        List<String> macs = new ArrayList<String>();
        for (MacBaseBo base : baseBos) {
            macs.add(base.getMac());
        }
        logger.info("macs=" + JSONObject.toJSONString(macs));
        //2. ??????????????????
        if (macs.size() > 0) {
            List<MacBaseBo> baseTemps = macBaseDao.getMacBaseBosByMacs(macs);
            if (null != baseTemps && baseTemps.size() > 0) {
                exits = true;
            }
        }

        return exits;
    }

    /**
     * ???????????????????????????mac
     *
     * @param ids
     * @param addType
     * @return
     */
    private List<MacBaseBo> getAddMac(Long[] ids, String addType) {
        List<MacBaseBo> baseBos = null;
        List<MacLatestBo> latestBos = null;
        //1. ??????ids???????????????mac????????????
        if ("runtime".equals(addType)) {
            List<MacRuntimeBo> runtimeBos = this.getMacRuntimeBosByIds(ids);
            //2. ??????????????????????????????
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
     * ???mac????????????[?????????]mac
     * 1. ??????ids???????????????mac????????????
     * 2. ??????????????????????????????
     * 3. ???????????????????????????(?????????????????????????????????mac??????)
     * 4. ??????????????????MAC?????????????????????????????????MAC?????????
     *
     * @param ids
     * @param addType(runtime:?????????,history:?????????,latest:??????mac???)
     */
    @Override
    public int addBaseMac(Long[] ids, String addType) {
        List<MacBaseBo> baseBos = null;
        List<MacLatestBo> latestBos = null;
        //1. ??????ids???????????????mac????????????
        if ("runtime".equals(addType)) {
            List<MacRuntimeBo> runtimeBos = this.getMacRuntimeBosByIds(ids);
            //2. ??????????????????????????????
            baseBos = macRuntimeToMacBase(runtimeBos);
        } else if ("history".equals(addType)) {
            List<MacHistoryBo> historyBos = this.getMacHistoryBosByIds(ids);
            baseBos = macHistoryToMacBase(historyBos);
        } else if ("latest".equals(addType)) {
            latestBos = this.getMacLatestBosByIds(ids);
            baseBos = macLatestToMacBase(latestBos);
        }

        //3. ???????????????????????????(?????????????????????????????????mac??????)
        int rows = 0;
        for (MacBaseBo base : baseBos) {
//			rows += macBaseDao.insertOrUpdate(base);
            rows = macBaseDao.addOrUpdate(base);
        }

        //4. ??????????????????MAC?????????????????????????????????MAC?????????
        if ("latest".equals(addType)) {
            delMacLatest(latestBos);
        }

        return rows;
    }

    /**
     * ??????ids????????????[??????MAC???]??????
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
     * ??????ids??????[??????mac???]??????
     *
     * @param ids
     * @return
     */
    private List<MacLatestBo> getMacLatestBosByIds(Long[] ids) {
        return latestMacDao.getMacLatestBosByIds(ids);
    }

    /**
     * ??????ids??????[???????????????]??????
     *
     * @param ids
     * @return
     */
    private List<MacHistoryBo> getMacHistoryBosByIds(Long[] ids) {
        return macHistoryDao.getMacHistoryBosByIds(ids);
    }

    /**
     * ??????ids??????[?????????]??????
     *
     * @param ids
     * @return
     */
    private List<MacRuntimeBo> getMacRuntimeBosByIds(Long[] ids) {
        return macRuntimeDao.getMacRuntimeBosByIds(ids);
    }

    /**
     * ????????????[??????mac???]???????????????[?????????]???????????????
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
     * ????????????mac?????????????????????????????????
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
     * ????????????[???????????????]???????????????[?????????]???????????????
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
     * ???????????????????????????????????????????????????
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
     * ????????????[?????????]???????????????[?????????]???????????????
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
     * ?????????????????????????????????????????????
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
            //????????????node?????????ip
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
            //????????????node?????????ip
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
            //????????????node?????????ip
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
            //????????????node?????????ip
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
            //????????????node?????????ip
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

    //?????????????????????????????????
    private JSONArray getShowTopoDevs() {
        //?????????????????????????????????
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
