package com.zrk1000.demo;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Created by rongkang on 2017-05-18.
 */
@Component
public class UserInterceptor extends EmptyInterceptor {

    @Override
    public boolean onLoad(
            Object entity,
            Serializable id,
            Object[] state,
            String[] propertyNames,
            Type[] types) {
        System.out.println("=============");
        System.out.println(entity);
        System.out.println(id);
        System.out.println(state);
        System.out.println(propertyNames);
        System.out.println(types);
        return false;
    }

    @Override
    public boolean onSave(
            Object entity,
            Serializable id,
            Object[] state,
            String[] propertyNames,
            Type[] types) {
        System.out.println("=============");
        System.out.println(entity);
        System.out.println(id);
        System.out.println(state);
        System.out.println(propertyNames);
        System.out.println(types);
        return false;
    }

    @Override
    public void onDelete(
            Object entity,
            Serializable id,
            Object[] state,
            String[] propertyNames,
            Type[] types) {
        System.out.println("=============");
        System.out.println(entity);
    }

    @Override
    public boolean onFlushDirty(
            Object entity,
            Serializable id,
            Object[] currentState,
            Object[] previousState,
            String[] propertyNames,
            Type[] types) {
        System.out.println("=============");
        System.out.println(entity);
        return false;
    }

    @Override
    public void postFlush(Iterator entities) {
        System.out.println("=============");
    }

    @Override
    public void preFlush(Iterator entities) {
        System.out.println("=============");
    }

    @Override
    public Boolean isTransient(Object entity) {
        System.out.println("=============");
        return null;
    }

    @Override
    public Object instantiate(String entityName, EntityMode entityMode, Serializable id) {
        System.out.println("=============");
        return null;
    }

    @Override
    public int[] findDirty(
            Object entity,
            Serializable id,
            Object[] currentState,
            Object[] previousState,
            String[] propertyNames,
            Type[] types) {
        System.out.println("=============");
        return null;
    }

    @Override
    public String getEntityName(Object object) {
        System.out.println("=============");
        return null;
    }

    @Override
    public Object getEntity(String entityName, Serializable id) {
        System.out.println("=============");
        return null;
    }

    @Override
    public void afterTransactionBegin(Transaction tx) {
        System.out.println("=============");
    }

    @Override
    public void afterTransactionCompletion(Transaction tx) {
        System.out.println("=============");
    }

    @Override
    public void beforeTransactionCompletion(Transaction tx) {
        System.out.println("=============");
    }

    @Override
    public String onPrepareStatement(String sql) {
        System.out.println("=============");
        return sql;
    }

    @Override
    public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
        System.out.println("=============");
    }

    @Override
    public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException {
        System.out.println("=============");
    }

    @Override
    public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
        System.out.println("=============");
    }
}
