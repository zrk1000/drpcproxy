package com.zrk1000.proxy.akka;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import com.alibaba.fastjson.JSON;
import com.zrk1000.proxy.bolt.BoltHandle;
import com.zrk1000.proxy.rpc.drpc.DrpcRequest;
import com.zrk1000.proxy.rpc.drpc.DrpcResponse;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/4/28
 * Time: 9:29
 */
public class AkkaActor extends UntypedActor {

    private static Logger logger = LoggerFactory.getLogger(AkkaActor.class);

    public static ActorSystem actorSystem = ActorSystem.create("actorSystem");

    private BasicOutputCollector collector;
    private BoltHandle boltHandle;

    public static Props props(final BasicOutputCollector collector, final BoltHandle boltHandle) {
        return Props.create(new Creator<AkkaActor>() {
            public AkkaActor create() throws Exception {
                return new AkkaActor(collector,boltHandle);
            }
        });
    }

    public AkkaActor(BasicOutputCollector collector,BoltHandle boltHandle){
        this.collector = collector;
        this.boltHandle = boltHandle;

    }

    public void onReceive(Object message) throws Exception {
        if(logger.isDebugEnabled())
            logger.debug("do AkkaActor onReceive,message:{}",message);
        logger.info("do AkkaActor onReceive");
        Tuple tuple = (Tuple) message;
        String param = tuple.getString(0);
        DrpcRequest request = JSON.parseObject(param,DrpcRequest.class);
        DrpcResponse response = boltHandle._execute(request);
        collector.emit(new Values(JSON.toJSONString(response), tuple.getValue(1)));
        context().stop(self());
    }
}

    