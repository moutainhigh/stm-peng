package com.mainsteam.stm.dataprocess.bigData;

import java.util.concurrent.Callable;

//import com.chinawiserv.kafka.producer.inter.KafkaProducerTool;
import com.mainsteam.kafka.producer.inter.KafkaProducerTool;

public class KafakCallable implements Callable<Boolean> {
	
	private KafkaProducerTool tool;
	
	public KafkaProducerTool getTool() {
		return tool;
	}

	public void setTool(KafkaProducerTool tool) {
		this.tool = tool;
	}

	private final static KafakCallable callkafak = new KafakCallable();
	
	@Override
	public Boolean call() throws Exception {
		return tool.testConnect();
	}
	
	public static KafakCallable getInstance(){
		return callkafak;
	}

}
