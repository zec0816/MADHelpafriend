package com.example.helpafriend;
public class Db_Contract {

    // Your local server IP
    public static String ip = "192.168.1.108"; // Replace with the correct IP if needed

    // User Authentication
    public static final String urlRegister = "http://" + ip + "/helpafriend/register.php";
    public static final String urlLogin = "http://" + ip + "/helpafriend/login.php";

    // Forum Post Management
    public static final String urlSubmitPost = "http://" + ip + "/helpafriend/submit_post.php";
    public static final String urlGetPost = "http://" + ip + "/helpafriend/get_posts.php";

    // Like Management
    public static final String urlAddLike = "http://" + ip + "/helpafriend/lovepost.php";

}
