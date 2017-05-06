package com.zrk1000.proxy.bolt;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.zrk1000.proxy.akka.AkkaActor;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

/**
 * Created by rongkang on 2017-04-03.
 */
public class DispatchBolt extends BaseBasicBolt{

    public static ActorSystem actorSystem = ActorSystem.create("actorSystem");
    public BoltHandle boltHandle;

    public DispatchBolt(BoltHandle boltHandle) {
        this.boltHandle = boltHandle;
    }


    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf,context);
        boltHandle.prepare();
    }

    public void execute(Tuple tuple, BasicOutputCollector collector) {
        ActorRef actorRef = actorSystem.actorOf(AkkaActor.props(collector,boltHandle));
        actorRef.tell(tuple,null);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("result", "return-info"));
    }



}
