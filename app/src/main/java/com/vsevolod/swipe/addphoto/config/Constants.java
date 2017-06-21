package com.vsevolod.swipe.addphoto.config;

/**
 * Created by vsevolod on 08.04.17.
 */

public class Constants {
    public static final String RESPONSE_STATUS_OK = "OK";
    public static final String RESPONSE_STATUS_BAD = "BAD";
    public static final String RESPONSE_STATUS_INIT = "INIT";
    public static final String RESPONSE_STATUS_DIE = "DIE";
    public static final String RESPONSE_STATUS_AUTH = "AUTH";
    public static final String RESPONSE_STATUS_PARAM = "PARAM";
    public static final String RESPONSE_STATUS_FAIL = "FAIL";
    public static final String RESPONSE_TREE_SUB_STATUS_NM = "NM";//not modified
    public static final String RESPONSE_TREE_SUB_STATUS_CH = "CH";// ?
    public static final String RESPONSE_TREE_SUB_STATUS_NT = "NT";//not tested
    public static final String RESPONSE_AUTH_SUB_STATUS_TEL = "TEL";//number not valid
    public static final String RESPONSE_AUTH_SUB_STATUS_PASS = "PASS";//password not valid
    public static final String RESPONSE_AUTH_SUB_STATUS_VALID = "VALID";
    static final String BASE_URL = "http://crm.myaso.net.ua/ext/api/";
    public static final String MEDIA_TYPE_IMAGE = "image/*";
    public static final String ACTION_SELECT_PICTURE = "Select Picture";
    public static final String EXTENSION_JPG = ".jpg";
    public static final String DATA_MODEL_STATE_CREATED = "Created";
    public static final String DATA_MODEL_STATE_ACCEPTED = "Accepted";
    public static final String DATA_MODEL_STATE_REVIEW = "Review";
    public static final String DATA_MODEL_STATE_DECLINED = "Declined";
    public static final String DATA_MODEL_STATE_NEED_SYNC = "Need sync";
    public static final String DATA_MODEL_STATE_PARAM = "PARAM";
    public static final String INTENT_KEY_PATH = "image/";
    public static final String INTENT_KEY_PHOTO_RES = "photo resource";
    public static final String INTENT_KEY_EXIT = "com.vsevolod.swipe.addphoto.EXIT";
    public static final String INTENT_KEY_NOTIFY = "notify";
    public static final String INTENT_KEY_SERVER_PHOTO_URL = "server photo url";
    public static final String INTENT_KEY_STORAGE_PHOTO_URL = "storage photo url";
    public static final int CAPTURE_PICTURE_REQUEST = 31;
    public static final int SELECT_PICTURE_REQUEST = 12;
    public static final int THUMB_SIZE = 500;
    public static final int PHONE_NUMBER_LENGTH = 13;
    public static final int MIN_PASSWORD_LENGTH = 3;
    private static final int MILLISECONDS_SEC = 1000;
    public static final int MILLISECONDS_MINUTE = MILLISECONDS_SEC * 60;
    public static final int MILLISECONDS_HOUR = MILLISECONDS_MINUTE * 60;
    public static final int MILLISECONDS_DAY = MILLISECONDS_HOUR * 24;
    public static final int MIN_TIME_BEFORE_NEXT_SYNC = MILLISECONDS_SEC * 5; // 5 sec
    public static final int MILLISECONDS_FIVE_MIN = MILLISECONDS_MINUTE * 5; // 5 min
    public static final int MILLISECONDS_TEN_SEC = MILLISECONDS_SEC * 10; // 10  sec
}