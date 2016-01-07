dexter-monitor is to monitor web servers and check if the server is alive

* how to use
1. write config.json file
 - email property should be set properly in your environment(SMTP or WebService)
 - I tested only WebService version.
 - refer to test/backend/test-mailing.js

2. register your target server to monitor in the server-list.json file
 - you can refer to server-list.sample.json file
 - heartbeat propperty should be set properly
 - target server to monitor should provide the web service

3. run dexter-monitor as a web server
 - $ node app.js -p=port_number
 - you can see the web site: http://your_host:port_number
 - you have to remove log/dexter-monitor.log file when it is too big

 * future features
1. check a server's resources such as CPU, Memory, etc.
2. rerun a server which is down