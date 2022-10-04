# Jenkins Pipeline for hybris

## Description
This project contains scripts for Jenkins CI projects with SAP Commerce Cloud. The Pipelines are defined as [Scripted Pipeline](https://www.jenkins.io/doc/book/pipeline/syntax/#scripted-pipeline). It means that they are written in Groovy.

## Jenkins Configuration
- add Global Shared Library - with name "shared-library"
- ![alt text](https://media.github.tools.sap/user/488/files/e254d2cd-a0a9-42eb-833a-efacb447cbc5?raw=true)
- using inbuilt node with label "master"
- create credentials for github repository.
- create credentials for SAP Commerce Cloud.
- create credentials for Sonar and add it to Sonar Plugin
- configure pull request builder
- ![alt text](https://media.github.tools.sap/user/488/files/5605c6b0-dd35-4522-aa5b-605a1087ab88?raw=true)

### Jenkins Plugin
- [Hidden Parameter](https://wiki.jenkins.io/display/JENKINS/Jenkins+Hidden+Parameter+Plugin) - This plugin adds support for Parameter. After the plugin is installed,in job configuration's page,you can see Hidden Parameter.
- [Extensible Choice Parameter](https://plugins.jenkins.io/extensible-choice-parameter/) - This plugin adds "Extensible Choice" as a build parameter.You can select how to retrieve choices, including the way to share choices among all jobs.
- [GitHub Pull Request Builder](https://plugins.jenkins.io/ghprb/) - This plugin builds pull requests in github and report results.
- [Environment Dashboard](https://plugins.jenkins.io/environment-dashboard/) - This Jenkins plugin creates a custom view which can be used as a dashboard to display which code release versions have been deployed to which test and production environments (or devices).
- [SonarQube Scanner](https://plugins.jenkins.io/sonar/) - This plugin allow easy integration of SonarQube™, the open source platform for Continuous Inspection of code quality.
- [Job DSL](https://plugins.jenkins.io/job-dsl/) -  The Job DSL plugin attempts to solve this problem by allowing jobs to be defined in a programmatic form in a human readable file.
- [Masked Password](https://plugins.jenkins.io/mask-passwords/) - This plugin allows masking passwords that may appear in the console, including the ones defined as build parameters.
- [Pipeline Utility Steps](https://plugins.jenkins.io/pipeline-utility-steps/) - Small, miscellaneous, cross platform utility steps for Jenkins Pipeline jobs.

## How to start?
1. To start using this project create a pipeline Job in Jenkins which will act as the seeder job. Pipeline can be configured as below. Once this job is ran manually, this will create 2 new jobs - 
   1. ![alt text](https://media.github.tools.sap/user/488/files/60f24775-c971-4ebe-a100-f60971fe56c5?raw=true)
      1. One for every PR quality check 
      2. One for build and deploy to CCv2
2. In order to use these 2 job, you need to place the right SAP Commerce artifact in `$JENKINS_HOME/userContent/` check `extractCommerce.groovy` for details.
3. If any change is required on these jobs, don't change it manually instead use this repository to make the changes and run the seeder job again.
