pipeline {
  agent any
  stages {
    stage('initStep') {
      steps {
        tool(name: 'gradle-4.5.1', type: 'gradle-4.5.1')
      }
    }
    stage('Build') {
      steps {
        sh 'gradle build'
      }
    }
  }
  tools {
    gradle 'gradle-4.5.1'
  }
}