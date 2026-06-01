def sendNotificationToN8n(String status, String stageName, String image, String containerName, String message = "") {
    script {
        withCredentials([
            string(credentialsId: 'n8n-webhook', variable: 'N8N_WEBHOOK_URL')
        ]) {
            def payload = [
                project  : env.JOB_NAME,
                stage    : stageName,
                status   : status,
                build    : env.BUILD_NUMBER,
                branch   : env.GIT_BRANCH,
                image    : image ?: "N/A",
                container: containerName ?: "N/A",
                url      : "YOUR_WEB_APP_URL",
                message  : message,
                timestamp: new Date().format("yyyy-MM-dd'T'HH:mm:ssXXX")
            ]

            def body = groovy.json.JsonOutput.toJson(payload)

            try {
                httpRequest acceptType: 'APPLICATION_JSON',
                            contentType: 'APPLICATION_JSON',
                            httpMode: 'POST',
                            requestBody: body,
                            url: N8N_WEBHOOK_URL,
                            validResponseCodes: '200:299'

                echo "n8n webhook (${status}) sent successfully."
            } catch (err) {
                echo "Failed to send n8n webhook (${status}): ${err}"
            }
        }
    }
}

pipeline {
    agent any

    triggers {
        githubPush() 
    }

    environment {
        REGISTRY_USER = "YOUR_REGISTRY_USER"
        IMAGE_NAME = "condo-swift-api"
        DOCKER_HUB    = credentials('docker-hub-creds')
    }

    stages {
        stage('Build + Test') {
            when {
                expression { 
                    return env.GIT_BRANCH == 'origin/master' || env.GIT_BRANCH == 'master' 
                }
            }
            steps {
                sh 'mvn clean verify'
            }
        }

        stage('Build & Push Docker') {
            when {
                expression { 
                    return env.GIT_BRANCH == 'origin/master' || env.GIT_BRANCH == 'master' 
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
                    return env.GIT_BRANCH == 'origin/master' || env.GIT_BRANCH == 'master' 
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
                    return env.GIT_BRANCH == 'origin/master' || env.GIT_BRANCH == 'master' 
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
        success {
            echo 'Deploy Success!'
            sendNotificationToN8n(
                'SUCCESS',
                'Deploy Completed',
                "${REGISTRY_USER}/${IMAGE_NAME}:latest",
                IMAGE_NAME
            )
        }
    
        failure {
            echo 'Build Failed!'
            sendNotificationToN8n(
                'FAILED',
                env.STAGE_NAME ?: 'Unknown Stage',
                'N/A',
                'N/A',
                currentBuild.currentResult
            )
        }

        aborted {
            echo 'Build Aborted!'
            sendNotificationToN8n(
                'ABORTED',
                env.STAGE_NAME ?: 'Unknown Stage',
                'N/A',
                'N/A',
                currentBuild.currentResult
            )
        }
    
        always {
            cleanWs()
        }
    }
}