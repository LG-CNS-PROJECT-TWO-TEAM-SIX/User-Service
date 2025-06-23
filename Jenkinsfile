def APP_NAME
def APP_VERSION
def DOCKER_IMAGE_NAME
def PROD_BUILD = false

pipeline {
    agent {
        node {
            label 'master'
        }
    }

    parameters {
        gitParameter branch: '',
            branchFilter: '.*',
            defaultValue: 'origin/main',
            description: '', listSize: '0',
            name: 'TAG',
            quickFilterEnabled: false,
            selectedValue: 'DEFAULT',
            sortMode: 'DESCENDING_SMART',
            tagFilter: '*',
            type: 'PT_BRANCH_TAG'

        booleanParam defaultValue: false, description: '', name: 'RELEASE'
    }

    environment {
        GIT_URL = "https://github.com/LG-CNS-PROJECT-TWO-TEAM-SIX/User-Service.git"
        GITHUB_CREDENTIAL = "github-token"
        ARTIFACTS = "build/libs/**"
        DOCKER_REGISTRY = "haribonyam"
        DOCKERHUB_CREDENTIAL = 'dockerhub-token'
        DISCORD_WEBHOOK = "https://discord.com/api/webhooks/1386572756358791168/sAQu_esRsWKDccimDKVALESFvyMvRW-KaiLYERTnAYtfwEh24ZCY_aazvIFf14wh81bP"
    }

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: "30", artifactNumToKeepStr: "30"))
        timeout(time: 120, unit: 'MINUTES')
    }

    tools {
        gradle 'Gradle 8.14.2'
        jdk 'OpenJDK 17'
        dockerTool 'Docker'
    }

    stages {
        stage('Set Version') {
            steps {
                script {
                    APP_NAME = sh (
                        script: "gradle -q getAppName",
                        returnStdout: true
                    ).trim()
                    APP_VERSION = sh (
                        script: "gradle -q getAppVersion",
                        returnStdout: true
                    ).trim()

                    DOCKER_IMAGE_NAME = "${DOCKER_REGISTRY}/${APP_NAME}:${APP_VERSION}"

                    echo "IMAGE_NAME is ${APP_NAME}"
                    echo "IMAGE_VERSION is ${APP_VERSION}"
                    echo "DOCKER_IMAGE_NAME is ${DOCKER_IMAGE_NAME}"

                    echo "TAG is ${params.TAG}"
                    if (!params.TAG.startsWith('origin') && !params.TAG.endsWith('/main')) {
                        if (params.RELEASE) {
                            DOCKER_IMAGE_NAME += '-RELEASE'
                            PROD_BUILD = true
                        } else {
                            DOCKER_IMAGE_NAME += '-TAG'
                        }
                    }
                }
            }
        }

        stage('Build & Test Application') {
            steps {
                sh "gradle clean build"
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build "${DOCKER_IMAGE_NAME}"
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry("", DOCKERHUB_CREDENTIAL) {
                        docker.image("${DOCKER_IMAGE_NAME}").push()
                    }
                    sh "docker rmi ${DOCKER_IMAGE_NAME}"
                }
            }
        }
    }

    post {
        success {
            echo '✅ 빌드 성공!'
            sh """
                curl -H "Content-Type: application/json" \
                     -X POST \
                     -d '{"content": "✅ *[빌드 성공]*\\n- 프로젝트: ${APP_NAME}\\n- 태그: ${params.TAG}\\n- 이미지: ${DOCKER_IMAGE_NAME}"}' \
                     $DISCORD_WEBHOOK
            """
        }

        failure {
            echo '❌ 빌드 실패...'
            sh """
                curl -H "Content-Type: application/json" \
                     -X POST \
                     -d '{"content": "❌ *[빌드 실패]*\\n- 프로젝트: ${APP_NAME}\\n- 태그: ${params.TAG}"}' \
                     $DISCORD_WEBHOOK
            """
        }
    }
}
