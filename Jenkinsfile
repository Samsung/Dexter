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
agent { dockerfile {
        filename 'Dockerfile'
        dir 'project/dexter-server'
    }
    } 
steps {
dir(path: 'project/dexter-server') {
sh 'docker -v'
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