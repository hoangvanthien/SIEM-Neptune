<?php
    session_start();
    $error_msg = "";
    $username = $_SESSION["USERNAME"];
    if ($_SERVER["REQUEST_METHOD"] == "POST") {
        $old_password = $_POST["old_password"];
        $password = $_POST["password"];
        $password2 = $_POST["password2"];

        if ($old_password != "neptune") {
            header("HTTP/1.0 403 Forbidden");
            $error_msg = "Incorrect old password";
            error_log("Neptune: Failed to change password for ".$username.". Old password is incorrect.");
            // die();
        } else 

        if ($password != $password2) {
            header("HTTP/1.0 401 Unauthorized");
            $error_msg = "New passwords mismatched.";
            error_log("Neptune: Failed to change password for ".$username.". Two passwords entered mismatched.");
            // die();
        } else {
            $error_msg = "Changed password successfully! (but of course this is just a demo webserver so no new account is created)";
            error_log("Neptune: Successfully changed password for ".$username.".");
        }
    
    }
?>
<html>
    <head>
        <title>Change password</title>
    </head>
    <body>
        <form action="" method="post">
            <label>Old password:</label><br>
            <input type="password" name="old_password" class="box"><br>
            <label>New password:</label><br>
            <input type="password" name="password" class="box"><br>
            <label>Re-enter your new password:</label><br>
            <input type="password" name="password2" class="box"><br>
            <br>
            <button type="submit"> Submit </button>
            <div><?php echo $error_msg; ?></div>
        </form>
    </body>
    <script>
      if ( window.history.replaceState ) {
         window.history.replaceState( null, null, window.location.href );
      }
   </script>
</html>