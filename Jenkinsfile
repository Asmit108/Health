pipeline {
    agent any
    environment {
        SPRING_DATASOURCE_USERNAME = credentials('db-username')
        SPRING_DATASOURCE_PASSWORD = credentials('db-password')
        SPRING_AI_OPENAI_API_KEY = credentials('api-key')
        JWT_SECRET = credentials('jwt-secret')
        SPRING_SSL_KEY_STORE_PASSWORD= credentials('keystore-password')
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                bat 'set'
                bat 'where ssh'
                bat 'ssh -V'
//                bat 'mvn clean install -Dspring.datasource.url=jdbc:mysql://localhost:3306/health'
                bat 'mvn clean install'
            }
        }
//        stage('Deploy to Docker') {
//            steps {
//                bat 'docker-compose up -d --build'
//            }
//        }
        stage('Deploy to Production') {
            steps {
                sshagent(credentials: ['ec2-ssh-key']) {
                    bat '''
                    scp target/check-0.0.1-SNAPSHOT.jar ubuntu@13.204.66.133:~/Health/target/
                    ssh ubuntu@13.204.66.133 ^
                    "cd ~/Health && ^
                    git pull && ^
                    docker compose down && ^
                    docker compose up -d --build"
                    '''
                }
            }
        }
    }
}