pipeline {
agent any
stages {
stage('InitStep') {
steps {
sh '''java -version;
echo "shell message"'''
}
}
stage('Build') {
parallel {
stage('Build') {
steps {
dir(path: 'project') {
sh 'gradle build -x test'
}
}
}
stage('Docker') {
steps {
dir(path: 'project/dexter-server') {
sh 'docker pull srpol/dexter:latest'
sh 'docker run —restart=always —name=dexter-TEST_PROJECT -td -p 4982:4982 —env DBHOST=dexter-test —env DBNAME=my_dexter_db —env DBUSER=root —env DBPASSWORD=gre4d sha256:f2952df1d03b18d420d8ba93b4b627b780e6c6faf864559c5b2a5263ed53405c'
}
}
}
}
}

stage('UnitTests') {
steps {
dir(path: 'project') {
sh 'gradle test '
}
}
}


stage('Finish') {
steps {
sh 'echo "The end"'
}
}
}
tools {
gradle 'gradle'
}
}