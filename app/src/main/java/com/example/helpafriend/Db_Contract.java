package com.example.helpafriend;

public class Db_Contract {

    public static String ip = "192.168.1.113"; //change to your ip

    public static final String urlRegister = "http://"+ip+"/helpafriend//register.php";
    public static final String urlLogin = "http://"+ip+"/helpafriend//login.php";
    public static final String urlSubmitPost = "http://"+ip+"/helpafriend//submit_post.php";
    public static final String urlGetPost = "http://"+ip+"/helpafriend//get_posts.php";
    public static final String urlGetNearbyOKU = "http://"+ip+"/helpafriend/get_nearby_oku.php";
    public static final String urlStoreLocation = "http://"+ip+"/helpafriend/store_location.php";

    public static final String urlSubmitComment = "http://"+ip+"/helpafriend/submit_comment.php";

    public static final String urlGetComment = "http://"+ip+"/helpafriend/get_comment.php";

}