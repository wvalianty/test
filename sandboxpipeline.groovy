pipelineJob('aladdin_backend_py310_sandbox_test') {
    definition {
           cps {
             script('''
pipeline {
    agent any
    options {
        disableConcurrentBuilds()
    }
    parameters {
        stringParam('TAG', '', '')
    }
    environment {
        REPO="git@1:aladdin_backend.git"
        DEPLOYMENT_REPO="git@1:aladdin_backend.git"
        CREDENTIALS_ID="872d4bac-5ec1-4130-b127-5ae615538062"
        AGENT_NAME="auth_py310_sandbox_py310_v1"
        AVATAR_API_URL="https://1/fabistrano/v1"
    }
    stages {
        stage("code") {
            steps {
                checkout([$class: 'GitSCM',
                    branches: [[name: "$TAG"]],
                    extensions: [],
                    userRemoteConfigs: [[credentialsId: env.CREDENTIALS_ID, url: env.REPO]]
                ])
                sshagent(credentials: [env.CREDENTIALS_ID]) {
                    sh"""#!/bin/bash
                        git config user.email "cd-jenkins@localhost"
                        git config user.name "cd-jenkins"
                        git push $DEPLOYMENT_REPO $TAG
                    """
                }
                script {
                    env.BUILD_COMMIT = sh returnStdout: true, script: 'git log -n 1'
                    env.PREVIOUS_TAG = currentBuild.getPreviousBuild().getRawBuild().actions.find{ it instanceof ParametersAction }?.parameters[0].getValue()
                    changes = sh returnStdout: true, script: '/usr/local/bin/v1.1.2/jenkins-changes-add.py'
                    addchangestobuildchangelog changelogText: changes
                }
            }
        }
        stage("build_deploy") {
            environment {
                STAGE_COMMAND="fab build --branch=${TAG} --version=${BUILD_ID} && fab deploy --version=${BUILD_ID}"
            }
            steps {
                wrap([$class: "BuildUser"]) {
                    sh "/usr/bin/python3 /usr/local/bin/v1.1.2/jenkins-submit.py"
                }
            }
        }
    }
    post {
        always {
            influxDbPublisher customPrefix: '', customProjectName: '${JOB_NAME}', jenkinsEnvParameterField: '', jenkinsEnvParameterTag: '', selectedTarget: 'jenkins\'s influxdb'
        }
    }
}
              '''.stripIndent())
       sandbox()
          }
      }
}

