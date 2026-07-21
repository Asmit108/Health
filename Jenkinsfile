pipeline {
    agent any
    environment {
        SPRING_DATASOURCE_USERNAME = credentials('db-username')
        SPRING_DATASOURCE_PASSWORD = credentials('db-password')
        SPRING_AI_OPENAI_API_KEY = credentials('api-key')
        JWT_SECRET = credentials('jwt-secret')
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Deploy to Docker') {
            steps {
                sh 'docker-compose up -d --build'
            }
        }
    }
}