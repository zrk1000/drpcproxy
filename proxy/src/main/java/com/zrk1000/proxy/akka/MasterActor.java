package com.zrk1000.proxy.akka;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/5/16
 * Time: 18:02
 */
public class MasterActor extends UntypedActor {

    public static Props props(){
        return Props.create(new Creator<Actor>() {
            public Actor create() throws Exception {
                return new MasterActor();
            }
        });
    }

    @Override
    public void onReceive(Object message) throws Exception {
        ActorRef worker = context().actorOf(WorkerActor.props());
        worker.tell(message, self());
    }
}

    