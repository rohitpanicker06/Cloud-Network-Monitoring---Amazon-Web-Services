README.md

## Overview
Agent developed for fetching latest vpc-flow logs stored in S3-Bucket and parse it for various Operations.
##Operations Perfomed:
1. Fetch Vpc-FLow logs periodically and parse it.
3. Generate Flow Logs Record.
4. Push the logs to an user-defined API.
5. Could be used as an adapter to fetch logs for:
   
   a) AIOps : Event Correlation, Anomaly Detection, Causality Determination.
   
   b) Network-Flow Visualization
   
   c) Track and analyze all Network Interfaces within a VPC.

## Dockerhub repository

[Dockerhub](https://hub.docker.com/repository/docker/061119981998/vpcflowlogspoc-05)

## Requirements
1. You must have an AWS Account.
2. Access Id and Access Key : must have permissions to read flow_logs from S3 bucket.
3. VPC-Flowlogs enabled with storage option set-as S3-bucket.
4. API Tokens: where you wish to publish/push the flow-logs.


## PARAMETERS

#### 1. AWS IAM Security Credentials 
AWS_ACCESS_ID       	
  
AWS_SECRET_KEY 
 
#### 2. Api Tokens
 API_ACCESS_ID    		 
API_SECRET_KEY

#### 3. DOMAIN/COMPANY  : For pushing logs.  		    
 Domain/Company Name.
e.g. API : https://rohitpanicker.github.com/resource-id

Domain/Company Name : rohitpanicker

#### 4.Bucket Name 
   The S3 bucket name where you have stored your Vpc Flow logs.

#### 5.Directory Name 
 If you have vpc flow logs stored inside a specified folder, please specify the folder name.

#### 6.VPC_CIDR 
 The CIDR of your vpc  eg. 10.0.0.0/24

#### 7.LogLevel 			  
eg. DEBUG,INFO,WARN,ERROR


###-------------------------------------------------------------------
### PROJECT VERSION
###-------------------------------------------------------------------


### 1.  Standalone-version

#### IF You are cloning this project , please provide the following values inside the config.properties file.  Placeholders have been provided.

Provide AWS Crendentials : As Environment varibales.

1.AWS_ACCESS_KEY_ID         

2.AWS_SECRET_ACCESS_KEY

3.API_ACCESS_KEY

4.API_SECRET_KEY

5.COMPANY NAME

6.Bucket

7.DIRECTORY.

8.VPC_CIDR

9.LOG LEVEL.


### 2. Dockerized-version
#### If You are deploying it using helm charts, please download the helm chart named 'vpc-poc' and insert values inside values.yaml file. Placeholders have been provided.


All listed parameters are specified as container environment variables at runtime.


1.AWS_ACCESS_KEY

2.AWS_SECRET_KEY

3.API_ACCESS_KEY

4.API_SECRET_KEY

5.COMPANY NAME

6.S3-Bucket

7.Bucket-DIRECTORY.

8.VPC_CIDR



### Running Dockerized version

####Make sure you have the helm chart.

```bash
command:  helm install user-generated-name helm-chart-name

e.g: helm install vpc-poc-helm vpc-poc

```

#### If providing a new yaml file for deployment.

```bash
helm install -f values.yaml vpc-poc-helm vpc-poc
```


###-------------------------------------------------------------------
###       Log4j Logging and Log Rotation Properties

log4j.rootLogger=DEBUG, fileLogger, stdout
log4j.appender.fileLogger=org.apache.log4j.RollingFileAppender
log4j.appender.fileLogger.layout=org.apache.log4j.PatternLayout
log4j.appender.fileLogger.layout.ConversionPattern=%d [%t] %-5p (%F:%L) - %m%n
log4j.appender.fileLogger.File=example.log
log4j.appender.fileLogger.MaxFileSize=5MB
log4j.appender.fileLogger.MaxBackupIndex=5


log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.Threshold=INFO
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d [%t] %-5p (%F:%L) - %m%n


###-------------------------------------------------------------------


###-------------------------------------------------------------------
###       Refreshing Application Configuration


WatchService on the Configuration files have been hooked, which will automatically update the Application Configuration Parameters. 




No need for restarting the application upon updation of config files.


###-------------------------------------------------------------------