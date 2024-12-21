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

}