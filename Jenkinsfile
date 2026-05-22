pipeline {
    agent any

    triggers {
        githubPush() 
    }

    environment {
        REGISTRY_USER = "jeyzdev"
        IMAGE_NAME = "condo-swift-api"
        DOCKER_HUB    = credentials('docker-hub-creds')
    }

    stages {
        stage('Build + Test + Sonar') {
            when {
                expression { 
                    return env.GIT_BRANCH == 'origin/main' || env.GIT_BRANCH == 'main' 
                }
            }
            steps {
                sh 'mvn clean verify'
            }
        }

        stage('Build & Push Docker') {
            when {
                expression { 
                    return env.GIT_BRANCH == 'origin/main' || env.GIT_BRANCH == 'main' 
                }
            }
            steps {
                sh 'echo ${DOCKER_HUB_PSW} | docker login -u ${DOCKER_HUB_USR} --password-stdin'
                sh 'docker build -t ${REGISTRY_USER}/${IMAGE_NAME}:latest .'
                sh 'docker push ${REGISTRY_USER}/${IMAGE_NAME}:latest'
            }
        }

        stage('Stop Old Container') {
            when {
                expression { 
                    return env.GIT_BRANCH == 'origin/main' || env.GIT_BRANCH == 'main' 
                }
            }
            steps {
                sh '''
                docker stop ${IMAGE_NAME} || true
                docker rm ${IMAGE_NAME} || true
                docker rmi ${REGISTRY_USER}/${IMAGE_NAME}:latest || true
                '''
            }
        }

        stage('Deploy') {
            when {
                expression { 
                    return env.GIT_BRANCH == 'origin/main' || env.GIT_BRANCH == 'main' 
                }
            }
            steps {
                sh '''
                docker run -d -p 8082:8080 \
                --name ${IMAGE_NAME} \
                --restart always \
                --env-file /home/ubuntu/condo-swift/.env \
                ${REGISTRY_USER}/${IMAGE_NAME}:latest
                '''
                
                sh 'docker logout'
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}