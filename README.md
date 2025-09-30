<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://codium.ai/images/pr_agent/logo-dark.png" width="330">
  <source media="(prefers-color-scheme: light)" srcset="https://codium.ai/images/pr_agent/logo-light.png" width="330">
  <img src="https://codium.ai/images/pr_agent/logo-light.png" alt="logo" width="330">

</picture>

# QODO/Pr-Agent

This is a fork of the original pr-agent with GEBIT-specific changes like support for deepseek-r1 and improved context size handling.

To build the new docker container run:

```bash
docker build -f .\docker\Dockerfile -t pr-agent:0.5 -t pr-agent:latest .
docker login -u <username> docker-registry.local.gebit.de:5000
docker tag pr-agent:0.5 docker-registry.local.gebit.de:5000/dinf/pr-agent:0.5
docker push docker-registry.local.gebit.de:5000/dinf/pr-agent --all-tags
```

## Example

```bash
docker run --rm --name pr-agent-review -e GITLAB.URL="https://gitlab.local.gebit.de/" -e OPENAI.API_BASE="<litellm-key>" -e OPENAI.API_BASE="https://llm.dinf.gebit.dev/v1" -e CONFIG.GIT_PROVIDER="gitlab" -e GITLAB.PERSONAL_ACCESS_TOKEN=<gitlab-key> pr-agent:0.11 --pr_url https://gitlab.local.gebit.de/sparpos/sparpos-kassa/-/merge_requests/2377 review --ignore.glob=["*.xml"] --config.model="openai/deepseek-r1" --config.model_turbo="openai/deepseek-r1" --config.fallback_models="openai/deepseek-r1" --config.max_model_tokens=8500 --config.custom_model_max_tokens=8500 --config.ai_timeout=500 --config.verbosity_level=2  --config.require_ticket_analysis_review=false --config.publish_output_progress=false --config.publish_output=false
```