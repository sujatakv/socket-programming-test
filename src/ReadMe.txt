1> Install JDK 1.6 or 1.7
2> Compile DatabaseManager.java and SocketClient.java
	javac DatabaseManager.java
	javac SocketClient.java
3> Edit device.properties to provide IP & port where the device is hosting server and streaming the data
4> Edit database.properties to provide IP & port and other details regarding the database. Currently there are 10 parameters which are mapped to the column name in database. New parameters can be added. But on the lefthand side the parameter should always be param1, param2.. etc. There should be continuity in numbering.
5> Before running the program the mysql-connector jar should be included to class path. Following is the command to do so in linux. Find an equivalent command in Windows and then run it on the terminal where you run your program.
	export CLASSPATH=/home/sujata/myProjects/test-socket/src/mysql-connector-java-5.0.8-bin.jar:$CLASSPATH
6> Make sure the device is up and socket stream is up before running this program. Otherwise, you will get connection refused error.
7> Run the program,
	java SocketClient 
   This program run in infinite loop, continuously reading the messages from the stream.
