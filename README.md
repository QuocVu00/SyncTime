Nhánh backend:
feature/backend-ktor-postgresql

Cách chạy:
docker compose up -d

$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"

$env:Path="$env:JAVA_HOME\bin;$env:Path"

.\gradlew :app:run

Test:
http://localhost:8080/health
