<?php
   session_start();
   if (isset($_SESSION["USERNAME"])) {
      header("location: welcome.php");
   }
   $username_error = "";
   $error = "";
   if ($_SERVER["REQUEST_METHOD"] == "POST") {
      $username = trim($_POST["username"]);
      if (empty($username)) {
         $username_error = "Please enter your username!";
      }
      $password = $_POST["password"];
      
      $successful = false;

      if ($username == "thien_hoang" || $username == "quan_nguyen" || $username == "khuong_lu" || $username == "hieu_le") {
         if (password_verify($password, password_hash("neptune", PASSWORD_DEFAULT))) {
            $successful = true;
         }
      }

      if ($successful) {
         $_SESSION["USERNAME"] = $username;
         header("location: welcome.php");
         $error = "";
         error_log("Neptune: User ".$username." has logged in successfully.");
      } else {
         header("HTTP/1.0 401 Unauthorized");
         $error = "Wrong username or password!";
         error_log("Neptune: Failed login for username " . $username . " and password (md5 hashed) " . md5($password));
      }
   }
?>
<html>   
   <head>
      <link rel="icon" href="/favicon.ico">
      <title>Login Page</title>
   </head>
   
   <body>
	   <b>Login</b>
      <form action = "" method = "post">
         <label>Username  :</label><br>
         <input type = "text" name = "username" class = "box"/><br>
         <?php echo $username_error; ?><br>

         <label>Password  :</label><br>
         <input type = "password" name = "password" class = "box" /><br>
         <br>

         <input type = "submit" value = " Submit "/><br>
      </form>
      <?php echo $error; ?><br>
      No account? <a href="register.php">Register</a> here.
   </body>
   <script>
      if ( window.history.replaceState ) {
         window.history.replaceState( null, null, window.location.href );
      }
   </script>
</html>