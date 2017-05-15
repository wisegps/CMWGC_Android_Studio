package com.wgc.cmwgc.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by Administrator on 2016/12/5.
 */
public class DBManager {

    private final static String dbName = "device_upload_db";
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public DBManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }

    /**
     * 插入一条记录
     *
     * @param deviceData
     */
    public void insertDeviceData(DeviceDataEntity deviceData) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DeviceDataEntityDao userDao = daoSession.getDeviceDataEntityDao();
        userDao.insert(deviceData);
    }

    /**
     * 插入用户集合
     *
     * @param deviceDataEntitys
     */
    public void insertDeviceDataList(List<DeviceDataEntity> deviceDataEntitys) {
        if (deviceDataEntitys == null || deviceDataEntitys.isEmpty()) {
            return;
        }
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DeviceDataEntityDao deviceDataEntityDao = daoSession.getDeviceDataEntityDao();
        deviceDataEntityDao.insertInTx(deviceDataEntitys);
    }

    /**
     * 删除一条记录
     *
     * @param deviceDataEntity
     */
    public void deleteDeviceData(DeviceDataEntity deviceDataEntity) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DeviceDataEntityDao userDao = daoSession.getDeviceDataEntityDao();
        userDao.delete(deviceDataEntity);
    }

    /**
     * 删除全部记录
     */
    public void deleteDeviceDataAll() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DeviceDataEntityDao userDao = daoSession.getDeviceDataEntityDao();
        userDao.deleteAll();
    }




    /**
     * 更新一条记录
     *
     * @param deviceDataEntity
     *
     */
    public void updateDeviceData(DeviceDataEntity deviceDataEntity) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DeviceDataEntityDao userDao = daoSession.getDeviceDataEntityDao();
        userDao.update(deviceDataEntity);
    }

    /**
     * 查询用户列表
     */
    public List<DeviceDataEntity> queryDeviceDataList() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DeviceDataEntityDao deviceDataEntityDao = daoSession.getDeviceDataEntityDao();
        QueryBuilder<DeviceDataEntity> qb = deviceDataEntityDao.queryBuilder();
        List<DeviceDataEntity> list = qb.list();
        return list;
    }

}
