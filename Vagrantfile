# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/trusty64"

  # Be sure to have the following vagrant plugin installed.
  # vagrant plugin install vagrant-vbguest
  config.vm.synced_folder ".", "/vagrant"

  config.vm.provider "virtualbox" do |vb|
    vb.name = "jaqy"
    vb.gui = false
    vb.memory = "3072"
  end

  config.vm.define "jaqy" do |jaqy|
    jaqy.vm.hostname = "jaqy"
    jaqy.vm.network "private_network", ip: "10.2.3.11"
    jaqy.vm.provision "shell", path: "vmsetup/install.sh"
    # mysql
    jaqy.vm.network "forwarded_port", guest: 3306, host: 3306
    # postgresql
    jaqy.vm.network "forwarded_port", guest: 5432, host: 5432
    jaqy.vm.network "forwarded_port", guest: 5433, host: 5433
    jaqy.vm.network "forwarded_port", guest: 10000, host: 10000
  end

  config.ssh.forward_x11 = true
end
