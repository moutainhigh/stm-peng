package com.mainsteam.stm.state.ext.listener;

import com.mainsteam.stm.common.sync.DataSyncPO;
import com.mainsteam.stm.common.sync.DataSyncService;
import com.mainsteam.stm.common.sync.DataSyncTypeEnum;
import com.mainsteam.stm.lock.LockCallback;
import com.mainsteam.stm.lock.LockService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 策略变更计算基类
 */
public abstract class AbstractProfileChangesListener {

    private static final Log logger = LogFactory.getLog(AbstractProfileChangesListener.class);

    @Autowired
    private DataSyncService dataSyncService;
    @Autowired
    private LockService lockService;

    private LinkedBlockingQueue<DataSyncPO> dataSyncPOS = new LinkedBlockingQueue<>();

    public abstract void process(DataSyncPO dataSyncPO) throws Exception;

    public abstract DataSyncTypeEnum get();

    public void process() {
        while (true) {
            DataSyncPO take = null;
            try {
                take = dataSyncPOS.take();
                this.process(take);
            } catch (Exception e) {
                if(logger.isWarnEnabled()) {
                    logger.warn("profile listener occurs exception: "+ e.getMessage() + "," + take, e);
                }
                //重新保存进去，以便下次修复后再重试
                this.dataSyncService.save(take);
            }
        }
    }

    @PostConstruct
    public void init() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try{
                        lockService.sync("AbstractProfileChangesListener", new LockCallback<List<DataSyncPO>>() {
                            @Override
                            public List<DataSyncPO> doAction() {
                                List<DataSyncPO> dataSyncs = dataSyncService.selectBatch(get());
                                if(null != dataSyncs && !dataSyncs.isEmpty()) {
                                    List<Long> ids = new ArrayList<>(dataSyncs.size());
                                    for(DataSyncPO dataSyncPO : dataSyncs) {
                                        try {
                                            dataSyncPOS.put(dataSyncPO);//blocking operation
                                        } catch (InterruptedException e) {
                                            if(logger.isWarnEnabled()){
                                                logger.warn(e.getMessage(), e);
                                            }
                                        }
                                        ids.add(dataSyncPO.getId());
                                    }
                                    dataSyncService.delete(ids);
                                }
                                return null;
                            }
                        }, 10);

                        TimeUnit.SECONDS.sleep(30);
                    }catch (Exception e) {
                        if(logger.isWarnEnabled()) {
                            logger.warn(e.getMessage(), e);
                        }
                    }

                }

            }
        });

        thread.setName("AbstractProfileChangesListener-" + this.getClass().getSimpleName());
        thread.start();
        if(logger.isInfoEnabled()) {
            logger.info("AbstractProfileChangesListener "+this.getClass().getSimpleName()+"starts...");
        }

        Thread processor = new Thread(new Runnable() {
            @Override
            public void run() {
                process();
            }
        });
        processor.setName("AbstractProfileChangesListener Worker -" + this.getClass().getSimpleName());
        processor.start();
        if(logger.isInfoEnabled()) {
            logger.info("AbstractProfileChangesListener Worker "+this.getClass().getSimpleName()+"starts...");
        }
    }
}
