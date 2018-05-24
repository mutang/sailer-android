package com.jkys.sailerxwalkview.dbservice;

import android.content.ContentValues;
import android.util.Log;

import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by on
 * Author: Zern
 * DATE: 16/12/19
 * Time: 15:05
 * Email:AndroidZern@163.com
 */

public class SailerDBService {

    private static class SingletonHolder {
        private static final SailerDBService INSTANCE = new SailerDBService();
    }

    private SailerDBService() {
    }

    public static final SailerDBService getInstance() {
        return SailerDBService.SingletonHolder.INSTANCE;
    }

    // 测试代码 没测试不靠谱
    private void insert(SailerSQLData data) {
        data.save();
    }

    // 测试代码 没测试不靠谱
    private void insertList(List<SailerSQLData> datas) {
        if (datas == null) return;
        DataSupport.saveAll(datas);
    }

    // 测试代码 没测试别用
    private void update(SailerSQLData data){
        if (data == null) return;
        ContentValues c = new ContentValues();
        c.put("fileMD5", data.getFileMD5());
        DataSupport.updateAll(SailerSQLData.class, c, "fileName = ?", data.getFileName());
    }

    public void updateList(List<SailerSQLData> datas) {
        if (datas == null) return;
        DataSupport.deleteAll(SailerSQLData.class);
        List<SailerSQLData> all = DataSupport.findAll(SailerSQLData.class);
        for (int i = 0; i < all.size(); i++) {
            Log.d("ZernH5DBDeLall", all.get(i).getFileName() + "--" + all.get(i).getFileMD5());
        }
        DataSupport.saveAll(datas);
        List<SailerSQLData> all1 = DataSupport.findAll(SailerSQLData.class);
        for (int i = 0; i < all1.size(); i++) {
            Log.d("ZernH5DBADDall", all1.get(i).getFileName() + "--" + all1.get(i).getFileMD5());
        }
    }

    public Map<String, String> findAll(){
        Map map = new HashMap<String, String>();
        List<SailerSQLData> all = DataSupport.findAll(SailerSQLData.class);
        for (SailerSQLData data : all) {
            Log.d("ZernH5DBFindall", data.getFileName() + "--" + data.getFileMD5());
            map.put(data.getFileName(), data.getFileMD5());
        }
        return map;
    }

}
