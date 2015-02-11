package com.traxit.common;

public interface Config {
	//static final String TraxitServerURL = "http://192.168.1.91/traxit/api/api.php" ;       //    Local
	
	//static final String TraxitServerURL = "http://198.1.123.59/~traxitor/api/api.php";
	static final String TraxitServerURL = "http://traxit.org/api/api.php";
	static final String DeviceToken = "deviceToken" ;                  //  Deivce token id
	static final String UserEmail = "userEmail";
	static final String UserPassword = "userPassword";
	static final String UserName = "userName";
	static final String FirstName = "firstName";
	static final String LastName = "lastName";
	static final String CountryCode = "countryCode";
	static final String PhoneNumber = "phoneNumber";
	static final String AccountType = "accountType";
	static final String UserLocationShared = "userLocationSahred";
	static final String RememberPassword = "rememberPassword";
	static final String LoginStatus = "loginStatus";
	static final String DataDirectoryPath = "directoryPath";
	static final String UserPhotoName = "userPhotoName";
	static final String LinkedFriendEmailList = "linkedFriendEmailList";          //    List of email that is registerd as friend.
	static final String GroupIndex = "groupIndex";
	static final String FriendIndex = "friendIndex";
	static final String UnGroup = "ungroup";
	static final String FriendEmail = "friendEmail";
	static final String FriendName = "friendName";
	static final String GroupName = "groupName";


	static final String kAccountTypeGoogle = "google";
	static final String kAccountTypeFaceBook="facebook";
	static final String kAccountTypeLinkedIn="linkedIn";
	static final String KAccountTypeManual = "manual";
	public static  enum FriendCellType {sectionHeader, groupCell, ungroupCell, requestCell}
	
	static final String GOOGLE_SENDER_ID = "336203363098";  // Place here your Google project id
	static final String TAG = "TRAXIT GCM";
    static final String DISPLAY_MESSAGE_ACTION =
            "com.als.proofofdelivery.DISPLAY_MESSAGE";
    static final String EXTRA_MESSAGE = "message";
    
    static final String Notification_Message = "notificationMessage";
    static final String Notification_Update_Friends_Data = "updateFriendsInfo";
    static final String Notification_Update_Address = "updateFriendsAddress";
    
    //-------------Push Notification Key-----------
    static final String kPushMessageType = "message";
    static final String kPushInviteType = "invite";
    static final String kPushDeclineType = "decline";
    static final String kPushAcceptType = "accept";
    static final String kPushLocationType  = "updateLocation";
    
    static final int UPLOAD_POST_IMAGE_WIDTH = 300;
    static final int UPLOAD_POST_IMAGE_HEIGHT = 400;
    
}
