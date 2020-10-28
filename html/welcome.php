<?php
    session_start();
    if (!isset($_SESSION["USERNAME"])) {
        header("HTTP/1.0 401 Unauthorized");
        include("require_login.php");
        error_log("Neptune: Unauthorized access to welcome.php. User has not logged in.");
        die();
    }

?>
<html>
    <head>
        <title>Welcome</title>
    </head>
    <body>
        Welcome to planet Neptune, <?php echo $_SESSION["USERNAME"]?>.<br>
        <?php if ($_SESSION["USERNAME"] == "thien_hoang") { ?>
        You can access your secret lounge <a href="/special/code01542.php">here</a>.<br>
        <?php } ?>
        <a href="/logout.php">Log out.</a><br>
        <a href="/change_password.php">Change your password</a><br>
        
    </body>
</html>