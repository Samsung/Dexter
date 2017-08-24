# Dexter Server
Dexter Server is a back-end server running on NodeJS and MySql in the Dexter platform. Users are required to install Dexter Server in advance when need to manage code quality history using Web based admin console.  

# Getting started
This guide is for developers who need to run the server in local environment.

> Refer to full installation guide for details
[Dexter Server Wiki Pages](https://dexter.atlassian.net/wiki/display/DW/Dexter+Server)


# Docker Guide
## Installing on Docker

1. Pull the latest image from DockerHub:

```
$ docker pull srpol/dexter:latest
```

2. Run image

```
$ docker run --restart=always --name=<YOUR_CONTAINER_NAME> -td -p 4982:4982 --env DBHOST=<DBHOST> --env DBNAME=<DBNAME> --env DBUSER=<DBUSER> --env DBPASSWORD=<DBPASSWORD> <IMAGE_NAME>
```

where:
   -  `<DBHOST>` - address of Dexter database (in order to create database, use create_dexter_db.sh)
   - `<DBNAME` - name of Dexter database
   - `<DBUSER>` - database user name
   - `<DBPASSWORD>` - database user password
   - `<IMAGE_NAME>` - (optional) your image name 
      

By default, instance is available on port 4982. You can change it, by changing the first argument in -p parameter, e.g `-p 8080:4982`.

## Viewing logs 

Just use following command: 

```
$ docker logs -f <IMAGE_NAME>
```


# Vagrant Guide
## Prerequisites
- Git client : https://git-scm.com/downloads
- Vagrant : https://www.vagrantup.com/downloads.html
- Virtual Box : https://www.virtualbox.org/wiki/Downloads

## Install on Vagrant

1. check out source code in your local directory
```
> mkdir [workspace] <= any folder name
> cd workspace
> git clone https://github.com/Samsung/Dexter.git
> cd Dexter/dexter-server
```
2. setup MySql and NodeJS using Vagrant
```
> vagrant up
```
Initially Vagrant will do provision process which will download os box image, install packages and set up MySql DB & NodeJS automatically.
You can check the detail provisioning sequence by **Vagrantfile** and shell scripts in **/provision** folder.
3. Installation completed!

## Web Admin

http://localhost:4982/defect
```
- default admin : id:admin, pw:dex#0001
- default user  : id:user, pw:dexter
```

## Vagrant commands
Here is some essential commands what we will use.
```
> vagrant up : start
> vagrant ssh : login into Dexter Server
> vagrant halt : stop
> vagrant provision : execute provision
> shared folder : /vagrant <--sync--> ./ [local]
```

### Dexter server
User can control Dexter Server service
```
vagrant> sudo service dexter start
vagrant> sudo service dexter stop
vagrant> sudo service dexter restart
```
Or
```
vagrant> cd /vagrant & run.sh
```


### Dexter server log monitoring
```
vagrant> tail -f /vagrant/log/dexter.log
```

### MySql server
User can control Dexter Server service
```
vagrant> sudo service mysql start
vagrant> sudo service mysql stop
vagrant> sudo service mysql restart
```

mysql admin
```
vagrant> mysql -u root -p1234
vagrant> mysql -u dexter-user -pmypassword my_dexter_db
```
