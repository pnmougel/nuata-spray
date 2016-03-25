
### Create user

adduser nico sudo

Exit and log as the new user

### Reconfigure locales (optional)

```bash
sudo locale-gen en_US en_US.UTF-8
sudo dpkg-reconfigure locales
```

```bash
sudo apt-get install git
```

### Lighttpd

```bash
sudo apt-get install lighttpd
```

### Get SSL certificate (based on https://nwgat.ninja/setting-up-letsencrypt-with-lighttpd/)

Stop lighttpd service

```bash
sudo service lighttpd stop
```

Download letsencrypt and generate the keys

```bash
git clone https://github.com/letsencrypt/letsencrypt
cd letsencrypt
./letsencrypt-auto certonly --standalone -d api.nuata.io -d nuata.io -d admin.nuata.io
```

The keys should be stored in /etc/letsencrypt/live/api.nuata.io/fullchain.pem
They should be combined into ssl.pem

```bash
sudo su
cd /etc/letsencrypt/live/api.nuata.io/
cat privkey.pem cert.pem > ssl.pem
```

Forward Secrecy & Diffie Hellman Ephemeral Parameters

```bash
cd /etc/ssl/certs
openssl dhparam -out dhparam.pem 4096
```

### Install Oracle JDK 8

sudo apt-add-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer

### Elasticsearch

Download and install elasticsearch

```bash
wget https://download.elasticsearch.org/elasticsearch/release/org/elasticsearch/distribution/deb/elasticsearch/2.2.1/elasticsearch-2.2.1.deb`
sudo dpkg -i elasticsearch-2.2.1.deb`
```

Configure data path

```bash
mkdir $HOME/elasticsearch
mkdir $HOME/elasticsearch/data
mkdir $HOME/elasticsearch/logs
sudo chown -R elasticsearch:elasticsearch $HOME/elasticsearch
```

Open elasticsearch config file
```bash
sudo nano /etc/elasticsearch/elasticsearch.yml
```

Change `path.data: /home/nico/elasticsearch/data` and `path.logs: /home/nico/elasticsearch/logs`

