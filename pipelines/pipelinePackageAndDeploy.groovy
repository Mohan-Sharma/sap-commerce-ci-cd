def codeNumber;
def projectDir = '$WORKSPACE'
def tagToBuild;

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
        timeout(time: 90, unit: 'MINUTES') // build should take no more than 1.5 hour
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
                            currentBuild.displayName = "${currentBuild.number}-concur-hybris-${params.BUILD_NAME}"
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

                stage('Commit _ui resources') {
                    tools {
                        nodejs 'NodeJS'
                    }
                    steps{
                        script{
                            tagToBuild = addUiResources("${projectDir}", "modern-times-ccv2", "${params.PROJECT_REPO}", "${currentBuild.displayName}")
                        }
                    }
                }
            }
        }

        stage('CCv2 Build/Deploy') {
            stages {
                stage('Create Build') {
                    steps{
                        script{
                            codeNumber = commerceCloudBuild(tagToBuild, "TARDIS")
                            checkCommerceCloudBuildStatus(codeNumber)
                        }
                    }
                }

                stage('Deploy Build') {
                    steps{
                        script{
                            deploymentCode = commerceCloudDeploy(codeNumber, "${params.DB_UPDATE_MODE}", "${params.ENVIRONMENT_ID}", "${params.DEPLOY_STRATEGY}")
                            checkCommerceCloudDeployStatus(deploymentCode)
                        }
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
