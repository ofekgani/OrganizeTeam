package com.myapp.organizeteam.Core;

public class ConstantNames {

    //Path to information in firebase
    public static final String USER_PATH  = "user";
    public static final String TEAM_PATH  = "team";
    public static final String TOKEN_PATH = "tokens";
    public static final String MEETINGS_PATH = "meeting";

    //Keys in firebase
    //if you want to save data in firebase
    public static final String DATA_USER_EMAIL  = "email";
    public static final String DATA_USER_NAME  = "fullName";
    public static final String DATA_USER_LOGO  = "logo";
    public static final String DATA_USER_TEAMS  = "team";
    public static final String DATA_USER_ID  = "keyID";
    public static final String DATA_USER_TEAM = "team";

    public static final String DATA_TEAM_NAME  = "name";
    public static final String DATA_TEAM_DESCRIPTION  = "description";
    public static final String DATA_TEAM_LOGO  = "logo";
    public static final String DATA_TEAM_ID  = "keyID";
    public static final String DATA_TEAM_HOST_ID  = "hostID";

    public static final String DATA_REQUEST_TO_JOIN = "RequestJoin";
    public static final String DATA_USERS_AT_TEAM = "users";

    //Save data in Intent
    public static final String USER  = "user"; //save user object.
    public static final String USER_EMAIL  = "userEmail";
    public static final String USER_NAME  = "userName";
    public static final String USER_LOGO  = "userLogo";
    public static final String USER_KEY_ID  = "userID";
    public static final String USER_TEAM = "userTeam";

    public static final String TEAM = "team"; //save team object.
    public static final String TEAMS_LIST = "teamsList"; //Save list of teams.
    public static final String TEAM_NAME  = "teamName";
    public static final String TEAM_DESCRIPTION  = "teamDescription";
    public static final String TEAM_LOGO  = "teamLogo";
    public static final String TEAM_HOST  = "host"; //save user object.
    public static final String HOST_ID = "hostID";
    public static final String HOST_TOKEN = "hostToken";
    public static final String TEAM_KEY_ID  = "teamID";

    public static final String TOKEN = "token"; //save token object
    public static final String USER_TOKEN = "userToken";


    public static final String DATA_USER_PHONE = "phone";
    public static final String MEETING = "meeting";
    public static final String DATA_MEETING_STATUS = "status";
    public static final String MEETING_KEY_ID = "meetingID";
    public static final String ROLE_PATH = "role";
    public static final String ROLE_NAME = "roleName";
    public static final String ROLE_DESCRIPTION = "roleDescription";
    public static final String DATA_USERS_SELECTED = "users";
    public static final String DATA_USER_ROLES = "roles";
    public static final String ROLES_LIST = "rulesList";
    public static final String DATA_ROLE_MEETING_PERMISSION  = "meeting";
    public static final String ROLE_MEETING_PERMISSION = "meetingPermission";
    public static final String USER_ROLES = "rolesList";
    public static final String USER_PERMISSIONS_MEETING = "userPermissionMeetings";
    public static final String DATA_MEETING_PUBLISH_TO = "publishTo";
    public static final String USER_ACTIVITY_PATH = "userActivity";
    public static final String DATA_USER_MEETINGS = "meetings";
    public static final String USERS_LIST = "usersList";
}
