#!/bin/bash

# ---- Install Java ----
sudo apt-get -y install openjdk-8-jre

# ---- Install a few different databases for testing ----
# PostgreSQL
sudo apt-get -y install postgresql
# setup the password
sudo -u postgres psql -c "alter user postgres with password '';"

# MySQL
sudo debconf-set-selections <<< 'mysql-server mysql-server/root_password password vagrant'
sudo debconf-set-selections <<< 'mysql-server mysql-server/root_password_again password vagrant'
sudo apt-get -y install mysql-server

# Travis CI MySQL database user is travis, password is blank
mysql -u root --password=vagrant <<EOF
CREATE USER 'travis'@'localhost' IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON * . * TO 'travis'@'localhost';
FLUSH PRIVILEGES;
EOF

# ---- Install a graphical file comparison tool ----
sudo apt-get -y install meld

# ---- Setup .profile ----
PROFILE='
PATH=${PATH}:/vagrant/tests/bin/
'
echo "$PROFILE" >> ~ubuntu/.profile

# ---- Setup .bashrc ----
BASHRC="
alias dir='ls -l'
alias ll='dir -a'
alias rd=rmdir

alias jq='java -jar /vagrant/dist/jaqy-1.0.jar'

cd /vagrant
"
echo "$BASHRC" >> ~ubuntu/.bashrc
