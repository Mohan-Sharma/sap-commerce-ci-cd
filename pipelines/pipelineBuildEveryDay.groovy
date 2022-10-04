def relativeJunitLogsPath = 'core-customize/hybris/log/junit'
def projectDir = '$WORKSPACE'

pipeline {

    libraries {
        lib("shared-library@${params.JENKINS_SCRIPT_BRANCH}")
    }

    agent {
        node {
            label 'master'
        }
    }

    options {
        skipDefaultCheckout(true) // No more 'Declarative: Checkout' stage
    }

    stages {
        stage('Platform Build') {
            tools {
                jdk 'SapMachine11'
            }
            stages {

                stage('Prepare build name') {
                    steps{
                        script{
                            currentBuild.displayName = "${currentBuild.number}-concur-hybris"
                            currentBuild.description = "Building ${GIT_BRANCH}"
                        }
                    }
                }

                stage('Checkout code') {
                    steps {
                        cleanWs()
                        script {
                            checkoutRepository("${projectDir}", "${params.PROJECT_TAG}", "${params.PROJECT_REPO}")
                        }
                    }
                }

                stage('Extract suite') {
                    steps{
                        script {
                            extractCommerce(projectDir)
                        }
                    }
                }

                stage('Build suite') {
                    steps {
                        script {
                            executeCommerceBuild(projectDir)
                        }
                    }
                }

                stage('Run SonarQube') {
                    steps {
                        echo "SonarQube coming soon..."
                        //sonarqubeCheck("${BUILD_TAG}_develop", projectDir, "${params.SONAR_REPO_NAME}", "${params.SONAR_URL}") // Pipeline status is set as UNSTABLE if Sonar Quality Gate fails but build is SUCCESSFUL
                        //failIfBuildUnstable() // Fails build if Quality Gate fails
                    }
                }

                stage('Run Tests') {
                    steps {
                        echo "Running test coming soon..."
                        //executeAntTasks(projectDir, "yunitinit alltests -Dtestclasses.packages=${params.PACKAGE_TO_TEST}", 'dev')
                    }
                }
            }
        }
    }

    // post build actions
    post {
        success {
            emailNotification("${env.JOB_BASE_NAME}", "${currentBuild.number}", "${currentBuild.currentResult}", "${env.BUILD_URL}")
        }
        failure {
            emailNotification("${env.JOB_BASE_NAME}", "${currentBuild.number}", "${currentBuild.currentResult}", "${env.BUILD_URL}")
        }
        aborted {
            emailNotification("${env.JOB_BASE_NAME}", "${currentBuild.number}", "${currentBuild.currentResult}", "${env.BUILD_URL}")
        }
    }
}
