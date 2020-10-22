<?php
    session_start();
    if (isset($_SESSION["USERNAME"])) {
        header("location: welcome.php");
    }
    $error_msg = "";
    if ($_SERVER["REQUEST_METHOD"] == "POST") {
        $username = $_POST["username"];
        $password = $_POST["password"];
        $password2 = $_POST["password2"];

        if ($username == "thien_hoang" || $username == "quan_nguyen" || $username == "khuong_lu" || $username == "hieu_le") {
            header("HTTP/1.0 409 Conflict");
            $error_msg = $username." already exists.";
            error_log("Neptune: Failed to register for ".$username.". Account already exists.");
            // die();
        } else 

        if ($password != $password2) {
            header("HTTP/1.0 401 Unauthorized");
            $error_msg = "Passwords mismatched.";
            error_log("Neptune: Failed to register for ".$username.". Two passwords entered mismatched.");
            // die();
        } else {
            $error_msg = "Registered successfully! (but of course this is just a demo webserver so no new account is created)";
            error_log("Neptune: New account created for ".$username.".");
        }
    
    }
?>
<html>
    <head>
        <title>Register</title>
    </head>
    <body>
        <form action="" method="post">
            <label>Username:</label><br>
            <input type="text" name="username" class="box"><br>
            <label>Password:</label><br>
            <input type="password" name="password" class="box"><br>
            <label>Re-enter your password:</label><br>
            <input type="password" name="password2" class="box"><br>
            <br>
            <button type="submit"> Submit </button>
            <div><?php echo $error_msg; ?></div>
        </form>
        <p>Already had an account? <a href="/login.php">Login</a> here.</p>
    </body>
    <script>
      if ( window.history.replaceState ) {
         window.history.replaceState( null, null, window.location.href );
      }
   </script>
</html>