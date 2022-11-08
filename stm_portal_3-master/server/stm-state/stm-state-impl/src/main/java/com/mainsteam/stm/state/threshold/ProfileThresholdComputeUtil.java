/**
 * 
 */
package com.mainsteam.stm.state.threshold;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.mainsteam.stm.caplib.dict.OperatorEnum;
import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;

/** 
 * @author 作者：ziw
 * @date 创建时间：2016年11月15日 上午11:29:44
 * @version 1.0
 */
/** 
 */
public class ProfileThresholdComputeUtil {
	private final static Map<String, StateCal> calMap = new HashMap<>();

	static {
		init();
	}

	@PostConstruct
	public static void init() {
		calMap.put(OperatorEnum.Great.toString(), new StateCal() {
			@Override
			public PerfMetricStateEnum doAction(ProfileThreshold pt, Float md) {
				return md == null ? null
						: (md > parse(pt.getThresholdValue())) ? pt
								.getPerfMetricStateEnum() : null;
			}
		});
		calMap.put(OperatorEnum.GreatEqual.toString(), new StateCal() {
			@Override
			public PerfMetricStateEnum doAction(ProfileThreshold pt, Float md) {
				return md == null ? null
						: (md >= parse(pt.getThresholdValue())) ? pt
								.getPerfMetricStateEnum() : null;
			}
		});
		calMap.put(OperatorEnum.Less.toString(), new StateCal() {
			@Override
			public PerfMetricStateEnum doAction(ProfileThreshold pt, Float md) {
				return md == null ? null
						: (md < parse(pt.getThresholdValue())) ? pt
								.getPerfMetricStateEnum() : null;
			}
		});
		calMap.put(OperatorEnum.LessEqual.toString(), new StateCal() {
			@Override
			public PerfMetricStateEnum doAction(ProfileThreshold pt, Float md) {
				return md == null ? null
						: (md <= parse(pt.getThresholdValue())) ? pt
								.getPerfMetricStateEnum() : null;
			}
		});
		calMap.put(OperatorEnum.Equal.toString(), new StateCal() {
			@Override
			public PerfMetricStateEnum doAction(ProfileThreshold pt, Float md) {
				return md == null ? null : (md.equals(parse(pt
						.getThresholdValue()))) ? pt.getPerfMetricStateEnum()
						: null;
			}
		});
	}

	private static Float parse(String val) {
		try {
			return Float.valueOf(val);
		} catch (Exception e) {
			return 0f;
		}
	}

	static interface StateCal {
		PerfMetricStateEnum doAction(ProfileThreshold pt, Float md);
	}

	public static MetricStateEnum compute(ProfileThreshold pt, Float md) {
		PerfMetricStateEnum state = calMap.get(pt.getExpressionOperator()).doAction(pt, md);
		if (state != null) {
			return mapper(state);
		}
		return null;
	}

	private static MetricStateEnum mapper(final PerfMetricStateEnum state) {
		switch (state) {
		case Major:
			return MetricStateEnum.SERIOUS;
		case Minor:
			return MetricStateEnum.WARN;
		case Normal:
		case Indeterminate:
		default:
			return MetricStateEnum.NORMAL;
		}
	}
}
