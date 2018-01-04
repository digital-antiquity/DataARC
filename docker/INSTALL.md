For Ubuntu, follow instructions:
1. Install Docker https://www.unixmen.com/container-docker-compose-ubuntu-16-04/
2. I cloned git into /opt/dataarc/DataARC/
3. I setup the src/main/resources/dataarc.properties
4. I added the src/main/resources/solr/configsets/dataARC/managed-schema
5. docker-compose up
6. I also installed nginx and ported 8020 to 80
7. lastly I installed ufw and locked down to only 80. 8443, and 22 being accessible on the machine
