package com.zrk1000.proxy.bolt;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.tuple.Tuple;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/5/16
 * Time: 18:39
 */
public class WrapTuple {

    private Tuple tuple;

    private BasicOutputCollector collector;

    private BoltHandle boltHandle;

    public WrapTuple(Tuple tuple, BasicOutputCollector collector,BoltHandle boltHandle) {
        this.tuple = tuple;
        this.collector = collector;
        this.boltHandle = boltHandle;
    }

    public Tuple getTuple() {
        return tuple;
    }

    public void setTuple(Tuple tuple) {
        this.tuple = tuple;
    }

    public BasicOutputCollector getCollector() {
        return collector;
    }

    public void setCollector(BasicOutputCollector collector) {
        this.collector = collector;
    }

    public BoltHandle getBoltHandle() {
        return boltHandle;
    }

    public void setBoltHandle(BoltHandle boltHandle) {
        this.boltHandle = boltHandle;
    }
}

    