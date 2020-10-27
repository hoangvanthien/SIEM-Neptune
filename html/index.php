<?php 
    session_start();
    if (isset($_SESSION["USERNAME"]))
        header("Location: welcome.php");
    else
        header("Location: login.php");
?>
