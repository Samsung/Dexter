# Dexter Server
Dexter Server is a back-end server running on NodeJS and MySql in the Dexter platform. Users are required to install Dexter Server in advance when need to manage code quality history using Web based admin console.  

## Get started
This guide is for developers who need to run the server in local environment.

If you are a DevOps or a developer who needs to setup Dexter Server using binary file, then please refer to the below site guide for the installation.

> [Dexter Server Wiki Pages](https://dexter.atlassian.net/wiki/display/DW/Dexter+Server)

## Prerequisites
- Git client : https://git-scm.com/downloads
- Vagrant : https://www.vagrantup.com/downloads.html
- Virtual Box : https://www.virtualbox.org/wiki/Downloads

## Install

1. check out source code in your local directory
```
> mkdir workspace
> cd workspace
> git clone https://github.com/Samsung/Dexter.git
> cd Dexter/dexter-server
```

2. setup MySql using Vagrant
```
> vagrant up
```

If you run this command in first, vagrant will do provisioning which will take some time. Maybe better to run before you go out for lunch or meeting.
You can check the sequence of provision in Vagrantfile and shell scripts in /provision folder.

3. setup Node modules
```
> npm install
```

4. start Dexter Server by nodejs
```
> node app.js
```

5. completed installation!

## Web Admin
```
http://localhost:4982/defect
- default admin : id:admin, pw:dex#0001
- default user  : id:user, pw:dexter
```

## Vagrant control
Vagrant is a tool to enable to provide the same development environment in local across developers. Please visit the website if you need more information. [Vagrant website](https://www.vagrantup.com/)

We are using Ubuntu 64bit os image and MySql server inside.

Here is some essential commands what we need to know.
```
> vagrant up : start
> vagrant ssh : login to vagrant instance
> vagrant halt : stop
> vagrant provision : execute provision
> shared folder : /vagrant <--sync--> ./ [local]
```

### Dexter server
User can control Dexter Server service
```
> sudo service dexter-server start
> sudo service dexter-server stop
> sudo service dexter-server restart
```

Or
```
> cd /vagrant & run.sh
```

### Dexter server log monitoring
```
> tail -f /vagrant/log/dexter.log
```

### MySql server
User can control Dexter Server service
```
> sudo service mysql start
> sudo service mysql stop
> sudo service mysql restart
```

mysql admin
```
> mysql -u root -p1234
> mysql -u dexter-user -pmypassword my_dexter_db
```
