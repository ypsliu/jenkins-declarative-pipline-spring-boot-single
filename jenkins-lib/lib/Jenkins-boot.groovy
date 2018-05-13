SERVICE_NAME="service-account"
SERVICE_VERSION="1.0.1"
SCM_URL="git@10.50.10.214:jcpt/caifubao-jcpt.git"
SCM_BRANCH="test"
BUILD_ROOT_PATH="caifubao-service/"
pipeline {
    agent any
    tools {
        jdk 'java1.8'
        maven 'maven3'
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: "${SCM_BRANCH}", credentialsId: 'wuzhao', url: "${SCM_URL}"
            }
        }
        stage('Build') {
            steps {
                sh "mvn clean package install -Dmaven.test.skip=true -pl ${BUILD_ROOT_PATH}/${SERVICE_NAME}/"
            }
        }
        stage('Stash'){
            steps {
                stash includes: "${BUILD_ROOT_PATH}/${SERVICE_NAME}/target/*.jar", name:"${SERVICE_NAME}"
            }
        }
        node('test') {
            stage('UPLOAD'){
                steps {
                    unstash "${SERVICE_NAME}"
                }
            }
        }
    }
    post {
        success {
            echo 'success!'
        }
        failure {
            echo 'fail!'
        }
        aborted {
            echo 'aborted!'
        }
    }
}