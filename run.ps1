#$REPOSITORY=./mvnw org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression="docker.image.prefix" -q -DforceStdout
$Env:REPOSITORY=(./mvnw org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression="docker.image.prefix" -q -DforceStdout)
$Env:IMAGE=(./mvnw org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression="project.artifactId" -q -DforceStdout)
$Env:REVISION=(./mvnw org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression="revision" -q -DforceStdout)
$Env:CHANGELIST=(./mvnw org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression="changelist" -q -DforceStdout)
$Env:TAG = $Env:REVISION
$Env:TAG += $Env:CHANGELIST

& docker network inspect prox 2>&1

if($_ -is [System.Management.Automation.ErrorRecord]) {
  docker network create --driver=overlay prox
}

docker stack deploy -c src/main/docker/docker-compose.yml ($Env:IMAGE)