HADOOP_CLASSPATH=$(hadoop classpath)

all:
	mkdir -p class-folder
	javac -cp `hadoop classpath` -d class-folder/ WordCount.java
	jar -cvf ./wordcount.jar -C class-folder/ .
clean:
	rm -rf class-folder/*
	rm wordcount.jar
run:
	hadoop jar /home/to778287/try-out/wordcount.jar org.myorg.WordCount /user/to778287/input /usr/to778287/output
