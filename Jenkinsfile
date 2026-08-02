pipeline {
    agent any
    environment {
        SPRING_DATASOURCE_USERNAME = credentials('db-username')
        SPRING_DATASOURCE_PASSWORD = credentials('db-password')
        SPRING_AI_OPENAI_API_KEY = credentials('api-key')
        JWT_SECRET = credentials('jwt-secret')
        SPRING_SSL_KEY_STORE_PASSWORD = credentials('keystore-password')
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
                bat '"C:\\Program Files\\Git\\usr\\bin\\ssh-agent.exe" -s'
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
                withCredentials([
                        sshUserPrivateKey(
                                credentialsId: 'ec2-ssh-key',
                                keyFileVariable: 'SSH_KEY'
                        ),
                        string(
                                credentialsId: 'jwt-secret',
                                variable: 'JWT_SECRET'
                        ),
                        string(
                                credentialsId: 'api-key',
                                variable: 'SPRING_AI_OPENAI_API_KEY'
                        ),
                        string(
                                credentialsId: 'keystore-password',
                                variable: 'SPRING_SSL_KEY_STORE_PASSWORD'
                        ),
                        string(
                                credentialsId: 'db-username',
                                variable: 'SPRING_DATASOURCE_USERNAME'
                        ),
                        string(
                                credentialsId: 'db-password',
                                variable: 'SPRING_DATASOURCE_PASSWORD'
                        )
                ]) {
                    bat '''
                    scp -i "%SSH_KEY%" -o StrictHostKeyChecking=no target/check-0.0.1-SNAPSHOT.jar ubuntu@13.204.66.133:~/
                    
                    ssh -i "%SSH_KEY%" -o StrictHostKeyChecking=no ubuntu@13.204.66.133 "cd ~/Health && \
                    mkdir -p target && \
                    mv -f ~/check-0.0.1-SNAPSHOT.jar target/ && \
                    echo SPRING_DATASOURCE_URL=%SPRING_DATASOURCE_URL% > .env && \
                    echo SPRING_DATASOURCE_USERNAME=%SPRING_DATASOURCE_USERNAME% >> .env && \
                    echo "SPRING_DATASOURCE_PASSWORD"=%SPRING_DATASOURCE_PASSWORD% >> .env && \
                    echo JWT_SECRET=%JWT_SECRET% >> .env && \
                    echo SPRING_AI_OPENAI_API_KEY=%SPRING_AI_OPENAI_API_KEY% >> .env && \
                    echo SPRING_AI_OPENAI_CHAT_MODEL=%SPRING_AI_OPENAI_CHAT_MODEL% >> .env && \
                    echo SPRING_AI_RETRY_MAX_ATTEMPTS=%SPRING_AI_RETRY_MAX_ATTEMPTS% >> .env && \
                    echo "SPRING_SSL_KEY_STORE_PASSWORD"=%SPRING_SSL_KEY_STORE_PASSWORD% >> .env && \
                    git pull && \
                    docker compose down && \
                    docker compose up -d --build"
                    '''
                }
            }
        }
    }
}