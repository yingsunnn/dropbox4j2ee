package com.sirrobyn.dropboxapi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Account;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;
import com.dropbox.client2.session.WebAuthSession.WebAuthInfo;

public class DropBoxAPI {

	private static AccessType ACCESS_TYPE;
	
	public DropBoxAPI () {
		ACCESS_TYPE = AccessType.DROPBOX;
	}
	
	public DropBoxAPI (String accessType) {
		if ("DROPBOX".equals(accessType)) {
			ACCESS_TYPE = AccessType.DROPBOX;
		} else if ("APP_FOLDER".equals(accessType)) {
			ACCESS_TYPE = AccessType.APP_FOLDER;
		}
	} 
	
	/**
	 * 
	 * @param appKey
	 * @param appSecret
	 * @param callback
	 * @return example:
	 {
		"auth_url":"https://www.dropbox.com:443/1/oauth/authorize?oauth_token=wz39umdohyij219&locale=en",
		"request_token_key":"wz39umdohyij219",
		"request_token_secret":"kjds0bqev8x4k40"
	 }
	 * @throws DropboxException
	 */
	public String requestToken(String appKey, String appSecret, String callback)
			throws DropboxException {
		AppKeyPair appKeys = new AppKeyPair(appKey, appSecret);
		WebAuthSession session = new WebAuthSession(appKeys, ACCESS_TYPE);
		WebAuthInfo authInfo = session.getAuthInfo(callback);
		RequestTokenPair pair = authInfo.requestTokenPair;

		StringBuilder resultJson = new StringBuilder();
		resultJson.append("{");
		resultJson.append("\"auth_url\":\"" + authInfo.url + "\",");
		resultJson.append("\"request_token_key\":\"" + pair.key + "\",");
		resultJson.append("\"request_token_secret\":\"" + pair.secret + "\"");
		resultJson.append("}");
		return resultJson.toString();
	}
	
	/**
	 * 
	 * @param appKey
	 * @param appSecret
	 * @param requestTokenKey
	 * @param requestTokenSecret
	 * @return example
	 {
		"uid":"134965674",
		"access_token_key":"s63tlegwkkfc9yl",
		"access_token_secret":"9vlg5lyc2w142oq"
	 }
	 * @throws DropboxException
	 */
	public String accessToken (String appKey, String appSecret, String requestTokenKey, String requestTokenSecret) throws DropboxException {
		AppKeyPair consumerTokenPair = new AppKeyPair(appKey, appSecret);
        WebAuthSession session = new WebAuthSession(consumerTokenPair, ACCESS_TYPE);
        RequestTokenPair pair = new RequestTokenPair(requestTokenKey, requestTokenSecret);
        String uid = session.retrieveWebAccessToken(pair);
        
        StringBuilder resultJson = new StringBuilder();
        resultJson.append("{");
        resultJson.append("\"uid\":\"" + uid + "\",");
        resultJson.append("\"access_token_key\":\"" + session.getAccessTokenPair().key + "\",");
        resultJson.append("\"access_token_secret\":\"" + session.getAccessTokenPair().secret + "\"");
        resultJson.append("}");
        return resultJson.toString();
	}
	
	/**
	 * 
	 * @param appKey
	 * @param appSecret
	 * @param accessTokenKey
	 * @param accessTokenSecret
	 * @return example:
	 {
		"country":"US",
		"displayName":"yve sun",
		"quota":"2147483648",
		"quotaNormal":"1509623",
		"referralLink":"https://www.dropbox.com/referrals/NTEzNDk2NTY3NDk",
		"uid":"134965674"
	 }
	 * @throws DropboxException
	 */
	public String getAccountInfo (String appKey, String appSecret, String accessTokenKey, String accessTokenSecret) throws DropboxException {
		AppKeyPair consumerTokenPair = new AppKeyPair(appKey, appSecret);
        WebAuthSession session = new WebAuthSession(consumerTokenPair, AccessType.DROPBOX);
        AccessTokenPair reAuthTokens = new AccessTokenPair(accessTokenKey, accessTokenSecret);
        DropboxAPI<WebAuthSession> mDBApi = new DropboxAPI(session);
        mDBApi.getSession().setAccessTokenPair(reAuthTokens);
        
        Account info = mDBApi.accountInfo();

        StringBuilder resultJson = new StringBuilder();
        resultJson.append("{");
        resultJson.append("\"country\":\"" + info.country + "\",");
        resultJson.append("\"displayName\":\"" + info.displayName + "\",");
        resultJson.append("\"quota\":\"" + info.quota + "\",");
        resultJson.append("\"quotaNormal\":\"" + info.quotaNormal + "\",");
        resultJson.append("\"referralLink\":\"" + info.referralLink + "\",");
        resultJson.append("\"uid\":\"" + info.uid + "\"");
        resultJson.append("}");
        
        return resultJson.toString();
	}
	
	/**
	 * 
	 * @param dropboxPath root "/"
	 * @param appKey
	 * @param appSecret
	 * @param accessTokenKey
	 * @param accessTokenSecret
	 * @return example:
	 * [
		[{"file_name":"Getting Started.pdf"},{"is_folder":"false"}],
		[{"file_name":"ic_launcher.png"},{"is_folder":"false"}],
		[{"file_name":"Photos"},{"is_folder":"true"}]
	   ]
	 * @throws DropboxException
	 */
	public String metadata (String dropboxPath, String appKey, String appSecret, String accessTokenKey, String accessTokenSecret) throws DropboxException {
		AppKeyPair consumerTokenPair = new AppKeyPair(appKey, appSecret);
        WebAuthSession session = new WebAuthSession(consumerTokenPair, AccessType.DROPBOX);
        AccessTokenPair reAuthTokens = new AccessTokenPair(accessTokenKey, accessTokenSecret);
        DropboxAPI<WebAuthSession> mDBApi = new DropboxAPI(session);
        mDBApi.getSession().setAccessTokenPair(reAuthTokens);
        
        Entry entries = mDBApi.metadata(dropboxPath, 100, null, true, null);

        StringBuilder resultJson = null;
        
        for (Entry e : entries.contents) {
            if (!e.isDeleted) {
            	if (resultJson == null) {
            		resultJson = new StringBuilder();
            		resultJson.append("[");
            		resultJson.append("[{\"file_name\":\"" + e.fileName() + "\"}, {\"is_folder\":\"" + String.valueOf(e.isDir) + "\"}]");
            	} else {
            		resultJson.append(",[{\"file_name\":\"" + e.fileName() + "\"}, {\"is_folder\":\"" + String.valueOf(e.isDir) + "\"}]");
            	}
            	System.out.print("Is Folder: " + String.valueOf(e.isDir));
               	System.out.println(", Item Name: " + e.fileName());
            }
        }
        
        if (resultJson != null) 
        	resultJson.append("]");
        else 
        	resultJson = new StringBuilder();
        
        return resultJson.toString();
	}
	
	public File downloadFile (String dropboxPath, String appKey, String appSecret, String accessTokenKey, String accessTokenSecret) {
		AppKeyPair consumerTokenPair = new AppKeyPair(appKey, appSecret);
        WebAuthSession session = new WebAuthSession(consumerTokenPair, ACCESS_TYPE);
        AccessTokenPair reAuthTokens = new AccessTokenPair(accessTokenKey, accessTokenSecret);
        DropboxAPI<WebAuthSession> mDBApi = new DropboxAPI(session);
        mDBApi.getSession().setAccessTokenPair(reAuthTokens);
        
		// Get file.
		OutputStream outputStream = null;
		try {
		    File file = new File(dropboxPath);
		    outputStream = new FileOutputStream(file);
		    DropboxFileInfo info = mDBApi.getFile(dropboxPath, null, outputStream, null);
		    
		    return file;
		} catch (DropboxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		    if (outputStream != null) {
		        try {
		            outputStream.close();
		        } catch (IOException e) {}
		    }
		}
		return null;
	}
	
	/**
	 * 
	 * @param path like : "/ic_launcher.png"
	 * @param inputStream
	 * @param appKey
	 * @param appSecret
	 * @param accessTokenKey
	 * @param accessTokenSecret
	 */
	public void uploadFile (String dropboxPath, InputStream inputStream, long length, String appKey, String appSecret, String accessTokenKey, String accessTokenSecret) {
		AppKeyPair consumerTokenPair = new AppKeyPair(appKey, appSecret);
        WebAuthSession session = new WebAuthSession(consumerTokenPair, ACCESS_TYPE);
        AccessTokenPair reAuthTokens = new AccessTokenPair(accessTokenKey, accessTokenSecret);
        DropboxAPI<WebAuthSession> mDBApi = new DropboxAPI(session);
        mDBApi.getSession().setAccessTokenPair(reAuthTokens);
        //Uploading content.
        
        try {
        	Entry newEntry = mDBApi.putFile(dropboxPath, inputStream, length, null, null);
        } catch (DropboxUnlinkedException e) {
        	e.printStackTrace();
        } catch (DropboxException e) {
        	e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {}
            }
        }
	}
	
	public static void main(String[] args) throws DropboxException {
		String appKey = "26vn2ynois4yfeb";
		String appSecret = "zvqzq94803cvj3w";
		String CALL_BACK = null;

		String requestTokenKey = "9hgrccm7wj54zge";
		String requestTokenSecret = "2ltepamoe3qp4b5";
		
		String accessTokenKey = "s63tlegwkkfc9yl";
		String accessTokenSecret = "9vlg5lyc2w142oq";
		
		DropBoxAPI api = new DropBoxAPI();
		
		//System.out.println(api.requestToken(appKey, appSecret, CALL_BACK));
		//System.out.println(api.accessToken(appKey, appSecret, requestTokenKey, requestTokenSecret));
		//System.out.println(api.getAccountInfo(appKey, appSecret, accessTokenKey, accessTokenSecret));
		//System.out.println(api.metadata("/", appKey, appSecret, accessTokenKey, accessTokenSecret));
	}

}
