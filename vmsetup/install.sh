#!/bin/bash

# ---- Install Oracle JDK 8 ----
# add Oracle Repository
sudo apt-add-repository -y ppa:webupd8team/java
sudo apt-get update
# accept license non-iteractive
sudo echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
# install
sudo apt-get install -y oracle-java8-installer
# make sure Java 8 becomes default java
sudo apt-get install -y oracle-java8-set-default

# ---- Install a few different databases for testing ----
# ==== PostgreSQL ====
sudo apt-get -y install postgresql
# setup the password
sudo -u postgres psql -c "alter user postgres with password '';"
# Let PostgreSQL to listen connections from all ips (not just local host)
ver=`/bin/ls -C1 /etc/postgresql`
sudo cp /vagrant/vmsetup/p*.conf /etc/postgresql/${ver}/main/
# Restart the PostgreSQL server
sudo /etc/init.d/postgresql restart

# ---- PostGIS ----
sudo echo 'deb http://apt.postgresql.org/pub/repos/apt/ trusty-pgdg main' > /etc/apt/sources.list.d/pgdg.list
sudo wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -
sudo apt-get update
sudo apt-get install -y --force-yes postgis postgresql-9.3-postgis-2.3
sudo -u postgres psql -c "CREATE EXTENSION postgis; CREATE EXTENSION postgis_topology;" postgres
sudo /etc/init.d/postgresql restart

# Create the swap file for running MySQL
sudo fallocate -l 4G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

# ==== MySQL ====
sudo debconf-set-selections <<< 'mysql-server mysql-server/root_password password vagrant'
sudo debconf-set-selections <<< 'mysql-server mysql-server/root_password_again password vagrant'
sudo apt-get -y install mysql-server-5.6

# Travis CI MySQL database user is travis, password is blank
mysql -u root --password=vagrant <<EOF
CREATE DATABASE vagrant;
CREATE DATABASE travis;
CREATE USER 'travis' IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON * . * TO 'travis';
FLUSH PRIVILEGES;
EOF

# Set the password for MySQL root to empty
mysql -u root --password=vagrant -e "SET PASSWORD FOR 'root'@'localhost' = PASSWORD('')"
mysql -u root <<EOF
CREATE USER 'root'@'%' IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON * . * TO 'root'@'%';
FLUSH PRIVILEGES;
EOF

# Let MySQL to listen connections from all ips (not just local host)
sudo sed -i 's/bind-address.*=.*/bind-address=0.0.0.0/' /etc/mysql/my.cnf

# Restart the MySQL server
sudo /etc/init.d/mysql restart

# ---- Install a graphical file comparison tool ----
sudo apt-get -y install meld

# ---- Setup .profile ----
PROFILE='
PATH=${PATH}:/vagrant/tests/bin/
'
echo "$PROFILE" >> ~vagrant/.profile

# ---- Setup .bashrc ----
BASHRC="
alias dir='ls -l'
alias ll='dir -a'
alias md=mkdir
alias rd=rmdir
alias where=which
alias grep='grep --color=auto'

alias jq='java -jar /vagrant/dist/jaqy-1.1.0.jar'
alias jqe='java -ea -jar /vagrant/dist/jaqy-1.1.0.jar'

cd /vagrant
"
echo "$BASHRC" >> ~vagrant/.bashrc
echo ".@load /vagrant/jaqy-avro/target/jaqy-avro-1.1.0.jar" > ~vagrant/.jqrc
echo ".@load /vagrant/jaqy-s3/target/jaqy-s3-1.1.0.jar" >> ~vagrant/.jqrc

chown vagrant ~vagrant/.jqrc
chgrp vagrant ~vagrant/.jqrc
