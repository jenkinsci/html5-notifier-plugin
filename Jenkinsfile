#!groovy

if (JENKINS_URL == 'https://ci.jenkins.io/') {
    buildPlugin(configurations: buildPlugin.recommendedConfigurations())
    return
}

pipeline {
    agent {
        docker {
            image 'maven:3.6-jdk-8'
        }
    }
    options {
        timeout(time: 60, unit: 'MINUTES')
        ansiColor('xterm')
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn -B compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B verify test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn -B package'
                archiveArtifacts 'target/*.hpi'
            }
        }
    }
}
