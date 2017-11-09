# bachelorarbeit
A repository containing all code for my Bachelor's thesis.

## Starting the server
To start a server locally, simply download the released `*.jar` and run `java -jar *.jar`. 
If you want to start from code instead of downloading the `*.jar`, make sure to haven `Maven` installed and run `mvn clean package spring-boot:run` at the projects root.

## Starting the android app
When you have downloaded the `*.apk` from the release section in this repository, simply use your phone to install it there. Make sure to authorize foreign content in developer options!
If you want to install it from code, have Android-Studio (or similar) installed, import the project and run `install` within it.

## How to run against a local server
Since only the final servers from the master branch are deployed to AWS ([Level1|http://alxgrk-bachelor-level1.eu-central-1.elasticbeanstalk.com/] | [Level2|http://alxgrk-bachelor-level2.eu-central-1.elasticbeanstalk.com/] | [Level3|http://alxgrk-bachelor-level3.eu-central-1.elasticbeanstalk.com/]), you need to start the server for a specific stage on a specific *change0\** branch locally.
If you then want to connect to this server from the android app, you have to do the following:
1. start the server (see [Starting the server|Starting the server])
2. connect you phone to the computer (via USB)
3. activate *USB-Debugging* in the developer options of your phone (see [here|https://www.greenbot.com/article/2457986/android/how-to-enable-developer-options-on-your-android-phone-or-tablet.html]) and authorize the connected computer
4. activate *USB-Tethering* in the network settings of your phone
5. run `ifconfig` (on any Unix-based system) / `ipconfig` (on Windows)
6. extract the connection starting with something similar to `enp0s20u3: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>` - this is your usb tethering connection
7. copy the IPv4 inet adress (e.g. `192.168.42.37`)
8. go to the android app settings and write down this address to the server level field you want to connect to. **DON'T FORGET TO ADD THE PORT (8080) TO THE ADDRESS!** (leads to e.g. `http://192.168.42.37:8080`)


