package com.example.helpafriend;

public class Db_Contract {

    public static String ip = "192.168.0.165"; //change to your ip
    public static final String urlRegister = "http://"+ip+"/helpafriend//register.php";
    public static final String urlLogin = "http://"+ip+"/helpafriend//login.php";
    public static final String urlSubmitPost = "http://"+ip+"/helpafriend//submit_post.php";
    public static final String urlGetPost = "http://"+ip+"/helpafriend//get_posts.php";
    public static final String urlGetNearbyOKU = "http://"+ip+"/helpafriend/get_nearby_oku.php";
    public static final String urlStoreLocation = "http://"+ip+"/helpafriend/store_location.php";
    public static final String urlUpdateStatus = "http://"+ip+"/helpafriend/update_status.php";
    public static final String urlGetAcceptedRequests = "http://" + ip + "/helpafriend/get_accepted_requests.php";
    public static final String urlGetLeaderboard = "http://" + ip + "/helpafriend/get_leaderboard.php";
    public static final String urlUpdateProfile = "http://" + ip + "/helpafriend/updateProfile.php";
    public static final String urlDeleteProfile = "http://" + ip + "/helpafriend/deleteProfile.php";
    public static final String urlFetchUserPoints = "http://" + ip + "/helpafriend/fetch_user_points.php";
    public static final String urlFetchHelpHistory = "http://" + ip + "/helpafriend/fetch_help_history.php";
    public static final String urlGetComment = "http://"+ip+"/helpafriend/get_comment.php";
    public static final String urlSubmitComment = "http://"+ip+"/helpafriend/submit_comment.php";

    public static final String urlLikes = "http://"+ip+"/helpafriend/likes.php";
}