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