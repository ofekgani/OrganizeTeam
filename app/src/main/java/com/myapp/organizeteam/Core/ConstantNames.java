package com.myapp.organizeteam.Core;

public class ConstantNames {

    //Path to information in firebase
    public static final String USER_PATH  = "user";
    public static final String TEAM_PATH  = "team";
    public static final String TOKEN_PATH = "tokens";
    public static final String MEETINGS_PATH = "meeting";
    public static final String TASK_PATH = "task";
    public static final String ROLE_PATH = "role";
    public static final String POST_PATH = "post";
    public static final String USER_ACTIVITY_PATH = "userActivity";
    public static final String USER_STATUSES_PATH = "userStatus";
    public static final String MEETINGS_HISTORY_PATH = "meetingHistory";
    public static final String TASKS_HISTORY_PATH = "taskHistory";

    //Keys in firebase
    //User
    public static final String DATA_USER_EMAIL  = "email";
    public static final String DATA_USER_NAME  = "fullName";
    public static final String DATA_USER_LOGO  = "logo";
    public static final String DATA_USER_TEAMS  = "team";
    public static final String DATA_USER_ID  = "keyID";
    public static final String DATA_USER_TEAM = "team";
    public static final String DATA_USER_PHONE = "phone";

    //Team path
    public static final String DATA_TEAM_ID  = "keyID";
    public static final String DATA_REQUEST_TO_JOIN = "RequestJoin";

    //Task path
    public static final String DATA_PUBLISH_TO = "publishTo";
    public static final String DATA_TASK_REPLIES = "responses";
    public static final String DATA_TASK_CONFIRM = "confirmStatus";
    public static final String DATA_TASK_TITLE = "title";

    //Meeting path
    public static final String DATA_MEETING_STATUS = "status";

    //User activity path
    public static final String DATA_USER_TASKS = "tasks";
    public static final String DATA_USER_ROLES = "roles";
    public static final String DATA_USER_MEETINGS = "meetings";
    public static final String DATA_USER_POSTS = "posts";

    //Role path
    public static final String DATA_ROLE_MEETING_PERMISSION  = "meetingPermission";
    public static final String DATA_ROLE_TASK_PERMISSION = "tasksPermission";
    public static final String DATA_ROLE_POST_PERMISSION = "postsPermission";
    public static final String DATA_ROLE_NAME = "name";
    public static final String DATA_ROLE_DESCRIPTION = "description";

    //Submitter
    public static final String DATA_SUBMITTER_RESPONSES = "responses";

    //Other
    public static final String DATA_USERS_LIST = "users";

    //Save data in Intent
    //Constructs
    public static final String USER  = "user";
    public static final String TEAM = "team";
    public static final String TOKEN = "token";
    public static final String MEETING = "meeting";
    public static final String ROLE = "role";
    public static final String TASK = "task";
    public static final String SUBMITTER = "submitter";
    public static final String POST = "post";

    //User
    public static final String USER_NAME  = "userName";
    public static final String USER_LOGO  = "userLogo";
    public static final String USER_KEY_ID  = "userID";

    //Team
    public static final String TEAMS_LIST = "teamsList"; //Save list of teams.
    public static final String TEAM_HOST  = "host"; //save user object.
    public static final String HOST_TOKEN = "hostToken";
    public static final String TEAM_KEY_ID  = "teamID";

    //Role
    public static final String ROLE_NAME = "roleName";
    public static final String ROLE_DESCRIPTION = "roleDescription";
    public static final String ROLES_LIST = "rolesList";
    public static final String ROLE_MEETING_PERMISSION = "meetingPermission";

    //User activity
    public static final String USER_ROLES = "rolesList";
    public static final String USER_PERMISSIONS_MEETING = "userPermissionMeetings";
    public static final String USER_PERMISSIONS_TASK = "userPermissionTasks";
    public static final String USER_PERMISSIONS_POST = "userPermissionPosts";

    //Submitter
    public static final String USER_SUBMITTER = "userSubmitter";

    //Other
    public static final String USERS_LIST = "usersList";
    public static final String REQUESTS_LIST = "requestsList";
    public static final String USER_PERMISSIONS = "userPermissions";

    public static final String DATA_USER_STATUS_ARRIVED = "arrived";
    public static final String DATA_USER_STATUS_MISSING = "missing";
    public static final String DATA_USER_STATUS_CONFIRM = "confirm";
    public static final String DATA_USER_STATUS_UNSUBMITTED = "unsubmitted";
    public static final String DATA_USER_STATUS_SUBMITTED = "submitted";
}
