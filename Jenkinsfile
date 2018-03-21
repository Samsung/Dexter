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
steps {
dir(path: 'project') {
sh 'gradle build -x test'
}

}
}
stage('Docker') {
steps {
dir(path: 'project/dexter-server') {
sh 'echo "vs message"'
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