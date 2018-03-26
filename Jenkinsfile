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
agent any
steps {
}
}
}
}
}

stage('UT') {
parallel {

stage('UTmessage') {
steps {
sh 'echo "UT message"'
}
}
stage('UnitTests') {
steps {
dir(path: 'project') {
sh 'gradle test '
}

}
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
gradle 'gradle-4.5.1'
}
}