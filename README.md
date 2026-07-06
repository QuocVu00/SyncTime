chạy backend
mở docker 
docker compose up -d

tenminal chạy

$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"

$env:Path="$env:JAVA_HOME\bin;$env:Path"

java -version

.\gradlew :app:run
