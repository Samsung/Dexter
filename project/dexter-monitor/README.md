# Dexter Monitor
Dexter Monitor is a dashboard for monitoring the status of defects and users for all active [Dexter Servers](https://github.com/Samsung/Dexter/tree/master/project/dexter-server)

## How to use
#### Install modules for node(v4.4.7) and bower(v1.7.9)
```
$ npm install
$ bower install
```

#### Write `config.json` file
1. `snapshotDayOfWeek`
	- Default value(`2`) means midnight Tuesday(`0`: Sunday, ... , `6`: Saturday)
    - It creates a snapshot for all active servers at midnight on the specified day of week.
2. `email`
    - Should be set properly in your environment(SMTP or WebService)
    - Refer to [test/back-end/test-mailing.js](https://github.com/Samsung/Dexter/blob/master/project/dexter-monitor/test/back-end/test-mailing.js)
    - WebService version was only tested
3. `userInfoUrl`
    - Should be implemented by yourself(Non-Samsung user)
    - This URL should get `userId` as a query parameter and return the user information like the format below
    	- If your `userInfoUrl` is

         >  "userInfoUrl":"http://yourUserInfoUrl?userId="


          and you call

         >  http://yourUserInfoUrl?userId=sangwoo7.lee

          , then the URL should return

         >     [{"userid":"sangwoo7.lee",
         >     "cn":"Sangwoo Lee",
         >     "department":"Dexter Team",
         >     "title":"Software engineer",
         >     "employeenumber":"1234"}]


#### Run dexter-monitor as a web server
```
$ node app.js -p=port_number
```
 - You can see the web site: `http://your_host:port_number`
 - You have to remove `log/dexter-monitor.log` file when it is too big

## Future features
1. Check a server's resources such as CPU, Memory, etc.
2. Rerun a server which is down