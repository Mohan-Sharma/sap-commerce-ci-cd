//https://www.jenkins.io/doc/pipeline/steps/job-dsl/
//https://www.jenkins.io/doc/book/pipeline/jenkinsfile/
node('master') {
  checkout scm
  jobDsl targets: ['dsl/seedJobBuilder.groovy'].join('\n'),
       removedJobAction: 'IGNORE',
       removedViewAction: 'IGNORE',
       lookupStrategy: 'SEED_JOB'
}