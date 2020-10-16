<?php
    session_start();
    if (!isset($_SESSION["USERNAME"])) {
        header("HTTP/1.0 401 Unauthorized");
        include("../require_login.php");
        error_log("Neptune: Unauthorized access to /special/code01542.php. User has not logged in.");
        die();
    }
    if ($_SESSION["USERNAME"] != "thien_hoang") {
        header("HTTP/1.1 403 Forbidden");
        http_response_code(403);
        die("Forbidden");
    }
?>
<html>
    <head>
        <title>Code 01542 - <?php echo $_SESSION["USERNAME"]; ?></title>
    </head>
    <body>
        <h1>WELCOME TO THE SECRET LOUNGE</h1>
    </body>
</html>