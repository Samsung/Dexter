#! /usr/bin/env bash

echo -e "\n--- installing Mysql Server now... ---\n"

sudo apt-get --yes install debconf-utils
sudo debconf-set-selections <<< "mysql-server mysql-server/root_password password 1234"
sudo debconf-set-selections <<< "mysql-server mysql-server/root_password_again password 1234"

sudo apt-get --yes install mysql-server

sudo service mysql stop
echo -e "copy mysql config to /etc/my.cnf"
sudo cp /vagrant/provision/my.cnf /etc/my.cnf
sudo service mysql start
