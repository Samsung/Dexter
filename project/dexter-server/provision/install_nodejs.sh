#! /usr/bin/env bash

echo -e "\n--- installing NodeJS now... ---\n"

curl -sL https://deb.nodesource.com/setup_4.x | sudo -E bash -
sudo apt-get install -y nodejs

echo -e "\n--- update package dependencies ---\n"
cd /vagrant
npm install
sudo chmod +x run.sh

echo -e "\n--- register dexter as a service... ---\n"
sudo service dexter stop
sudo cp /vagrant/provision/dexter.conf /etc/init
sudo service dexter start
