def pipelineRepo = 'https://github.tools.sap/CONCUR-DIGITAL-STORE/concur-jenkins'
def projectRepo = 'https://github.tools.sap/CONCUR-DIGITAL-STORE/concur-hybris'
def projectTag = '${GIT_BRANCH}'
def sonarUrl = 'http://localhost:9000'
def projectRepoName = 'concur-hybris'
def packageToTest = 'com.sap.concur.*'

def subscriptionId = '${SUBSCRIPTION_ID}'
def token = '${CLOUD_API_TOKEN}'
def buildName = 'ccv2'
def environment = '${ENVIRONMENT_ID}'

// ****************************
// *** JOB PARAMETERS
// ****************************
class JobParameters {

    static void setLogs(job) {
        job.with {
            logRotator(-1, 15, -1, -1)
        }
    }

    static void setLibraryBranchParam(job) {
        job.with {
            parameters {
                stringParam('JENKINS_SCRIPT_BRANCH', 'main', 'Library branch name')
            }
        }
    }

    static void setProjectRepository(job, projectRepo) {
        job.with {
            parameters {
                stringParam('PROJECT_REPO', projectRepo, 'URL for the code repository containing your code')
            }
        }
    }

    static void setProjectTag(job, projectTag) {
        job.with {
            parameters {
                stringParam('PROJECT_TAG', projectTag, 'Tag or branch to use from your code project repository')
            }
        }
    }

    static void setProjectName(job, projectRepoName) {
        job.with {
            parameters {
                stringParam('PROJECT_REPO_NAME', projectRepoName, 'Identifier for your project')
            }
        }
    }

    static void setSonarUrl(job, sonarUrl) {
        job.with {
            parameters {
                stringParam('SONAR_URL', sonarUrl, 'Sonar Url')
            }
        }
    }

    static void setPackageToTest(job, packageToTest) {
        job.with {
            parameters {
                stringParam('PACKAGE_TO_TEST', packageToTest, 'Package(s) to test')
            }
        }
    }

    static void setBuildName(job, buildName) {
        job.with {
            parameters {
                stringParam('BUILD_NAME', buildName, 'Build Name to be used as an identifier in Cloud Portal')
            }
        }
    }

    static void setDatabaseUpdateMode(job) {
        job.with {
            parameters {
                choiceParam('DB_UPDATE_MODE', ['NONE', 'UPDATE', 'INITIALIZE'], 'Possible options for databaseUpdateMode are NONE, UPDATE, and INITIALIZE')
            }
        }
    }

    static void setEnvironment(job, environment) {
        job.with {
            parameters {
                choiceParam('ENVIRONMENT_ID', ['d1', 'd2', 's1'], 'The environment ID to deploy to')
            }
        }
    }

    static void setStrategy(job) {
        job.with {
            parameters {
                choiceParam('DEPLOY_STRATEGY', ['ROLLING_UPDATE', 'RECREATE'], 'Deployment strategy (ROLLING_UPDATE or RECREATE)')
            }
        }
    }
}

// ****************************
// *** JOB DEFINITION
// ****************************

def buildCommerce = pipelineJob('build-commerce') {

    properties {
        githubProjectUrl("${projectRepo}")
    }

    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url("${pipelineRepo}")
                        credentials("a06c8283-d34d-483f-a9c3-6c82059a4277")
                    }
                    branch('${JENKINS_SCRIPT_BRANCH}')
                }
                scriptPath('pipelines/pipelineBuildEveryDay.groovy')
                lightweight(false)
            }
        }
    }
    triggers {
        githubPullRequest {
            admin('I504180')
            triggerPhrase('build please')
            useGitHubHooks()
            permitAll()
            displayBuildErrorsOnDownstreamBuilds()
            extensions {
                commitStatus {
                    context('Jenkins')
                    completedStatus('SUCCESS', 'All is well')
                    completedStatus('FAILURE', 'Something went wrong. Investigate!')
                    completedStatus('ERROR', 'Something went really wrong. Investigate!')
                }
                buildStatus {
                    completedStatus('SUCCESS', 'There were no errors, go have a cup of coffee...')
                    completedStatus('FAILURE', 'There were errors, for info, please see...')
                    completedStatus('ERROR', 'There was an error in the infrastructure, please contact...')
                }
            }
        }
    }
}

JobParameters.setLogs(buildCommerce)
JobParameters.setLibraryBranchParam(buildCommerce)
JobParameters.setProjectRepository(buildCommerce, projectRepo)
JobParameters.setProjectTag(buildCommerce, projectTag)
JobParameters.setProjectName(buildCommerce, projectRepoName)
JobParameters.setSonarUrl(buildCommerce, sonarUrl)
JobParameters.setPackageToTest(buildCommerce, packageToTest)

def packageAndDeploy = pipelineJob('package-and-deploy') {
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url("${pipelineRepo}")
                        credentials("a06c8283-d34d-483f-a9c3-6c82059a4277")
                    }
                    branch('${JENKINS_SCRIPT_BRANCH}')
                }
                scriptPath('pipelines/pipelinePackageAndDeploy.groovy')
                lightweight(false)
            }
        }
    }
}


JobParameters.setLogs(packageAndDeploy)
JobParameters.setLibraryBranchParam(packageAndDeploy)
JobParameters.setBuildName(packageAndDeploy, buildName)
JobParameters.setProjectTag(packageAndDeploy, projectTag)
JobParameters.setDatabaseUpdateMode(packageAndDeploy)
JobParameters.setEnvironment(packageAndDeploy, environment)
JobParameters.setStrategy(packageAndDeploy)
JobParameters.setProjectRepository(packageAndDeploy, projectRepo)

// ****************************
// *** PIPELINE LIST VIEW DEFINITION
// ****************************

listView('Commerce Pipelines') {
    description('All hybris build and deploy jobs')
    jobs {
        names(
            'build-commerce',
            'package-and-deploy',
        )
    }
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
}
