def mergeRequestUrl = env.MERGE_REQUEST_URL
env.DOCKER_HOST = "tcp://build-host01.local.gebit.de:2952"
withCredentials([string(credentialsId: 'gitlab-api-review', variable: 'GITLAB_TOKEN'), string(credentialsId: 'openai-api', variable: 'OPENAI_API_KEY')]) {
    stage('Running review') {
        node {
            ansiColor('xterm') {
                script {
                    println "Reviewing merge request ${mergeRequestUrl}"
                    if (mergeRequestUrl == null) {
                        println "No merge request url found"
                        currentBuild.result = 'ABORTED'
                        return
                    }
                    if (env.GITLAB_TOKEN == null || env.GITLAB_TOKEN == '') {
                        println "No gitlab token found"
                        currentBuild.result = 'ABORTED'
                        return
                    } else {
                        println "Loaded gitlab token: " + env.GITLAB_TOKEN.substring(0, 5) + " length " + env.GITLAB_TOKEN.length()
                    }
                    def openAI_apiKey = env.OPENAI_API_KEY
                    if (openAI_apiKey == null) {
                        println "No openAI api key found"
                        currentBuild.result = 'ABORTED'
                        return
                    }
                    println "Initialized checks succeeded."
                    try {
                        sh('docker run --rm --name pr-agent-describe -e GITLAB.URL="https://gitlab.local.gebit.de/" -e OLLAMA__API_BASE="https://rtx-dev03.dinf.gebit.dev" -e CONFIG.GIT_PROVIDER="gitlab" -e GITLAB.PERSONAL_ACCESS_TOKEN=$GITLAB_TOKEN docker-registry.local.gebit.de:5000/dinf/pr-agent:0.11 --pr_url $MERGE_REQUEST_URL describe --ignore.glob=["*.xml"] --config.model="ollama/deepseek-r1:14b" --config.model_turbo="ollama/deepseek-r1:14b" --config.fallback_models="ollama/deepseek-r1:14b" --config.max_model_tokens=8500 --config.custom_model_max_tokens=8500 --config.ai_timeout=500 --config.verbosity_level=2 --config.publish_output_progress=false')
                    } finally {
                        try {
                            sh('docker rm -f pr-agent-describe')
                        } catch(Exception e) {
                            println "Describe container was already deleted"
                        }
                    }
                    try {
                        sh('docker run --rm --name pr-agent-review -e GITLAB.URL="https://gitlab.local.gebit.de/" -e OLLAMA__API_BASE="https://rtx-dev03.dinf.gebit.dev" -e CONFIG.GIT_PROVIDER="gitlab" -e GITLAB.PERSONAL_ACCESS_TOKEN=$GITLAB_TOKEN docker-registry.local.gebit.de:5000/dinf/pr-agent:0.11 --pr_url $MERGE_REQUEST_URL review --ignore.glob=["*.xml"] --config.model="ollama/deepseek-r1:14b" --config.model_turbo="ollama/deepseek-r1:14b" --config.fallback_models="ollama/deepseek-r1:14b" --config.max_model_tokens=8500 --config.custom_model_max_tokens=8500 --config.ai_timeout=500 --config.verbosity_level=2  --config.require_ticket_analysis_review=false --config.publish_output_progress=false')
                    } finally {
                        try {
                            sh('docker rm -f pr-agent-review')
                        } catch(Exception e) {
                            println "Review container was already deleted"
                        }
                    }
                }
            }
        }
    }
}