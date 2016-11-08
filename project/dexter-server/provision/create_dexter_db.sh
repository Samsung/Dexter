#! /usr/bin/env bash

echo -e "\n-- create Dexter DB --\n"

#wget -O dexter-db.sql https://dexter.atlassian.net/wiki/download/attachments/6258746/dexter-db.sql?api=v2

mysql -u root -p1234 -e "create database if not exists my_dexter_db DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci; GRANT ALL PRIVILEGES ON my_dexter_db.* TO 'dexter-user'@'localhost' IDENTIFIED BY 'mypassword'"

# drop tables
#mysql -u dexter-user -pmypassword my_dexter_db < /vagrant/config/remove-database.sql

mysql -u dexter-user -pmypassword my_dexter_db < /vagrant/config/ddl.sql
