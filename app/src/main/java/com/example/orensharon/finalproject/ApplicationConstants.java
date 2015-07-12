package com.example.orensharon.finalproject;

/**
 * Created by orensharon on 4/20/15.
 */
public class ApplicationConstants
{
    // Routing server host url
    public static final String ROUTING_SERVER_URL = "http://www.sharon-se-server.dynu.com";

    // Routing server API endpoints
    public static final String LOGIN_API = ROUTING_SERVER_URL + ":9004/Login/auth/json";
    public static final String IP_GET_API = ROUTING_SERVER_URL + ":9002/IPGetterService/json";

    // Safe endpoints
    public static final String PHOTO_INSERT_API_SUFFIX = ":9003/StreamService/Photo/Insert";
    public static final String PHOTO_UPDATE_API_SUFFIX = ":9003/StreamService/Photo/Update";
    public static final String PHOTO_GET_LIST_API_SUFFIX = ":9003/StreamService/Photo/Get";
    public static final String PHOTO_GET_API_SUFFIX = ":9003/StreamService/Photo/Get/";
    public static final String CONTACT_INSERT_API_SUFFIX = ":9003/StreamService/Contact/Insert";
    public static final String CONTACT_UPDATE_API_SUFFIX = ":9003/StreamService/Contact/Update";
    public static final String CONTACT_GET_API_SUFFIX = ":9003/StreamService/Contact/Get";


    /* JSON parameters keys */

    // Requests types
    public static final String IP_REQUEST_TAG = "ip_request";
    public static final String LOGIN_REQUEST_TAG= "login";
    public static final String LIST_REQUEST_TAG = "list_request";

    // Login
    public static final String AUTH_TOKEN_KEY = "Token";
    public static final String LOGIN_USERNAME_KEY = "Username";
    public static final String LOGIN_PASSWORD_KEY = "Password";

    // IP Getter
    public static final String IP_GETTER_IP_ADDRESS_KEY = "IP";


    // Data streaming
    public static final String CONTENT_STREAM_UPLOAD_RESULT_KEY = "UploadResult";  // MD5
    public static final String SAFE_RESPONSE_LIST_OF_PHOTOS = "GetListOfPhotosResult";
    public static final String SAFE_RESPONSE_LIST_OF_CONTACTS = "GetContactsResult";

    // Contact properties of JSON request
    public static final String CONTACT_ID_KEY = "Id";
    public static final String CONTACT_DISPLAY_NAME_KEY = "DisplayName";
    public static final String CONTACT_PHOTO_URI_KEY = "PhotoId";
    public static final String CONTACT_PHONES_KEY = "Phones";
    public static final String CONTACT_PHONE_NUMBER_KEY = "Number";
    public static final String CONTACT_PHONE_TYPE_KEY = "Type";
    public static final String CONTACT_EMAIL_KEY = "Emails";
    public static final String CONTACT_ADDRESSES_KEY = "Addresses";
    public static final String CONTACT_ADDRESS_ADDRESS_KEY = "Address";
    public static final String CONTACT_ADDRESS_TYPE_KEY = "Type";
    public static final String CONTACT_ORGANIZATION_KEY = "Organization";
    public static final String CONTACT_ORGANIZATION_TITLE_KEY = "Title";
    public static final String CONTACT_ORGANIZATION_COMPANY_KEY = "Company";
    public static final String CONTACT_NOTES_KEY = "Notes";

    // Photo properties of JSON request
    public static final String PHOTO_READ_ID_KEY= "RealId";
    public static final String PHOTO_DATE_CREATED_KEY= "DateCreated";
    public static final String PHOTO_GEO_LOCATION_KEY= "GeoLocation";






    /* End JSON parameters keys */

    // HTTP Status codes
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_CONFLICT = 409;
    public static final int HTTP_INTERNAL_SERVER_ERROR = 500;
    public static final int HTTP_METHOD_NOT_ALLOWED = 405;

    // HTTP Posts constants
    public static final String POST_BODY_BOUNDARY = "xx";

    // HTTP Headers
    public static final String HEADER_CONTENT_MD5 = "Content-MD5";
    public static final String HEADER_AUTHORIZATION = "Authorization";

    // Body Parameters of the upload content requests
    public static final String CONTENT_TYPE_OF_CONTENT_KEY = "TypeOfContent";
    public static final String CONTENT_ID_KEY = "Id";
    public static final String CONTENT_CREATED_TIME_STAMP_KEY = "CreatedTimeStamp";
    public static final String CONTENT_MODIFIED_TIME_STAMP_KEY = "ModifiedTimeStamp";
    public static final String DEVICE_PHYSICAL_ADDRESS_TOKEN = "PhysicalAddress";

    // Type of contents (photos, contacts, videos etc...)
    public static final String TYPE_OF_CONTENT_PHOTO = "Photo";
    public static final String TYPE_OF_CONTENT_CONTACT = "Contact";
    public static final String TYPE_OF_CONTENT_SMS = "Sms";
    public static final String TYPE_OF_CONTENT_CALL = "Call";

    // Phones Types
    public static final String[] CONTACT_PHONE_TYPES =
            { "Custom", "Home", "Mobile", "Work", "Work Fax", "Home Fax", "Pager", "Other", "Callback" };

    // Addresses / Email Addresses Types
    public static final String[] CONTACT_ADDRESSES_TYPES =
            { "Custom", "Home", "Work", "Other" };


    // Default init values of needed data, When the value of property is set
    // to those values it means the data doesn't exist (for example: the application doesn't
    // knows the IP address of the safe it will set to be 'no-ip' value
    // Session constants
    public static final String NO_IP_VALUE = "";
    public static final String NO_TOKEN_VALUE = "";



    // Fragments/activities tags
    public static final String SETTING_FRAGMENT_TAG = "SETTINGS_FRAGMENT";
    public static final String ACCOUNT_FRAGMENT_TAG = "ACCOUNT_FRAGMENT";



    // Content Keys
    public static final String LAST_ID_CONTACTS = "LAST_ID_CONTACTS";
    public static final String BACK_UP_LIST_CONTACTS = "BACK_UP_LIST_CONTACTS";
    public static final String UNSYNCED_CONTACTS = "UNSYNCED_CONTACTS";
    public static final String LAST_ID_PHOTOS = "LAST_ID_PHOTOS";
    public static final String BACK_UP_LIST_PHOTOS = "BACK_UP_LIST_PHOTOS";
    public static final String UNSYNCED_PHOTOS = "UNSYNCED_PHOTOS";

    public static final String CUSTOM_FONT_PATH = "fonts/LHANDW.TTF";


    public static enum FeedContentKeys {
        PHOTOS(PHOTO_GET_API_SUFFIX, PHOTO_GET_LIST_API_SUFFIX),
        CONTACTS(CONTACT_GET_API_SUFFIX, CONTACT_GET_API_SUFFIX);

        private String mContentAPI, mContentListAPI;

        private FeedContentKeys(String contentAPI, String listAPI) {
            mContentAPI = contentAPI;
            mContentListAPI = listAPI;
        }

        public String getContentAPI() {
            return mContentAPI;
        }

        public String getContentListAPI() {
            return mContentListAPI;
        }


    }
}
