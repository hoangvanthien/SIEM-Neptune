<?php
    session_start();
    error_log("Neptune: ".$_SESSION["USERNAME"]." has logged out of the system.");
    unset($_SESSION["USERNAME"]);
    header("location: login.php");
?>