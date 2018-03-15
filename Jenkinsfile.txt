pipeline {
agent any
stages {
stage('InitStep') {
steps {
sh '''java -version;
echo "tt shell message"'''
}
}
stage('Build') {
steps {
dir(path: 'project') {
sh 'gradle build -x test'
}

}
}
stage('UT') {
parallel {
stage('UTmessage') {
steps {
sh 'echo "tt UT message"'
}
}
stage('UnitTests') {
steps {
dir(path: 'project') {
+ sh 'gradle test '
}

}
}
}
}
stage('Finish') {
steps {
sh 'echo "tt The end"'
}
}
}
tools {
gradle 'gradle-4.5.1'
}
}