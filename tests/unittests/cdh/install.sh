#!/bin/bash

# ---- Install Java ----
yum -y install java-1.6.0-openjdk

# ---- Install a graphical file comparison tool ----
yum -y install meld

# ---- Setup .profile ----
PROFILE='
PATH=${PATH}:/vagrant/tests/bin/
'
echo "$PROFILE" >> ~cloudera/.bash_profile

# ---- Setup .bashrc ----
BASHRC="
unalias rm
unalias cp
unalias mv

alias dir='ls -l'
alias ll='dir -a'
alias rd=rmdir

alias jq='java -jar /vagrant/jaqy-console/target/jaqy-console-1.0.jar'

cd /vagrant
"
echo "$BASHRC" >> ~cloudera/.bashrc
