package com.chc.dochoo.conversations;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.chc.exceptions.InternalErrorException;
import com.chc.found.models.DatabaseHelper;
import com.chc.found.network.NetworkRequestsUtil;
import com.chc.found.presenters.NewsPresenter;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

public class NewsModel {
	
	private static final int PAGE_LIMIT = 25;

	private static final String TAG = "NewsModel";
	
	private NewsPresenter mPresenter;
	
	public NewsModel(NewsPresenter presenter) {
		mPresenter = presenter;
	}
	
	/**
	 * select all news from database having the same entity id
	 * @param dbhelper
	 * @param entityId
	 * @return
	 */
	public List<NewsMessage> getCachedNewsList(DatabaseHelper dbhelper, String entityId) {
		RuntimeExceptionDao<NewsMessage, Long> newsMsgRuntimeDao = dbhelper.getNewsMsgRuntimeDao();
		QueryBuilder<NewsMessage, Long> qb = newsMsgRuntimeDao.queryBuilder();
		try {
		  qb.orderBy(NewsMessage.COLUMN_NAME_TIME, false)
		    .where()
		    .eq(NewsMessage.COLUMN_NAME_ENTITY_ID, entityId);
			return newsMsgRuntimeDao.query(qb.prepare());
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return new ArrayList<NewsMessage>(0);
	}
	
	public NewsMessage getCachedNewsById(DatabaseHelper dbhelper, String id) {
		RuntimeExceptionDao<NewsMessage, Long> newsMsgRuntimeDao = dbhelper.getNewsMsgRuntimeDao();
		QueryBuilder<NewsMessage, Long> queryBuilder = newsMsgRuntimeDao.queryBuilder();
		NewsMessage res = null;
		try {
			queryBuilder.where().eq(NewsMessage.COLUMN_NAME_ID, id);
			List<NewsMessage> resList = newsMsgRuntimeDao.query(queryBuilder.prepare());
			if (resList != null && !resList.isEmpty()) res = resList.get(0);
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		
		return res;
	}
	
	public void loadNewsFromServer(DatabaseHelper dbhelper, String docId, String pushId) {
		new LoadNewsTask(dbhelper).execute(docId, pushId);
	}
	
	public synchronized void writeToDB(DatabaseHelper dbhelper, List<NewsMessage> msgList) {
		RuntimeExceptionDao<NewsMessage, Long> newsMsgRuntimeDao = dbhelper.getNewsMsgRuntimeDao();
//		// delete old data
//		for (NewsMessage n : newsMsgRuntimeDao) {
//			newsMsgRuntimeDao.delete(n);
//		}
//		// only stores first page into database
//		if (msgList.size() > PAGE_LIMIT)
//			msgList = msgList.subList(0, PAGE_LIMIT);
		for (NewsMessage m : msgList) {
			newsMsgRuntimeDao.createOrUpdate(m);
		}
	}
	
	/**
	 * get news list server, will do nothing if the params number not equals to 2
	 * @param params params[0] is docId, params[1] is pushId
	 * @return
	 */
	private class LoadNewsTask extends AsyncTask<String, Void, String> {
		
		private DatabaseHelper dbhelper;
		private String docId;
		
		public LoadNewsTask(DatabaseHelper dbhelper) {
			super();
			this.dbhelper = dbhelper;
		}

		@Override
		protected String doInBackground(String... params) {
			String news = null;
			if(params.length != 2)
				return news;
			
			docId = params[0];
			String pushId = params[1];
			
			try {
				news = NetworkRequestsUtil.getNews(docId, pushId);
			} catch (IOException e) {
				Log.e(TAG, e.getMessage(), e);
			} catch (InternalErrorException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return news;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			List<NewsMessage> newsList = new ArrayList<NewsMessage>();
			if (StringUtils.isBlank(result)) {
				Log.e(TAG, "news result is blank!");
			} else {				
//				if (Apis.DEBUG) Log.w(TAG, result);
				try {
					JSONArray jsonArray = new JSONArray(result);
					for (int i = 0; i < jsonArray.length(); ++i) {
						JSONObject jsonObject = jsonArray.optJSONObject(i);
						NewsMessage news = new NewsMessage(jsonObject, docId);
						newsList.add(news);
					}
					writeToDB(dbhelper, newsList);
				} catch (Exception e) {
					newsList.clear(); // clear the list to indicate there is no valid message
					Log.e(TAG, e.getMessage(), e);
				}
			}
			
			mPresenter.onMessageLoaded(newsList);
		}

	} 
}
