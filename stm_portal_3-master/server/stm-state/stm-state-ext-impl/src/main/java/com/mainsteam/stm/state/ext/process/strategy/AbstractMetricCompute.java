package com.mainsteam.stm.state.ext.process.strategy;

import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricThreshold;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.TimelineService;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.state.ext.StateComputeContext;
import com.mainsteam.stm.state.ext.process.bean.HasChangedResult;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public abstract class AbstractMetricCompute {

    private static final Log logger = LogFactory.getLog(AbstractMetricCompute.class);

    private static final int PROFILE_INVALID_FLAG = 0;//策略无效标志

    public static final int EMPTY_METRIC_DATA = 0;

    @Autowired
    private TimelineService timelineService;
    @Autowired
    private ProfileService profileService;

    public abstract Object compute(ProfileThreshold profileThreshold, StateComputeContext context) throws Exception;

    protected List<ProfileThreshold> dealWithThresholds(StateComputeContext context) {
        CustomMetric customMetric = context.getCustomMetric();
        List<ProfileThreshold> profileThresholds = null;
        if (null == customMetric) {
            profileThresholds = queryThresholds(context);
            if(null == profileThresholds){
                if(logger.isDebugEnabled()) {
                    MetricCalculateData metricData = context.getMetricData();
                    logger.debug("metric{"+metricData+"} can't find threshold, don't compute...");
                }
                return null;
            }
        } else { //自定义指标
            if(!customMetric.getCustomMetricInfo().isAlert() || !customMetric.getCustomMetricInfo().isMonitor())
                return null;
            List<CustomMetricThreshold> customMetricThresholds = customMetric.getCustomMetricThresholds();
            if(null != customMetricThresholds) {
                profileThresholds = new ArrayList<>(3);
                for (CustomMetricThreshold customMetricThreshold : customMetric.getCustomMetricThresholds()) {
                    if(StringUtils.isNotBlank(customMetricThreshold.getThresholdExpression()) || customMetricThreshold.getMetricState() == MetricStateEnum.NORMAL)
                        profileThresholds.add(convert(customMetricThreshold));
                }
                sort(profileThresholds);
            }else {
                if(logger.isInfoEnabled()) {
                    MetricCalculateData metricData = context.getMetricData();
                    logger.info("missing custom metric threshold : (instId/metricId/profile/timeline:" +
                            metricData.getResourceInstanceId() + "/" + metricData.getMetricId() + "/" + metricData.getProfileId() +
                            "/" + metricData.getTimelineId() + ").");
                }
            }
        }
        return profileThresholds;

    }

    /**
     * 获取性能指标阈值，根据指标阈值来判断
     * @return
     */
    protected List<ProfileThreshold> queryThresholds(StateComputeContext context) {
        try {
            MetricCalculateData metricData = context.getMetricData();
            List<ProfileThreshold> profileThresholds;
            if (metricData.getTimelineId() > PROFILE_INVALID_FLAG) {//基线策略
                ProfileMetric profileMetric = timelineService.getMetricByTimelineIdAndMetricId(metricData.getTimelineId(), metricData.getMetricId());
                if (null != profileMetric) {
                    if (!profileMetric.isAlarm()) {
                        return null;
                    }
                    profileThresholds = timelineService.getThresholdByTimelineIdAndMetricId(profileMetric.getTimeLineId(), profileMetric.getMetricId());
                    //profileMetric.setMetricThresholds(profileThresholds);
                    context.getAdditions().put("flappingCount", profileMetric.getAlarmFlapping());
                }else{
                    if (logger.isWarnEnabled()) {
                        logger.warn("Fail to query timeline profile(data:" + metricData.getResourceInstanceId()
                                + "/" + metricData.getMetricId() + "/" + metricData.getTimelineId() +").");
                    }
                    return null;
                }
            } else {
                if (metricData.getProfileId() == PROFILE_INVALID_FLAG) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Metric profile is invalid(data:" + metricData.getResourceInstanceId()
                                + "/" + metricData.getMetricId() + ").");
                    }
                    return null;
                }
                ProfileMetric profileMetric = profileService.getMetricByProfileIdAndMetricId(metricData.getProfileId(), metricData.getMetricId());
                if (null == profileMetric) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Metric profile is null(data:" + metricData.getResourceInstanceId()
                                + "/" + metricData.getMetricId()+"/" + metricData.getProfileId() + ").");
                    }
                    return null;
                } else if (!profileMetric.isAlarm()) {
                    return null;
                }
                context.getAdditions().put("flappingCount", profileMetric.getAlarmFlapping());
                profileThresholds = profileMetric.getMetricThresholds();

            }
            //需要将没有阈值表达式的策略阈值删除
            if(null != profileThresholds) {
                Iterator<ProfileThreshold> iterator = profileThresholds.iterator();
                while (iterator.hasNext()) {
                    ProfileThreshold next = iterator.next();
                    if(StringUtils.isBlank(next.getThresholdExpression()) && next.getPerfMetricStateEnum() != PerfMetricStateEnum.Normal)
                        iterator.remove();
                }
            }
            if(profileThresholds.isEmpty()) {
                logger.info("missing profile threshold : (instId/metric/profile/timeline:" +
                        metricData.getResourceInstanceId() + "/" + metricData.getMetricId() + "/" + metricData.getProfileId() + "/" + metricData.getTimelineId() + ").");
                return null;
            }
            sort(profileThresholds);
            return profileThresholds;
        } catch (Exception e) {
            if (logger.isErrorEnabled()){
                logger.error("Query metric profile occurs exception(data:"+
                        context.getMetricData().getResourceInstanceId()+ "/" +
                        context.getMetricData().getMetricId() + ";profile:"+
                        context.getMetricData().getProfileId()+"/" + context.getMetricData().getTimelineId()+")." + e.getMessage(), e);
            }
            return null;
        }
    }

    protected void sort(List<ProfileThreshold> profileThresholdList) {
        if(null != profileThresholdList) {
            Collections.sort(profileThresholdList, new Comparator<ProfileThreshold>() {
                @Override
                public int compare(ProfileThreshold o1, ProfileThreshold o2) {
                    return o1.getPerfMetricStateEnum().getStateVal() > o2.getPerfMetricStateEnum().getStateVal() ? -1 :
                            (o1.getPerfMetricStateEnum().getStateVal() < o2.getPerfMetricStateEnum().getStateVal() ? 1 : 0);
                }
            });
        }
    }

    //自定义指标策略转为之标准指标测试格式
    protected ProfileThreshold convert(CustomMetricThreshold customMetricThreshold) {

        ProfileThreshold profileThreshold = new ProfileThreshold();
        profileThreshold.setMetricId(customMetricThreshold.getMetricId());
        profileThreshold.setThresholdExpression(customMetricThreshold.getThresholdExpression());
        profileThreshold.setAlarmTemplate(customMetricThreshold.getAlarmTemplate());

        switch (customMetricThreshold.getMetricState()) {
            case WARN:
                profileThreshold.setPerfMetricStateEnum(PerfMetricStateEnum.Minor);
                break;
            case NORMAL:
                profileThreshold.setPerfMetricStateEnum(PerfMetricStateEnum.Normal);
                break;
            case SERIOUS:
                profileThreshold.setPerfMetricStateEnum(PerfMetricStateEnum.Major);
                break;
            case CRITICAL:
                profileThreshold.setPerfMetricStateEnum(PerfMetricStateEnum.Critical);
        }

        return profileThreshold;
    }

    //根据策略阈值进行计算
    protected MetricStateEnum computeByThresholds(List<ProfileThreshold> profileThresholds, StateComputeContext context) {
        if(profileThresholds == null || profileThresholds.isEmpty()){
            return null;
        }
        MetricCalculateData metricData = context.getMetricData();
        try{
            PerfMetricStateEnum current = PerfMetricStateEnum.Normal;
            ProfileThreshold threshold = null;
            for (ProfileThreshold pt : profileThresholds) {
                if(pt.getPerfMetricStateEnum() == PerfMetricStateEnum.Normal){
                    threshold = pt;
                    continue;
                }
                Object flag ;
                try{
                    flag = compute(pt, context);
                }catch (Exception e){
                    if(logger.isWarnEnabled()){
                        logger.warn("metric state compute error(data:" + metricData.getResourceInstanceId()
                                + "/" + metricData.getMetricId() + "/"+ Arrays.toString(metricData.getMetricData())+")," + e.getMessage());
                    }
                    continue;
                }
                if(null != flag) {
                    if(logger.isDebugEnabled()) {
                        logger.debug("metric("+metricData.getResourceInstanceId() + "/"+metricData.getMetricId()+ ") computes result:"
                                + flag + ";metric value is " + Arrays.toString(metricData.getMetricData()) + "; threshold:" + pt.getThresholdExpression()
                                + ";expected state:" + pt.getPerfMetricStateEnum());
                    }
                    if(flag instanceof Boolean) {
                        Boolean isMatched = (Boolean) flag;
                        if(isMatched){
                            threshold = pt;
                            current = pt.getPerfMetricStateEnum();
                            break;
                        }
                    }else if(flag instanceof HasChangedResult){
                        //如果存在多个阈值表达式，其中一个为hasChanged，那么状态发生变化后，将无法自动恢复
                        if(((HasChangedResult) flag).getResult() == Boolean.TRUE){ //has changed阈值表达式返回值计算结果
                            //Has Changed不使用Flapping计算
                            context.getAdditions().put("preMetricData", ((HasChangedResult) flag).getPreMetricData());
                            context.getAdditions().put("skipMetricFlapping", Boolean.TRUE);
                            context.getAdditions().put("isAlarm", Boolean.TRUE);
                            context.getAdditions().put("hasChangedExp", Boolean.TRUE); //标识是否变化表达式
                            threshold = pt;
                            current = pt.getPerfMetricStateEnum();
                            break;
                        }else {
                            context.getAdditions().put("hasChangedReturn", Boolean.TRUE);
                        }
                    }
                }else{
                    if(logger.isDebugEnabled()) {
                        logger.debug("no metric("+metricData.getResourceInstanceId() + "/"+metricData.getMetricId()+ ") computes result; metric value is " +
                                Arrays.toString(metricData.getMetricData()) + "; threshold[" +
                                pt.getThresholdExpression() + "]; expected state:" + pt.getPerfMetricStateEnum());
                    }
                }
            }
            if(null != threshold)
                context.getAdditions().put("profileThreshold", threshold);
            else {
                if (logger.isDebugEnabled()) {
                    logger.debug("failing to compute metric state {" + profileThresholds + "},metric data:" + metricData);
                }
                return null;
            }
            return current == PerfMetricStateEnum.Major ? MetricStateEnum.SERIOUS : current == PerfMetricStateEnum.Minor
                    ? MetricStateEnum.WARN : current == PerfMetricStateEnum.Critical ? MetricStateEnum.CRITICAL : MetricStateEnum.NORMAL;
        }catch (Exception throwable) {
            if(logger.isWarnEnabled()) {
                logger.warn("metric state compute error(data:" + metricData.getResourceInstanceId()
                        + "/" + metricData.getMetricId() + "/"+ Arrays.toString(metricData.getMetricData())+")," + throwable.getMessage(), throwable);
            }
        }
        return null;
    }

}
