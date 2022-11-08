/**
 * 
 */
package com.mainsteam.stm.profilelib.remote;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.FrequentEnum;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.dao.ProfileQueryDAO;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.MetricSetting;
import com.mainsteam.stm.profilelib.obj.MonitorProfile;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileMetricMonitor;
import com.mainsteam.stm.profilelib.obj.Timeline;
import com.mainsteam.stm.profilelib.obj.TimelineInfo;
import com.mainsteam.stm.profilelib.obj.TimelineProfileMetricMonitor;
import com.mainsteam.stm.profilelib.util.ProfileCache;

/**
 * @author ziw
 * 
 */
public class ProfileLoaderService implements ProfileLoaderServiceMBean {

	private static final Log logger = LogFactory
			.getLog(ProfileLoaderService.class);

	private ProfileQueryDAO queryDAO;

	private ProfileService profileService;

	private CapacityService capacityService;

	private ProfileCache profileCache;
	
	public ProfileCache getProfileCache() {
		return profileCache;
	}

	public void setProfileCache(ProfileCache profileCache) {
		this.profileCache = profileCache;
	}

	/**
	 * 
	 */
	public ProfileLoaderService() {
	}

	/**
	 * @param capacityService
	 *            the capacityService to set
	 */
	public final void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	/**
	 * @param profileService
	 *            the profileService to set
	 */
	public final void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}

	/**
	 * @param queryDAO
	 *            the queryDAO to set
	 */
	public final void setQueryDAO(ProfileQueryDAO queryDAO) {
		this.queryDAO = queryDAO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.profilelib.remote.ProfileLoaderServiceMBean#loadProfile
	 * (long)
	 */
	@Override
	public MonitorProfile[] loadProfile(int nodeGroupId) {
		if (logger.isDebugEnabled()) {
			logger.debug("loadProfile start nodeGroupId=" + nodeGroupId);
		}
		MonitorProfile[] toLoadProfiles = null;
		List<Long> profileIds = queryDAO.queryProfileByDCS(nodeGroupId);
		if (profileIds == null || profileIds.size() <= 0) {
			if (logger.isWarnEnabled()) {
				logger.warn("loadProfile no profile is found.");
			}
		} else {
			long[] profileId = new long[profileIds.size()];
			for (int i = 0; i < profileId.length; i++) {
				profileId[i] = profileIds.get(i);
			}
			toLoadProfiles = loadProfileByProfileId(profileId,nodeGroupId);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("loadProfile end");
		}
		return toLoadProfiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.profilelib.remote.ProfileLoaderServiceMBean#
	 * loadProfileByProfileId(long[])
	 */
	@Override
	public MonitorProfile[] loadProfileByProfileId(long[] profileIds,int nodeGroupId) {
		if (logger.isInfoEnabled()) {
			logger.info("loadProfileByProfileId start profileId.length="
					+ profileIds.length + " nodeGroupId" + nodeGroupId);
		}
		
		MonitorProfile[] profiles = null;
		List<MonitorProfile> monitorProfiles = new ArrayList<>(
				profileIds.length * 5);
		try {
			for (int i = 0; i < profileIds.length; i++) {
				Profile p = profileService.getProfilesById(profileIds[i],nodeGroupId);
				if(p == null){
					if(logger.isWarnEnabled()){
						logger.warn("profile not found profileId=" + profileIds[i]);
					}
					continue;
				}
				MonitorProfile monitorProfile = toMonitorProfile(p);
				if (monitorProfile == null) {
					continue;
				}
				if (logger.isInfoEnabled()) {
					logger.info("loadProfileByProfileId profileId="
							+ profileIds[i]);
				}
				monitorProfiles.add(monitorProfile);
				List<Profile> childProfiles = p.getChildren();
				if (childProfiles != null && childProfiles.size() > 0) {
					for (Profile child : childProfiles) {
						monitorProfile = toMonitorProfile(child);
						if (monitorProfile == null) {
							continue;
						}
						monitorProfiles.add(monitorProfile);
					}
				}
			}

			if (monitorProfiles.size() > 0) {
				profiles = new MonitorProfile[monitorProfiles.size()];
				monitorProfiles.toArray(profiles);
				logger.info("profileSize = " + profiles.length);
			}
		} catch (ProfilelibException e) {
			if (logger.isErrorEnabled()) {
				logger.error("loadProfileByProfileId", e);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("loadProfileByProfileId end");
		}
		return profiles;
	}

	private List<ProfileMetricMonitor> toProfileMetricMonitors(
			List<ProfileMetric> profileMetrics, String resourceId) {
		List<ProfileMetricMonitor> profileMetricMonitors = new ArrayList<>(
				profileMetrics.size());
		for (ProfileMetric profileMetric : profileMetrics) {
			ResourceMetricDef mDef = capacityService.getResourceMetricDef(
					resourceId, profileMetric.getMetricId());
			if (mDef == null) {
				if (logger.isErrorEnabled()) {
					StringBuilder b = new StringBuilder(100);
					b.append("DCS load profileMetric by getResourceMetricDef mothod");
					b.append(" profileId=").append(profileMetric.getProfileId());
					b.append(" resourceId=").append(resourceId);
					b.append(" metricId=").append(profileMetric.getMetricId());
					b.append(" not found!");
					logger.error(b.toString());
				}
				continue;
			}
			ProfileMetricMonitor monitor = new ProfileMetricMonitor();
			monitor.setMetricId(profileMetric.getMetricId());
			monitor.setMonitor(profileMetric.isMonitor());
			monitor.setMonitorFeq(FrequentEnum.valueOf(profileMetric
					.getDictFrequencyId()));
			monitor.setProfileId(profileMetric.getProfileId());
			if(profileMetric.getTimeLineId() > 0){
				monitor.setTimeline(true);
				monitor.setTimelineId(profileMetric.getTimeLineId());
			}else{
				monitor.setTimeline(false);
				monitor.setTimelineId(-1);
			}
			
			/**
			 * 保证可用性指标在前
			 */
			if (mDef.getMetricType() == MetricTypeEnum.AvailabilityMetric) {
				profileMetricMonitors.add(0, monitor);
			} else {
				profileMetricMonitors.add(monitor);
			}
		}
		return profileMetricMonitors;
	}

	private MonitorProfile toMonitorProfile(Profile p) {
		MetricSetting metricSetting = p.getMetricSetting();
		if (metricSetting == null || metricSetting.getMetrics() == null
				|| metricSetting.getMetrics().size() <= 0) {
			if(logger.isWarnEnabled()){
				logger.warn("profileId=" + p.getProfileInfo().getProfileId() + " metric is null");
			}
			return null;
		}
		MonitorProfile monitorProfile = new MonitorProfile();
		monitorProfile.setProfileId(p.getProfileInfo().getProfileId());
		monitorProfile.setParentProfileId(p.getProfileInfo()
				.getParentProfileId());
		monitorProfile.setProfileInstanceRelations(p
				.getProfileInstanceRelations());
		List<ProfileMetric> profileMetrics = metricSetting.getMetrics();
		List<ProfileMetricMonitor> profileMetricMonitors = toProfileMetricMonitors(
				profileMetrics, p.getProfileInfo().getResourceId());
		monitorProfile.setProfileMetricMonitors(profileMetricMonitors);

		List<Timeline> timelines = p.getTimeline();
		if (timelines != null && timelines.size() > 0) {
			List<TimelineProfileMetricMonitor> timelineProfileMetricMonitors = new ArrayList<>(
					timelines.size());
			for (Timeline timeline : timelines) {
				metricSetting = timeline.getMetricSetting();
				if (metricSetting == null || metricSetting.getMetrics() == null
						|| metricSetting.getMetrics().size() <= 0) {
					continue;
				}
				profileMetrics = metricSetting.getMetrics();
				TimelineProfileMetricMonitor m = new TimelineProfileMetricMonitor();
				TimelineInfo timelineInfo = timeline.getTimelineInfo();
				m.setEndTime(timelineInfo.getEndTime());
				m.setStartTime(timelineInfo.getStartTime());
				m.setProfileId(monitorProfile.getProfileId());
				m.setProfileMetricMonitors(toProfileMetricMonitors(
						profileMetrics, p.getProfileInfo().getResourceId()));
				m.setTimelineId(timelineInfo.getTimeLineId());
				m.setTimelineType(timelineInfo.getTimeLineType());
				timelineProfileMetricMonitors.add(m);
			}
			monitorProfile
					.setTimelineProfileMetricMonitors(timelineProfileMetricMonitors);
		}
		return monitorProfile;
	}
}
