ONEline Auctions Installation Guide:

In order for our product to work on our user’s system, the user must have a mysql server installed and running, as well as their JDBC driver. MySQL downloads can be found here: 
http://dev.mysql.com/downloads/mysql/
And the JDBC driver downloads can be found here:
http://dev.mysql.com/downloads/connector/j/

Once the server is installed, you can start the server in terminal or command line with the command:
mysql.server start 

Once this is up and running, we can establish a connection to the db. 

(THIS IS ONLY IN THE CASE OF OUR PROJECT, OTHERWISE THE DATABASE CONNECTION WOULD NOT BE A LOCALHOST CONNECTION.)

Once Mysql is installed, ensure that this directory exists:
/usr/local/var/mysql/
This directory will be used to store backup data .txt files. 

After all of these steps are completed, the user could then run the JAR file, or import the ONEline .java files to execute the software. 