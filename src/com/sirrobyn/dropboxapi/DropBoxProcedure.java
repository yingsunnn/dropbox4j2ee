package com.sirrobyn.dropboxapi;

import java.io.File;

import oracle.sql.BLOB;
import oracle.sql.CLOB;

import com.dropbox.client2.exception.DropboxException;

public class DropBoxProcedure {
	
	private static DropBoxAPI api = null;
	private static Utils utils = null;
	
	public DropBoxProcedure () {
		api = new DropBoxAPI();
		utils = new Utils();
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
	public static CLOB requestToken (String appKey, String appSecret, String callback) {
		try {
			return utils.stringToCLOB( api.requestToken(appKey, appSecret, callback));
		} catch (DropboxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static CLOB requestToken (String appKey, String appSecret) {
		return requestToken(appKey, appSecret, null); 
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
	public static CLOB accessToken (String appKey, String appSecret, String requestTokenKey, String requestTokenSecret) {
		try {
			return utils.stringToCLOB( api.accessToken(appKey, appSecret, requestTokenKey, requestTokenSecret));
		} catch (DropboxException e) {
			e.printStackTrace();
		}
		return null;
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
	public static CLOB getAccountInfo (String appKey, String appSecret, String accessTokenKey, String accessTokenSecret) {
		try {
			return utils.stringToCLOB( api.getAccountInfo(appKey, appSecret, accessTokenKey, accessTokenSecret));
		} catch (DropboxException e) {
			e.printStackTrace();
		}
		return null;
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
	public static CLOB metadata (String dropboxPath, String appKey, String appSecret, String accessTokenKey, String accessTokenSecret) {
		try {
			return utils.stringToCLOB( api.metadata(dropboxPath, appKey, appSecret, accessTokenKey, accessTokenSecret));
		} catch (DropboxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static BLOB downloadFile (String dropboxPath, String appKey, String appSecret, String accessTokenKey, String accessTokenSecret) {
		File file = api.downloadFile(dropboxPath, appKey, appSecret, accessTokenKey, accessTokenSecret);
		
		return utils.stringToBLOB(file.toString());
	}
	
	public static CLOB uploadFile (String dropboxPath, BLOB blob, String appKey, String appSecret, String accessTokenKey, String accessTokenSecret) {
		api.uploadFile(dropboxPath, utils.blobToInputstream(blob), blob.getLength(), appKey, appSecret, accessTokenKey, accessTokenSecret);
		
		return null;
	}
	
}
