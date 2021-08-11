@echo off
mvn clean package -Dmaven.test.skip=true
copy /y jaqy-avro\target\jaqy-avro*.jar dist & copy /y jaqy-s3\target\jaqy-s3*.jar dist & copy /y jaqy-azure\target\jaqy-azure*.jar dist
